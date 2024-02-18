package com.accmanagement.accmanager.exceptions;

public class InsufficientBalanceException extends Exception{

    public InsufficientBalanceException(){
        super("Insufficient balance");
    }

}
