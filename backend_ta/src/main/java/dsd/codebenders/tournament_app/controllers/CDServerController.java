package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.errors.BadAdminRequestException;
import dsd.codebenders.tournament_app.errors.UnauthorizedAuthenticationException;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.services.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "admin/api/cd_server")
public class CDServerController {

    private final ServerService serverService;
    private final PlayerService playerService;

    @Autowired
    public CDServerController(ServerService serverService, PlayerService playerService) {
        this.serverService = serverService;
        this.playerService = playerService;
    }

    @PostMapping(value = "register")
    public void register(@RequestBody Server server) throws BadAdminRequestException, UnauthorizedAuthenticationException {
        if(!isAdminLogged()) {
            throw new UnauthorizedAuthenticationException("Admin rights required");
        }
        if(!isServerValid(server)) {
            throw new BadAdminRequestException("Address or token is missing");
        }
        if(serverService.getServerByAddress(server.getAddress()) != null) {
            throw new BadAdminRequestException("Server already registered");
        } else {
            serverService.addServer(server);
        }
    }

    @PostMapping(value = "update")
    public void update(@RequestBody Server server) throws BadAdminRequestException, UnauthorizedAuthenticationException {
        if(!isAdminLogged()) {
            throw new UnauthorizedAuthenticationException("Admin rights required");
        }
        if(!isServerValid(server)) {
            throw new BadAdminRequestException("Address or token is missing");
        }
        if(serverService.getServerByAddress(server.getAddress()) == null) {
            throw new BadAdminRequestException("Server not found");
        } else {
            serverService.updateServer(server);
        }
    }

    @PostMapping(value = "delete")
    public void delete(@RequestBody Server server) throws BadAdminRequestException, UnauthorizedAuthenticationException {
        if(!isAdminLogged()) {
            throw new UnauthorizedAuthenticationException("Admin rights required");
        }
        if(server.getAddress() == null || server.getAddress().isBlank()) {
            throw new BadAdminRequestException("Address is missing");
        }
        if(serverService.getServerByAddress(server.getAddress()) == null) {
            throw new BadAdminRequestException("Server not found");
        } else {
            serverService.deleterServer(server);
        }
    }

    private boolean isAdminLogged() {
        return playerService.getSelf().getUsername().equals("admin");
    }

    private boolean isServerValid(Server server) {
        return !(server.getAddress() == null || server.getAddress().isBlank()
                || server.getAdminToken() == null || server.getAdminToken().isBlank());
    }

}
