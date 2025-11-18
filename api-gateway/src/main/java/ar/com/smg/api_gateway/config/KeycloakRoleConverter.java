package ar.com.smg.api_gateway.config;

import org.springframework.cglib.core.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Convierte los roles de Keycloak en authorities de Spring Security
 * Para API Gateway (reactivo)
 */
public class KeycloakRoleConverter implements Converter {

  @Override
  public Flux<GrantedAuthority> convert(Object obj, Class target, Object context) {
    // Convert Object to Jwt
    Jwt jwt = (Jwt) obj;

    Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
    return Flux.fromIterable(authorities);
  }

  private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    // Extraer roles del realm
    Map<String, Object> realmAccess = jwt.getClaim("realm_access");

    if (realmAccess == null || !realmAccess.containsKey("roles")) {
      return List.of();
    }

    @SuppressWarnings("unchecked")
    Collection<String> roles = (Collection<String>) realmAccess.get("roles");

    if (roles == null) {
      return List.of();
    }

    // Convertir roles a authorities con prefijo ROLE_
    return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
            .collect(Collectors.toList());
  }
}
