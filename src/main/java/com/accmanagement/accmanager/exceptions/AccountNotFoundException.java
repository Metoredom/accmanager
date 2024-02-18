package com.accmanagement.accmanager.exceptions;

public class AccountNotFoundException extends Exception{

    public AccountNotFoundException(){
        super("Account not found");
    }

}
