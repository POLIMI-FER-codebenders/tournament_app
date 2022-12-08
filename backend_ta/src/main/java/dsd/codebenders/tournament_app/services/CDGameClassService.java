package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.CDGameCLassRepository;
import dsd.codebenders.tournament_app.entities.CDGameClass;
import dsd.codebenders.tournament_app.entities.GameClass;
import dsd.codebenders.tournament_app.entities.Server;
import org.springframework.stereotype.Service;

@Service
public class CDGameClassService {

    private final CDGameCLassRepository cdGameCLassRepository;

    public CDGameClassService(CDGameCLassRepository cdGameCLassRepository) {
        this.cdGameCLassRepository = cdGameCLassRepository;
    }

    public CDGameClass getCDGameClassByServer(GameClass gameClass, Server server) {
        return cdGameCLassRepository.findByRealClassAndServer(gameClass, server);
    }

    public void addNewCDGameClass(CDGameClass cdGameClass) {
        cdGameCLassRepository.save(cdGameClass);
    }

}
