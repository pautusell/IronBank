package com.ironhack.ironbank.model;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
public class ThirdParty {
    private String hashedKey;
}
