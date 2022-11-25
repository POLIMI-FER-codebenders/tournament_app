package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.CDPlayerRepository;
import dsd.codebenders.tournament_app.entities.CDPlayer;
import dsd.codebenders.tournament_app.entities.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CDPlayerService {

    private final CDPlayerRepository cdPlayerRepository;

    @Autowired
    public CDPlayerService(CDPlayerRepository cdPlayerRepository) {
        this.cdPlayerRepository = cdPlayerRepository;
    }

    public CDPlayer getCDPlayerByServer(Player player, String server) {
        return cdPlayerRepository.findByRealPlayerAndServer(player, server);
    }

    public void addNewCDPlayer(CDPlayer cdPlayer) {
        cdPlayerRepository.save(cdPlayer);
    }

}
