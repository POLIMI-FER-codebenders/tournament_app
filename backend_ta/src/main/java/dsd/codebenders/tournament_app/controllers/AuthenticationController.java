package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.errors.BadAuthenticationRequestException;
import dsd.codebenders.tournament_app.errors.UnauthorizedAuthenticationException;
import dsd.codebenders.tournament_app.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
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
        if(!isUsernameValid(username) || !isEmailValid(email) || !isPasswordValid(password)) {
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

    private boolean isUsernameValid(String username) {
        String pattern = "^[a-zA-Z][a-zA-Z0-9]{2,19}$";
        return username != null && username.matches(pattern);
    }

    private boolean isEmailValid(String email) {
        if (email == null || email.length() > 40) {
            return false;
        }
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    private boolean isPasswordValid(String password) {
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,20}$";
        return password != null && password.matches(pattern);
    }

}
