package com.ironhack.ironbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IronBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(IronBankApplication.class, args);
    }

}
