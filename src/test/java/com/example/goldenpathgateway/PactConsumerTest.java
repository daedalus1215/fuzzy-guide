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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "Transactions")
public class PactConsumerTest {
    private final static long ACCOUNT_ID = 123;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Transaction transaction = new Transaction(
            321,
            LocalDate.of(2022, 2, 2).toEpochDay(),
            12.00,
            "merchantName",
            "transactionSummary",
            new Account(ACCOUNT_ID,
                    "memberName",
                    null));

    @Pact(consumer = "Transactions")
    public RequestResponsePact transactionsWithSpecificAccountId(PactDslWithProvider builder) {
        return builder
                .uponReceiving("A request to your gateway with specific accountId")
                .path("/accounts/" + ACCOUNT_ID + "/transactions")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(LambdaDsl.newJsonArray((o) -> o.object((transaction) -> {
                    transaction.numberType("transactionId", this.transaction.getTransactionId());
                    transaction.numberType("date", this.transaction.getDate());
                    transaction.numberType("amount", this.transaction.getAmount());
                    transaction.stringType("merchantName", this.transaction.getMerchantName());
                    transaction.stringType("summary", this.transaction.getSummary());
                    transaction.object("account", (account) -> {
                        account.numberType("accountId", ACCOUNT_ID);
                        account.stringType("memberName", this.transaction.getAccount().getMemberName());
                    });
                })).build())
                .toPact();
    }

    @Pact(consumer = "Transactions")
    public RequestResponsePact transactionsWithSpecificAccountIdAndFromDate(PactDslWithProvider builder) {

        return builder
                .uponReceiving("A request to your gateway with specific accountId and fromDate")
                //@TODO: Clean this up
                .path("/accounts/" + ACCOUNT_ID + "/transactions")
                .method("GET")
                .matchQuery("fromDate", "\\d{4}-\\d{2}-\\d{2}")
                .willRespondWith()
                .status(200)
                .body(LambdaDsl.newJsonArray((o) -> o.object((transaction) -> {
                    transaction.numberType("transactionId", this.transaction.getTransactionId());
                    transaction.numberType("date", this.transaction.getDate());
                    transaction.numberType("amount", this.transaction.getAmount());
                    transaction.stringType("merchantName", this.transaction.getMerchantName());
                    transaction.stringType("summary", this.transaction.getSummary());
                    transaction.object("account", (account) -> {
                        account.numberType("accountId", ACCOUNT_ID);
                        account.stringType("memberName", this.transaction.getAccount().getMemberName());
                    });
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
        List<Transaction> actual = objectMapper.readValue(response.getBody(), new TypeReference<>() {
        });

        // Assert
        assertEquals(1, actual.size());
        assertEquals(200, response.getStatusCodeValue());
        assertThat(actual.get(0)).usingRecursiveComparison().isEqualTo(transaction);
    }

    @Test
    @PactTestFor(pactMethod = "transactionsWithSpecificAccountIdAndFromDate", port = "9999")
    public void withAccountIdAndFromDate_getAccountTransactions_returnsExpectedTransaction(MockServer mockServer) throws JsonProcessingException {
        // Arrange
        final RestTemplate restTemplate = new RestTemplate();
        final String url = mockServer.getUrl().concat("/accounts/" + ACCOUNT_ID + "/transactions?fromDate="+"2022-02-01");

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        List<Transaction> actual = objectMapper.readValue(response.getBody(), new TypeReference<>() {
        });

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertThat(actual.get(0)).usingRecursiveComparison().isEqualTo(transaction);
    }
}
