package dsd.codebenders.tournament_app.errors;

public class CDServerAlreadyRegisteredException extends Exception{

    public CDServerAlreadyRegisteredException() {
        super("Server already registered");
    }

}
