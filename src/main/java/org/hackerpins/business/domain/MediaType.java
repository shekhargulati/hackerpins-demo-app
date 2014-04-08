package org.hackerpins.business.domain;

/**
 * Created by shekhargulati on 04/04/14.
 */
public enum MediaType {

    PHOTO("photo"),VIDEO("video");
    private final String type;

    MediaType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
