package com.serverless.sql

class FileSQL {
    public static final String FIND_FILES_BY_USER_ID = '''
        SELECT * FROM FILES WHERE user_id = 
    '''

    public static final String CREATE_FILES = '''
        INSERT INTO FILES (name, size, created_at, updated_at, user_id) VALUES (?, ?, ?, ?, ?)
    '''
}
