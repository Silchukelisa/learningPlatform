package com.override.service;

import com.override.model.DogPictures;
import com.override.repository.DogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

@Service
public class DogPicturesService {

    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private PlatformUserService platformUserService;

    List<SoftReference<DogPictures>> dogPicturesSoft = new ArrayList<>();

    public void uploadFile(MultipartFile file, String login) throws IOException {

        DogPictures dogPictures = new DogPictures();
        dogPictures.setContent(file.getBytes());
        dogPictures.setType(file.getContentType());
        dogPictures.setName(file.getOriginalFilename());
        dogPictures.setUser(platformUserService.findPlatformUserByLogin(login));
        dogRepository.save(dogPictures);
        dogPicturesSoft.add(new SoftReference<>(dogPictures));

    }

    public List<SoftReference<DogPictures>> getAllByUserLogin(String login) {
        /*List<DogPictures> dogPictures = dogRepository.findAllByUserId(
                platformUserService.findPlatformUserByLogin(login).getId());
        List<SoftReference<DogPictures>> dogPicturesSoft = new ArrayList<>();
        for (DogPictures dogPicture : dogPictures) {
            dogPicturesSoft.add(new SoftReference<>(dogPicture));
        }*/
        /*for (DogPictures dogPicture : dogPictures) {
            dogPicture= null;
            System.out.println(dogPicture);
        }*/
        return dogPicturesSoft;
    }
}
