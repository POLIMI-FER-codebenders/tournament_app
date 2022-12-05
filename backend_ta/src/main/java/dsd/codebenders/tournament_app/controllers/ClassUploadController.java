package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.services.ClassService;
import dsd.codebenders.tournament_app.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "api/classes")
public class ClassUploadController {

    private final PlayerService playerService;
    private final ClassService classService;

    @Autowired
    public ClassUploadController(PlayerService playerService, ClassService classService) {
        this.playerService = playerService;
        this.classService = classService;
    }

    @PostMapping("/upload")
    public void uploadFile(@RequestBody MultipartFile file) {
        Player author = playerService.getSelf();
        classService.uploadFile(file, author);
    }

}
