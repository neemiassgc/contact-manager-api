package contact.manager.api.global;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeParseException;
import java.util.Objects;

@ControllerAdvice
public class GlobalErrorController {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> resolveResponseStatusException(final ResponseStatusException rse) {
        return ResponseEntity
            .status(rse.getStatusCode())
            .contentType(MediaType.TEXT_PLAIN)
            .body(Objects.requireNonNullElseGet(rse.getReason(), () -> "No reasons"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ViolationResponse> resolveResponseStatusException(final MethodArgumentNotValidException manve) {
        final ViolationResponse violationResponse = new ViolationResponse(manve.getFieldErrors());
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(violationResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> resolveHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        if (exception.getRootCause() instanceof DateTimeParseException)
            return ResponseEntity.badRequest().body("Invalid date format. Expected format is YYYY-MM-DD.");

        return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(
            Objects.isNull(exception.getRootCause())
                ? exception.getMessage()
                : exception.getRootCause().getMessage()
        );
    }
}
