package com.serverless.model.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serverless.model.dto.ProjectDto;
import com.serverless.utility.enums.State;

import java.time.LocalDateTime;

public class Project {
    private long id;
    private String title;
    private String plot;
    private State state;
    private long memberId;
    private LocalDateTime createAt;

    public Project() {
    }

    public Project(ProjectDto projectDto) {
        this.id = projectDto.getId();
        this.title = projectDto.getTitle();
        this.plot = projectDto.getTitle();
        this.state = projectDto.getState();
        this.memberId = projectDto.getMemberId();
        this.createAt = projectDto.getCreateAt();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

}
