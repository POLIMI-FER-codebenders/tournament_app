package dsd.codebenders.tournament_app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailsService playerDetailsService;
    private final JsonAuthenticationFailureHandler jsonAuthenticationFailureHandler;

    @Autowired
    public WebSecurityConfig(UserDetailsService userDetailsService, JsonAuthenticationFailureHandler jsonAuthenticationFailureHandler) {
        this.playerDetailsService = userDetailsService;
        this.jsonAuthenticationFailureHandler = jsonAuthenticationFailureHandler;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(playerDetailsService);
        return provider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return playerDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeHttpRequests((requests) -> requests
                        .antMatchers("/authentication/register").permitAll()
                        .antMatchers("/authentication/failure").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/authentication/error")
                        .loginProcessingUrl("/authentication/login")
                        .permitAll()
                        .defaultSuccessUrl("/authentication/success")
                        .failureHandler(jsonAuthenticationFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout.invalidateHttpSession(true).permitAll());

        return http.build();
    }

}
