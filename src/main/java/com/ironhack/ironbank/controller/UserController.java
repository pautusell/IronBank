package com.ironhack.ironbank.controller;

import com.ironhack.ironbank.dto.*;
import com.ironhack.ironbank.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/admin/list-users")
    public List<UserDtoResponse> getAllUsers(){
        return userService.findAllUsers();
    }
    @GetMapping("/admin/list-ah")
    public List<AccountHolderDtoResponse> getAllAccountHolders(){
        return userService.findAllAccountHolders();
    }

    @PostMapping("/ah/create")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountHolderDtoResponse newAccountHolder(@RequestBody @Valid AccountHolderDtoRequest accountHolderDto){
        return userService.newAccountHolder(accountHolderDto);
    }
    @PostMapping("/admin/create")
    @ResponseStatus(HttpStatus.CREATED)
    public AdminDtoResponse newAdmin(@RequestBody @Valid AdminDtoRequest adminDto){
        return userService.newAdmin(adminDto);
    }

    @PutMapping("/admin/update-ah/{username}")
    public AccountHolderDtoResponse updateAH(@PathVariable(name = "username") String username,
                                          @RequestBody @Valid AccountHolderDtoRequest accountHolderDtoRequest){
        return userService.updateAH(username, accountHolderDtoRequest);
    }

    @PutMapping("/ah/update-main-address/{username}")
    public AccountHolderAddressDtoResponse updateAccHolderMainAddress(@PathVariable(name = "username") @NotBlank @Pattern(regexp = "\\d{8}+[A-Z]") String username,
                                             @RequestBody @Valid AddressDtoRequest addressDtoRequest){
        return userService.updateAccHolderMainAddress(username, addressDtoRequest);
    }

    @PutMapping("/ah/update-mailing-address/{username}")
    public AccountHolderAddressDtoResponse updateAccHolderMailingAddress(@PathVariable(name = "username") @NotBlank @Pattern(regexp = "\\d{8}+[A-Z]") String username,
                                                                      @RequestBody @Valid AddressDtoRequest addressDtoRequest){
        return userService.updateAccHolderMailingAddress(username, addressDtoRequest);
    }

    @GetMapping("/reset-password")
    public ResetPasswordResponse resetPassword(@RequestParam String username){
        return userService.resetPassword(username);
    }

    @DeleteMapping("/admin/delete/{username}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String deleteUser(@PathVariable(name = "username") String username){
        return userService.deleteUser(username);
    }

}
