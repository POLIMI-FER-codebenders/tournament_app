package dsd.codebenders.tournament_app.requests;

public class CreateInvitationRequest {
    private Long idInvitedPlayer;
    private Long idTeam;

    public Long getIdInvitedPlayer() {
        return idInvitedPlayer;
    }

    public Long getIdTeam() {
        return idTeam;
    }

    public void setIdInvitedPlayer(Long idInvitedPlayer) {
        this.idInvitedPlayer = idInvitedPlayer;
    }

    public void setIdTeam(Long idTeam) {
        this.idTeam = idTeam;
    }
}
