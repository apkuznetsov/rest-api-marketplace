package kuznetsov.marketplace.services.jwt;

public interface JwtService {

  String generateAccessToken(String email, String role);

  boolean validateAccessToken(String token);

  String getEmailFromAccessToken(String token);

  String getRoleFromAccessToken(String token);

}
