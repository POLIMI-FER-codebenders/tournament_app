package dsd.codebenders.tournament_app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailsService playerDetailsService;
    private final CorsAuthenticationFailureHandler corsAuthenticationFailureHandler;
    private final CorsAuthenticationSuccessHandler corsAuthenticationSuccessHandler;
    private final CorsLogoutSuccessHandler corsLogoutSuccessHandler;
    private final PasswordEncoder passwordEncoder;
    @Value("${request-debug:false}")
    private Boolean debug;

    @Autowired
    public WebSecurityConfig(UserDetailsService userDetailsService, CorsAuthenticationFailureHandler corsAuthenticationFailureHandler, CorsAuthenticationSuccessHandler corsAuthenticationSuccessHandler, CorsLogoutSuccessHandler corsLogoutSuccessHandler, PasswordEncoder passwordEncoder) {
        this.playerDetailsService = userDetailsService;
        this.corsAuthenticationFailureHandler = corsAuthenticationFailureHandler;
        this.corsAuthenticationSuccessHandler = corsAuthenticationSuccessHandler;
        this.corsLogoutSuccessHandler = corsLogoutSuccessHandler;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(playerDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
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
                .authorizeHttpRequests((requests) -> {
                            if (debug) {
                                requests
                                        .anyRequest().permitAll();
                            } else {
                                requests
                                        .antMatchers("/authentication/register", "/authentication/failure").permitAll()
                                        .antMatchers("/api/tournament/list").permitAll()
                                        .antMatchers("/watch").permitAll()
                                        .anyRequest().authenticated();
                            }
                        }
                )
                .formLogin((form) -> form
                        .loginPage("/authentication/error")
                        .loginProcessingUrl("/authentication/login")
                        .permitAll()
                        .successHandler(corsAuthenticationSuccessHandler)
                        .failureHandler(corsAuthenticationFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/authentication/logout")
                        .logoutSuccessHandler(corsLogoutSuccessHandler)
                        .invalidateHttpSession(true).permitAll()
                );

        return http.build();
    }

}
