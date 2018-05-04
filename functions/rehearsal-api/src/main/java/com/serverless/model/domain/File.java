package com.serverless.model.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.serverless.utility.enums.MediaType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

public class File {
    private long id;
    private String name;
    private MediaType mediaType;
    private long size;
    private long memberId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

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

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
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

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", name='" + name  +
                ", mediaType" + mediaType +
                ", size=" + size +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", memberId=" + memberId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof File)) return false;

        File file = (File) o;

        return new EqualsBuilder()
                .append(getId(), file.getId())
                .append(getMediaType(), file.getMediaType())
                .append(getSize(), file.getSize())
                .append(getName(), file.getName())
                .append(getCreatedAt(), file.getCreatedAt())
                .append(getUpdatedAt(), file.getUpdatedAt())
                .append(getMemberId(), file.getMemberId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getName())
                .append(getMediaType())
                .append(getSize())
                .append(getCreatedAt())
                .append(getUpdatedAt())
                .append(getMemberId())
                .toHashCode();
    }
}
