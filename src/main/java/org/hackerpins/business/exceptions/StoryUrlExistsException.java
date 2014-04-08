package org.hackerpins.business.exceptions;

/**
 * Created by shekhargulati on 04/04/14.
 */
public class StoryUrlExistsException extends RuntimeException {

    public StoryUrlExistsException(String message) {
        super(message);
    }

    public StoryUrlExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
