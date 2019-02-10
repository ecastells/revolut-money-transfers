package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.error.ConnectionException;
import com.revolut.moneytransfers.model.Entity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface GenericDTO<T extends Entity> {
    T fromResultSet(ResultSet rs) throws SQLException;

    default T getEntity(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet rs = preparedStatement.executeQuery()) {
            if (rs != null && rs.next()){
                return fromResultSet(rs);
            }
        }
        return null;
    }

    default List<T> getListEntities(PreparedStatement preparedStatement) throws SQLException {
        List<T> entities = new ArrayList<>();
        try (ResultSet rs = preparedStatement.executeQuery()) {
            if (rs != null){
                while (rs.next()) {
                    entities.add(fromResultSet(rs));
                }
            }
        }
        return entities;
    }

    default boolean insertEntity(T entity, PreparedStatement preparedStatement) throws SQLException {
        int rows = preparedStatement.executeUpdate();
        Long generatedId = null;

        if (rows > 0) {
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getLong(1);
                }
            }
        }

        if (generatedId == null) {
            throw new ConnectionException("Getting exception when trying to insert: {}" + entity);
        }
        entity.setId(generatedId);
        return true;
    }

    default boolean updateEntity(T entity, PreparedStatement preparedStatement) throws SQLException {
        if( preparedStatement.executeUpdate() < 1){
            throw new ConnectionException("Getting exception updating the Entity {}: " + entity);
        }
        return true;
    }

}
