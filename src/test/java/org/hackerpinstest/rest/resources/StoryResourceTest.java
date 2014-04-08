package org.hackerpinstest.rest.resources;

import org.hackerpins.business.bean_validation.ImageOrVideoSrcUrl;
import org.hackerpins.business.builders.StoryBuilder;
import org.hackerpins.business.domain.Story;
import org.hackerpins.business.producers.EntityManagerProducer;
import org.hackerpins.business.services.StoryService;
import org.hackerpins.rest.config.RestConfig;
import org.hackerpins.rest.resources.StoryResource;
import org.hackerpins.rest.utils.Constants;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shekhargulati on 04/04/14.
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StoryResourceTest {

    @Deployment(testable = false)
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

    @Test
    public void test1SubmitStory(@ArquillianResource URL base) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(URI.create(new URL(base, "api/v1/stories").toExternalForm()));
        Story story = new StoryBuilder().setTitle("OpenShift Rocks").setDescription("OpenShift Rocks").setUrl("http://openshift.com").createStory();
        story.setScore(11);
        story.getTags().addAll(Arrays.asList("java", "nodejs", "ruby"));
        Response response = webTarget.request().post(Entity.entity(story, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(201, response.getStatus());
        Story readEntity = response.readEntity(Story.class);
        Assert.assertEquals("OpenShift Rocks", readEntity.getTitle());
        Assert.assertNotNull(readEntity.getId());
    }

    @Test
    public void test2UpdateStory(@ArquillianResource URL base) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(URI.create(new URL(base, "api/v1/stories").toExternalForm()));
        Story story = new StoryBuilder().setTitle("OpenShift Rocks updated").setDescription("OpenShift Rocks updated").setUrl("http://openshift.com").createStory();
        story.setId(Long.valueOf(1L));
        story.setScore(11);
        story.getTags().addAll(Arrays.asList("java", "nodejs", "ruby"));
        Response response = webTarget.request().header("Accept", MediaType.APPLICATION_JSON).put(Entity.entity(story, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, response.getStatus());
        Story readEntity = response.readEntity(Story.class);
        System.out.println("Updated Story: " + readEntity);
        Assert.assertEquals("OpenShift Rocks updated", readEntity.getTitle());
    }

    @Test
    public void test3FindStory(@ArquillianResource URL base) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(URI.create(new URL(base, "api/v1/stories/1").toExternalForm()));
        Response response = webTarget.request().get();
        Assert.assertEquals(200, response.getStatus());
        Story readEntity = response.readEntity(Story.class);
        System.out.println("Read Story: " + readEntity);
        Assert.assertEquals("OpenShift Rocks updated", readEntity.getTitle());
    }

    @Test
    public void test4AllStories(@ArquillianResource URL base) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(URI.create(new URL(base, "api/v1/stories").toExternalForm()));
        GenericType<List<Story>> listGenericType = new GenericType<List<Story>>() {
        };
        List<Story> stories = webTarget.request().get(listGenericType);
        Assert.assertEquals(1, stories.size());
        Assert.assertEquals(3, stories.get(0).getTags().size());
    }

    @Test
    public void test5ShouldLikeStoryOneTime(@ArquillianResource URL base) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(URI.create(new URL(base, "api/v1/stories/1/like").toExternalForm()));
        Story story = webTarget.request().post(null, Story.class);
        Assert.assertEquals(1, story.getLikes());
    }

    @Test
    public void test6ShouldDisLikeStoryOneTime(@ArquillianResource URL base) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(URI.create(new URL(base, "api/v1/stories/1/dislike").toExternalForm()));
        Story story = webTarget.request().post(null, Story.class);
        Assert.assertEquals(1, story.getDislikes());
    }
}
