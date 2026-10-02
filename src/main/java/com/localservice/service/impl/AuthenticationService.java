package com.localservice.service.impl;

import org.springframework.transaction.annotation.Transactional;
import com.localservice.config.JwtService;
import com.localservice.dto.AuthResponse;
import com.localservice.dto.LoginRequest;
import com.localservice.dto.ProviderRegistrationRequest;
import com.localservice.dto.RefreshTokenRequest;
import com.localservice.dto.RegisterRequest;
import com.localservice.entity.RefreshToken;
import com.localservice.entity.Role;
import com.localservice.entity.User;
import com.localservice.repository.RefreshTokenRepository;
import com.localservice.repository.UserRepository;
import com.localservice.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository,
                                 RefreshTokenRepository refreshTokenRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<ApiResponse<AuthResponse>> registerCustomer(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername()) || userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("error", "Username or email already exists"));
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.PROVIDER);
        user.setEnabled(true);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", "Customer registered"));
    }

    public ResponseEntity<ApiResponse<AuthResponse>> registerProvider(ProviderRegistrationRequest req) {
        if (userRepository.existsByUsername(req.getUsername()) || userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("error", "Username or email already exists"));
        }

        if (req.getPassword() == null || req.getPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("error", "Password must be at least 6 characters long"));
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.PROVIDER);
        user.setEnabled(true);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", "Provider registered successfully. Awaiting admin approval.", null));
    }

    public ResponseEntity<ApiResponse<AuthResponse>> login(LoginRequest req, Role requiredRole) {
        Optional<User> opt = userRepository.findByUsername(req.getUsername());
        if (opt.isEmpty() || !passwordEncoder.matches(req.getPassword(), opt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>("error", "Invalid credentials"));
        }

        User user = opt.get();
        if (requiredRole != null && user.getRole() != requiredRole) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>("error", "Access denied for this role"));
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        String access = jwtService.generateAccessToken(user.getUsername(), user.getRole());
        String refresh = jwtService.generateRefreshToken(user.getUsername());

        RefreshToken refreshToken = new RefreshToken(refresh, user, jwtService.getExpiration(refresh));
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.save(refreshToken);

        AuthResponse authResponse = new AuthResponse(access, refresh);
        return ResponseEntity.ok(new ApiResponse<>("success", "Logged in", authResponse));
    }

    public ResponseEntity<ApiResponse<AuthResponse>> login(LoginRequest req) {
        return login(req, null);
    }

    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(RefreshTokenRequest req) {
        String token = req.getRefreshToken();
        if (token == null || token.isBlank() || !jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error", "Invalid refresh token"));
        }

        var claims = jwtService.parseClaims(token);
        String username = claims.getSubject();

        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error", "User not found"));
        }

        User user = opt.get();
        Optional<RefreshToken> stored = refreshTokenRepository.findByToken(token);
        if (stored.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("error", "Refresh token not recognized"));
        }

        String access = jwtService.generateAccessToken(username, user.getRole());
        String refresh = jwtService.generateRefreshToken(username);

        refreshTokenRepository.deleteByUser(user);
        RefreshToken newRt = new RefreshToken(refresh, user, jwtService.getExpiration(refresh));
        refreshTokenRepository.save(newRt);

        AuthResponse authResponse = new AuthResponse(access, refresh);
        return ResponseEntity.ok(new ApiResponse<>("success", "Token refreshed", authResponse));
    }
    
    @Transactional
    public ResponseEntity<ApiResponse<Object>> logout(String refreshToken) {
        Optional<RefreshToken> rt = refreshTokenRepository.findByToken(refreshToken);
        if (rt.isPresent()) {
            refreshTokenRepository.delete(rt.get());
        }
        return ResponseEntity.ok(new ApiResponse<>("success", "Logged out"));
    }
}
