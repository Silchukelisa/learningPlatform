package com.override.controller.rest;

import com.override.model.AuthRequest;
import com.override.service.AuthService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService authService;

    @PostMapping("/login")
    @ApiOperation(value = "Если логина нет в базе, то UsernameNotFoundException(\"Пользователь с логином \" + " +
            "login + \" не найден!\", если логин есть, но не верный пароль, то AuthException" +
            "(\"Данные пользователя неверны\"), если логин и пароль верны, то возвращается токен пользователя.")
    public String auth(@RequestBody AuthRequest request) {
        return authService.login(request.getLogin(), request.getPassword());
    }
}