package dsd.codebenders.tournament_app.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dsd.codebenders.tournament_app.services.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

public class SpoofingInterceptor implements HandlerInterceptor {
    private final static Logger log = LoggerFactory.getLogger(HandlerInterceptor.class);
    private final PlayerService playerService;

    public SpoofingInterceptor(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //log.info("Filter executed");
        if (request.getParameter("playerID") != null && !request.getParameter("playerID").isEmpty()) {
            playerService.spoofID(Long.valueOf(request.getParameter("playerID")));
        } else {
            playerService.spoofID(0L);
        }
        return true;
    }
}