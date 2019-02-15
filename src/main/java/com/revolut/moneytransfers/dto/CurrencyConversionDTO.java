package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.model.Currency;
import com.revolut.moneytransfers.model.CurrencyConversion;

public interface CurrencyConversionDTO {
    /**
     * Returns a {@link CurrencyConversion} object from the database by its fromCurrency and toCurrency specified
     *
     * @param fromCurrency the id of the Account
     * @param toCurrency the id of the Account
     * @return CurrencyConversion object
     */
    CurrencyConversion getCurrencyConversion(Currency fromCurrency, Currency toCurrency);
}
