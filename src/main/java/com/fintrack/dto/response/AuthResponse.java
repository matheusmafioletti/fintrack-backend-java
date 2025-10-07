package com.fintrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String type;
    private Long expiresIn;
    private UserResponse user;

    public AuthResponse(String token, Long expiresIn, UserResponse user) {
        this.token = token;
        this.type = "Bearer";
        this.expiresIn = expiresIn;
        this.user = user;
    }
}

