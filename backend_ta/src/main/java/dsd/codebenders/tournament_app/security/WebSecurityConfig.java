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

    private final UserDetailsService userDetailsService;

    @Autowired
    public WebSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                /*.authorizeHttpRequests((requests) -> requests
                        .antMatchers("/authentication/register").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginProcessingUrl("/authentication/login")
                        .permitAll()
                        .defaultSuccessUrl("/authentication/success")
                        .failureUrl("/authentication/failure")
                        .permitAll()
                )
                .logout(logout -> logout.invalidateHttpSession(true).permitAll())*/;

        return http.build();
    }

}
