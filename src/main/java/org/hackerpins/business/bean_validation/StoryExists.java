package org.hackerpins.business.bean_validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by shekhargulati on 06/04/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Constraint(validatedBy = {StoryExistsValidator.class})
public @interface StoryExists {

    String message() default "No story exist with given story id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
