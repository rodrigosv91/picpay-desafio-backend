package br.com.rdsv.picpaydesafiobackend.notification;

import br.com.rdsv.picpaydesafiobackend.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotificationConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConsumer.class);

    private RestClient restClient;

    public NotificationConsumer(RestClient.Builder builder){
        this.restClient = builder
                .baseUrl("https://mocki.io/v1/b9f43cb9-ac08-433c-b47a-7f84a539e1b5")
                .build();
    }

    @KafkaListener(topics = "transaction-notification", groupId = "picpay-desafio-backend")
    public void receiveNotification(Transaction transaction){
        LOGGER.info("Nontifying Transaction: {}...", transaction);

        var response = restClient.get()
                .retrieve()
                .toEntity(Notification.class);

        if(response.getStatusCode().isError() || !response.getBody().message())
            throw new NotificationException("Error sending notification!");

        LOGGER.info("Notification has been sent: {}...", response.getBody());
    }
}
