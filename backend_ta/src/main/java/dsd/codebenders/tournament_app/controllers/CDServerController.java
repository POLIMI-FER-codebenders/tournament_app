package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.errors.BadAdminRequestException;
import dsd.codebenders.tournament_app.errors.CDServerAlreadyRegisteredException;
import dsd.codebenders.tournament_app.errors.CDServerNotFoundException;
import dsd.codebenders.tournament_app.errors.UnauthorizedAuthenticationException;
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
    public void register(@RequestBody Server server) throws BadAdminRequestException, UnauthorizedAuthenticationException {
        if(!isAdminLogged()) {
            throw new UnauthorizedAuthenticationException("Admin rights required");
        }
        if(!isServerValid(server)) {
            throw new BadAdminRequestException("Address or token is missing");
        }
        try {
            serverService.addServer(server);
        } catch (CDServerAlreadyRegisteredException e) {
            throw new BadAdminRequestException(e.getMessage());
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

    private boolean isAdminLogged() {
        return playerService.getSelf().getUsername().equals("admin");
    }

    private boolean isServerValid(Server server) {
        return !(server.getAddress() == null || server.getAddress().isBlank()
                || server.getAdminToken() == null || server.getAdminToken().isBlank());
    }

}
