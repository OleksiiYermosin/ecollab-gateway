package ua.kpi.ecollab.auth.service;

import io.jsonwebtoken.*;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.kpi.ecollab.auth.repository.UserRepository;

@Service
public class AuthService {
  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.token.validity}")
  private long tokenValidity;

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
  }

  public boolean checkLoginAndUsername(String username, String password) {
    return userRepository
        .getUserEntityByUsername(username)
        .getPassword()
        .equals(passwordEncoder.encode(password));
  }

  public long getUserId(String username) {
    return userRepository
            .getUserEntityByUsername(username)
            .getId();
  }

  public String getNameDetails(String username) {
    return userRepository
            .getUserEntityByUsername(username)
            .getName();
  }

  public String generateToken(String username) {
    Claims claims = Jwts.claims().setSubject(username);
    long nowMillis = System.currentTimeMillis();
    long expMillis = nowMillis + tokenValidity;
    Date exp = new Date(expMillis);
    return Jwts.builder()
        .claim("Username", username)
        .claim("Id", userRepository.getUserEntityByUsername(username).getId())
        .setClaims(claims)
        .setIssuedAt(new Date(nowMillis))
        .setExpiration(exp)
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }
}
