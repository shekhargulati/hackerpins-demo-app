package org.hackerpins.business.builders;


import org.hackerpins.business.domain.Media;
import org.hackerpins.business.domain.Story;

public class StoryBuilder {
    private String url;
    private String title;
    private String description;
    private Media media;

    public StoryBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public StoryBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public StoryBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public StoryBuilder setMedia(Media media) {
        this.media = media;
        return this;
    }

    public Story createStory() {
        return new Story(url, title, description, media);
    }
}
