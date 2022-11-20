package kuznetsov.marketplace.services.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import kuznetsov.marketplace.services.auth.dto.UserCreateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtProviderDefault implements JwtProvider {

  private final String accessSecret;
  private final Duration expirationAccessTime;

  public JwtProviderDefault(
      @Value("${auth.jwt-access-secret}")
      String accessSecretKey,
      @Value("${auth.jwt-access-expiration-time}")
      Duration expirationAccessTime) {

    this.accessSecret = Base64.getEncoder()
        .encodeToString(
            accessSecretKey.getBytes(StandardCharsets.UTF_8)
        );
    this.expirationAccessTime = expirationAccessTime;
  }

  public String createAccessToken(UserCreateDto authDto) {
    Claims claims = Jwts.claims().setSubject(authDto.getEmail());
    claims.put("role", authDto.getRole());

    Instant issuedAt = Instant.now();
    Instant expireAt = issuedAt.plus(this.expirationAccessTime);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(Date.from(issuedAt))
        .setExpiration(Date.from(expireAt))
        .signWith(SignatureAlgorithm.HS256, accessSecret)
        .compact();
  }

  public boolean validateAccessToken(String token) {
    try {
      Jws<Claims> claimsJws = Jwts.parser()
          .setSigningKey(this.accessSecret)
          .parseClaimsJws(token);

      return !claimsJws
          .getBody()
          .getExpiration()
          .before(new Date());

    } catch (ExpiredJwtException e) {
      log.error("Token Expired Exception", e);
    } catch (UnsupportedJwtException e) {
      log.error("Unsupported Jwt Exception", e);
    } catch (MalformedJwtException e) {
      log.error("Malformed Jwt Exception", e);
    } catch (SignatureException e) {
      log.error("Invalid Signature Exception", e);
    } catch (Exception e) {
      log.error("Invalid Token Exception", e);
    }

    return false;
  }

  public String getEmailFromAccessToken(String token) {
    return Jwts.parser()
        .setSigningKey(accessSecret)
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public String getRoleFromAccessToken(String token) {
    return (String) Jwts.parser()
        .setSigningKey(accessSecret)
        .parseClaimsJws(token)
        .getBody()
        .get("role");
  }

}