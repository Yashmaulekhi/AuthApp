package auth.auth_app_backend.auth.services.impl;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    private static final Logger logger = LoggerFactory.getLogger(CookieService.class);

    private final String refreshTokenCookieName;
    private final boolean cookieHttpOnly;
    private final boolean cookieSecure;
    private final String cookieDomain;
    private final String cookieSameSite;

    public CookieService(
            @Value("${jwt.refresh-token-cookie-name}") String refreshTokenCookieName,
            @Value("${jwt.cookie-http-only}") boolean cookieHttpOnly,
            @Value("${jwt.cookie-secure}") boolean cookieSecure,
            @Value("${jwt.cookie-domain}") String cookieDomain,
            @Value("${jwt.cookie-same-site}") String cookieSameSite
    ) {
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.cookieHttpOnly = cookieHttpOnly;
        this.cookieSecure = cookieSecure;
        this.cookieDomain = cookieDomain;
        this.cookieSameSite = cookieSameSite;
    }

    public String getRefreshTokenCookieName() {
        return refreshTokenCookieName;
    }

    // Attach refresh token cookie
    public void attachRefreshCookie(HttpServletResponse response,
                                    String value,
                                    int maxAge) {

        logger.info("Attaching refresh token cookie.");

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
                .from(refreshTokenCookieName, value)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .sameSite(cookieSameSite)
                .maxAge(maxAge);

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            builder.domain(cookieDomain);
        }

        ResponseCookie cookie = builder.build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // Clear refresh token cookie
    public void clearRefreshToken(HttpServletResponse response) {

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
                .from(refreshTokenCookieName, "")
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .sameSite(cookieSameSite)
                .maxAge(0);

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            builder.domain(cookieDomain);
        }

        ResponseCookie cookie = builder.build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // Prevent browser caching
    public void addNoStoreHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}