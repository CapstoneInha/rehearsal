package com.serverless.model.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

public class FileDto {
    private long id;
    private String name;
    private long size;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long userId;

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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof FileDto)) return false;

        FileDto file = (FileDto) o;

        return new EqualsBuilder()
                .append(getId(), file.getId())
                .append(getSize(), file.getSize())
                .append(getName(), file.getName())
                .append(getCreatedAt(), file.getCreatedAt())
                .append(getUpdatedAt(), file.getUpdatedAt())
                .append(getUserId(), file.getUserId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getName())
                .append(getSize())
                .append(getCreatedAt())
                .append(getUpdatedAt())
                .append(getUserId())
                .toHashCode();
    }
}
