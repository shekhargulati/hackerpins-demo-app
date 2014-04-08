package org.hackerpins.rest.resources;

import org.hackerpins.business.bean_validation.StoryExists;
import org.hackerpins.business.domain.Story;
import org.hackerpins.business.services.GooseExtractorClient;
import org.hackerpins.business.services.StoryService;
import org.hackerpins.rest.utils.Constants;
import org.hibernate.validator.constraints.URL;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Created by shekhargulati on 04/04/14.
 */
@Path("stories")
public class StoryResource {

    @Inject
    private StoryService storyService;

    @Resource
    private ManagedExecutorService mes;

    @Inject
    private GooseExtractorClient gooseExtractorClient;

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response submitStory(@NotNull @Valid Story story) {
        Story submittedStory = storyService.save(story);
        return Response.status(Response.Status.CREATED).entity(submittedStory).build();
    }

    @GET
    @Produces("application/json")
    @Path("/{id}")
    public Story findStory(@NotNull @StoryExists @PathParam("id") Long id) {
        return storyService.findStoryWithComments(id);
    }


    @GET
    @Produces("application/json")
    public List<Story> hotStories(@Min(Constants.START_FOR_QUERY) @QueryParam("start") int start, @Max(Constants.MAX_RESULTS_FOR_QUERY) @QueryParam("max") int max) {
        return storyService.hotStories(start, max);
    }

    @GET
    @Produces("application/json")
    @Path("/upcoming")
    public List<Story> upcomingStories(@Min(Constants.START_FOR_QUERY) @QueryParam("start") int start, @Max(Constants.MAX_RESULTS_FOR_QUERY) @QueryParam("max") int max) {
        return storyService.upcomingStories(start, max);
    }

    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateStory(@NotNull @Valid Story story) {
        Story updatedStory = storyService.update(story);
        return Response.status(Response.Status.OK).entity(updatedStory).build();
    }

    @GET
    @Produces("application/json")
    @Path("/suggest")
    public void suggest(@Suspended final AsyncResponse asyncResponse, @URL @QueryParam("storyUrl") final String storyUrl) {
        mes.submit(new Runnable() {
            @Override
            public void run() {
                Map<String, String> map = gooseExtractorClient.fetchImageAndDescription(storyUrl);
                if (map.size() == 0) {
                    asyncResponse.resume(Response.status(Response.Status.NO_CONTENT).build());
                    return;
                }
                asyncResponse.resume(Response.status(Response.Status.OK).entity(map).build());
            }
        });
    }

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("/{id}/like")
    public Response like(@PathParam("id") long id) {
        Story story = storyService.like(id);
        return Response.status(Response.Status.OK).entity(story).build();
    }

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("/{id}/dislike")
    public Response dislike(@PathParam("id") long id) {
        Story story = storyService.dislike(id);
        return Response.status(Response.Status.OK).entity(story).build();
    }
}
