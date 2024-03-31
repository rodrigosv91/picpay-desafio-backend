package br.com.rdsv.picpaydesafiobackend.authorization;

public class UnauthorizedTransactionException extends RuntimeException{
    public UnauthorizedTransactionException(String message){
        super(message);
    }
}
