package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.ServerRepository;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.errors.CDServerAlreadyRegisteredException;
import dsd.codebenders.tournament_app.errors.CDServerNotFoundException;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerService {
    private final ServerRepository serverRepository;

    @Autowired
    public ServerService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    public Server getCDServer() {
        List<Server> serverList = serverRepository.findAllActive();
        Server lessLoaded = null;
        Integer minimumLoad = Integer.MAX_VALUE;
        for(Server s: serverList) {
            Integer load = HTTPRequestsSender.sendGetRequest(s, "/admin/api/load", Integer.class);
            if(load < minimumLoad) {
                minimumLoad = load;
                lessLoaded = s;
            }
        }
        return lessLoaded;
    }

    public void addServer(Server server) throws CDServerAlreadyRegisteredException {
        Server storedServer = serverRepository.findByAddress(server.getAddress());
        if(storedServer == null) {
            server.setActive(true);
            serverRepository.save(server);
        } else if (storedServer.isActive()) {
            throw new CDServerAlreadyRegisteredException();
        } else {
            serverRepository.updateToken(server.getAdminToken(), storedServer);
            serverRepository.updateAsActive(storedServer);
        }
    }

    public void updateServer(Server server) throws CDServerNotFoundException {
        Server storedServer = serverRepository.findByAddress(server.getAddress());
        if(storedServer == null || !storedServer.isActive()) {
            throw new CDServerNotFoundException();
        } else {
            serverRepository.updateToken(server.getAdminToken(), storedServer);
        }
    }

    public void deleteServer(Server server) throws CDServerNotFoundException {
        Server storedServer = serverRepository.findByAddress(server.getAddress());
        if(storedServer == null || !storedServer.isActive()) {
            throw new CDServerNotFoundException();
        } else {
            serverRepository.updateAsInactive(storedServer);
        }
    }

}
