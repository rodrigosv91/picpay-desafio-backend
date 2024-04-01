package br.com.rdsv.picpaydesafiobackend.notification;

import br.com.rdsv.picpaydesafiobackend.transaction.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.client.RestClient;

public class NotificationConsumer {
    private RestClient restClient;

    public NotificationConsumer(RestClient.Builder builder){
        this.restClient = builder
                .baseUrl("https://mocki.io/v1/b9f43cb9-ac08-433c-b47a-7f84a539e1b5")
                .build();
    }

    @KafkaListener(topics = "transaction-notification",groupId = "picpay-desafio-backend")
    public void receiveNotification(Transaction transaction){
        var response = restClient.get()
                .retrieve()
                .toEntity(Notification.class);

        if(response.getStatusCode().isError() || !response.getBody().message())
            throw new NotificationException("Erro sending notification!");
    }
}
