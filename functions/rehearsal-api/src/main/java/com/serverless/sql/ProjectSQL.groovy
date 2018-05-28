package com.serverless.sql

class ProjectSQL {
    public static final String FIND_PROJECTS_BY_USER_ID = '''
        SELECT p.*, f.name as file_name
        FROM PROJECTS p
        LEFT JOIN HISTORIES h on h.project_id = p.id and h.event_type = 'CREATE_PROJECT'
        LEFT JOIN FILES f on f.id = h.file_id
        WHERE p.member_id = :memberId
    '''

    public static final String FIND_PROJECT_BY_ID = '''
        SELECT p.*, f.name as file_name
        FROM PROJECTS p
        LEFT JOIN HISTORIES h on h.project_id = p.id and h.event_type = 'CREATE_PROJECT'
        LEFT JOIN FILES f on f.id = h.file_id
        WHERE p.id = :projectId
    '''

    public static final String CREATE_PROJECTS = '''
        INSERT INTO PROJECTS (title, plot, state, create_at, member_id) 
        VALUES (:title, :plot, :state, :createAt, :memberId)
    '''

    public static final String UPDATE_PROJECTS_BY_ID = '''
        UPDATE PROJECTS 
        SET title= :title, plot= :plot, state= :state, member_id= :memberId 
        WHERE id= :id
    '''
}
