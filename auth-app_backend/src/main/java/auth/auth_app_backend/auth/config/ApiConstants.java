package auth.auth_app_backend.auth.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConstants {

    public static final String[] AUTH_PUBLIC_URLS = {
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/auth/**",

            "/oauth2/**",
            "/login/**",
            "/error"
    };
    public static final String[] AUTH_ADMIN_URLS= {
            "/users/**"
    };

    public static final String[] AUTH_GUEST_URLS= {

    };

    public static final String ADMIN_ROLE = "ADMIN";
    public static final String GUEST_ROLE = "GUEST";
}
