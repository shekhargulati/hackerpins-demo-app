package org.hackerpinstest.business.domain;

import org.hackerpins.business.bean_validation.ImageOrVideoSrcUrl;
import org.hackerpins.business.builders.StoryBuilder;
import org.hackerpins.business.domain.Comment;
import org.hackerpins.business.domain.Media;
import org.hackerpins.business.domain.MediaType;
import org.hackerpins.business.domain.Story;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Created by shekhargulati on 04/04/14.
 */
@RunWith(Arquillian.class)
public class StoryTest {

    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(JavaArchive.class).addClasses(Story.class, Media.class, MediaType.class, StoryBuilder.class, Comment.class).addPackage(ImageOrVideoSrcUrl.class.getPackage()).addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private Validator validator;

    @Test
    public void shouldValidateEntityWhenAllFieldsAreCorrect() throws Exception {
        Story story = new StoryBuilder().setTitle("Hello World").setDescription("hello world").setUrl("http://openshift.com").createStory();
        story.setId(Long.valueOf(1L));
        Set<ConstraintViolation<Story>> constraintViolations = validator.validate(story);
        Assert.assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldGiveConstraintVoilationWhenUrlIsInvalid() {
        Story story = new StoryBuilder().setTitle("Hello World").setDescription("hello world").setUrl("openshift").createStory();
        story.setId(Long.valueOf(1L));
        Set<ConstraintViolation<Story>> constraintViolations = validator.validate(story);
        Assert.assertEquals(1, constraintViolations.size());
    }

    @Test
    public void shouldGiveConstraintVoilationWhenDescriptionIsGreaterThan4000Characters() throws Exception {
        Story story = new StoryBuilder().setTitle("Hello World").
                setDescription(conact("hello world", 1000)).
                setUrl("http://openshift.com").
                createStory();
        story.setId(Long.valueOf(1L));
        Set<ConstraintViolation<Story>> constraintViolations = validator.validate(story);
        Assert.assertEquals(1, constraintViolations.size());
        ConstraintViolation<Story> constraintViolation = constraintViolations.iterator().next();
        Assert.assertEquals("{javax.validation.constraints.Size.message}", constraintViolation.getMessageTemplate());
    }

    @Test
    public void shouldGiveConstraintVoilationWhenMediaUrlIsInvalid() throws Exception {
        Story story = new StoryBuilder().setUrl("http://openshift.com").setTitle("hello world").setDescription("hello world").setMedia(new Media("abc", MediaType.PHOTO)).createStory();
        story.setId(Long.valueOf(1L));
        Set<ConstraintViolation<Story>> constraintViolations = validator.validate(story);
        Assert.assertEquals(1, constraintViolations.size());
        ConstraintViolation<Story> constraintViolation = constraintViolations.iterator().next();
        Assert.assertEquals("Invalid Media type", constraintViolation.getMessageTemplate());
    }

    @Test
    public void shouldGiveConstraintVoilationWhenMediaUrlIsNotPhoto() throws Exception {
        Story story = new StoryBuilder().setUrl("http://openshift.com").setTitle("hello world").setDescription("hello world").setMedia(new Media("http://abc.com", MediaType.PHOTO)).createStory();
        story.setId(Long.valueOf(1L));
        Set<ConstraintViolation<Story>> constraintViolations = validator.validate(story);
        Assert.assertEquals(1, constraintViolations.size());
        ConstraintViolation<Story> constraintViolation = constraintViolations.iterator().next();
        Assert.assertEquals("Invalid Media type", constraintViolation.getMessageTemplate());
    }

    @Test
    public void shouldNotGiveConstraintVoilationWhenMediaUrlIsPhoto() throws Exception {
        Story story = new StoryBuilder().setUrl("http://openshift.com").setTitle("hello world").setDescription("hello world").setMedia(new Media("http://abc.com/test.png", MediaType.PHOTO)).createStory();
        story.setId(Long.valueOf(1L));
        Set<ConstraintViolation<Story>> constraintViolations = validator.validate(story);
        Assert.assertEquals(0, constraintViolations.size());
    }

    @Test
    public void shouldGiveConstraintVoilationWhenMediaUrlIsNotVideo() throws Exception {
        Story story = new StoryBuilder().setUrl("http://openshift.com").setTitle("hello world").setDescription("hello world").setMedia(new Media("http://abc.com", MediaType.VIDEO)).createStory();
        story.setId(Long.valueOf(1L));
        Set<ConstraintViolation<Story>> constraintViolations = validator.validate(story);
        Assert.assertEquals(1, constraintViolations.size());
        ConstraintViolation<Story> constraintViolation = constraintViolations.iterator().next();
        Assert.assertEquals("Invalid Media type", constraintViolation.getMessageTemplate());
    }

    @Test
    public void shouldNotGiveConstraintVoilationWhenMediaUrlIsVideo() throws Exception {
        Story story = new StoryBuilder().setUrl("http://openshift.com").setTitle("hello world").setDescription("hello world").setMedia(new Media("http://abc.com/test.mp4", MediaType.VIDEO)).createStory();
        story.setId(Long.valueOf(1L));
        Set<ConstraintViolation<Story>> constraintViolations = validator.validate(story);
        Assert.assertEquals(0, constraintViolations.size());
    }

    private static String conact(String str, int times) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }

}