package br.com.rdsv.picpaydesafiobackend.authorization;

import br.com.rdsv.picpaydesafiobackend.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AuthorizerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizerService.class);

    private RestClient restClient;

    public AuthorizerService(RestClient.Builder builder){
        this.restClient = builder
                .baseUrl("https://mocki.io/v1/175ac44e-797e-4466-a801-2f20dd95fff1")
                .build();
    }

    public void authorize(Transaction transaction){
        LOGGER.info("Authorizing Transaction: {}", transaction);

        var response = restClient.get()
                .retrieve()
                .toEntity(Authorization.class);

        if(response.getStatusCode().isError() || !response.getBody().isAuthorized())
            throw new UnauthorizedTransactionException("Transaction not Authorized!");

        LOGGER.info("Transaction Authorized: {}", transaction);
    }
}
