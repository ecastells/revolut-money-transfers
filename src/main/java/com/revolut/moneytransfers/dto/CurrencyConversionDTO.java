package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.model.Currency;
import com.revolut.moneytransfers.model.CurrencyConversion;

public interface CurrencyConversionDTO {
    CurrencyConversion getCurrencyConversion(Currency fromCurrency, Currency toCurrency);
}
