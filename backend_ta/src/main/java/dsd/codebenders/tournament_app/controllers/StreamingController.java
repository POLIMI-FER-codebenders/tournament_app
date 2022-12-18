package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.errors.BadRequestException;
import dsd.codebenders.tournament_app.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/streaming")
public class StreamingController {

    private final MatchService matchService;

    @Autowired
    public StreamingController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping(value = "/score")
    public Map<String, Integer> getStreamingScore(Long matchId){
        Map<String, Integer> map = new HashMap<>();
        Match match;
        if(matchService.findById(matchId).isPresent()) {
            match = matchService.findById(matchId).get();
        } else {
            throw new BadRequestException("Requested match not found");
        }
        map.put("attackersScore", match.getStreamedAttackersScore());
        map.put("defendersScore", match.getStreamedDefendersScore());
        return map;
    }

}
