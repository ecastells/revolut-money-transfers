package com.revolut.moneytransfers.model;

public enum Currency {
    ARG(1), EUR(2), USD(3);

    private int id;

    Currency(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
