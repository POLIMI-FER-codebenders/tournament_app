package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.ServerRepository;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ServerService {

    @Value("${code-defenders.address:http://codedef1.duckdns.org:8080/}")
    private String serversString;
    @Value("${code-defenders.token:SDUzl14p2gmoAWFNcEPleWP9uZekwSDC}")
    private String tokensString;
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

    public void updateServerTable(List<String> servers, List<String> tokens) {
        int length = Math.min(servers.size(), tokens.size());
        for(int i = 0; i < length; i++) {
            Server server = serverRepository.findByAddress(servers.get(i));
            if(server != null) {
                serverRepository.updateToken(tokens.get(i), server);
            } else {
                serverRepository.save(new Server(servers.get(i), tokens.get(i)));
            }
        }
    }

    @EventListener(ContextRefreshedEvent.class)
    public void cdServersInitializer() {
        List<String> servers = Arrays.stream(serversString.split(",")).map(String::trim).toList();
        List<String> tokens = Arrays.stream(tokensString.split(",")).map(String::trim).toList();
        updateServerTable(servers, tokens);
    }

}
