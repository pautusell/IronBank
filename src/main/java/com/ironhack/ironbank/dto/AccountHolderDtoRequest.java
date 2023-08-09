package com.ironhack.ironbank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class AccountHolderDtoRequest {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank @Pattern(regexp = "\\d{8}+[A-Z]")
    private String username;
    @NotBlank
    private String password;
    @Value("ROLE_USER")
    private String roles;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dob;
    @NotBlank
    @Email
    private String email;

    public AccountHolderDtoRequest(String name, String username, String password, LocalDate dob, String email) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.dob = dob;
        this.email = email;
    }
}
