package com.example.goldenpathgateway;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "Transactions")
public class PactConsumerTest {
    private final static long ACCOUNT_ID = 123;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TransactionDto transactionDto = new TransactionDto(
            "456",
            "2022-02-02",
            50.00,
            "Amazon",
            "XP Explained Book",
            "123"
    );

    @Pact(consumer = "Transactions")
    public RequestResponsePact transactionsWithSpecificAccountId(PactDslWithProvider builder) {
        return builder
                .given("accountId")
                .uponReceiving("A request to your gateway with the specific accountId")
                .path("/accounts/" + ACCOUNT_ID + "/transactions")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(LambdaDsl.newJsonArray((o) -> o.object((transaction) -> {
                    transaction.stringType("accountId", this.transactionDto.getAccountId());
                    transaction.numberType("amount", this.transactionDto.getAmount());
                    transaction.stringType("date", this.transactionDto.getDate());
                    transaction.stringType("merchantName", this.transactionDto.getMerchantName());
                    transaction.stringType("summary", this.transactionDto.getSummary());
                    transaction.stringType("transactionId", this.transactionDto.getTransactionId());          })).build())
                .toPact();
    }

    @Pact(consumer = "Transactions")
    public RequestResponsePact transactionsWithSpecificAccountIdAndFromDate(PactDslWithProvider builder) {

        return builder
                .given("accountId and fromDate")
                .uponReceiving("A request to your gateway with specific accountId and fromDate")
                //@TODO: Clean this up
                .path("/accounts/" + ACCOUNT_ID + "/transactions")
                .method("GET")
                .matchQuery("fromDate", "\\d{4}-\\d{2}-\\d{2}")
                .willRespondWith()
                .status(200)
                .body(LambdaDsl.newJsonArray((o) -> o.object((transaction) -> {
                    transaction.stringType("accountId", this.transactionDto.getAccountId());
                    transaction.numberType("amount", this.transactionDto.getAmount());
                    transaction.stringType("date", this.transactionDto.getDate());
                    transaction.stringType("merchantName", this.transactionDto.getMerchantName());
                    transaction.stringType("summary", this.transactionDto.getSummary());
                    transaction.stringType("transactionId", this.transactionDto.getTransactionId());
                })).build())
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "transactionsWithSpecificAccountId", port = "9999")
    public void withAccountId_getAccountTransactions_returnsExpectedTransaction(MockServer mockServer) throws JsonProcessingException {
        // Arrange

        final RestTemplate restTemplate = new RestTemplate();
        final String url = mockServer.getUrl().concat("/accounts/" + ACCOUNT_ID + "/transactions");

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        List<TransactionDto> actual = objectMapper.readValue(response.getBody(), new TypeReference<>() {
        });

        System.out.println("actual");
        System.out.println(actual);
        // Assert
        assertEquals(1, actual.size());
        assertEquals(200, response.getStatusCodeValue());
        assertThat(actual.get(0)).usingRecursiveComparison().isEqualTo(transactionDto);
    }

    @Test
    @PactTestFor(pactMethod = "transactionsWithSpecificAccountIdAndFromDate", port = "9999")
    public void withAccountIdAndFromDate_getAccountTransactions_returnsExpectedTransaction(MockServer mockServer) throws JsonProcessingException {
        // Arrange
        final RestTemplate restTemplate = new RestTemplate();
        final String url = mockServer.getUrl().concat("/accounts/" + ACCOUNT_ID + "/transactions?fromDate=" + "2022-02-01");

        // Act
        final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        final List<TransactionDto> actual = ((List<TransactionDto>) objectMapper.readValue(response.getBody(), new TypeReference<>() {
        }));
        System.out.println("actual");
        System.out.println(actual);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertThat(actual.get(0)).usingRecursiveComparison().isEqualTo(transactionDto);
    }
}
