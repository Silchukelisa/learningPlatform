package com.override.controller.rest;

import com.override.model.Payment;
import com.override.service.CustomStudentDetailService;
import com.override.service.PaymentService;
import dto.PaymentDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentRestController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    @ApiOperation(value = "Сохраняет или обновляет данные Payment в paymentRepository, возвращает ResponseEntity<>(body:\"Счет на оплату добавлен!\", HttpStatus.OK)")
    public ResponseEntity<String> saveOrUpdatePayment(@RequestBody PaymentDTO paymentDTO,
                                                      @AuthenticationPrincipal CustomStudentDetailService.CustomStudentDetails user) {
        paymentService.save(paymentDTO, user.getUsername());
        return new ResponseEntity<>("Счет на оплату добавлен!", HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation(value = "Возвращает список всех платежей List<Payment>")
    private List<Payment> getAllPayments() {
        return paymentService.getAll();
    }
}