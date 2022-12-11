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

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
public class WebConfig implements WebMvcConfigurer {

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


    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            String encodedPassword = encoder().encode(adminPassword);
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("admin.password", encodedPassword);
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
                .allowedOrigins("http://localhost", webServerAddress);
    }
}
