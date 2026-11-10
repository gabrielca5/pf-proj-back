package br.insper.pf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

// Todas as rotas exigem token do Auth0.
// Quem pode o que fica no @PreAuthorize de cada rota do controller.
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // Claims que a Action do Auth0 coloca no token (ver README)
    public static final String CLAIM_ROLES = "https://pf/roles";
    public static final String CLAIM_EMAIL = "https://pf/email";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(rolesDoToken())));
        return http.build();
    }

    // Transforma ["ADMIN"] do token em ROLE_ADMIN, que e o que o hasRole('ADMIN') procura
    private JwtAuthenticationConverter rolesDoToken() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName(CLAIM_ROLES);
        roles.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }
}
