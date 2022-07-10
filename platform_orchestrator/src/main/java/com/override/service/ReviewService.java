package com.override.service;

import com.override.feign.NotificatorFeign;
import com.override.mapper.ReviewMapper;
import com.override.model.Authority;
import com.override.model.PlatformUser;
import com.override.repository.PlatformUserRepository;
import com.override.repository.ReviewRepository;
import dto.ReviewDTO;
import dto.ReviewFilterDTO;
import enums.Communication;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private PlatformUserRepository platformUserRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private NotificatorFeign notificatorFeign;

    public static String CONFIRMATED_REVIEW_MESSAGE_TELEGRAM = "Ментор %s подтвердил ревью %s в %s";
    public static String DELETED_REVIEW_MESSAGE_TELEGRAM = "Ментор вынужден был отменить ревью. " +
            "Попробуйте записаться на другое время";
    public static String CHANGED_REVIEW_TIME_MESSAGE_TELEGRAM = "Ментор изменил время ревью. Ревью пройдет %s в %s";
    public static String CHANGED_REVIEW_MENTOR_MESSAGE_TELEGRAM = "На Ваше ревью сменился ментор. Его проведет: %s %s в %s ";
    public static String NEW_REVIEW_MESSAGE_TELEGRAM = "Новый запрос на ревью от пользователя %s";

    /**
     * Saves a new or changes an existing review
     * If the review is new, then the user's login is assigned to the student
     * Let's deal with conditions.
     * reviewDTO.getId() == null, reviewDTO.getStudentLogin() == null - A notification is sent to Telegram about
     * a new review request from a student to a mentor. A student's username is recorded in the data of review.
     * When a review is just being created, it is assumed that this request is made by a student,
     * which means that the review does not have an id and its login is not passed to it
     * <p>
     * reviewDTO.getBookedTime() != null && reviewDTO.getMentorLogin() == "" -
     * A review confirmation notification is sent to the student in a telegram.  A mentor's username is recorded in the data of review.
     * <p>
     * reviewDTO.getMentorLogin() != null && reviewDTO.getBookedTime() != null - changes are made to an already confirmed review. there is two ways:
     * !reviewDTO.getMentorLogin().equals(userLogin) -A notification about the change of the mentor will be sent with an indication of the time.
     * <p>
     * reviewDTO.getMentorLogin().equals(userLogin) -
     * if the logins are the same, a time change notification is sent.
     *
     * @param reviewDTO review information obtained from the request
     * @param userLogin username of the user making the request
     */
    public void saveOrUpdate(ReviewDTO reviewDTO, String userLogin) {
        if (reviewDTO.getId() == null && reviewDTO.getStudentLogin() == null) {
            List<PlatformUser> mentorList = platformUserRepository.findByAuthoritiesContains(
                    new Authority(2L, "ROLE_ADMIN"));
            for (PlatformUser mentor : mentorList) {
                sendMessage(mentor.getLogin(), String.format(NEW_REVIEW_MESSAGE_TELEGRAM,
                        userLogin), Communication.TELEGRAM);
                reviewDTO.setStudentLogin(userLogin);
            }
        }
        if (reviewDTO.getBookedTime() != null && reviewDTO.getMentorLogin() == "") {
            sendMessage(reviewDTO.getStudentLogin(), String.format(CONFIRMATED_REVIEW_MESSAGE_TELEGRAM, userLogin,
                    reviewDTO.getBookedDate(), reviewDTO.getBookedTime()), Communication.TELEGRAM);
            reviewDTO.setMentorLogin(userLogin);
        } else if (reviewDTO.getMentorLogin() != null && reviewDTO.getBookedTime() != null) {
            if (!reviewDTO.getMentorLogin().equals(userLogin)) {
                sendMessage(reviewDTO.getStudentLogin(), String.format(CHANGED_REVIEW_MENTOR_MESSAGE_TELEGRAM,
                        userLogin, reviewDTO.getBookedTime(), reviewDTO.getBookedDate()), Communication.TELEGRAM);
            } else {
                sendMessage(reviewDTO.getStudentLogin(), String.format(CHANGED_REVIEW_TIME_MESSAGE_TELEGRAM,
                        reviewDTO.getBookedTime(), reviewDTO.getBookedDate()), Communication.TELEGRAM);
            }
        }
        reviewRepository.save(reviewMapper.dtoToEntity(reviewDTO,
                platformUserRepository.findFirstByLogin(reviewDTO.getStudentLogin()),
                platformUserRepository.findFirstByLogin(reviewDTO.getMentorLogin())));
    }

    public void sendMessage(String login, String message, Communication type) {
        try {
            notificatorFeign.sendMessage(login, message, type);
        } catch (FeignException e) {
            System.out.println("FeignException. Возможно, проблема в том, что логин " + login + " Telegram не существует");
        }
    }

    public void delete(Long id) {
        sendMessage(reviewRepository.findById(id).get().getStudent().getLogin(),
                DELETED_REVIEW_MESSAGE_TELEGRAM, Communication.TELEGRAM);
        reviewRepository.deleteById(id);
    }

    /**
     * Searches for the necessary reviews using the filter
     * Reviews can be found by student or mentor login and by date
     * If these parameters have null, then we are looking for new reviews that have not yet assigned a mentor and time
     *
     * @param reviewFilterDTO comes from request
     * @return returns a list of reviewDTOs obtained by mapping the list of found reviews
     */
    public List<ReviewDTO> find(ReviewFilterDTO reviewFilterDTO) {
        if (reviewFilterDTO.getMentorLogin() != null) {
            return (reviewRepository.findReviewByMentorLogin(reviewFilterDTO.getMentorLogin())).stream()
                    .map(reviewMapper::entityToDto).collect(Collectors.toList());
        }
        if (reviewFilterDTO.getStudentLogin() != null && reviewFilterDTO.getBookedDate() != null) {
            return (reviewRepository.findReviewByStudentLoginAndBookedDate(reviewFilterDTO.getStudentLogin(),reviewFilterDTO.getBookedDate())).stream()
                    .map(reviewMapper::entityToDto).collect(Collectors.toList());
        }
        if (reviewFilterDTO.getStudentLogin() != null) {
            return (reviewRepository.findReviewByStudentLogin(reviewFilterDTO.getStudentLogin())).stream()
                    .map(reviewMapper::entityToDto).collect(Collectors.toList());
        }
        if (reviewFilterDTO.getBookedDate() != null) {
            return (reviewRepository.findReviewByBookedDate(reviewFilterDTO.getBookedDate())).stream()
                    .map(reviewMapper::entityToDto).collect(Collectors.toList());
        } else {
            return (reviewRepository.findReviewByMentorIsNull()).stream()
                    .map(reviewMapper::entityToDto).collect(Collectors.toList());
        }
    }

    public void sendScheduledNotification() {
        reviewRepository.findReviewByBookedDate(LocalDateTime.now().plusMinutes(10).toLocalDate())
                .stream()
                .filter(review -> review.getBookedTime() != null)
                .filter(review -> LocalDateTime.of(review.getBookedDate(),
                        review.getBookedTime()).isAfter(LocalDateTime.now()))
                .filter(review -> review.getBookedTime().isBefore(LocalTime.now().plusMinutes(10)))
                .forEach(review -> {
                    String messageText = "Скоро ревью у @" + review.getStudent().getLogin() +
                            " с @" + review.getMentor().getLogin() + "\n" +
                            review.getBookedDate() + " " + review.getBookedTime() +
                            "\nТема: " + review.getTopic();
                    notificatorFeign.sendMessage(review.getStudent().getLogin(), messageText, Communication.TELEGRAM);
                    notificatorFeign.sendMessage(review.getMentor().getLogin(), messageText, Communication.TELEGRAM);
                });
    }


}

