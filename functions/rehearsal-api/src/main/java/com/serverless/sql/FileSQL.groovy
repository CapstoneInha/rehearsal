package com.serverless.sql

class FileSQL {

    public static final String FIND_FILE_BY_ID = '''
        SELECT * FROM FILES WHERE id = :id
    '''

    public static final String CREATE_FILES = '''
        INSERT INTO FILES (name, media_type, size, created_at, updated_at, member_id) 
        VALUES (:name, :mediaType, :size, :createdAt, :updatedAt, :memberId)
    '''

    public static final String FIND_FILES_BY_PROJECT_ID = '''
        SELECT f.*
        FROM FILES f
        INNER JOIN HISTORIES h ON h.file_id = f.id
                                  and h.event_type = :eventType
                                  and h.project_id = :projectId
    '''

}
