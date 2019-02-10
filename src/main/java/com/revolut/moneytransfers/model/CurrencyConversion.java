package com.revolut.moneytransfers.model;

import java.math.BigDecimal;

public class CurrencyConversion implements Entity{
    private Long id;
    private Currency fromCurrency;
    private Currency toCurrency;
    private BigDecimal rateChange;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(Currency fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(Currency toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getRateChange() {
        return rateChange;
    }

    public void setRateChange(BigDecimal rateChange) {
        this.rateChange = rateChange;
    }
}
