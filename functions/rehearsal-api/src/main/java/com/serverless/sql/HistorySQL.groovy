package com.serverless.sql

class HistorySQL {
    public static final String CREATE_HISTORY = '''
        INSERT INTO HISTORIES (project_id, file_id, event_type, create_at) 
        VALUES (:projectId, :fileId, :eventType, :createAt)
    '''
}
