package com.ironhack.ironbank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminDtoRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String roles = "ROLE_USER,ROLE_ADMIN";

}
