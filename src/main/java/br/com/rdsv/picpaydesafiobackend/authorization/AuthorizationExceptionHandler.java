package br.com.rdsv.picpaydesafiobackend.authorization;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AuthorizationExceptionHandler {
    @ExceptionHandler(UnauthorizedTransactionException.class)
    public ResponseEntity<Object> handle(UnauthorizedTransactionException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
