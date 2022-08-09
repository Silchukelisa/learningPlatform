package com.override.controller.rest;

import com.override.model.PersonalData;
import com.override.service.PersonalDataService;
import com.override.service.RequestInNotificationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/personalData")
public class PersonalDataRestController {

    @Autowired
    private PersonalDataService personalDataService;

    @Autowired
    private RequestInNotificationService requestInNotificationService;

    @PatchMapping("{userLogin}")
    @ApiOperation(value = "Сохраняет персональные данные в personalDataRepository, а так же сохраняет в данные в RecipientRepository в Нотификатор")
    public void patch(@RequestBody PersonalData personalData,
                      @PathVariable String userLogin) {
        personalDataService.save(personalData, userLogin);
        requestInNotificationService.saveRecipient(personalData, userLogin);
    }
}
