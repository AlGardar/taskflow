package org.gardar.taskflow.controller;

import jakarta.validation.Valid;
import org.gardar.taskflow.dto.LoginRequest;
import org.gardar.taskflow.dto.TokenResponse;
import org.gardar.taskflow.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        String token = authService.authenticate(request.username(), request.password());
        return new TokenResponse(token);
    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody LoginRequest request) {
        authService.register(request);
    }
}
