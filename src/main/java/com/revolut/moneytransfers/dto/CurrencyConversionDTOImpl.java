package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.db.DBUtil;
import com.revolut.moneytransfers.model.Currency;
import com.revolut.moneytransfers.model.CurrencyConversion;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class CurrencyConversionDTOImpl implements CurrencyConversionDTO, GenericDTO<CurrencyConversion>{

    private DBUtil dbUtil;
    public static final String GET_CURRENCY_CONVERSION_BY_FROM_TO_CURRENCY = "SELECT * FROM currency_conversion where from_currency_id = ? AND to_currency_id = ?";

    @Inject
    public CurrencyConversionDTOImpl(DBUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    @Override
    public CurrencyConversion getCurrencyConversion(Currency fromCurrency, Currency toCurrency) {
        return dbUtil.executeOnlyReadQuery(GET_CURRENCY_CONVERSION_BY_FROM_TO_CURRENCY, preparedStatement -> {
            preparedStatement.setLong(1, fromCurrency.getId());
            preparedStatement.setLong(2, toCurrency.getId());
            return getEntity(preparedStatement);
        }).getResult();
    }

    @Override
    public CurrencyConversion fromResultSet(ResultSet rs) throws SQLException {
        CurrencyConversion currencyConversion = new CurrencyConversion();
        currencyConversion.setId(rs.getLong("id"));
        currencyConversion.setFromCurrency(Currency.getCurrencyById(rs.getLong("from_currency_id")));
        currencyConversion.setToCurrency(Currency.getCurrencyById(rs.getLong("to_currency_id")));
        currencyConversion.setRateChange(rs.getBigDecimal("rate_change"));
        return currencyConversion;
    }

}
