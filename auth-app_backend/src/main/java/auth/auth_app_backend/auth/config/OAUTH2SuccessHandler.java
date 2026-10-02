package auth.auth_app_backend.auth.config;

import auth.auth_app_backend.auth.entities.Provider;
import auth.auth_app_backend.auth.entities.RefreshToken;
import auth.auth_app_backend.auth.entities.User;
import auth.auth_app_backend.auth.repositories.RefreshTokenRepository;
import auth.auth_app_backend.auth.repositories.UserRepository;

import auth.auth_app_backend.auth.services.impl.CookieService;
import auth.auth_app_backend.auth.services.impl.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAUTH2SuccessHandler implements AuthenticationSuccessHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.auth.frontend.success-redirect}")
    private String formSuccessUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        logger.info("Successful authentication");
        logger.info(authentication.toString());

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // identify user
        String registrationId = "unknown";

        if (authentication instanceof OAuth2AuthenticationToken token) {
            registrationId = token.getAuthorizedClientRegistrationId();
        }

        logger.info("registrationId: {}", registrationId);
        logger.info("user: {}", oAuth2User.getAttributes());

        User user;

        switch (registrationId) {

            case "google" -> {

                String googleId = oAuth2User.getAttribute("sub");
                String email = oAuth2User.getAttribute("email");
                String name = oAuth2User.getAttribute("name");
                String picture = oAuth2User.getAttribute("picture");

                User userNew = User.builder()
                        .name(name)
                        .email(email)
                        .providerUserId(googleId)
                        .image(picture)
                        .provider(Provider.Google)
                        .build();

                user=userRepository.findByEmail(email).orElseGet(
                        ()->
                            userRepository.save(userNew));


            }
            case "github"->{
                String name=oAuth2User.getAttributes().getOrDefault("login","").toString();
                String githubId=oAuth2User.getAttributes().getOrDefault("id","").toString();
                String image=oAuth2User.getAttributes().getOrDefault("avatar_url","").toString();
                String email=oAuth2User.getAttributes().get("email").toString();
                if(email==null){
                    email=name+"@github.com";
                }
                User userNew = User.builder()
                        .name(name)
                        .email(email)
                        .providerUserId(githubId)
                        .image(image)
                        .provider(Provider.Github)
                        .build();
                user=userRepository.findByEmail(email).orElseGet(
                        ()->
                            userRepository.save(userNew)


                );
            }


            default -> throw new RuntimeException("Invalid registration id");
        }


        //ussername
        //user email
        //new usercreate
        //jwt token token ke sath front __pe fir redirect

        //refresh tokenbsns bana kr dunga
        String jti=UUID.randomUUID().toString();
        RefreshToken refreshToken=RefreshToken.builder()
                .jti(jti)
                .user(user)
                .revoked(false)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .build();
        refreshTokenRepository.save(refreshToken);
        String accessToken= jwtService.generateAccessToken(user);
        String refreshTokens= jwtService.generateRefreshToken(user,refreshToken.getJti());

        cookieService.attachRefreshCookie(
                response,
                refreshTokens,
                (int) jwtService.getRefreshTtlSeconds()
        );



        response.sendRedirect(formSuccessUrl);
    }
}