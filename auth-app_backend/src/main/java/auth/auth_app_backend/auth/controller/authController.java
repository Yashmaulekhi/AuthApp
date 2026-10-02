package auth.auth_app_backend.auth.controller;

import auth.auth_app_backend.auth.payload.LoginRequest;
import auth.auth_app_backend.auth.payload.RefreshTokenRequest;
import auth.auth_app_backend.auth.payload.TokenResponse;
import auth.auth_app_backend.auth.payload.UserDto;
import auth.auth_app_backend.auth.entities.Provider;
import auth.auth_app_backend.auth.entities.RefreshToken;
import auth.auth_app_backend.auth.entities.User;
import auth.auth_app_backend.auth.repositories.RefreshTokenRepository;
import auth.auth_app_backend.auth.repositories.UserRepository;
import auth.auth_app_backend.auth.services.impl.CookieService;
import auth.auth_app_backend.auth.services.impl.JwtService;
import auth.auth_app_backend.auth.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.Arrays;
import jakarta.servlet.http.Cookie;



@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class authController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService
            jwtService;
    private final ModelMapper modelMapper;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieService cookieService;
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userDto));
    }


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        //authenticate
        Authentication authenticate = authenticate(loginRequest);
        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        if (!user.isEnabled()) {
            throw new DisabledException("User is Disabled");
        }
        //generate refresh token
        String jti= UUID.randomUUID().toString();
        var refreshTokenOb=RefreshToken.builder()
                .jti(jti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false).build();
        refreshTokenRepository.save(refreshTokenOb);
        //generate access token
        String accessToken=jwtService.generateAccessToken(user);
        String refreshToken =
                jwtService.generateRefreshToken(user, refreshTokenOb.getJti());

        //use cookie service to attach refresh token in cookie
        cookieService.attachRefreshCookie(
                response,
                refreshToken,
                (int) jwtService.getRefreshTtlSeconds()
        );
        cookieService.addNoStoreHeaders(response);

        TokenResponse tokenResponse = TokenResponse.of(
                accessToken,
                refreshToken,
                jwtService.getAccessTtlSeconds(),
                "Bearer",   // or "access", depending on your API design
                modelMapper.map(user, UserDto.class)
        );
        return ResponseEntity.ok(tokenResponse);
    }

    private Authentication authenticate(LoginRequest loginRequest) {
        try {
            User user = userRepository.findByEmail(loginRequest.email())
                    .orElseThrow(() ->
                            new BadCredentialsException("Invalid email or password"));

            if (user.getProvider() == Provider.Google) {
                throw new BadCredentialsException(
                        "This account was created using Google. Please sign in with Google."
                );
            }

            if (user.getProvider() == Provider.Github) {
                throw new BadCredentialsException(
                        "This account was created using GitHub. Please sign in with GitHub."
                );
            }
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        } catch (Exception e) {
            throw new BadCredentialsException("invalid username and password");
        }
    }
    //access and refresh token renew

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest body,
            HttpServletResponse response,
            HttpServletRequest request) {

        String refreshTokens = readRefreshTokenFromRequest(body, request)
                .orElseThrow(() -> new BadCredentialsException("Invalid Refresh Token"));

        if (!jwtService.isRefreshTokenValid(refreshTokens)) {
            throw new BadCredentialsException("Invalid Refresh token type");
        }

        String jti = jwtService.getJti(refreshTokens);
        UUID userId = jwtService.getUserId(refreshTokens);

        RefreshToken storedRefreshToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new BadCredentialsException("Refresh token not recognized"));

        if (storedRefreshToken.isRevoked()) {
            throw new BadCredentialsException("Refresh token expired or revoked");
        }

        if (storedRefreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("Refresh token expired");
        }

        // Get user from stored refresh token
        User user = storedRefreshToken.getUser();

        // Generate new access token
        String accessToken = jwtService.generateAccessToken(user);

        TokenResponse tokenResponse = TokenResponse.of(
                accessToken,
                refreshTokens,
                jwtService.getAccessTtlSeconds(),
                "Bearer",
                modelMapper.map(user, UserDto.class)
        );

        return ResponseEntity.ok(tokenResponse);
    }
    //thismethod will read refresh token from request header or body
    private Optional<String> readRefreshTokenFromRequest(
            RefreshTokenRequest body,
            HttpServletRequest request) {


            // 1. Read from cookie
            if (request.getCookies() != null) {

                Optional<String> fromCookie =
                        Arrays.stream(request.getCookies())
                                .filter(cookie ->
                                        cookieService.getRefreshTokenCookieName()
                                                .equals(cookie.getName()))
                                .map(Cookie::getValue)
                                .filter(v -> !v.isBlank())
                                .findFirst();

                if (fromCookie.isPresent()) {
                    return fromCookie;
                }
            }

            // 2. Read from request body
            if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
                return Optional.of(body.refreshToken());
            }
            String refreshHeader= request.getHeader("X-Refresh-Token");
            if(refreshHeader != null && !refreshHeader.isBlank()){
                return Optional.of(refreshHeader.trim());
            }
            return Optional.empty();
        }
        // logic to read from body or cookie
    }



