package dsd.codebenders.tournament_app.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import dsd.codebenders.tournament_app.dao.ServerRepository;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.errors.*;
import dsd.codebenders.tournament_app.responses.LoadResponse;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.net.ConnectException;
import java.util.List;

@Service
public class ServerService {
    private final ServerRepository serverRepository;

    @Autowired
    public ServerService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    public Server getCDServer() throws CDServerUnreachableException {
        List<Server> serverList = serverRepository.findAllActive();
        Server lessLoaded = null;
        Integer minimumLoad = Integer.MAX_VALUE;
        for(Server s: serverList) {
            Integer load;
            try {
                load = HTTPRequestsSender.sendGetRequest(s, "/admin/api/load", LoadResponse.class).getTotal();
            } catch (RestClientException e) {
                continue;
            }
            if(load < minimumLoad) {
                minimumLoad = load;
                lessLoaded = s;
            }
        }
        if(lessLoaded == null) {
            throw new CDServerUnreachableException("All CD servers are unreachable");
        }
        return lessLoaded;
    }

    public Server getServerByAddress(String address) {
        String tmpAddress = address;
        if(address.charAt(address.length() - 1) == '/') {
            tmpAddress = address.substring(0, address.length() - 1);
        }
        return serverRepository.findByAddress(tmpAddress);
    }

    public void addServer(Server server) throws CDServerAlreadyRegisteredException {
        Server storedServer = getServerByAddress(server.getAddress());
        if(storedServer == null) {
            server.setActive(true);
            String address = server.getAddress();
            if(address.charAt(address.length() - 1) == '/') {
                address = address.substring(0, address.length() - 1);
                server.setAddress(address);
            }
            serverRepository.save(server);
        } else if (storedServer.isActive()) {
            throw new CDServerAlreadyRegisteredException();
        } else {
            serverRepository.updateToken(server.getAdminToken(), storedServer);
            serverRepository.updateAsActive(storedServer);
        }
    }

    public void updateServer(Server server) throws CDServerNotFoundException {
        Server storedServer = getServerByAddress(server.getAddress());
        if(storedServer == null || !storedServer.isActive()) {
            throw new CDServerNotFoundException();
        } else {
            serverRepository.updateToken(server.getAdminToken(), storedServer);
        }
    }

    public void deleteServer(Server server) throws CDServerNotFoundException {
        Server storedServer = getServerByAddress(server.getAddress());
        if(storedServer == null || !storedServer.isActive()) {
            throw new CDServerNotFoundException();
        } else {
            serverRepository.updateAsInactive(storedServer);
        }
    }

    public boolean isTokenValid(Server server) throws CDServerUnreachableException, BadRequestToCDException {
        boolean isValid = true;
        try {
            HTTPRequestsSender.sendGetRequest(server, "/admin/api/auth/self", String.class);
        } catch(RestClientException e) {
            if (e.getCause() instanceof ConnectException) {
                throw new CDServerUnreachableException(server.getAddress() + " server unreachable");
            } else {
                throw new BadRequestToCDException("Bad request to CD");
            }
        }
        return isValid;
    }

}
