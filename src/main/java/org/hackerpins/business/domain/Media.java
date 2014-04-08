package org.hackerpins.business.domain;

import org.hibernate.validator.constraints.URL;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Created by shekhargulati on 04/04/14.
 */
@Embeddable
public class Media {
    @URL
    private String mediaUrl;
    @Enumerated(EnumType.STRING)
    private MediaType type;

    public Media() {
    }

    public Media(String mediaUrl, MediaType type) {
        this.mediaUrl = mediaUrl;
        this.type = type;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }
}
