package org.hackerpins.rest.resources;

import org.hackerpins.business.bean_validation.StoryExists;
import org.hackerpins.business.domain.Comment;
import org.hackerpins.business.services.StoryService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * Created by shekhargulati on 06/04/14.
 */
@Path("/stories/{storyId}/comments")
public class CommentResource {

    @Inject
    private StoryService storyService;
    @Inject
    private Logger logger;

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response addCommentsToStory(@NotNull @StoryExists @PathParam("storyId") Long storyId, @Valid Comment comment) {
        Comment addedComment = storyService.addCommentToStory(storyId, comment);
        return Response.status(Response.Status.CREATED).entity(addedComment).build();
    }
}
