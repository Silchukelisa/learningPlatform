package com.override.controller;

import com.override.annotation.MaxFileSize;
import com.override.model.DogPictures;
import com.override.service.CustomStudentDetailService;
import com.override.service.DogPicturesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.List;

@Controller
@RequestMapping("/dogPictures")
public class DogPicturesController {
    @Autowired
    private DogPicturesService dogPicturesService;

    @PostMapping
    @MaxFileSize("${documentSizeLimit.forPersonalData}")
    public String personalDataDocumentUpload(@AuthenticationPrincipal CustomStudentDetailService.CustomStudentDetails user,
                                             @RequestParam("file") MultipartFile multipartFile) throws IOException {
        dogPicturesService.uploadFile(multipartFile, user.getUsername());
        return "redirect:/dogPictures";
    }


    @GetMapping("/currentUser")
    @ResponseBody
    public List<SoftReference<DogPictures>> getAllFilesInfoForCurrentUser(@AuthenticationPrincipal CustomStudentDetailService.CustomStudentDetails user) {
        return dogPicturesService.getAllByUserLogin(user.getUsername());
    }
}
