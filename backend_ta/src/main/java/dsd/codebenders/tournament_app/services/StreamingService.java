package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.entities.Match;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class StreamingService extends ThreadPoolTaskScheduler {

    public void addViewer(Match match) {
        // open web socket
        // increase number of users following match
    }

    public void removeViewer(Match match) {
        // decrease number of users following match
        // close the socket
    }

    // Called every X seconds
    public void getUpdates() {
        // get from DB all games currently being watched
        // for every game:
            // get updates for the game:
            // for every socket on that game:
                // set timers to send events to that socket (by calling schedule)
    }

}
