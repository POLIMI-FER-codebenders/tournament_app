package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.GameClassRepository;
import dsd.codebenders.tournament_app.entities.GameClass;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.errors.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ClassService {

    private final GameClassRepository gameClassRepository;

    @Autowired
    public ClassService(GameClassRepository gameClassRepository) {
        this.gameClassRepository = gameClassRepository;
    }


    public GameClass uploadClass(MultipartFile file, Player author) {
        String filename = file.getOriginalFilename();

        if(gameClassRepository.existsByFilename(filename)){
            throw new BadRequestException("A class with the same filename has been already uploaded, choose another one.");
        }

        // Check by calling a CD API whether the class compiles and can be used on CD

        GameClass gameClass = new GameClass();
        gameClass.setFilename(filename);
        gameClass.setAuthor(author);
        try {
            gameClass.setData(file.getBytes());
        } catch (IOException e) {
            throw new BadRequestException("Cannot read contents of multipart file");
        }

        gameClassRepository.save(gameClass);
        return gameClass;
    }

    public List<GameClass> getAllClasses() {
        return gameClassRepository.findAll();
    }
}
