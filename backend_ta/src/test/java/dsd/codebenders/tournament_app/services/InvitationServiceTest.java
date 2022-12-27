package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.InvitationRepository;
import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Invitation;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.utils.InvitationStatus;
import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;
import dsd.codebenders.tournament_app.entities.utils.TeamRole;
import dsd.codebenders.tournament_app.errors.BadRequestException;
import dsd.codebenders.tournament_app.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc
class InvitationServiceTest {

    @Autowired
    private InvitationService invitationService;
    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private TeamRepository teamRepository;

    private Team team;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        team = new Team("team1", 2, null, TeamPolicy.CLOSED, false, LocalDate.now());
        player1 = new Player("player1", "player1@gmail.com", "pass");
        player1.setIsAdmin(false);
        player1.setTeam(team);
        player2 = new Player("player2", "player2@gmail.com", "pass");
        player2.setIsAdmin(false);
        playerRepository.save(player1);
        playerRepository.save(player2);
        team.setCreator(player1);
        team.addTeamMember(player1);
        teamRepository.save(team);

    }

    @Test
    void getPending() {
        // create the invitation and persist it on the database
        Invitation invitation = new Invitation(player2, team, InvitationStatus.PENDING);
        invitationRepository.save(invitation);
        assertTrue(invitationService.getPending(player2).contains(invitation));
    }

    @Test
    void createInvitationSuccessful() {
        // call the service method to create an invitation
        Invitation invitation = invitationService.createInvitation(player1, player2.getID(), team.getID());
        assertTrue(invitationService.getPending(player2).contains(invitation));
    }

    @Test
    void createInvitationErrors() {
        assertThrows(ResourceNotFoundException.class, () -> invitationService.createInvitation(player1, -200L, team.getID()));

        assertThrows(ResourceNotFoundException.class, () -> invitationService.createInvitation(player1, player2.getID(), -1L));

        assertThrows(BadRequestException.class, () -> invitationService.createInvitation(player2, player1.getID(), team.getID()));

        Invitation invitation = new Invitation(player2, team, InvitationStatus.PENDING);
        invitationRepository.save(invitation);
        assertThrows(BadRequestException.class, () -> invitationService.createInvitation(player1, player2.getID(), team.getID()));
    }

    @Test
    void createInvitationPlayerAlreadyInTheTeam() {
        player2.setTeam(team);
        playerRepository.save(player2);
        assertThrows(BadRequestException.class, () -> invitationService.createInvitation(player1, player2.getID(), team.getID()));
    }

    @Test
    void acceptInvitationSuccessful() {
        // create the invitation and persist it on the database
        Invitation invitation = new Invitation(player2, team, InvitationStatus.PENDING);
        invitationRepository.save(invitation);

        invitationService.acceptInvitation(player2, invitation.getID());

        assertEquals(player2.getTeam(), team);
        assertEquals(player2.getRole(), TeamRole.MEMBER);

        assertEquals(invitation.getStatus(), InvitationStatus.ACCEPTED);
    }

    @Test
    void acceptInvitationErrors() {

        assertThrows(ResourceNotFoundException.class, () -> invitationService.acceptInvitation(player2, 200L));

        // create the invitation and persist it on the database
        Invitation invitation = new Invitation(player2, team, InvitationStatus.PENDING);
        invitationRepository.save(invitation);

        assertThrows(BadRequestException.class, () -> invitationService.acceptInvitation(player1, invitation.getID()));

        // try to accept an already rejected invitation
        invitation.setStatus(InvitationStatus.REJECTED);
        invitationRepository.save(invitation);

        assertThrows(BadRequestException.class, () -> invitationService.acceptInvitation(player2, invitation.getID()));
    }

    @Test
    void acceptInvitationTeamInvolvedInATournament() {
        // create the invitation and persist it on the database
        Invitation invitation = new Invitation(player2, team, InvitationStatus.PENDING);
        invitationRepository.save(invitation);

        // try to accept invitation for a team currently involved in a tournament
        team.setInTournament(true);
        teamRepository.save(team);
        assertThrows(BadRequestException.class, () -> invitationService.acceptInvitation(player2, invitation.getID()));
    }

    @Test
    void acceptInvitationWhileAlreadyInTheTeam() {
        team.setMaxNumberOfPlayers(3);
        teamRepository.save(team);

        // create the invitation and persist it on the database
        Invitation invitation = new Invitation(player2, team, InvitationStatus.PENDING);
        invitationRepository.save(invitation);

        // try to accept the invitation while already in that team
        team.addTeamMember(player2);
        teamRepository.save(team);
        assertThrows(BadRequestException.class, () -> invitationService.acceptInvitation(player2, invitation.getID()));
    }

    @Test
    void acceptInvitationAlreadyInATeam() {
        // setup another team and insert player2 into it
        Team team2 = new Team("team2", 2, null, TeamPolicy.CLOSED, false, LocalDate.now());
        team2.setCreator(player2);
        teamRepository.save(team2);
        player2.setTeam(team2);
        playerRepository.save(player2);

        // create the invitation and persist it on the database
        Invitation invitation = new Invitation(player2, team, InvitationStatus.PENDING);
        invitationRepository.save(invitation);

        // player2 tries to accept an invitation while being in another team
        assertThrows(BadRequestException.class, () -> invitationService.acceptInvitation(player2, invitation.getID()));
    }

    @Test
    void acceptInvitationTeamIsFull() {
        player2.setTeam(team);
        playerRepository.save(player2);
        team.addTeamMember(player2);
        teamRepository.save(team);

        Player player3 = new Player("player3", "player3@gmail.com", "pass");
        player3.setIsAdmin(false);
        playerRepository.save(player3);

        // create the invitation and persist it on the database
        Invitation invitation = new Invitation(player3, team, InvitationStatus.PENDING);
        invitationRepository.save(invitation);

        // player3 tries to accept an invitation but the team is full
        assertThrows(BadRequestException.class, () -> invitationService.acceptInvitation(player3, invitation.getID()));
    }

    @Test
    void rejectInvitationSuccessful() {
        // create the invitation and persist it on the database
        Invitation invitation = new Invitation(player2, team, InvitationStatus.PENDING);
        invitationRepository.save(invitation);

        invitationService.rejectInvitation(player2, invitation.getID());

        assertNotEquals(player2.getTeam(), team);

        assertEquals(invitation.getStatus(), InvitationStatus.REJECTED);
    }

    @Test
    void rejectInvitationErrors() {

        assertThrows(ResourceNotFoundException.class, () -> invitationService.rejectInvitation(player2, 200L));

        // create the invitation and persist it on the database
        Invitation invitation = new Invitation(player2, team, InvitationStatus.PENDING);
        invitationRepository.save(invitation);

        // try to reject someone else's invitation
        assertThrows(BadRequestException.class, () -> invitationService.rejectInvitation(player1, invitation.getID()));

        // try to reject an already rejected invitation
        invitation.setStatus(InvitationStatus.REJECTED);
        invitationRepository.save(invitation);

        assertThrows(BadRequestException.class, () -> invitationService.rejectInvitation(player2, invitation.getID()));
    }

}
