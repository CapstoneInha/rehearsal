package com.serverless.dao;

import com.serverless.model.domain.History;
import com.serverless.sql.HistorySQL;
import com.serverless.utility.template.JdbcTemplate;

import java.sql.SQLException;

public class HistoryDao {
    private static HistoryDao historyDao;

    private HistoryDao() {
    }

    public static HistoryDao createHistoryDao() {
        if (historyDao == null) {
            historyDao = new HistoryDao();
        }

        return historyDao;
    }

    public long create(History history) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        long insertId = jdbcTemplate.create(HistorySQL.CREATE_HISTORY, history);
        jdbcTemplate.batchUpdate();
        jdbcTemplate.close();
        return insertId;
    }

}
