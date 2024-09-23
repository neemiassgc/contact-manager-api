package spring.manager.api.contact.constraint;

public class MinValidator extends Validator<Min> {

    @Override
    protected int init(Min constraintAnnotation) {
        return constraintAnnotation.value();
    }

    @Override
    protected boolean check(String value) {
        return value.length() >= super.annotationValue;
    }
}