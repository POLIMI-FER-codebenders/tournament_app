package dsd.codebenders.tournament_app.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import dsd.codebenders.tournament_app.dao.GameClassRepository;
import dsd.codebenders.tournament_app.entities.CDGameClass;
import dsd.codebenders.tournament_app.entities.GameClass;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.errors.BadRequestException;
import dsd.codebenders.tournament_app.errors.CDServerUnreachableException;
import dsd.codebenders.tournament_app.requests.CDClassUploadRequest;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ClassService {

    private final GameClassRepository gameClassRepository;
    private final ServerService serverService;
    private final CDGameClassService cdGameClassService;

    @Autowired
    public ClassService(GameClassRepository gameClassRepository, ServerService serverService, CDGameClassService cdGameClassService) {
        this.gameClassRepository = gameClassRepository;
        this.serverService = serverService;
        this.cdGameClassService = cdGameClassService;
    }


    public GameClass uploadClass(MultipartFile file, Player author) {
        String filename = file.getOriginalFilename();

        if(gameClassRepository.existsByFilename(filename)){
            throw new BadRequestException("A class with the same filename has been already uploaded, choose another one.");
        }

        GameClass gameClass = new GameClass();
        gameClass.setFilename(filename);
        gameClass.setAuthor(author);
        try {
            gameClass.setData(file.getBytes());
        } catch (IOException e) {
            throw new BadRequestException("Cannot read contents of multipart file");
        }

        // Check by calling a CD API whether the class compiles and can be used on CD
        CDGameClass cdGameClass;

        // Get the least loaded server
        Server server;
        try {
            server = serverService.getCDServer();
        } catch (CDServerUnreachableException e) {
            throw new BadRequestException("Unable to upload class. " + e.getMessage());
        }

        try {
            String source = new String(gameClass.getData(), StandardCharsets.UTF_8);
            CDClassUploadRequest body = new CDClassUploadRequest(gameClass.getFilename(), source);
            cdGameClass = HTTPRequestsSender.sendPostRequest(server, "/admin/api/class/upload", body, CDGameClass.class);
        } catch (HttpClientErrorException e) {
            throw new BadRequestException("Unable to upload class. " + e.getMessage());
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Unable to upload class. " + e.getMessage());
        }
        gameClassRepository.save(gameClass);

        cdGameClass.setRealClass(gameClass);
        cdGameClass.setServer(server);
        cdGameClassService.addNewCDGameClass(cdGameClass);

        return gameClass;
    }

    public List<GameClass> getAllClasses() {
        return gameClassRepository.findAll();
    }
}
