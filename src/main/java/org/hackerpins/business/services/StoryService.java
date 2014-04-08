package org.hackerpins.business.services;

import org.hackerpins.business.domain.Comment;
import org.hackerpins.business.domain.Story;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by shekhargulati on 04/04/14.
 */
@Stateless
public class StoryService {

    @Inject
    private Logger logger;
    @Inject
    private EntityManager entityManager;

    public Story save(@Valid @NotNull Story story) {
        entityManager.persist(story);
        return story;
    }

    public Story findOne(Long storyId) {
        return entityManager.find(Story.class, storyId);
    }

    public Story findStory(Long storyId) {
        return entityManager.createNamedQuery("Story.findOne", Story.class).setParameter("id", storyId).getSingleResult();
    }

    public List<Story> hotStories(int start, int max) {
        return entityManager.createNamedQuery("Story.hotStories", Story.class).setFirstResult(start).setMaxResults(max).getResultList();
    }

    public List<Story> upcomingStories(int start, int max) {
        return entityManager.createNamedQuery("Story.upcomingStories", Story.class).setFirstResult(start).setMaxResults(max).getResultList();
    }

    public long count() {
        return entityManager.createNamedQuery("Story.count", Long.class).getSingleResult();
    }


    public Story update(Story story) {
        return entityManager.merge(story);
    }

    public Story like(long id) {
        Story story = this.findOne(id);
        entityManager.refresh(story, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        story.setLikes(story.getLikes() + 1);
        story.setScore(story.getScore() + 1);
        story = this.update(story);
        return findStory(story.getId());
    }

    public Story dislike(long id) {
        Story story = this.findOne(id);
        entityManager.refresh(story, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        story.setDislikes(story.getDislikes() + 1);
        story.setScore(story.getScore() - 1);
        story = this.update(story);
        return findStory(story.getId());
    }

    public Comment addCommentToStory(Long storyId, Comment comment) {
        Story story = this.findOne(storyId);
        comment.setStory(story);
        story.getComments().add(comment);
        entityManager.persist(comment);
        return readComment(comment.getId());
    }

    public Comment readComment(Long commentId) {
        return entityManager.createNamedQuery("Comment.findOne", Comment.class).setParameter("commentId", commentId).getSingleResult();
    }

    public Story findStoryWithComments(Long id) {
        Story story = entityManager.createNamedQuery("Story.findOne", Story.class).setParameter("id", id).getSingleResult();
        List<Comment> comments = entityManager.createNamedQuery("Comment.findAllForStory", Comment.class).setParameter("story", story).getResultList();
        story.getComments().addAll(comments);
        return story;
    }
}
