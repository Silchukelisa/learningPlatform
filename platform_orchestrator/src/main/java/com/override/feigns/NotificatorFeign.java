package com.override.feigns;


import dtos.MessageDTO;
import dtos.RecipientDTO;
import enums.Communications;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notification")
public interface NotificatorFeign {

    @GetMapping("/calls/balance")
    Double getBalance();

    @PostMapping("/teleMessages")
    void sendTelegramMessages(@RequestBody MessageDTO message);

    @PostMapping("/saveRecipients")
    void saveRecipients(@RequestBody RecipientDTO recipientDTO);

    @PostMapping("/communications")
    ResponseEntity<String> sendMessage(@RequestParam("user") String user, @RequestParam("message") String message, @RequestParam("type") Communications... type);
}
