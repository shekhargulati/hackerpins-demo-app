package org.hackerpinstest.business.services;

import org.hackerpins.business.bean_validation.ImageOrVideoSrcUrl;
import org.hackerpins.business.builders.StoryBuilder;
import org.hackerpins.business.domain.Comment;
import org.hackerpins.business.domain.Media;
import org.hackerpins.business.domain.MediaType;
import org.hackerpins.business.domain.Story;
import org.hackerpins.business.producers.EntityManagerProducer;
import org.hackerpins.business.services.StoryService;
import org.hackerpins.rest.config.RestConfig;
import org.hackerpins.rest.resources.StoryResource;
import org.hackerpins.rest.utils.Constants;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.transaction.RollbackException;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shekhargulati on 04/04/14.
 */
@RunWith(Arquillian.class)
public class StoryServiceTest {

    @Deployment
    public static Archive<?> deployment() {
        WebArchive webArchive = ShrinkWrap.create(WebArchive.class).
                addPackage(ImageOrVideoSrcUrl.class.getPackage()).
                addPackage(StoryBuilder.class.getPackage()).
                addPackage(Story.class.getPackage()).
                addPackage(EntityManagerProducer.class.getPackage()).
                addPackage(StoryService.class.getPackage()).
                addPackage(RestConfig.class.getPackage()).
                addPackage(StoryResource.class.getPackage()).
                addPackage(Constants.class.getPackage()).
                addAsResource("META-INF/persistence.xml").
                addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
        System.out.println(webArchive.toString(true));
        return webArchive;

    }

    @Inject
    private StoryService storyService;
    @Inject
    private EntityManager entityManager;
    @Inject
    private UserTransaction userTransaction;

    @Before
    public void setup() throws Exception {
        userTransaction.begin();
        entityManager.createQuery("DELETE from Story s").executeUpdate();
        userTransaction.commit();
    }

    @Test
    public void shouldThrownConstraintVoilationWhenStoryIsNull() throws Exception {
        try {
            storyService.save(null);
        } catch (EJBException e) {
            Throwable cause = e.getCause();
            Assert.assertTrue(cause instanceof ConstraintViolationException);
        }
    }

    @Test
    public void shouldThrowConstraintViolationWhenStoryDataIsInvalid() throws Exception {
        try {
            storyService.save(new Story());
        } catch (EJBException e) {
            Throwable cause = e.getCause();
            Assert.assertTrue(cause instanceof ConstraintViolationException);
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) cause;
            Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
            Assert.assertEquals(3, constraintViolations.size());
        }
    }

    @Test
    public void shouldSaveStoryWhenDataIsValid() throws Exception {
        Story persistedStory = submitStory();
        Assert.assertNotNull(persistedStory.getId());
    }

    @Test
    public void shouldSaveStoryWithMedia() throws Exception {
        StoryBuilder storyBuilder = new StoryBuilder().setUrl("http://openshiftrocks.com").setTitle("OpenShift Rocks!!").setDescription("OpenShift Rocks!!").setMedia(new Media("http://abc.com/test.png", MediaType.PHOTO));
        Story persistedStory = storyService.save(storyBuilder.createStory());
        Assert.assertNotNull(persistedStory.getId());
    }

    @Test
    public void shouldThrowStoryUrlExistsExceptionWhenStoryAlreadyExistsInDatabase() throws Exception {
        StoryBuilder storyBuilder = new StoryBuilder().setUrl("http://openshiftrocks.com").setTitle("OpenShift Rocks!!").setDescription("OpenShift Rocks!!").setMedia(new Media("http://abc.com/test.png", MediaType.PHOTO));
        storyService.save(storyBuilder.createStory());
        try {
            storyService.save(storyBuilder.createStory());
        } catch (EJBTransactionRolledbackException e) {
            Throwable rollbackException = e.getCause();
            Assert.assertTrue(rollbackException instanceof RollbackException);
            Throwable persistenceException = ((RollbackException) rollbackException).getCause();
            Assert.assertTrue(persistenceException instanceof PersistenceException);
        }

    }

    @Test
    public void shouldFindOneStory() throws Exception {
        Story story = submitStory();
        Assert.assertEquals(storyService.findOne(story.getId()), story);
    }

    @Test
    public void shouldReturnNullWhenStoryDoesNotExistWithStoryId() throws Exception {
        Story story = storyService.findOne(Long.valueOf(1L));
        Assert.assertNull(story);
    }

    @Test
    public void shouldFindAllPersistedStoriesOrderByTimestamp() throws Exception {
        submitStory(10);
        List<Story> stories = storyService.hotStories(0, 10);
        Assert.assertEquals(10, stories.size());

    }

    @Test
    public void testCountStories() throws Exception {
        submitStory(10);
        long count = storyService.count();
        Assert.assertEquals(10, count);

    }

    @Test
    public void shouldUpdateStory() throws Exception {
        Story story = submitStory();
        story.setTitle("Hello world");
        story.getTags().addAll(Arrays.asList("java", "nodejs", "python"));
        Story updatedStory = storyService.update(story);
        Assert.assertEquals("Hello world", updatedStory.getTitle());
        Assert.assertEquals(3, updatedStory.getTags().size());
    }

    @Test
    public void shouldLikeStory10Times() throws Exception {
        final Story story = submitStory();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        final CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    storyService.like(story.getId());
                    latch.countDown();
                }
            });
        }
        latch.await();
        Assert.assertEquals(10, storyService.findOne(story.getId()).getLikes());
    }

    @Test
    public void shouldDislikeStory10Times() throws Exception {
        final Story story = submitStory();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        final CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    storyService.dislike(story.getId());
                    latch.countDown();
                }
            });
        }
        latch.await();
        Assert.assertEquals(10, storyService.findOne(story.getId()).getDislikes());
    }

    @Test
    public void shouldAddCommentToStory() {
        Story story = submitStory();
        Comment comment = new Comment("Awesome story");
        Comment addedComment = storyService.addCommentToStory(story.getId(), comment);
        Assert.assertNotNull(addedComment.getId());
    }

    @Test
    public void shouldFetchStoryWithComments() {
        Story story = submitStory();
        Comment comment1 = new Comment("Awesome story 1");
        Comment comment2 = new Comment("Awesome story 2");
        Comment comment3 = new Comment("Awesome story 3");
        storyService.addCommentToStory(story.getId(), comment1);
        storyService.addCommentToStory(story.getId(), comment2);
        storyService.addCommentToStory(story.getId(), comment3);

        Story storyWithComments = storyService.findStoryWithComments(story.getId());
        Assert.assertEquals(3, storyWithComments.getComments().size());
        Assert.assertEquals("Awesome story 1", storyWithComments.getComments().get(0).getText());

    }

    private void submitStory(int n) {
        for (int i = 0; i < n; i++) {
            StoryBuilder storyBuilder = new StoryBuilder().setUrl("http://openshift.com" + i).setTitle("OpenShift Rocks!!").setDescription("OpenShift Rocks!!");
            Story story = storyBuilder.createStory();
            story.setScore(11);
            storyService.save(story);
        }
    }

    private Story submitStory() {
        StoryBuilder storyBuilder = new StoryBuilder().setUrl("http://openshift.com").setTitle("OpenShift Rocks!!").setDescription("OpenShift Rocks!!");
        return storyService.save(storyBuilder.createStory());
    }

}
