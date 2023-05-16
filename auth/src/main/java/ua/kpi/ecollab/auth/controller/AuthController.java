package ua.kpi.ecollab.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ua.kpi.ecollab.auth.service.AuthService;

import java.util.Map;

@RestController
public class AuthController {

  private final AuthService authService;

  @Autowired
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @ResponseBody
  @PostMapping(
      value = "/api/auth/login",
      consumes = "application/json",
      produces = "application/json")
  public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
    System.out.println("Accepted request");
    if (authService.checkLoginAndUsername(
        credentials.get("username"), credentials.get("password"))) {
      return new ResponseEntity<>(Map.of(), HttpStatus.UNAUTHORIZED);
    }
    String token = authService.generateToken(credentials.get("username"));
    return new ResponseEntity<>(
        Map.of(
            "token",
            token,
            "username",
            authService.getNameDetails(credentials.get("username")),
            "id",
            String.valueOf(authService.getUserId(credentials.get("username")))),
        HttpStatus.OK);
  }
}
