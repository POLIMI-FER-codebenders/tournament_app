package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.errors.*;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.services.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "admin/api/cd-server")
public class CDServerController {

    private final ServerService serverService;
    private final PlayerService playerService;

    @Autowired
    public CDServerController(ServerService serverService, PlayerService playerService) {
        this.serverService = serverService;
        this.playerService = playerService;
    }

    @PostMapping(value = "register")
    public void register(@RequestBody Server server)
            throws BadAdminRequestException, UnauthorizedAuthenticationException, CDServerUnreachableException {
        validateRequest(server);
        try {
            serverService.addServer(server);
        } catch (CDServerAlreadyRegisteredException e) {
            throw new BadAdminRequestException(e.getMessage());
        }
    }

    @PostMapping(value = "update")
    public void update(@RequestBody Server server)
            throws BadAdminRequestException, UnauthorizedAuthenticationException, CDServerUnreachableException {
        validateRequest(server);
        try {
            serverService.updateServer(server);
        } catch (CDServerNotFoundException e) {
            throw new BadAdminRequestException(e.getMessage());
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
        try {
            serverService.deleteServer(server);
        } catch (CDServerNotFoundException e) {
            throw new BadAdminRequestException(e.getMessage());
        }
    }

    private void validateRequest(@RequestBody Server server)
            throws UnauthorizedAuthenticationException, BadAdminRequestException, CDServerUnreachableException {
        if(!isAdminLogged()) {
            throw new UnauthorizedAuthenticationException("Admin rights required");
        }
        if(!isServerValid(server)) {
            throw new BadAdminRequestException("Address or token is missing");
        }
        if (!serverService.isTokenValid(server)) {
            throw new BadAdminRequestException("Token is invalid");
        }
    }

    private boolean isAdminLogged() {
        return playerService.getSelf().isAdmin();
    }

    private boolean isServerValid(Server server) {
        return !(server.getAddress() == null || server.getAddress().isBlank()
                || server.getAdminToken() == null || server.getAdminToken().isBlank());
    }

}
