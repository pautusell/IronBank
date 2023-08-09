package com.ironhack.ironbank.service;

import com.ironhack.ironbank.dto.*;
import com.ironhack.ironbank.enums.UserType;
import com.ironhack.ironbank.exception.OperationalException;
import com.ironhack.ironbank.exception.UserNotFoundException;
import com.ironhack.ironbank.model.Account;
import com.ironhack.ironbank.model.AccountHolder;
import com.ironhack.ironbank.model.Admin;
import com.ironhack.ironbank.model.User;
import com.ironhack.ironbank.repository.AccountRepository;
import com.ironhack.ironbank.repository.UserRepository;
import com.ironhack.ironbank.utils.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;


    public User findByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public List<UserDtoResponse> findAllUsers(){
        var userList = userRepository.findAll();
        List<UserDtoResponse> userDtoList = new ArrayList<>();
        for (User u : userList) {
            userDtoList.add(UserDtoResponse.fromUser(u));
        }
        return userDtoList;
    }

    public List<AccountHolderDtoResponse> findAllAccountHolders(){
        var userList = userRepository.findAllByUserType(UserType.ACCOUNT_HOLDER);
        List<AccountHolderDtoResponse> accHolderDtoList = new ArrayList<>();
        for (User u : userList) {
            accHolderDtoList.add(AccountHolderDtoResponse.fromAccountHolder((AccountHolder) u));
        }
        return accHolderDtoList;
    }

    public AccountHolderDtoResponse newAccountHolder(AccountHolderDtoRequest accountHolderDto){
        var accountHolder = new AccountHolder();
        accountHolder.setName(accountHolderDto.getName());
        accountHolder.setUsername(accountHolderDto.getUsername());
        accountHolder.setPassword(passwordEncoder.encode(accountHolderDto.getPassword()));
        accountHolder.setDob(accountHolderDto.getDob());
        accountHolder.setEmail(accountHolderDto.getEmail());
        accountHolder.setRoles(accountHolderDto.getRoles());
        return AccountHolderDtoResponse.fromAccountHolder(userRepository.save(accountHolder));
    }

    public AdminDtoResponse newAdmin(AdminDtoRequest adminDto){
        var admin = new Admin();
        admin.setName(adminDto.getName());
        admin.setUsername(adminDto.getUsername());
        admin.setPassword(passwordEncoder.encode(adminDto.getPassword()));
        admin.setRoles(adminDto.getRoles());
        return AdminDtoResponse.fromAdmin(userRepository.save(admin));
    }

    public ResetPasswordResponse resetPassword(String username) {
        var userFound = userRepository.findByUsername(username);
        var response = new ResetPasswordResponse();
        if (userFound.isPresent()) {
            // --> [NOT IMPLEMENTED]: Activate the service to send and email to the user.
            response.setMessage("An email has been sent to your associated email account with the instructions to update your password");
        } else response.setMessage("Username not found in DB");
        return response;
    }

    public AccountHolderDtoResponse updateAH(String username, AccountHolderDtoRequest accountHolderDtoRequest) {
        var accountHolderFound = (AccountHolder) findByUsername(username);
        accountHolderFound.setName(accountHolderDtoRequest.getName());
        accountHolderFound.setUsername(accountHolderDtoRequest.getUsername());
        accountHolderFound.setPassword(passwordEncoder.encode(accountHolderDtoRequest.getPassword()));
        accountHolderFound.setRoles("ROLE_USER");
        accountHolderFound.setDob(accountHolderDtoRequest.getDob());
        accountHolderFound.setEmail(accountHolderDtoRequest.getEmail());
        return AccountHolderDtoResponse.fromAccountHolder(userRepository.save(accountHolderFound));
    }

    public String deleteUser(String username) {
        var userToDelete = findByUsername(username);

        if (getAccountsByUserId(userToDelete.getId()).size() > 0) {
            throw new OperationalException("The User " + username + " still has associated Accounts. Can't be deleted");
        } else {
            userRepository.delete(userToDelete);
        }
        return "User " + username + " deleted successfully";
    }

    public List<AccountDto> getAccountsByUserLogged() {

        var accountsDtoList = new ArrayList<AccountDto>();
        for(Account account : accountRepository.findByPrimaryOwner_Id(getCurrentUser().getId())){
            accountsDtoList.add(AccountDto.fromAccount(account));
        }
        for(Account account : accountRepository.findBySecondaryOwner_Id(getCurrentUser().getId())){
            accountsDtoList.add(AccountDto.fromAccount(account));
        }
        return accountsDtoList;
    }

    public List<AccountDto> getAccountsByUserId(Long id) {

        var accountsDtoList = new ArrayList<AccountDto>();
        for(Account account : accountRepository.findByPrimaryOwner_Id(id)){
            accountsDtoList.add(AccountDto.fromAccount(account));
        }
        for(Account account : accountRepository.findBySecondaryOwner_Id(id)){
            accountsDtoList.add(AccountDto.fromAccount(account));
        }
        return accountsDtoList;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return findByUsername(authentication.getName());
    }

    public AccountHolderAddressDtoResponse updateAccHolderMainAddress(String username, AddressDtoRequest addressDtoRequest) {
        var accountHolderToUpdate = (AccountHolder) findByUsername(username);
        accountHolderToUpdate.setAddress(Address.fromAddressRequest(addressDtoRequest));
        return AccountHolderAddressDtoResponse.fromAccountHolder(userRepository.save(accountHolderToUpdate));
    }
    public AccountHolderAddressDtoResponse updateAccHolderMailingAddress(String username, AddressDtoRequest addressDtoRequest) {
        var accountHolderToUpdate = (AccountHolder) findByUsername(username);
        accountHolderToUpdate.setMailingAddress(Address.fromAddressRequest(addressDtoRequest));
        return AccountHolderAddressDtoResponse.fromAccountHolder(userRepository.save(accountHolderToUpdate));
    }
}
