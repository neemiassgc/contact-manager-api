package spring.manager.api.global;

import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

public final class ViolationResponse {

    @Getter
    private final List<String> fieldViolations = new ArrayList<>();

    public void putFieldViolation(final String violation) {
       fieldViolations.add(violation);
    }

    public void putFieldViolation(final FieldError fieldError) {
        putFieldViolation(fieldError.getDefaultMessage());
    }
}
