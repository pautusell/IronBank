package com.ironhack.ironbank.enums;

public enum AccountType {
    CHECKING("checking_account"),
    STUDENT_CHECKING("student_checking_account"),
    SAVINGS("savings_account"),
    CREDIT_CARD("credit_card_account");

    private final String dType;

    AccountType (String dType){
        this.dType = dType;
    }

    public String getDType() {
        return dType;
    }

    public static AccountType fromDType(String dTypeGiven) {
        for (AccountType accountType : AccountType.values()) {
            if (accountType.dType.equals(dTypeGiven)) {
                return accountType;
            }
        }
        return null;
    }

}
