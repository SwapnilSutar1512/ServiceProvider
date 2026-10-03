package com.localservice.service.impl;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localservice.config.JwtService;
import com.localservice.dto.AuthResponse;
import com.localservice.dto.LoginRequest;
import com.localservice.dto.ProviderRegistrationRequest;
import com.localservice.dto.RefreshTokenRequest;
import com.localservice.dto.RegisterRequest;
import com.localservice.entity.Category;
import com.localservice.entity.RefreshToken;
import com.localservice.entity.Role;
import com.localservice.entity.ServiceProvider;
import com.localservice.entity.User;
import com.localservice.exception.ResourceNotFoundException;
import com.localservice.repository.CategoryRepository;
import com.localservice.repository.RefreshTokenRepository;
import com.localservice.repository.ServiceProviderRepository;
import com.localservice.repository.UserRepository;
import com.localservice.util.ApiResponse;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository,
                                 RefreshTokenRepository refreshTokenRepository,
                                 ServiceProviderRepository serviceProviderRepository,
                                 CategoryRepository categoryRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.categoryRepository = categoryRepository;
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
        user.setRole(Role.CUSTOMER);
        user.setEnabled(true);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", "Customer registered"));
    }

    @Transactional
    public ResponseEntity<ApiResponse<AuthResponse>> registerProvider(ProviderRegistrationRequest req) {
        if (userRepository.existsByUsername(req.getUsername()) || userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("error", "Username or email already exists"));
        }

        if (serviceProviderRepository.findByEmailIgnoreCase(req.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("error", "A provider profile already exists for this email"));
        }

        if (req.getPassword() == null || req.getPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("error", "Password must be at least 6 characters long"));
        }

        if ((req.getLatitude() == null) != (req.getLongitude() == null)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("error", "Latitude and longitude must be provided together"));
        }

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected category not found"));

        ServiceProvider provider = new ServiceProvider();
        provider.setFullName(req.getName());
        provider.setBusinessName(req.getBusinessName());
        provider.setPhoneNumber(req.getMobile());
        provider.setEmail(req.getEmail());
        provider.setExperience(req.getExperience() == null ? 0 : req.getExperience());
        provider.setStreet(req.getStreet());
        provider.setLocality(req.getLocality());
        provider.setCity(req.getCity());
        provider.setState(req.getState());
        provider.setPincode(req.getPincode());
        provider.setLatitude(req.getLatitude());
        provider.setLongitude(req.getLongitude());
        provider.setWorkingHours(req.getWorkingHours());
        provider.setCategory(category);
        provider.setApprovalStatus("PENDING");
        provider.setActive(false);
        provider.setRating(0.0);

        ServiceProvider savedProvider = serviceProviderRepository.save(provider);

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.PROVIDER);
        user.setEnabled(true);
        user.setServiceProvider(savedProvider);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", "Provider registration submitted successfully. Awaiting admin approval.", null));
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

        if (user.getRole() == Role.PROVIDER) {
            ServiceProvider provider = user.getServiceProvider();
            if (provider == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>("error", "Provider profile is not available yet."));
            }
            if (!"APPROVED".equalsIgnoreCase(provider.getApprovalStatus())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>("error", "Provider account is pending admin approval."));
            }
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
