package com.override.controller;

import com.override.feigns.NotificatorFeign;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/notification")
public class NotificationController {
    NotificatorFeign notificatorFeign;

    @Autowired
    public NotificationController(NotificatorFeign notificatorFeign) {
        this.notificatorFeign = notificatorFeign;
    }

    @Value("${sms.url.replenish-balance}")
    private String urlToReplenishBalance;

    @GetMapping("/balance")
    public double getBalance() {
        return notificatorFeign.getBalance();
    }

    @GetMapping("/url/replenish")
    public String getUrlToReplenishBalance() {
        return urlToReplenishBalance;
    }
}
