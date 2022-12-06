package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.GameClass;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.requests.ClassChoiceRequest;
import dsd.codebenders.tournament_app.services.ClassService;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "api/classes")
public class ClassUploadController {

    private final PlayerService playerService;
    private final ClassService classService;
    private final TournamentService tournamentService;

    @Autowired
    public ClassUploadController(PlayerService playerService, ClassService classService, TournamentService tournamentService) {
        this.playerService = playerService;
        this.classService = classService;
        this.tournamentService = tournamentService;
    }

    @PostMapping("/upload")
    public void uploadFile(@RequestParam("file") MultipartFile file) {
        Player author = playerService.getSelf();
        classService.uploadFile(file, author);
    }

    @GetMapping("/get-all")
    public List<GameClass> getAllClasses(){
        return classService.getAllClasses();
    }

    @PostMapping("/post-choices")
    public void postChoice(@RequestBody ClassChoiceRequest classChoiceRequest){
        Player loggedPlayer = playerService.getSelf();
        tournamentService.postRoundChoice(classChoiceRequest, loggedPlayer);
    }

}
