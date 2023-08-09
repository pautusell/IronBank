package com.ironhack.ironbank.model;

import com.ironhack.ironbank.enums.UserType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
@DiscriminatorValue("admin")
public class Admin extends User{

    public Admin(){
        super.setUserType(UserType.ADMIN);
    }
    public Admin(String name, String username, String password, String roles) {
        super(name, username, password, roles);
        super.setUserType(UserType.ADMIN);
    }

}


