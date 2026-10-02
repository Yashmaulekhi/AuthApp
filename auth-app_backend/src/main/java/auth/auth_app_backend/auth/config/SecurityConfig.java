package auth.auth_app_backend.auth.config;

import auth.auth_app_backend.dtos.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
@EnableMethodSecurity(prePostEnabled = false)
public class SecurityConfig {

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private AuthenticationSuccessHandler handler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //    @Bean
//    public UserDetailsService users() {
//
//        UserDetails student1 = User.builder()
//                .username("ankit")
//                .password(passwordEncoder().encode("12345"))
//                .roles("STUDENT")
//                .build();
//
//        UserDetails student2 = User.builder()
//                .username("rahul")
//                .password(passwordEncoder().encode("12345"))
//                .roles("STUDENT")
//                .build();
//
//        return new InMemoryUserDetailsManager(student1, student2);
//
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ApiConstants.AUTH_PUBLIC_URLS).permitAll()
                        .requestMatchers(ApiConstants.AUTH_ADMIN_URLS).hasRole(ApiConstants.ADMIN_ROLE)
                        .requestMatchers(ApiConstants.AUTH_GUEST_URLS).hasRole(ApiConstants.GUEST_ROLE)
                        .anyRequest().authenticated()
                )
                

                .oauth2Login(oauth2 -> oauth2
                        .successHandler(handler)
                )

                .logout(AbstractHttpConfigurer::disable)

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(authenticationEntryPoint())
                )

                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {

        return (HttpServletRequest request,
                HttpServletResponse response,
                org.springframework.security.core.AuthenticationException ex) -> {

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");

            String message = "Unauthorized access";

            String error = (String) request.getAttribute("error");
            if (error != null) {
                message = error;
            } else if (ex.getMessage() != null) {
                message = ex.getMessage();
            }

            ApiError apiError = ApiError.of(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Unauthorized",
                    message,
                    request.getRequestURI(),
                    true
            );

            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(apiError));
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }
}