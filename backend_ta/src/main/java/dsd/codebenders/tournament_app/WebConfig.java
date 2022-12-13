package dsd.codebenders.tournament_app;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
public class WebConfig implements WebMvcConfigurer {

    private static String hoppscotchChrome = "chrome-extension://amknoiejhlmhancpahfcfcfhllgkpbld/";
    private static String hoppscotchFirefox = "moz-extension://5141c5d2-7d79-464c-be62-c6fdeb9aa105";
    @Value("${tournament-app.web-server.address:http://localhost:3000}")
    private String webServerAddress;
    @Value("${tournament-app.admin.password:admin}")
    private String adminPassword;
    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUsername;
    @Value("${spring.datasource.password}")
    private String dbPassword;
    @Value("${code-defenders.default-servers.token:}")
    private String defaultServersToken;
    @Value("${code-defenders.default-game-class.file-name:SimpleExample.java}")
    private String gameClassFileName;

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            String encodedPassword = encoder().encode(adminPassword);
            String gameClass = null;
            URL resource = WebConfig.class.getResource("/game_classes/" + gameClassFileName);
            try {
                gameClass = Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
            } catch (IOException | URISyntaxException | NullPointerException e) {
                System.err.println("ERROR: Unable to read default game class");
            }
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("admin.password", encodedPassword);
            placeholders.put("game-class.file-name", gameClassFileName);
            placeholders.put("game-class.data", gameClass);
            placeholders.put("default-servers.token", defaultServersToken);
            placeholders.put("default-servers.active", defaultServersToken.isEmpty() ? "0" : "1");
            Flyway myFlyway = Flyway.configure().dataSource(dbUrl, dbUsername, dbPassword).placeholders(placeholders).load();
            myFlyway.migrate();
        };
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins("http://localhost", webServerAddress, hoppscotchChrome, hoppscotchFirefox);
    }
}
