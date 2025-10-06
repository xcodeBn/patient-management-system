package io.xcodebn.authservice.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.xcodebn.authservice.dto.LoginRequestDto;
import io.xcodebn.authservice.dto.LoginResponseDto;
import io.xcodebn.authservice.service.AuthService;
import io.xcodebn.common.spring.exception.BusinessException;
import io.xcodebn.common.response.ApiResponse;
import io.xcodebn.common.response.ErrorCode;
import io.xcodebn.common.util.ResponseBuilder;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

@RestController()
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Generate token on user login")
    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        Optional<String> tokenOptional = authService.authenticate(loginRequestDto);
        if (tokenOptional.isEmpty()) {
            throw new BusinessException(
                ErrorCode.INVALID_CREDENTIALS,
                "Invalid username or password"
            );
        }
        LoginResponseDto loginResponseDto = new LoginResponseDto(tokenOptional.get());
        return ResponseBuilder.success(loginResponseDto);
    }

    @Operation(summary = "Validate token")
    @GetMapping("/validate")
    public ApiResponse<Void> validateToken(@RequestHeader("Authorization") String authHeader){

        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            throw new BusinessException(
                ErrorCode.UNAUTHORIZED,
                "Authorization header missing or invalid"
            );
        }

        if (!authService.validateToken(authHeader.substring(7))) {
            throw new BusinessException(
                ErrorCode.TOKEN_EXPIRED,
                "Token is invalid or expired"
            );
        }

        return ResponseBuilder.success(null);

    }

}
