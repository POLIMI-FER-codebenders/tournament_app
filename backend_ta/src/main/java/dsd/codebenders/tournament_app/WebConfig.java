package dsd.codebenders.tournament_app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
public class WebConfig implements WebMvcConfigurer {

    private static String hoppscotchChrome = "chrome-extension://amknoiejhlmhancpahfcfcfhllgkpbld/";
    private static String hoppscotchFirefox = "moz-extension://5141c5d2-7d79-464c-be62-c6fdeb9aa105";
    @Value("${tournament-app.web-server.address:http://localhost:3000}")
    private String webServerAddress;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins("http://localhost", webServerAddress, hoppscotchChrome, hoppscotchFirefox);
    }
}
