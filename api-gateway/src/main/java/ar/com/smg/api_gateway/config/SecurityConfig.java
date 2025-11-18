package ar.com.smg.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                    // Permitir acceso a Eureka y actuator sin autenticación
                    .pathMatchers("/eureka/**").permitAll()
                    .pathMatchers("/actuator/**").permitAll()

                    // Endpoints públicos (si los tienes)
                    .pathMatchers("/api/public/**").permitAll()

                    // Rutas específicas con roles
                    .pathMatchers("/api/members/**").hasAnyRole("USER", "ADMIN")
                    .pathMatchers("/api/claims/**").hasAnyRole("USER", "ADMIN")
                    .pathMatchers("/api/policies/**").hasAnyRole("USER", "ADMIN")
                    .pathMatchers("/api/admin/**").hasRole("ADMIN")

                    // Cualquier otra petición requiere autenticación
                    .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

    return http.build();
  }

  private ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
    ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
    return converter;

    return http.build();
  }
}
