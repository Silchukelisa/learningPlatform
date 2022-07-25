package com.override.service;

import com.override.mapper.CodeTryStatMapper;
import com.override.mapper.InterviewReportMapper;
import com.override.model.InterviewReport;
import com.override.model.Payment;
import com.override.model.enums.Status;
import com.override.repository.CodeTryRepository;
import com.override.repository.InterviewReportRepository;
import com.override.repository.PaymentRepository;
import dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MONTHS;

@Service
public class StatisticsService {
    @Autowired
    private CodeTryRepository codeTryRepository;
    @Autowired
    private CodeTryStatMapper codeTryStatMapper;
    @Autowired
    private InterviewReportRepository interviewReportRepository;
    @Autowired
    private InterviewReportMapper interviewReportMapper;
    @Autowired
    private PaymentRepository paymentRepository;

    public CodeTryStatDTO getCodeTryStatistics(int size) {
        return codeTryStatMapper.entityToDto(codeTryRepository.countStatsOfHardTasks(size),
                codeTryRepository.countStatsOfUsers(), codeTryRepository.countStatsOfStatus(),
                codeTryRepository.countCodeTryByChapterAndStep());
    }

    public Map<String, Long> countStatsOfHardTasks(int size) {
        return codeTryStatMapper.listToMapHardTask(
                codeTryRepository.countStatsOfHardTasks(size));
    }

    public SalaryDTO getSalaryStatistics() {
        LocalDate firstSalaryDate = interviewReportRepository.findAll().stream().min(Comparator.comparing(InterviewReport::getDate)).get().getDate();

        List<LocalDate> labels = Stream.iterate(firstSalaryDate, date -> date.plus(1, MONTHS))
                .limit(MONTHS.between(firstSalaryDate, LocalDate.now()) + 1)
                .collect(Collectors.toList());

        List<SalaryStatDTO> userSalaries = new ArrayList<>();

        List<String> allUserNames = interviewReportRepository.findAllByStatus(Status.ACCEPTED)
                .stream()
                .map(InterviewReport::getUserLogin)
                .distinct()
                .collect(Collectors.toList());

        for (String userLogin : allUserNames) {
            List<Integer> salaries = new ArrayList<>();
            List<InterviewReport> allStudentSalaries = interviewReportRepository.findAllByUserLoginAndStatus(userLogin, Status.ACCEPTED);

            for (LocalDate label : labels) {
                int salaryIfNotMatched = salaries.size() > 0 ? salaries.get(salaries.size() - 1) : 0;
                Integer salaryForLabel = allStudentSalaries.stream()
                        .filter(salary -> salary.getDate().getYear() == label.getYear() &&
                                salary.getDate().getMonthValue() == label.getMonthValue())
                        .map(InterviewReport::getMaxSalary)
                        .findFirst()
                        .orElse(salaryIfNotMatched);

                salaries.add(salaryForLabel);
            }
            userSalaries.add(interviewReportMapper.LoginAndSalariesToDto(userLogin, salaries));
        }
        return interviewReportMapper.salaryStatDtoToSalaryDto(labels, userSalaries);
    }

    public IncomeFromUsersDTO getAllPayment() {
        List<Payment> allPayment = paymentRepository.findAll();
        List<String> studentName = new ArrayList<>();
        List<Long> sum = new ArrayList<>();
        for (Payment payment : allPayment) {
            if(!studentName.contains(payment.getStudentName())) {
                studentName.add(payment.getStudentName());
                sum.add(payment.getSum());
            } else {
                sum.set(studentName.indexOf(payment.getStudentName()),
                        sum.get(studentName.indexOf(payment.getStudentName())) + payment.getSum());
            }
        }
        return IncomeFromUsersDTO.builder()
                .studentName(studentName)
                .income(sum)
                .build();
    }

    public GeneralIncomeDTO getGeneralPayment() {
        LocalDate firstSalaryDate = paymentRepository.findAll().stream().min(Comparator.comparing(Payment::getDate)).get().getDate();

        List<LocalDate> labels = Stream.iterate(firstSalaryDate, date -> date.plus(1, MONTHS))
                .limit(MONTHS.between(firstSalaryDate, LocalDate.now()) + 1)
                .collect(Collectors.toList());

        List<Payment> payments = paymentRepository.findAll();
        List<Long> income = new ArrayList<>();

        for (LocalDate label : labels) {
            income.add(0L);
            for (Payment payment : payments) {
                if(label.getMonthValue() == payment.getDate().getMonthValue()) {
                    income.set(labels.indexOf(label), income.get(labels.indexOf(label)) + payment.getSum());
                }
            }
        }

        return GeneralIncomeDTO.builder()
                .dataMonth(labels)
                .income(income)
                .build();
    }
}
