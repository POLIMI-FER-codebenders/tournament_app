package dsd.codebenders.tournament_app.errors;

public class RequestNotAuthorizedException extends RuntimeException {
    public RequestNotAuthorizedException() {
    }

    public RequestNotAuthorizedException(String message) {
        super(message);
    }
}
