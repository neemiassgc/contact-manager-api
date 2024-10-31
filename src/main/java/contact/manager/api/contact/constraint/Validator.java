package contact.manager.api.contact.constraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Annotation;
import java.util.Objects;

abstract class Validator<T extends Annotation> implements ConstraintValidator<T, String> {

    protected int annotationValue;

    @Override
    public void initialize(T constraintAnnotation) {
        this.annotationValue = init(constraintAnnotation);
    }

    protected abstract int init(final T constraintAnnotation);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(value)) return true;
        return check(value);
    }

    protected abstract boolean check(final String value);
}