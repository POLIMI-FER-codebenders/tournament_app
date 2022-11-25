package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.MatchRepository;
import dsd.codebenders.tournament_app.entities.CDPlayer;
import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchService {

    private final CDPlayerService cdPlayerservice;
    private final MatchRepository matchRepository;

    @Autowired
    public MatchService(CDPlayerService cdPlayerservice, MatchRepository matchRepository) {
        this.cdPlayerservice = cdPlayerservice;
        this.matchRepository = matchRepository;
    }

    public void createAndStartMatch(Match match) {
        // TODO: choose CD server
        String server = "http://localhost:4000";
        createCDPlayers(match.getAttackersTeam(), server);
        createCDPlayers(match.getDefendersTeam(), server);
        matchRepository.setServerById(match.getID(), server);
    }

    public Match getOngoingMatchByPlayer(Player player) {
        // TODO: implement findByPlayer
        //return matchRepository.findByPlayer(Player player);
        return new Match();
    }

    private void createCDPlayers(Team team, String server) {
        for(Player p: team.getMembers()) {
            CDPlayer cdPlayer = cdPlayerservice.getCDPlayerByServer(p, server);
            if(cdPlayer == null) {
                // TODO: crete temporary user
            }
        }
    }

}
