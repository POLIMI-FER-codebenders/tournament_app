package dsd.codebenders.tournament_app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailsService playerDetailsService;
    private final JsonAuthenticationFailureHandler jsonAuthenticationFailureHandler;
    private final JsonAuthenticationSuccessHandler jsonAuthenticationSuccessHandler;
    @Value("${request-debug:false}")
    private Boolean debug;

    @Autowired
    public WebSecurityConfig(UserDetailsService userDetailsService, JsonAuthenticationFailureHandler jsonAuthenticationFailureHandler, JsonAuthenticationSuccessHandler jsonAuthenticationSuccessHandler) {
        this.playerDetailsService = userDetailsService;
        this.jsonAuthenticationFailureHandler = jsonAuthenticationFailureHandler;
        this.jsonAuthenticationSuccessHandler = jsonAuthenticationSuccessHandler;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(playerDetailsService);
        provider.setPasswordEncoder(encoder());
        return provider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return playerDetailsService;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeHttpRequests((requests) -> {
                            if (debug) {
                                requests
                                        .anyRequest().permitAll();
                            } else {
                                requests
                                        .antMatchers("/authentication/register", "/authentication/failure").permitAll()
                                        .antMatchers("/api/tournament/list").permitAll()
                                        .anyRequest().authenticated();
                            }
                        }
                )
                .formLogin((form) -> form
                        .loginPage("/authentication/error")
                        .loginProcessingUrl("/authentication/login")
                        .permitAll()
                        .successHandler(jsonAuthenticationSuccessHandler)
                        .failureHandler(jsonAuthenticationFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/authentication/logout")
                        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK))
                        .invalidateHttpSession(true).permitAll()
                );

        return http.build();
    }

}
