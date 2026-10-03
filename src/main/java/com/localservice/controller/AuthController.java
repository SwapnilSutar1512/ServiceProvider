package com.localservice.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.localservice.config.JwtService;
import com.localservice.dto.AuthResponse;
import com.localservice.dto.LoginRequest;
import com.localservice.dto.ProviderRegistrationRequest;
import com.localservice.dto.RefreshTokenRequest;
import com.localservice.dto.RegisterRequest;
import com.localservice.entity.Role;
import com.localservice.entity.User;
import com.localservice.repository.UserRepository;
import com.localservice.service.impl.AuthenticationService;
import com.localservice.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping({"/api/auth", "/api/v1/auth"})
@Tag(name = "Authentication")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(AuthenticationService authenticationService, UserRepository userRepository, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/register/customer")
    public ResponseEntity<ApiResponse<AuthResponse>> registerCustomer(@RequestBody RegisterRequest req) {
        return authenticationService.registerCustomer(req);
    }

    @PostMapping("/register/provider")
    @Operation(summary = "Register a new service provider")
    public ResponseEntity<ApiResponse<AuthResponse>> registerProvider(@Valid @RequestBody ProviderRegistrationRequest req) {
        return authenticationService.registerProvider(req);
    }

    @PostMapping("/provider/login")
    @Operation(summary = "Provider login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginProvider(@RequestBody LoginRequest req) {
        return authenticationService.login(req, Role.PROVIDER);
    }

    @PostMapping("/admin/login")
    @Operation(summary = "Admin login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginAdmin(@RequestBody LoginRequest req) {
        return authenticationService.login(req, Role.ADMIN);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest req) {
        return authenticationService.login(req, null);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestBody RefreshTokenRequest req) {
        return authenticationService.refreshToken(req);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(@RequestBody RefreshTokenRequest req) {
        return authenticationService.logout(req.getRefreshToken());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(new ApiResponse<>("error", "Unauthorized"));
        }
        String username = auth.getName();
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse<>("error", "User not found"));
        }
        User user = opt.get();
        var profile = new UserProfileResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new ApiResponse<>("success", "User profile", profile));
    }

    public static class UserProfileResponse {
        private Long id;
        private String username;
        private String email;
        private String role;

        public UserProfileResponse(Long id, String username, String email, String role) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.role = role;
        }

        public Long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            return role;
        }
    }
}
