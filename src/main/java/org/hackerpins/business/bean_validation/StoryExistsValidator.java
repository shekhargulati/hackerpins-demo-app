package org.hackerpins.business.bean_validation;

import org.hackerpins.business.domain.Story;
import org.hackerpins.business.services.StoryService;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by shekhargulati on 06/04/14.
 */
public class StoryExistsValidator implements ConstraintValidator<StoryExists, Long> {
    @Inject
    private StoryService storyService;

    @Override
    public void initialize(StoryExists constraintAnnotation) {

    }

    @Override
    public boolean isValid(Long storyId, ConstraintValidatorContext context) {
        if (storyId == null) {
            return true;
        }
        Story story = storyService.findOne(storyId);
        return story != null ? true : false;
    }
}
