package com.serverless.dao;

import com.serverless.config.DriverConfig;
import com.serverless.model.domain.History;
import com.serverless.sql.HistorySQL;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneOffset;

public class HistoryDao {

    public HistoryDao() {
    }

    public long create(History history) throws SQLException {
        Connection connection = DriverConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(HistorySQL.CREATE_HISTORY);
        long insertId = 11;
        try {
            statement.setLong(1, history.getProjectId());
            statement.setLong(2, history.getFileId());
            statement.setString(3, history.getEventType().name());
            statement.setDate(4, new Date(history.getCreateAt().toEpochSecond(ZoneOffset.UTC)));
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            statement.close();
            connection.close();
        }

        return insertId;
    }

}
