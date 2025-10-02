package io.xcodebn.authservice.service;


import io.jsonwebtoken.JwtException;
import io.xcodebn.authservice.dto.LoginRequestDto;
import io.xcodebn.authservice.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Optional<String> authenticate(LoginRequestDto request){
        Optional<String> token = userService.findByEmail(request.getEmail())
                .filter(u -> passwordEncoder.matches(request.getPassword(),u.getPassword()))
                .map(u-> jwtUtil.generateToken(u.getEmail(),u.getRole()));
        return token;
    }

    public boolean validateToken(String token) {
        try{
            jwtUtil.validateToken(token);
            log.info("Token validated");
            return true;
        }
        catch (JwtException e){
            log.error("Failed to validate jwt {}",e.getMessage());
            return false;
        }
    }
}
