package com.ironhack.ironbank.controller;

import com.ironhack.ironbank.dto.ThirdPartyOpResponse;
import com.ironhack.ironbank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/third-party")
public class ThirdPartyController {

    private final AccountService accountService;

    @PutMapping("/{id}/charge")
    public ThirdPartyOpResponse thirdPartyCharge(@RequestHeader String hashedKey,
                                                 @PathVariable("id") Long accountId,
                                                 @RequestParam String secretKey,
                                                 @RequestParam BigDecimal amount,
                                                 @RequestParam String concept) {
        return accountService.thirdPartyCharge(hashedKey, accountId, secretKey, amount, concept);
    }

    @PutMapping("/{id}/deposit")
    public ThirdPartyOpResponse thirdPartyDeposit(@RequestHeader String hashedKey,
                                        @PathVariable("id") Long accountId,
                                        @RequestParam BigDecimal amount,
                                        @RequestParam String concept) {
        return accountService.thirdPartyDeposit(hashedKey, accountId, amount, concept);
    }
}
