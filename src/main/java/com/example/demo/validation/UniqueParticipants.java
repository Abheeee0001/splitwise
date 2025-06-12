package com.example.demo.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = UniqueParticipantValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueParticipants {
    String message() default "Duplicate participants are not allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
