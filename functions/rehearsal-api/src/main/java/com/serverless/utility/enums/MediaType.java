package com.serverless.utility.enums;

import org.apache.commons.lang3.StringUtils;

public enum MediaType {
    application_pdf, audio_x_m4a;

    public static MediaType parse(String param) {
        String mediaType = param
                .replaceFirst("/", "_")
                .replaceAll("-", "_");
        return MediaType.valueOf(mediaType);
    }

    public static String toString(MediaType mediaType) {
        return mediaType
                .name()
                .replaceFirst("_", "/")
                .replaceAll("_", "-");
    }
}
