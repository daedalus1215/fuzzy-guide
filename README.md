# fuzzy-guide

## What is this branch?
This branch has become a mono repo in order to support the Pact contract tests. Contract tests start with consumers of APIs.
Once the consumer contract test is written (in the gateway), we run it to generate a `TransactionConsumer-Transactions.json` contract file.
We carry this file over to our provider (Rest Api) and write a provider test to ensure the provider honors the contract.

## How to run the two projects
We can open `golden-path-restful-api` and `golden-path-gateway` in two separate IDEs. Run `golden-path-restful-api` and then run `golden-path-gateway`.
We then can use postman to hit `golden-path-gateway`, like so: http://127.0.0.1:8181/accounts/123/transactions. Gateway will forward the request to `golden-path-restful-api` and return the response.

## How to run the contract tests
First we go into `golden-path-gateway` to run `TransactionRouteTest` test suite. When it runs it will generate a file in
`build/pacts/TransactionConsumer-Transactions.json`. This file can be carried over to `golden-path-restful-api`. Specifically in the
`test/resources/pacts` directory. Once the .json is in the correct location we can run the test suite in `golden-path-restful-api`, located at:
`test/java/pacts/PactProviderTest`. This test suite will use the `TransactionConsumer-Transactions.json` file to run against and make sure
it honors the contract. 

