package com.ironhack.ironbank.model;

import com.ironhack.ironbank.enums.UserType;
import com.ironhack.ironbank.utils.Address;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@DiscriminatorValue("account_holder")
public class AccountHolder extends User{
    private LocalDate dob;
    private String email;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "streetName", column = @Column(name = "main_address_street")),
            @AttributeOverride( name = "postalCode", column = @Column(name = "main_address_postal_code")),
            @AttributeOverride( name = "city", column = @Column(name = "main_address_city")),
            @AttributeOverride( name = "province", column = @Column(name = "main_address_province")),
    })
    private Address address;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "streetName", column = @Column(name = "mailing_address_street")),
            @AttributeOverride( name = "postalCode", column = @Column(name = "mailing_address_postal_code")),
            @AttributeOverride( name = "city", column = @Column(name = "mailing_address_city")),
            @AttributeOverride( name = "province", column = @Column(name = "mailing_address_province")),
    })
    private Address mailingAddress;

    public AccountHolder(){
        super.setUserType(UserType.ACCOUNT_HOLDER);
    }

    public AccountHolder(String name, String username, String password, String roles, LocalDate dob, String email) {
        super(name, username, password, roles);
        this.dob = dob;
        this.email = email;
        super.setUserType(UserType.ACCOUNT_HOLDER);
    }

}
