package com.example.studentms.web;

import com.example.studentms.domain.Role;
import com.example.studentms.domain.User;
import com.example.studentms.repository.RoleRepository;
import com.example.studentms.repository.UserRepository;
import com.example.studentms.service.JwtService;
import com.example.studentms.web.dto.LoginRequest;
import com.example.studentms.web.dto.LoginResponse;
import com.example.studentms.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest req) {
    if (userRepository.findByUsername(req.getUsername()).isPresent()) {
      return ResponseEntity.badRequest().body(java.util.Map.of("message", "Username already exists"));
    }
    Role defaultRole = roleRepository.findByName("STUDENT")
        .orElseGet(() -> roleRepository.save(Role.builder().name("STUDENT").build()));
    Set<Role> roles = new HashSet<>();
    roles.add(defaultRole);
    User user = User.builder()
        .username(req.getUsername())
        .password(passwordEncoder.encode(req.getPassword()))
        .fullName(req.getFullName())
        .age(req.getAge())
        .gender(req.getGender())
        .roles(roles)
        .enabled(true)
        .build();
    User saved = userRepository.save(user);
    return ResponseEntity.created(URI.create("/api/users/" + saved.getId())).build();
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
    try {
      var authToken = new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
      authenticationManager.authenticate(authToken);
    } catch (BadCredentialsException ex) {
      return ResponseEntity.status(401).build();
    }

    User user = userRepository.findByUsername(req.getUsername()).orElseThrow();
    var userDetails = org.springframework.security.core.userdetails.User
        .withUsername(user.getUsername())
        .password(user.getPassword())
        .disabled(!user.isEnabled())
        .authorities(user.getRoles().stream().map(r -> "ROLE_" + r.getName()).toArray(String[]::new))
        .build();
    String token = jwtService.generateToken(userDetails);
    return ResponseEntity.ok(new LoginResponse(token));
  }
}
