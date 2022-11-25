package dsd.codebenders.tournament_app.controllers;

import java.util.List;

import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Tournament;
import dsd.codebenders.tournament_app.entities.utils.TournamentType;
import dsd.codebenders.tournament_app.requests.JoinTournamentRequest;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.services.TeamService;
import dsd.codebenders.tournament_app.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "api/tournament")
public class TournamentController {

    private final PlayerService playerService;
    private final TeamService teamService;
    private final TournamentService tournamentService;

    @Autowired
    public TournamentController(TeamService teamService, PlayerService playerService, TournamentService tournamentService) {
        this.playerService = playerService;
        this.teamService = teamService;
        this.tournamentService = tournamentService;
    }

    @GetMapping(value = "/list")
    public List<Tournament> getTournaments(@RequestParam(name = "type", required = false) String sType) {
        if (sType != null) {
            try {
                TournamentType type = TournamentType.valueOf(sType.toUpperCase());
                return tournamentService.getTournamentsOfType(type);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong tournament type specified");
            }
        } else {
            return tournamentService.getTournaments();
        }
    }

    @GetMapping(value = "/personal") //TODO Remove playerID injection when we have session identification
    public List<Tournament> getJoinedTournaments(@RequestParam(required = false) String playerID) {
        Player player;
        if (playerID != null) {
            player = playerService.findById(Long.valueOf(playerID));
        } else {
            player = playerService.findByUsername("ciccio");
        }
        return tournamentService.getJoinedTournaments(player);
    }

    @PostMapping(value = "/create")
    public Tournament createTournament(@RequestBody Tournament tournament) {
        //TODO Retrieve currently authenticated user from session
        Player creator = playerService.findByUsername("ciccio");
        return tournamentService.createTournament(tournament, creator);
    }

    /*@PostMapping(value = "/join")
    public Tournament joinTournament(@RequestParam(required = false) String playerID, @RequestBody JoinTournamentRequest request) {
        Player player;
        if (playerID != null) {
            player = playerService.findById(Long.valueOf(playerID));
        } else {
            player = playerService.findByUsername("ciccio");
        }
        return tournamentService.createTournament(tournament, creator);
    }*/
}
