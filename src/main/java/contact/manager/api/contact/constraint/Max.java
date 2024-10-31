package contact.manager.api.contact.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE_USE;

@Target({ FIELD, TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxValidator.class)
public @interface Max {

    String message() default "This field is not valid";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    int value();
}