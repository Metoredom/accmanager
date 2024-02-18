package com.accmanagement.accmanager.exceptions;

public class CurrencyMismatchingException extends Exception{

    public CurrencyMismatchingException(){
        super("Currency does not match with the receiver's account");
    }

}
