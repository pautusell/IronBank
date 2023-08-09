package com.ironhack.ironbank.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.ironbank.dto.AddressDtoRequest;
import com.ironhack.ironbank.model.AccountHolder;
import com.ironhack.ironbank.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@Data
public class Address {
    private String streetName;
    private Integer postalCode;
    private String city;
    private String province;

    public static Address fromAddressRequest(AddressDtoRequest addressDtoRequest){
        Address address = new Address();
        address.setStreetName(addressDtoRequest.getStreetName());
        address.setPostalCode(addressDtoRequest.getPostalCode());
        address.setCity(addressDtoRequest.getCity());
        address.setProvince(addressDtoRequest.getProvince());
        return address;
    }

}
