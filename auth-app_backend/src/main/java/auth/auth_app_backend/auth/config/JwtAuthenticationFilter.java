package auth.auth_app_backend.auth.config;

import auth.auth_app_backend.auth.helpers.UserHelper;
import auth.auth_app_backend.auth.repositories.UserRepository;
import auth.auth_app_backend.auth.services.impl.JwtService;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            // token extract and validate then authorization create and then security context ke andar set karunga
            String token = header.substring(7);

            try {

            if (!jwtService.isAccessTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }
                Jws<Claims> jws = jwtService.parseToken(token);
                Claims payload = jws.getPayload();
                String userId = payload.getSubject();
                UUID userUuid = UserHelper.parseUUID(userId);

                userRepository.findById(userUuid).ifPresent(user -> {
                    if (user.isEnabled()) {
                        List<GrantedAuthority> authorities =
                                user.getRole() == null
                                        ? List.of()
                                        : user.getRole().stream()
                                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                                        .collect(Collectors.toUnmodifiableList());

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        if (SecurityContextHolder.getContext().getAuthentication() == null) {
                            // final authentication to security
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    } // closes if(user.isEnabled())
                }); // closes lambda body + ifPresent(...)


            }catch(ExpiredJwtException e) {
                request.setAttribute("error","Token expired");

               // e.printStackTrace();
            }

            catch (JwtException e) {
                request.setAttribute("error","Invalid Token");
               // e.printStackTrace();
            } catch (Exception e) {
                request.setAttribute("error","Invalid Token");
               // e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);}
        @Override
        protected boolean shouldNotFilter(HttpServletRequest request)
        throws ServletException {
            return request.getRequestURI().startsWith("/api/auth/login");
        }
        }


