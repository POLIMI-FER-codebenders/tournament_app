package dsd.codebenders.tournament_app.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CorsAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${tournament-app.web-server.address:http://localhost:3000}")
    private String webServerAddress;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String origin = request.getHeader("Origin");
        if(origin.equals("http://localhost") && webServerAddress.equals("http://localhost:80")) {
            response.setHeader("Access-Control-Allow-Origin", "http://localhost");
        } else {
            response.setHeader("Access-Control-Allow-Origin", webServerAddress);
        }
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.sendRedirect("/authentication/success");
    }
}
