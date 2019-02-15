# revolut-money-transfers
Backend test for money transfers between accounts.

# Table of Contents
1. [About the Project](#about-the-project)
2. [How to Run](#how-to-run)
3. [Query Examples](#query-examples)

## About the Project
- This is an implementation a RESTful API for money transfers between accounts.
- Uses two kind of entities: Account and Transaction
- It creates the transaction and processes them by jobs in order to give a quickly response.
- In order to ensure that the entity is not modified until it is updated a lock with a write (exclusive) is used until the transaction is completed by select ... for update
- In order to simulate a numbers of executions in parallel, the JMeter tool was used. The configuration test plan can find over /util/MoneyTransfers.jmx

### Programming Languages and Frameworks used
- Java 8
- Sparkjava
- Google Guice
- Google Gson
- H2 database
- Logback
- Maven 3
- JUnit and Mockito

## How to Run
### Requirement

If you download the code, in order to run and compile this application you need to have installed:
- JRE 1.8
- Maven 3

Then run:

    mvn clean install

    java -jar target/revolut-money-transfers-1.0-SNAPSHOT-jar-with-dependencies.jar

Otherwise you have to be installed only JRE 1.8 and download directly the jar /util/revolut-money-transfers-1.0-SNAPSHOT-jar-with-dependencies.jar
And only execute:

    java -jar revolut-money-transfers-1.0-SNAPSHOT-jar-with-dependencies.jar

Note: The application by default runs over the port 8080. It can be configured from Configuration.java

## Query Examples

### Account
The account entity contains the owner of this account and a balance in the specified currency.

    {
        "id": <long>,
        "owner": <string>,
        "balance": <bigDecimal>,
        "currency": <string - one from "ARG", "EUR", "USD", "GBP">
    }
 
#### Create Account

The following request creates an account:

    POST /account
    {
        "owner": "Emiliano",
        "balance": 12.6,
        "currency": "ARG"
    }

The successfully response returns the account created with `ID` specified:

    HTTP 201
    {
        "id": 11,
        "owner": "Emiliano",
        "balance": 12.6,
        "currency": "ARG"
    }

And example of failure response contains:

    HTTP 422
    {
        "errorMessage": "owner, currency and balance must not be null or empty",
        "errorCode": "C002"
    }
    
#### Retrieve an Account

The following request retrieves an account by his `ID`:

    GET /account/{id}

The successfully response returns the account:

    HTTP 200
    {
        "id": 11,
        "owner": "Emiliano",
        "balance": 12.6,
        "currency": "ARG"
    }

And example of failure response contains:

    HTTP 404
    {
        "errorMessage": "Account not found",
        "errorCode": "C500"
    }  
    
#### Retrieve a list of Accounts

The following request retrieves a list of account:

    GET /account

The successfully response returns the list of accounts:

    HTTP 200
    [
        {
            "id": 11,
            "owner": "Emiliano",
            "balance": 12.6,
            "currency": "ARG"
        },
        {
            "id": 12,
            "owner": "Messi",
            "balance": 9999999999.50,
            "currency": "EUR"
        }
    ]

 
### Transaction
The transaction entity contains the origin and destination account and the amount in the specified currency as principal attributes.
The transaction is created but not processed immediately in order to give a quickly response to the client.
A transaction can be created when the origin account has more money than the specified in the transaction object.
The response of the transaction created returns PENDING as status, but the money is not transfering to the destination yet, until this will be processed.
When the transaction is processed, and if this is OK, the money is transferred to the destination and the status is CONFIRMED.
Otherwise it is going to try a specific number of retry and if it reach the limit, the money is returned to the origin account and the transaction is marked as REJECTED
    
     {
         "id": <long>,
         "fromAccountId": <long>,
         "amount": <bigDecimal>,
         "currency": <string - one from "ARG", "EUR", "USD", "GBP">,
         "toAccountId": <long>,
         "status": <string - one from "PENDING", "CONFIRMED", "REJECTED">,
         "creationDate": <timestamp>,
         "lastUpdatedDate": <timestamp>,
         "retryCreation": <int>
     }
  
#### Create Transaction
 
The following request creates a transaction:
 
     POST /transaction
     {
         "fromAccountId": "12",
         "amount": 10,
         "currency": "EUR",
         "toAccountId": "11"
     }
 
The successfully response returns the transaction created with `ID` specified:
 
     HTTP 201
     {
         "id": 1,
         "fromAccountId": 12,
         "amount": 10,
         "currency": "EUR",
         "toAccountId": 11,
         "status": "PENDING",
         "creationDate": "Feb 14, 2019 11:21:29 PM",
         "retryCreation": 0
     }
 
And example of failure response contains:
 
     HTTP 422
     {
         "errorMessage": "The following account does not have enough money",
         "errorCode": "C002"
     }
     
#### Retrieve an Transaction
 
The following request retrieves an transaction by his `ID`:
 
     GET /transaction/{id}
 
The successfully response returns the entities:
 
     HTTP 200
     {
         "id": 1,
         "fromAccountId": 12,
         "amount": 10,
         "currency": "EUR",
         "toAccountId": 11,
         "status": "CONFIRMED",
         "creationDate": "Feb 14, 2019 11:21:29 PM",
         "lastUpdatedDate": "Feb 14, 2019 11:21:31 PM",
         "retryCreation": 0
     }
 
And example of failure response contains:
 
     HTTP 404
     {
         "errorMessage": "Transaction not found",
         "errorCode": "C500"
     }
     
#### Retrieve a list of Transactions
 
The following request retrieves a list of transactions:
 
     GET /transaction?status=<status>
 
The successfully response returns the list of transactions:
 
     HTTP 200
     [
         {
             "id": 1,
             "fromAccountId": 12,
             "amount": 10,
             "currency": "EUR",
             "toAccountId": 11,
             "status": "CONFIRMED",
             "creationDate": "Feb 14, 2019 11:21:29 PM",
             "lastUpdatedDate": "Feb 14, 2019 11:21:31 PM",
             "retryCreation": 0
         },
         {
             "id": 2,
             "fromAccountId": 13,
             "amount": 100,
             "currency": "EUR",
             "toAccountId": 11,
             "status": "CONFIRMED",
             "creationDate": "Feb 14, 2019 11:23:29 PM",
             "lastUpdatedDate": "Feb 14, 2019 11:23:31 PM",
             "retryCreation": 0
         }
     ]   