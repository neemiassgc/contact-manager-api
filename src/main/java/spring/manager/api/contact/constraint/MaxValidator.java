package spring.manager.api.contact.constraint;

public class MaxValidator extends Validator<Max> {

    @Override
    protected int init(Max constraintAnnotation) {
        return constraintAnnotation.value();
    }

    @Override
    protected boolean check(String value) {
        return value.length() <= super.annotationValue;
    }
}