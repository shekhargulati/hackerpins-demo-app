package org.hackerpinstest.rest.resources;

import org.hackerpins.business.bean_validation.ImageOrVideoSrcUrl;
import org.hackerpins.business.builders.StoryBuilder;
import org.hackerpins.business.domain.Comment;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URL;

/**
 * Created by shekhargulati on 06/04/14.
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CommentResourceTest {

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
    public void shouldThrowConstraintVoilationExceptionWhenStoryIdIsInvalid(@ArquillianResource URL base) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(URI.create(new URL(base, "api/v1/stories/" + 100111 + "/comments").toExternalForm()));
        Comment comment = new Comment("Awesome story");
        Response response = webTarget.request().post(Entity.entity(comment, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testAddCommentsToStory(@ArquillianResource URL base) throws Exception {
        Story story = createStory(base);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(URI.create(new URL(base, "api/v1/stories/" + story.getId() + "/comments").toExternalForm()));
        Comment comment = new Comment("Awesome story");
        Comment addedComment = webTarget.request().post(Entity.entity(comment, MediaType.APPLICATION_JSON_TYPE), Comment.class);
        Assert.assertNotNull(addedComment);
    }

    private Story createStory(URL base) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(URI.create(new URL(base, "api/v1/stories").toExternalForm()));
        Story story = new StoryBuilder().setTitle("OpenShift Rocks").setDescription("OpenShift Rocks").setUrl("http://openshift.com").createStory();
        Response response = webTarget.request().post(Entity.entity(story, MediaType.APPLICATION_JSON_TYPE));
        Story readEntity = response.readEntity(Story.class);
        return readEntity;
    }
}
