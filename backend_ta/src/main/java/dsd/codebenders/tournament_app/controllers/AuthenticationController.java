package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.errors.BadAuthenticationRequestException;
import dsd.codebenders.tournament_app.errors.UnauthorizedAuthenticationException;
import dsd.codebenders.tournament_app.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    private final PlayerService playerService;

    @Autowired
    public AuthenticationController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping(value = "register")
    @ResponseBody
    public Map<String, String> register(@RequestBody Player player) throws BadAuthenticationRequestException {
        String username = player.getUsername();
        String email = player.getEmail();
        String password = player.getPassword();
        Map<String, String> jsonMap = new HashMap<>();
        int MAX_USERNAME_LENGTH = 40;
        int MAX_EMAIL_LENGTH = 40;
        if(username == null || email == null || password == null ||
                username.isBlank() || email.isBlank() || password.isBlank() ||
                username.length() > MAX_USERNAME_LENGTH || email.length() > MAX_EMAIL_LENGTH) {
            throw new BadAuthenticationRequestException("Some registration parameters are invalid");
        }
        if(playerService.checkUsernameAlreadyTaken(username)) {
            jsonMap.put("result", "Username already taken");
        } else if(playerService.checkEmailAlreadyTaken(email)) {
            jsonMap.put("result", "Email already taken");
        } else {
            playerService.addNewPlayer(player);
            jsonMap.put("result", "Registered");
        }
        return jsonMap;
    }

    @GetMapping(value = "error")
    public void error() throws UnauthorizedAuthenticationException {
        throw new UnauthorizedAuthenticationException("You are not authenticated!");
    }

    @GetMapping(value = "success")
    @ResponseBody
    public Map<String, Boolean> success() {
        Map<String, Boolean> map = new HashMap<>();
        map.put("result", true);
        return map;
    }

    @GetMapping(value = "failure")
    @ResponseBody
    public Map<String, Boolean> failure() {
        Map<String, Boolean> map = new HashMap<>();
        map.put("result", false);
        return map;
    }

    @GetMapping(value = "test")
    @ResponseBody
    public String test() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
