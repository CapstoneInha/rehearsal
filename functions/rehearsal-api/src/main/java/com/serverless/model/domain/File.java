package com.serverless.model.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serverless.model.dto.FileDto;
import com.serverless.utility.enums.MediaType;

import java.time.LocalDateTime;

public class File {
    private long id;
    private String name;
    private String mediaType;
    private long size;
    private long memberId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public File() {
    }

    public File(FileDto fileDto) {
        this.id = fileDto.getId();
        this.name = fileDto.getName();
        this.size = fileDto.getSize();
        this.mediaType = fileDto.getMediaType();
        this.createdAt = fileDto.getCreatedAt();
        this.updatedAt = fileDto.getUpdatedAt();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
