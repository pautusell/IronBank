package com.ironhack.ironbank.dto;

import com.ironhack.ironbank.model.Admin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDtoResponse {

    private String name;
    private String username;
    private String roles;

    public static AdminDtoResponse fromAdmin(Admin admin){
        var adminDto = new AdminDtoResponse();
        adminDto.setName(admin.getName());
        adminDto.setUsername(admin.getUsername());
        adminDto.setRoles(admin.getRoles());
        return adminDto;
    }




}
