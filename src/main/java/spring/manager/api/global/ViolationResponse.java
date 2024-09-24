package spring.manager.api.global;

import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public final class ViolationResponse {

    private final Map<String, List<String>> fieldViolations;

    ViolationResponse(final List<FieldError> fieldErrors) {
        this.fieldViolations =
        fieldErrors.stream().map(FieldError::getDefaultMessage)
            .collect(Collectors.groupingBy(message -> {
                assert message != null;
                return message.split(" ")[0];
            }));
    }
}