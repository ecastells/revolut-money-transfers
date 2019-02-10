package com.revolut.moneytransfers.service;

import com.revolut.moneytransfers.dto.AccountDTO;
import com.revolut.moneytransfers.dto.CurrencyConversionDTO;
import com.revolut.moneytransfers.dto.TransactionDTO;
import com.revolut.moneytransfers.model.Account;
import com.revolut.moneytransfers.model.CurrencyConversion;
import com.revolut.moneytransfers.model.Transaction;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;

@Singleton
public class TransactionServiceImpl implements TransactionService {

    TransactionDTO transactionDTO;
    CurrencyConversionDTO currencyConversionDTO;
    AccountDTO accountDTO;

    @Inject
    public TransactionServiceImpl(TransactionDTO transactionDTO, AccountDTO accountDTO, CurrencyConversionDTO currencyConversionDTO) {
        this.transactionDTO = transactionDTO;
        this.accountDTO = accountDTO;
        this.currencyConversionDTO = currencyConversionDTO;
    }

    @Override
    public Transaction getTransactionById(Long id) {
        return transactionDTO.getTransactionById(id);
    }

    @Override
    public List<Transaction> getTransactions() {
        return transactionDTO.getTransactions();
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {

        Transaction transactionResponse = null;

        // 1- Get the account
        Account fromAccount = accountDTO.getAccount(transaction.getFromAccountId());

        // 2- Generate Currency Conversion
        BigDecimal moneyToTransfer;
        if (fromAccount.getCurrency().equals(transaction.getCurrency())){
            moneyToTransfer = transaction.getAmount();
        } else {
            CurrencyConversion currencyConversion = currencyConversionDTO.getCurrencyConversion(transaction.getCurrency(), fromAccount.getCurrency());
            moneyToTransfer = currencyConversion != null ?  transaction.getAmount().multiply(currencyConversion.getRateChange()) : BigDecimal.ZERO;
        }

        // 3 - If conversion is greater than 0
        if (moneyToTransfer.compareTo(BigDecimal.ZERO) > 0){
            // TODO Reduce Money from origin
            transactionResponse = transactionDTO.createTransaction(transaction);
        }
        return transactionResponse;
    }
}
