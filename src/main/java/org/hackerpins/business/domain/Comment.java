package org.hackerpins.business.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Created by shekhargulati on 06/04/14.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Comment.findOne", query = "SELECT new Comment(c.id,c.text,c.createdAt) from Comment c where c.id =:commentId"),
        @NamedQuery(name = "Comment.findAllForStory", query = "SELECT new Comment(c.id,c.text,c.createdAt) from Comment c where c.story =:story")
})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 2000)
    private String text;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createdAt = new Date();

    @ManyToOne
    private Story story;

    public Comment() {
    }

    public Comment(String text) {
        this.text = text;
    }

    public Comment(Long id, String text, Date createdAt) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

}
