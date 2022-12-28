package dsd.codebenders.tournament_app.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import dsd.codebenders.tournament_app.dao.TournamentScoreRepository;
import dsd.codebenders.tournament_app.entities.*;
import dsd.codebenders.tournament_app.entities.score.MultiplayerScoreboard;
import dsd.codebenders.tournament_app.entities.score.Scoreboard;
import dsd.codebenders.tournament_app.entities.utils.MatchStatus;
import dsd.codebenders.tournament_app.requests.GameIdRequest;
import dsd.codebenders.tournament_app.services.MatchService;

import dsd.codebenders.tournament_app.services.TournamentSchedulerService;
import dsd.codebenders.tournament_app.services.TournamentService;

import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class EndMatchTask implements Runnable {

    private final Match match;
    private final MatchService matchService;
    private final TournamentService tournamentService;
    private final TournamentSchedulerService tournamentSchedulerService;
    private final Logger logger = LoggerFactory.getLogger(EndMatchTask.class);

    public EndMatchTask(Match match, MatchService matchService, TournamentService tournamentService, TournamentSchedulerService tournamentSchedulerService) {
        this.match = match;
        this.matchService = matchService;
        this.tournamentService = tournamentService;
        this.tournamentSchedulerService = tournamentSchedulerService;
    }

    @Override
    public void run() {
        if(match.getStatus() == MatchStatus.IN_PHASE_THREE) {
            Server server = match.getServer();
            try {
                HTTPRequestsSender.sendPostRequest(server, "/admin/api/game/end", new GameIdRequest(match), void.class);
            } catch (RestClientException | JsonProcessingException e) {
                System.err.println("ERROR: Match " + match.getID() + " failed while ending");
                if(matchService.setFailedMatchAndCheckRoundEnding(match)) {
                    tournamentSchedulerService.prepareRoundAndStartMatches(match.getTournament());
                }
                return;
            }
            boolean roundEnded = matchService.endMatchAndCheckRoundEnding(match);
            GameStatus gameStatus;
            try {
                gameStatus = HTTPRequestsSender.sendGetRequest(server, "/api/game", Map.of("gameId", match.getGameId().toString()), GameStatus.class);
            } catch (RestClientException e) {
                System.err.println("ERROR: Match " + match.getID() + " failed while setting winner");
                if(matchService.setFailedMatchAndCheckRoundEnding(match)) {
                    tournamentSchedulerService.prepareRoundAndStartMatches(match.getTournament());
                }
                return;
            }
            Team winner = computeWinner(gameStatus.getScoreboard());
            // add one point to the winner
            tournamentService.givePointsForWin(match.getTournament(), winner);
            logger.info("Match " + match.getID() + " ended, winner: " + winner.getName());
            matchService.setWinner(match, winner);
            if(roundEnded) {
                tournamentSchedulerService.prepareRoundAndStartMatches(match.getTournament());
            }
        }
    }

    private Team computeWinner(Scoreboard scoreboard) {
        MultiplayerScoreboard multiplayerScoreboard = (MultiplayerScoreboard) scoreboard;
        int attackersTotal = multiplayerScoreboard.getAttackersTotal().getPoints();
        int defendersTotal = multiplayerScoreboard.getDefendersTotal().getPoints();
        if(attackersTotal > defendersTotal) {
            logger.info("Attacker team won.");
            return match.getAttackersTeam();
        } else if(attackersTotal < defendersTotal) {
            logger.info("Defender team won.");
            return match.getDefendersTeam();
        } else {
            logger.info("Tie randomly picking a winner.");
            Random random = new Random();
            return random.nextInt(2) == 0 ? match.getAttackersTeam() : match.getDefendersTeam();
        }
    }

}
