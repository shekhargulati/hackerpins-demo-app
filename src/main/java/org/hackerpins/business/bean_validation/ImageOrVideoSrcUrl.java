package org.hackerpins.business.bean_validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by shekhargulati on 04/04/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
@ReportAsSingleViolation
@Constraint(validatedBy = {ImageOrVideoSrcUrlValidator.class})
public @interface ImageOrVideoSrcUrl {

    String message() default "Invalid Media type";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
