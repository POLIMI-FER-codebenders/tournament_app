package dsd.codebenders.tournament_app.services;

import java.util.List;
import java.util.Optional;

import dsd.codebenders.tournament_app.dao.CDPlayerRepository;
import dsd.codebenders.tournament_app.entities.CDPlayer;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.entities.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CDPlayerService {

    private final CDPlayerRepository cdPlayerRepository;

    @Autowired
    public CDPlayerService(CDPlayerRepository cdPlayerRepository) {
        this.cdPlayerRepository = cdPlayerRepository;
    }

    public CDPlayer getCDPlayerByServer(Player player, Server server) {
        return cdPlayerRepository.findByRealPlayerAndServer(player, server);
    }

    public List<CDPlayer> getCDPlayersByTeamAndServer(Team team, Server server) {
        return cdPlayerRepository.findByTeamAndServer(team, server);
    }

    public void addNewCDPlayer(CDPlayer cdPlayer) {
        cdPlayerRepository.save(cdPlayer);
    }

    public Optional<CDPlayer> findByUserIdAndServer(Integer userId, Server server) {
        return cdPlayerRepository.findByUserIdAndServer(userId, server);
    }
}
