package dsd.codebenders.tournament_app.errors;

public class HTTPResponseException extends Exception{

    private final int status;

    public HTTPResponseException(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
