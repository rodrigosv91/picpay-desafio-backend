package br.com.rdsv.picpaydesafiobackend.notification;

public class NotificationException extends RuntimeException {
    NotificationException(String message){
        super(message);
    }
}
