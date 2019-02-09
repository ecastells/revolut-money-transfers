package com.revolut.moneytransfers.model;

public enum Currency implements Entity{
    ARG(1L), EUR(2L), USD(3L);

    private Long id;

    Currency(Long id) {
        this.id = id;
    }

    @Override
    public void setId(Long id) {

    }

    @Override
    public Long getId() {
        return id;
    }
}
