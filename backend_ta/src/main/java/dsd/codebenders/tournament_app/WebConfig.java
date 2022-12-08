package dsd.codebenders.tournament_app;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
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

@Configuration
@EnableWebMvc
@EnableTransactionManagement
public class WebConfig implements WebMvcConfigurer {

    @Value("${tournament-app.web-server.address:http://localhost:3000}")
    private String webServerAddress;
    @Value("${tournament-app.admin.password:admin}")
    private String adminPassword;
    private final Flyway flyway;

    @Autowired
    public WebConfig(Flyway flyway){
        this.flyway = flyway;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return myFlyway -> {
            String encodedPassword = encoder().encode(adminPassword);
            System.setProperty("spring.flyway.placeholders.admin.password", encodedPassword);
            flyway.migrate();
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
