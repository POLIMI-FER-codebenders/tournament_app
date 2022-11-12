package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.errors.AuthenticationException;
import dsd.codebenders.tournament_app.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public String register(@RequestBody Player player) throws AuthenticationException {
        String username = player.getUsername();
        String email = player.getEmail();
        String password = player.getPassword();
        if(username == null || email == null || password == null ||
                username.isBlank() || email.isBlank() || password.isBlank() ) {
            throw new AuthenticationException("Some registration parameters are invalid");
        }
        if(playerService.checkUsernameAlreadyTaken(username)) {
            return "Username already taken";
        }
        if(playerService.checkEmailAlreadyTaken(email)) {
            return "Email already taken";
        }
        playerService.addNewPlayer(player);
        return "Authenticated";
    }

}
