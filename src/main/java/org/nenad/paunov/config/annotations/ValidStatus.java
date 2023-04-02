package org.nenad.paunov.config.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.nenad.paunov.config.validators.ValidStatusValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ValidStatusValidator.class})
public @interface ValidStatus {
	String message() default "Invalid game status";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
