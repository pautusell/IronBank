package com.ironhack.ironbank.dto;

import com.ironhack.ironbank.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDtoResponse {
    private Long id;
    private String name;
    private String username;
    private String roles;

    public static UserDtoResponse fromUser(User user){
        var userDto = new UserDtoResponse();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setUsername(user.getUsername());
        userDto.setRoles(user.getRoles());
        return userDto;
    }
}
