package com.example.goldenpathgateway;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonArray;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "Transactions")
public class TransactionRouteTest {
    @Pact(consumer = "TransactionConsumer")
    public RequestResponsePact transactions(PactDslWithProvider builder) {
        return builder
                .given("transactions exist with accountId")
                .uponReceiving("accountId")
                .path("/accounts/555/transactions")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(newJsonArray((o) -> {
                    o.object((refs) -> {
                        refs.stringType("transactionId", "1");
                        refs.stringType("date", "2022-02-01");
                        refs.numberType("amount", 50.0);
                        refs.stringType("merchantName", "Amazon");
                        refs.stringType("summary", "XP Explained (Book)");
                        refs.stringType("accountId", "555");
                    });
                    o.object(refs -> {
                        refs.stringType("transactionId", "2");
                        refs.stringType("date", "2022-02-02");
                        refs.numberType("amount", 350.0);
                        refs.stringType("merchantName", "Walmart");
                        refs.stringType("summary", "Standing Desk");
                        refs.stringType("accountId", "555");
                    });
                })
                        .build())
                .toPact();
    }

    @Pact(consumer = "TransactionConsumer")
    public RequestResponsePact transactionsFromDate(PactDslWithProvider builder) {
        return builder
                .given("transactions exist with accountId and date equal or greater than fromDate")
                .uponReceiving("accountId and fromDate")
                .path("/accounts/555/transactions")
                .method("GET")
                .matchQuery("fromDate", "2022-02-02")
                .willRespondWith()
                .status(200)
                .body(newJsonArray((o) -> o.object((refs) -> {
                    refs.stringType("transactionId", "2");
                    refs.stringType("date", "2022-02-02");
                    refs.numberType("amount", 350.0);
                    refs.stringType("merchantName", "Walmart");
                    refs.stringType("summary", "Standing Desk");
                    refs.stringType("accountId", "555");
                }))
                        .build())
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "transactions", port = "9999")
    public void withAccountId_getAccountTransactions_returnsExpectedTransactions(MockServer mockServer) throws IOException, JSONException {
        // Arrange
        final RestTemplate restTemplate = new RestTemplate();
        final String expected = JSONTestUtils.readFile("expectedTransactionsResponse.json");

        // Act
        final ResponseEntity<String> actual = restTemplate.getForEntity(
                mockServer.getUrl().concat("/accounts/555/transactions"),
                String.class);

        // Assert
        assertEquals(200, actual.getStatusCodeValue());
        JSONAssert.assertEquals(expected, actual.getBody(), JSONCompareMode.LENIENT);
    }

    @Test
    @PactTestFor(pactMethod = "transactionsFromDate", port = "9999")
    public void withAccountIdAndFromDate_getAccountTransactions_returnsExpectedTransaction(MockServer mockServer) throws IOException, JSONException {
        // Arrange
        final RestTemplate restTemplate = new RestTemplate();
        final String expected = JSONTestUtils.readFile("expectedFilteredTransactionsResponse.json");

        // Act
        final ResponseEntity<String> actual = restTemplate.getForEntity(
                mockServer.getUrl().concat("/accounts/555/transactions?fromDate=2022-02-02"),
                String.class);

        // Assert
        assertEquals(200, actual.getStatusCodeValue());
        JSONAssert.assertEquals(expected, actual.getBody(), JSONCompareMode.LENIENT);
    }
}
