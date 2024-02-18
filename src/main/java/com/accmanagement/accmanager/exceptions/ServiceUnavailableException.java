package com.accmanagement.accmanager.exceptions;

public class ServiceUnavailableException extends Exception{

    public ServiceUnavailableException(){
        super("Service not available");
    }

}
