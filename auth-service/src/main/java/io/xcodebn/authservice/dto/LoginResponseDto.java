package io.xcodebn.authservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class LoginResponseDto {
    private final String token;
}
