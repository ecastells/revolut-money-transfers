package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.error.ConnectionException;
import com.revolut.moneytransfers.model.Entity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface GenericDTO<T extends Entity> {

    /**
     * Returns the {@link Entity} to be mapped from the {@link ResultSet} execution
     *
     * @param rs where the values are getting for the mapping
     * @return mapped Entity
     *
     */
    T fromResultSet(ResultSet rs) throws SQLException;

    /**
     * Returns only one {@link Entity} from the database using the preconfigured {@link PreparedStatement}
     *
     * @param preparedStatement to be used for filtering and getting only one Entity
     * @return mapped Entity
     *
     */

    default T getEntity(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet rs = preparedStatement.executeQuery()) {
            if (rs != null && rs.next()){
                return fromResultSet(rs);
            }
        }
        return null;
    }

    /**
     * Returns a {@link List<Entity>} from the database using the preconfigured {@link PreparedStatement}
     *
     * @param preparedStatement to be used for filtering and getting a list of Entity
     * @return mapped Entity
     *
     */
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

    /**
     * Returns a {@link Boolean} true if the insertion on the database was successfully using the preconfigured {@link PreparedStatement}
     * If it was successfully it also add the id on the {@link Entity}
     *
     * @param preparedStatement to be used for creating the Entity
     * @return a boolean if it was successfully
     * @throws SQLException if there is problem on the database connection
     *
     */
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

    /**
     * Returns a {@link Boolean} true if the {@link Entity} could be updated without problem on the database using the preconfigured {@link PreparedStatement}
     *
     * @param preparedStatement to be used for updating the Entity
     * @return a boolean if it was successfully
     * @throws SQLException if there is problem on the database connection
     *
     */
    default boolean updateEntity(T entity, PreparedStatement preparedStatement) throws SQLException {
        if( preparedStatement.executeUpdate() < 1){
            throw new ConnectionException("Getting exception updating the Entity {}: " + entity);
        }
        return true;
    }

}
