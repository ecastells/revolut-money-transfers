package com.revolut.moneytransfers.model;

public enum Currency implements Entity{
    ARG(1L), EUR(2L), USD(3L), GBP(4L);

    private Long id;

    Currency(Long id) {
        this.id = id;
    }

    public static Currency getCurrencyById(Long id){
        Currency currency = null;
        for(Currency currency1: Currency.values()){
            if (currency1.getId().equals(id)){
                currency = currency1;
                break;
            }
        }
        return currency;
    }

    @Override
    public Long getId() {
        return id;
    }


    /**
     * This method should not be used in an Enum
     */
    @Override
    public void setId(Long id){
        throw new UnsupportedOperationException();
    }
}
