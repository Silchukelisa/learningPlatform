package com.override.controller.rest;

import com.override.model.JoinRequest;
import com.override.service.JoinRequestService;
import dto.JoinRequestStatusDTO;
import dto.RegisterUserRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Secured("ROLE_ADMIN")
public class JoinRequestRestController {

    @Autowired
    private JoinRequestService requestService;

    @PostMapping("/join/request")
    public JoinRequestStatusDTO saveJoinRequest(@RequestBody RegisterUserRequestDTO requestDTO) {
        return requestService.saveRequest(requestDTO);
    }

    @GetMapping("/join/request")
    public List<JoinRequest> getAllJoinRequests() {
        return requestService.getAllRequests();
    }

    @PostMapping("/join/request/accept/{id}")
    public void acceptJoinRequest(@PathVariable Long id) {
        requestService.responseForJoinRequest(true, id);
    }

    @PostMapping("/join/request/decline/{id}")
    public void declineJoinRequest(@PathVariable Long id) {
        requestService.responseForJoinRequest(false, id);
    }
}
