package org.hackerpins.business.domain;

import org.hackerpins.business.bean_validation.ImageOrVideoSrcUrl;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by shekhargulati on 04/04/14.
 */
@Entity
@NamedQueries(
        {
                @NamedQuery(name = "Story.hotStories", query = "SELECT new Story(s) from Story s where s.score >= 10 order by s.submittedAt desc "),
                @NamedQuery(name = "Story.count", query = "SELECT count(s) from Story s"),
                @NamedQuery(name = "Story.findOne", query = "SELECT new Story(s) from Story s where s.id =:id"),
                @NamedQuery(name = "Story.upcomingStories", query = "SELECT new Story(s) from Story s where s.score < 10 order by s.submittedAt desc "),

        }
)
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private int version;

    @NotNull
    @URL
    @Column(unique = true)
    private String url;

    @NotNull
    private String title;

    @NotNull
    @Size(max = 4000)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(updatable = false)
    private Date submittedAt = new Date();

    @ImageOrVideoSrcUrl
    @Embedded
    private Media media;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "Tags")
    private List<String> tags = new ArrayList<>();

    private long likes;

    private long dislikes;

    private long score;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "story")
    private List<Comment> comments = new ArrayList<>();

    public Story() {
    }


    public Story(String url, String title, String description, Media media) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.media = media;
    }

    public Story(Story story) {
        this.id = story.getId();
        this.url = story.getUrl();
        this.title = story.getTitle();
        this.description = story.getDescription();
        this.media = story.getMedia();
        this.tags = story.getTags();
        this.likes = story.getLikes();
        this.dislikes = story.getDislikes();
        this.submittedAt = story.submittedAt;
        this.score = story.score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public List<String> getTags() {
        return tags;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getDislikes() {
        return dislikes;
    }

    public void setDislikes(long dislikes) {
        this.dislikes = dislikes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Story{" +
                "id=" + id +
                ", version=" + version +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", submittedAt=" + submittedAt +
                ", media=" + media +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Story story = (Story) o;

        if (version != story.version) return false;
        if (description != null ? !description.equals(story.description) : story.description != null) return false;
        if (id != null ? !id.equals(story.id) : story.id != null) return false;
        if (title != null ? !title.equals(story.title) : story.title != null) return false;
        if (url != null ? !url.equals(story.url) : story.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + version;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

}
