package com.revolut.moneytransfers.model;

import java.util.Objects;

public class Currency {
    Integer id;
    String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return id.equals(currency.id) &&
                type.equals(currency.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}
