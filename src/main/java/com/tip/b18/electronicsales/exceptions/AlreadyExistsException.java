package com.tip.b18.electronicsales.exceptions;

public class AlreadyExistsException extends RuntimeException{
    public AlreadyExistsException(String msg){
        super(msg);
    }
}
