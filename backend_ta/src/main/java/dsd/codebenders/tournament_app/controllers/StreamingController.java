package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.errors.BadRequestException;
import dsd.codebenders.tournament_app.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public Map<String, String> getStreamingScore(@RequestParam(name = "matchId") Long matchId){
        Map<String, String> map = new HashMap<>();
        Match match;
        if(matchId == null) {
            throw new BadRequestException("Match id is missing");
        }
        if(matchService.findById(matchId).isPresent()) {
            match = matchService.findById(matchId).get();
        } else {
            throw new BadRequestException("Requested match not found");
        }
        map.put("attackersScore", match.getStreamedAttackersScore().toString());
        map.put("defendersScore", match.getStreamedDefendersScore().toString());
        map.put("status",match.getStatus().toString());
        return map;
    }

}
