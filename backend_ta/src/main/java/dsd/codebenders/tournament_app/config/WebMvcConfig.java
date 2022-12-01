package dsd.codebenders.tournament_app.config;

import dsd.codebenders.tournament_app.interceptors.SpoofingInterceptor;
import dsd.codebenders.tournament_app.services.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final static Logger log = LoggerFactory.getLogger(WebMvcConfig.class);
    private final PlayerService playerService;
    @Value("${request-debug:false}")
    private boolean debug;

    @Autowired
    public WebMvcConfig(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (debug) {
            registry.addInterceptor(new SpoofingInterceptor(playerService));
            //log.info("Added interceptor");
        }
    }
}