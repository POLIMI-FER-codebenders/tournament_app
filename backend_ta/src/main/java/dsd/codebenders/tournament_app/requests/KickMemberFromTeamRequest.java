package dsd.codebenders.tournament_app.requests;

public class KickMemberFromTeamRequest {
    private Long idKickedPlayer;

    public Long getIdKickedPlayer() {
        return idKickedPlayer;
    }

    public void setIdKickedPlayer(Long idKickedPlayer) {
        this.idKickedPlayer = idKickedPlayer;
    }
}
