package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.ServerRepository;
import dsd.codebenders.tournament_app.entities.Server;
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
        List<Server> serverList = serverRepository.findAll();
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

    public Server getServerByAddress(String address) {
        return serverRepository.findByAddress(address);
    }

    public void addServer(Server server) {
        serverRepository.save(server);
    }

    public void updateServer(Server server) {
        serverRepository.updateToken(server.getAddress(), server);
    }

    public void deleterServer(Server server) {
        serverRepository.delete(server);
    }

}
