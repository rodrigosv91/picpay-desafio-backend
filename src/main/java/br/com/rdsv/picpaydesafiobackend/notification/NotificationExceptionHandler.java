package br.com.rdsv.picpaydesafiobackend.notification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class NotificationExceptionHandler {
    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<Object> handle(NotificationException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
