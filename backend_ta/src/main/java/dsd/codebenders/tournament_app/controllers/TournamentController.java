package dsd.codebenders.tournament_app.controllers;

import java.util.List;
import java.util.Optional;

import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.Tournament;
import dsd.codebenders.tournament_app.entities.utils.TeamRole;
import dsd.codebenders.tournament_app.entities.utils.TournamentStatus;
import dsd.codebenders.tournament_app.entities.utils.TournamentType;
import dsd.codebenders.tournament_app.errors.BadRequestException;
import dsd.codebenders.tournament_app.errors.MatchCreationException;
import dsd.codebenders.tournament_app.requests.JoinTournamentRequest;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.services.TeamService;
import dsd.codebenders.tournament_app.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/tournament")
public class TournamentController {

    private final PlayerService playerService;
    private final TeamService teamService;
    private final TournamentService tournamentService;
    @Value("${game.tournament.league.min-teams:2}")
    private int minLeagueTeams;
    @Value("${game.tournament.knockout.min-teams:2}")
    private int minKnockoutTeams;
    @Value("${game.tournament.league.max-teams:8}")
    private int maxLeagueTeams;
    @Value("${game.tournament.knockout.max-teams:16}")
    private int maxKnockoutTeams;
    @Value("${game.tournament.league.min-team-size:1}")
    private int minLeagueTeamSize;
    @Value("${game.tournament.knockout.min-team-size:1}")
    private int minKnockoutTeamSize;
    @Value("${game.tournament.league.max-team-size:10}")
    private int maxLeagueTeamSize;
    @Value("${game.tournament.knockout.max-team-size:10}")
    private int maxKnockoutTeamSize;
    @Value("${request-debug:false}")
    private boolean debug;

    @Autowired
    public TournamentController(TeamService teamService, PlayerService playerService, TournamentService tournamentService) {
        this.playerService = playerService;
        this.teamService = teamService;
        this.tournamentService = tournamentService;
    }

    private Player spoofPlayer(Optional<Long> playerID) {
        if (debug && playerID.isPresent()) {
            if (playerService.findById(playerID.get()) != null) {
                return playerService.findById(playerID.get());
            } else {
                throw new BadRequestException("Spoofed ID not found");
            }
        } else {
            return playerService.getSelf();
        }
    }

    @GetMapping(value = "/list")
    public List<Tournament> getTournaments(@RequestParam(name = "type") Optional<String> sType) {
        if (sType.isPresent()) {
            try {
                TournamentType type = TournamentType.valueOf(sType.get().toUpperCase());
                return tournamentService.getTournamentsOfType(type);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Wrong tournament type specified");
            }
        } else {
            return tournamentService.getTournaments();
        }
    }

    @GetMapping(value = "/personal")
    public List<Tournament> getJoinedTournaments(@RequestParam Optional<Long> playerID) {
        Player player = spoofPlayer(playerID);
        return tournamentService.getJoinedTournaments(player);
    }

    @PostMapping(value = "/create")
    public Tournament createTournament(@RequestParam Optional<Long> playerID, @RequestBody Tournament tournament) {
        Player creator = spoofPlayer(playerID);
        if (tournament.getName() == null || tournament.getName().isEmpty()) {
            throw new BadRequestException("The tournament name cannot be empty");
        } else if (tournament.getName().length() > 255) {
            throw new BadRequestException("The maximum length of the tournament name is 255 characters");
        } else if (tournamentService.getActiveTournamentByName(tournament.getName()).isPresent()) {
            throw new BadRequestException("An active tournament with the same name already exists");
        } else if (tournament.getType().equals(TournamentType.LEAGUE)) {
            if (tournament.getNumberOfTeams() < minLeagueTeams) {
                throw new BadRequestException("The minimum number of teams for a league tournament is " + minLeagueTeams);
            } else if (tournament.getNumberOfTeams() > maxLeagueTeams) {
                throw new BadRequestException("The maximum number of teams for a league tournament is " + maxLeagueTeams);
            } else if (tournament.getTeamSize() < minLeagueTeamSize) {
                throw new BadRequestException("The minimum team size for a league tournament is " + minLeagueTeamSize);
            } else if (tournament.getTeamSize() > maxLeagueTeamSize) {
                throw new BadRequestException("The maximum team size for a league tournament is " + maxLeagueTeamSize);
            } else {
                return tournamentService.createTournament(tournament, creator);
            }
        } else {
            double log2 = Math.log(tournament.getNumberOfTeams()) / Math.log(2);
            if (Math.floor(log2) != Math.ceil(log2)) {
                throw new BadRequestException("The number of teams for a knockout tournament needs to be a power of 2");
            } else if (tournament.getNumberOfTeams() < minKnockoutTeams) {
                throw new BadRequestException("The minimum number of teams for a knockout tournament is " + minKnockoutTeams);
            } else if (tournament.getNumberOfTeams() > maxKnockoutTeams) {
                throw new BadRequestException("The maximum team size for a knockout tournament is " + maxKnockoutTeams);
            } else if (tournament.getTeamSize() < minKnockoutTeamSize) {
                throw new BadRequestException("The minimum team size for a knockout tournament is " + minKnockoutTeamSize);
            } else if (tournament.getTeamSize() > maxKnockoutTeamSize) {
                throw new BadRequestException("The maximum team size for a knockout tournament is " + maxKnockoutTeamSize);
            } else {
                return tournamentService.createTournament(tournament, creator);
            }
        }
    }

    @PostMapping(value = "/join")
    public Tournament joinTournament(@RequestParam Optional<Long> playerID, @RequestBody JoinTournamentRequest request) throws MatchCreationException {
        Player player = spoofPlayer(playerID);
        Optional<Tournament> optTournament = tournamentService.getTournamentByID(request.getIdTournament());
        if (player.getRole() != TeamRole.LEADER) {
            throw new BadRequestException("You are not the leader of the team");
        } else if (optTournament.isEmpty()) {
            throw new BadRequestException("Tournament not found");
        } else {
            Tournament tournament = optTournament.get();
            Team team = player.getTeam();
            if (tournamentService.getTournamentTeams(tournament).contains(team)) {
                throw new BadRequestException("The team is already registered in the tournament");
            } else if (team.isInTournament()) {
                throw new BadRequestException("The team is already participating in another tournament");
            } else if (tournament.getStatus() != TournamentStatus.TEAMS_JOINING) {
                throw new BadRequestException("The joining phase is already over");
            } else if (team.getTeamMembers().size() != tournament.getTeamSize()) {
                throw new BadRequestException("Only teams of " + tournament.getTeamSize() + " can participate in this tournament");
            } else {
                return tournamentService.addTeam(tournament, team);
            }
        }
    }

    @PostMapping(value = "/leave")
    public Tournament leaveTournament(@RequestParam Optional<Long> playerID) {
        Player player = spoofPlayer(playerID);
        if (player.getRole() != TeamRole.LEADER) {
            throw new BadRequestException("You are not the leader of the team");
        } else if (!player.getTeam().isInTournament()) {
            throw new BadRequestException("The team is not registered in a tournament");
        } else if (tournamentService.getActiveTournament(player).get().getStatus() != TournamentStatus.TEAMS_JOINING) {
            throw new BadRequestException("The tournament has already started"); //TODO handle forfeit
        } else {
            return tournamentService.removeTeam(tournamentService.getActiveTournament(player).get(), player.getTeam());
        }
    }
}
