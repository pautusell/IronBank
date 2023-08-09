package com.ironhack.ironbank.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressDtoRequest {

    @NotNull
    private String streetName;
    @NotNull
    private Integer postalCode;
    @NotNull
    private String city;
    @NotNull
    private String province;

}
