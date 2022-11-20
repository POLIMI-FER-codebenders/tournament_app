package dsd.codebenders.tournament_app.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(BadAuthenticationRequestException.class)
    public ResponseEntity<String> handleBadAuthenticationRequestException(BadAuthenticationRequestException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedAuthenticationException.class)
    public ResponseEntity<String> handleUnauthorizedAuthenticationException(UnauthorizedAuthenticationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

}
