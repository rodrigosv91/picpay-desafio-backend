package br.com.rdsv.picpaydesafiobackend.exceptions;

public class InvalidTransactionException extends RuntimeException{
    public InvalidTransactionException(String message){
        super(message);
    }
}
