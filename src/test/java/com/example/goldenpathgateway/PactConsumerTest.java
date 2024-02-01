package com.example.goldenpathgateway;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "CoursesCatalogue")
public class PactConsumerTest {

    @Pact(provider = "YourProviderName", consumer = "YourConsumerName")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        return builder
                .uponReceiving("A request to your gateway")
                .path("/accounts/123/transactions")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(newJsonBody((o) -> {
                    // Define the expected response structure
                    o.stringValue("someKey", "someValue");
                }).build())
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createPact", port ="9999")
    public void testYourProvider() throws JsonProcessingException {
        // Arrange

        // Use RestTemplate to send a request to your Gateway
        final RestTemplate restTemplate = new RestTemplate();
        final String url = "http://localhost:8181/accounts/123/transactions";

         final ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Use Jackson to deserialize the response body into a list of transactions
        List<Transaction> transactions = objectMapper.readValue(response.getBody(), new TypeReference<List<Transaction>>() {});
        System.out.println("transactions");
        System.out.println(transactions);
        // Assert that the response matches the expected Pact
        Assertions.assertEquals(response.getStatusCodeValue(), 200);

        // You might want to assert the response body based on your expected interactions
        // Example: assertThat(e



        // Act

        // Assert

    }

}
