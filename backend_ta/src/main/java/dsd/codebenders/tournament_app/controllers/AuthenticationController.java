package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.errors.AuthenticationException;
import dsd.codebenders.tournament_app.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Map<String, String> register(@RequestBody Player player) throws AuthenticationException {
        String username = player.getUsername();
        String email = player.getEmail();
        String password = player.getPassword();
        Map<String, String> jsonMap = new HashMap<>();
        if(username == null || email == null || password == null ||
                username.isBlank() || email.isBlank() || password.isBlank() ) {
            throw new AuthenticationException("Some registration parameters are invalid");
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

}
