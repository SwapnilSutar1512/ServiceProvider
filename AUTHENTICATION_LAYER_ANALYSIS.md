# ServiceLink Backend - Authentication Layer Analysis

## Executive Summary

Your ServiceLink backend implements a **JWT-based stateless authentication system** with role-based access control. This document breaks down the authentication architecture layer by layer.

---

## LAYER 1: Security Configuration Layer

### File: `SecurityConfig.java`

**Purpose**: Central security configuration for the entire application

**Configuration Details**:

1. **Password Encoding**: BCrypt (industry standard)
   - Automatically salts and hashes passwords
   - Slows down brute force attacks

2. **Session Management**: STATELESS (important!)
   - No server-side session storage
   - Each request must include authentication token
   - Enables horizontal scaling (stateless microservices)

3. **CSRF Protection**: DISABLED
   - Rationale: API-first design, tokens are in headers (safe from CSRF)
   - Not needed for stateless JWT-based APIs

4. **CORS**: ENABLED
   - Allows requests from other domains (needed for frontend)
   - Must configure allowed origins in production

5. **Exception Handlers**:
   - `JwtAuthenticationEntryPoint`: Handles 401 (unauthorized)
   - `JwtAccessDeniedHandler`: Handles 403 (forbidden)

6. **Authorization Rules**:
   ```
   /api/v1/auth/**          → Public (anyone can access)
   /api/v1/categories/**    → Public
   All other /api/v1/**     → Requires valid JWT token
   ```

7. **JWT Filter Chain**:
   - `JwtAuthenticationFilter` added before `UsernamePasswordAuthenticationFilter`
   - Intercepts every request
   - Extracts token from Authorization header
   - Validates token
   - Sets authentication in Spring Security context

---

## LAYER 2: JWT Token Management Layer

### File: `JwtService.java`

**Purpose**: Generate, validate, and parse JWT tokens

**Key Methods**:

1. **generateAccessToken(username, role)**
   - Creates 15-minute token
   - Includes username, role, expiration
   - Signed with HS256 algorithm
   - Used for API authentication

2. **generateRefreshToken(username)**
   - Creates 30-day token
   - Includes username, expiration
   - Stored in database for blacklisting on logout
   - Used only to generate new access tokens

3. **isTokenValid(token)**
   - Parses and validates token signature
   - Checks expiration
   - Returns true/false

4. **parseClaims(token)**
   - Extracts payload data (username, role, expiration)
   - Throws exception if invalid

**JWT Configuration File**: `JwtProperties.java`
```
Secret: Must be 256+ bits (set in application.properties)
Issuer: "ServiceLink"
Access Token: 15 minutes (900,000 ms)
Refresh Token: 30 days (2,592,000,000 ms)
Header: "Authorization"
Prefix: "Bearer "
```

---

## LAYER 3: Request Authentication Layer

### File: `JwtAuthenticationFilter.java`

**Purpose**: Intercepts HTTP requests and validates JWT tokens

**Flow**:

```
Incoming Request
    ↓
Extract Authorization header
    ↓
Check if header starts with "Bearer "
    ↓
Extract token (remove "Bearer " prefix)
    ↓
Validate token signature & expiration
    ↓
If VALID:
    ├─ Parse token to get username
    ├─ Load user details from database
    ├─ Create UsernamePasswordAuthenticationToken
    ├─ Set in SecurityContextHolder
    └─ Request continues with authentication
    
If INVALID or EXPIRED:
    ├─ Continue without authentication
    ├─ Later endpoint returns 401
    └─ Request blocked by @Secured annotation
```

---

## LAYER 4: Authentication Service Layer

### File: `AuthenticationService.java`

**Purpose**: Core authentication business logic

**Key Methods**:

### 1. registerCustomer(RegisterRequest)

```java
Logic:
1. Check if username OR email already exists
2. If yes, return 400 Bad Request
3. If no:
   - Create new User entity
   - Set role = ROLE_CUSTOMER
   - Encode password with BCrypt
   - Set enabled = true
   - Save to database
4. Return 201 Created
```

### 2. registerProvider(RegisterRequest)

```java
Same as registerCustomer, but:
- Set role = ROLE_SERVICE_PROVIDER
- Optionally stores fullName for service provider profile
```

### 3. login(LoginRequest)

```java
Logic:
1. Attempt authentication:
   - Use Spring AuthenticationManager
   - Verify username exists
   - Verify password matches (BCrypt comparison)
   - If fails, throw BadCredentialsException → 401

2. If authentication successful:
   - Get User from database
   - Generate Access Token (15 min)
   - Generate Refresh Token (30 days)
   
3. Store Refresh Token in database:
   - Delete any existing refresh token for user
   - Create RefreshToken entity with:
     * token value
     * user reference
     * expiration date
   - Save to database (for logout blacklisting)

4. Return AuthResponse with both tokens
```

### 4. refreshToken(RefreshTokenRequest)

```java
Logic:
1. Validate refresh token:
   - Check signature & expiration
   - If invalid, return 401

2. Parse token to get username

3. Verify user exists in database

4. Check if token exists in database:
   - Must match stored refresh token
   - If not, return 401

5. Generate new tokens:
   - New Access Token (15 min)
   - New Refresh Token (30 days)

6. Update database:
   - Delete old refresh token
   - Store new refresh token

7. Return AuthResponse with new tokens

Purpose: Extends login session
- Client calls when access token expires
- Avoids re-entering password
- 30-day session duration
```

### 5. logout(String refreshToken)

```java
Logic:
1. Find refresh token in database
2. If found, delete it
3. Return success (always, even if not found)

Purpose: Blacklist refresh token
- Prevents token reuse after logout
- User must login again to get new tokens
- Access token still valid until expiration (15 min)
```

---

## LAYER 5: REST Controller Layer

### File: `AuthController.java`

**Purpose**: HTTP endpoints for authentication operations

**Endpoints Summary**:

| Method | Endpoint | Auth Required | Purpose |
|--------|----------|---------------|---------|
| POST | /register/customer | No | Register as customer |
| POST | /register/provider | No | Register as service provider |
| POST | /login | No | Authenticate and get tokens |
| POST | /refresh-token | No | Get new access token |
| POST | /logout | No | Invalidate refresh token |
| GET | /me | YES | Get current user profile |

---

## LAYER 6: Data Persistence Layer

### User Entity (`User.java`)

```sql
TABLE users {
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('ROLE_ADMIN', 'ROLE_CUSTOMER', 'ROLE_SERVICE_PROVIDER') NOT NULL,
    enabled BOOLEAN DEFAULT true,
    accountNonLocked BOOLEAN DEFAULT true,
    service_provider_id BIGINT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (service_provider_id) REFERENCES service_providers(id)
}
```

### RefreshToken Entity

```sql
TABLE refresh_tokens {
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(500) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    expiryDate TIMESTAMP NOT NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
}
```

### Role Enum

```java
ROLE_ADMIN              // Full system access
ROLE_CUSTOMER           // Customer/consumer role
ROLE_SERVICE_PROVIDER   // Service provider role
```

---

## LAYER 7: Repositories Layer

### UserRepository

```java
Methods used:
- existsByUsername(String username)      → Check duplicate
- existsByEmail(String email)             → Check duplicate
- findByUsername(String username)        → Lookup for login
- save(User user)                        → Persist new user
```

### RefreshTokenRepository

```java
Methods used:
- findByToken(String token)              → Validate refresh token
- deleteByUser(User user)                → Logout (remove old tokens)
- save(RefreshToken token)               → Store new refresh token
- delete(RefreshToken token)             → Explicit logout
```

---

## AUTHENTICATION FLOW DIAGRAMS

### 1. REGISTRATION FLOW

```
┌─────────────────────────────────────────────────────────┐
│ User submits registration form                          │
│ (username, email, password, role)                       │
└────────────────────────┬────────────────────────────────┘
                         │
                         ▼
         ┌───────────────────────────────┐
         │ Check username/email exists   │
         └───────────┬───────────────────┘
                     │
         ┌───────────┴────────────┐
         │                        │
         ▼                        ▼
    EXISTS              DOES NOT EXIST
    │                        │
    ▼                        ▼
  Return 400         Create User Entity
  "Username or       ├─ Encode password (BCrypt)
   email exists"     ├─ Set role
                     ├─ Set enabled = true
                     └─ Save to DB
                             │
                             ▼
                     Return 201 Created
                     "User registered"
```

### 2. LOGIN FLOW

```
┌───────────────────────────────────────┐
│ User submits username + password      │
└────────────┬────────────────────────┘
             │
             ▼
    ┌────────────────────────┐
    │ AuthenticationManager  │
    │ - Validate username    │
    │ - Verify password      │
    └────────┬───────────────┘
             │
    ┌────────┴────────┐
    │                 │
    ▼                 ▼
  FAIL              SUCCESS
  │                 │
  ▼                 ▼
Return 401     Generate Tokens
"Invalid       ├─ Access Token (15 min)
 credentials"  ├─ Refresh Token (30 days)
               │
               ▼
         Store Refresh Token
         (Delete old ones)
               │
               ▼
         Return 200 OK
         {
           accessToken,
           refreshToken
         }
```

### 3. PROTECTED REQUEST FLOW

```
┌──────────────────────────────────────┐
│ Client sends API request with:       │
│ Authorization: Bearer <ACCESS_TOKEN> │
└────────────────┬─────────────────────┘
                 │
                 ▼
    ┌─────────────────────────┐
    │ JwtAuthenticationFilter │
    │ - Extract token         │
    │ - Validate signature    │
    │ - Check expiration      │
    └────────┬────────────────┘
             │
    ┌────────┴─────────┐
    │                  │
    ▼                  ▼
  INVALID            VALID
  │                  │
  ▼                  ▼
Request             Parse Claims
continues           ├─ Get username
WITHOUT AUTH        ├─ Get role
│                   │
▼                   ▼
Later endpoint  Load User Details
returns 401     │
                ▼
            Set SecurityContext
            │
            ▼
        Request continues
        WITH authentication
        │
        ▼
    Endpoint checks
    @Secured/@PreAuthorize
    │
    ├─ Match? → Process
    └─ No match? → 403 Forbidden
```

### 4. TOKEN REFRESH FLOW

```
┌─────────────────────────────────┐
│ Access Token Expired (15 min)   │
│ Client gets 401 Unauthorized    │
└────────────┬────────────────────┘
             │
             ▼
    ┌────────────────────────────┐
    │ Client calls               │
    │ POST /refresh-token with   │
    │ { refreshToken }           │
    └────────┬───────────────────┘
             │
             ▼
    ┌────────────────────────────┐
    │ Validate refresh token     │
    │ - Check signature          │
    │ - Check expiration         │
    │ - Check in database        │
    └────────┬───────────────────┘
             │
    ┌────────┴──────────┐
    │                   │
    ▼                   ▼
  INVALID            VALID
  │                  │
  ▼                  ▼
Return 401      Generate New Tokens
"Invalid        ├─ New Access Token
 refresh        ├─ New Refresh Token
 token"         │
                ▼
            Update Database
            (Delete old refresh token)
            │
            ▼
        Return 200 OK
        {
          accessToken,
          refreshToken
        }
        │
        ▼
    Client stores new tokens
    │
    ▼
    Retry original request
    with new accessToken
```

### 5. LOGOUT FLOW

```
┌──────────────────────────────────────┐
│ User clicks logout button            │
└────────────┬───────────────────────┘
             │
             ▼
    ┌────────────────────────────┐
    │ Client sends              │
    │ POST /logout with         │
    │ { refreshToken }          │
    └────────┬───────────────────┘
             │
             ▼
    ┌────────────────────────────┐
    │ Find refresh token in DB   │
    └────────┬───────────────────┘
             │
    ┌────────┴──────────┐
    │                   │
    ▼                   ▼
  FOUND              NOT FOUND
  │                  │
  ▼                  ▼
Delete from DB   Still return
│                success (idempotent)
├─ Prevents      │
│  token reuse   ▼
│             Return 200 OK
▼
Return 200 OK

Client:
1. Clears localStorage (tokens)
2. Clears state (user data)
3. Redirects to login
```

---

## SECURITY FEATURES SUMMARY

### ✅ Implemented Security Measures

1. **Password Security**
   - BCrypt hashing (salted, strong)
   - Never stored in plain text
   - Compared securely by Spring Security

2. **Token Security**
   - HS256 HMAC signature verification
   - 256+ bit secret key
   - Stateless validation (no DB lookup on each request)
   - Expiration time enforced

3. **Access Control**
   - Role-based authorization
   - Endpoint-level protection
   - Method-level security annotations possible

4. **Session Security**
   - Stateless (no JSESSIONID)
   - Each request must prove authentication
   - Refresh token stored server-side (can be blacklisted)

5. **Data Protection**
   - Unique username/email enforcement
   - Account lock status tracking
   - Timestamps for audit trail

### ⚠️ Production Considerations

1. **JWT Secret**
   - MUST be 256+ bits minimum
   - Change from default before production
   - Rotate periodically
   - Never commit to version control

2. **HTTPS**
   - Always use HTTPS in production
   - Prevents token interception
   - Required for secure cookies (if used)

3. **CORS Configuration**
   - Whitelist specific domains
   - Don't use wildcard (*) for sensitive endpoints
   - Validate Origin header

4. **Token Expiration**
   - Access token: 15 min (short-lived)
   - Refresh token: 30 days (longer, stored server-side)
   - Consider sliding window for UX

5. **Logging & Monitoring**
   - Log failed login attempts
   - Monitor token refresh patterns
   - Alert on suspicious activity

---

## DATABASE SCHEMA

```sql
-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('ROLE_ADMIN', 'ROLE_CUSTOMER', 'ROLE_SERVICE_PROVIDER') NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    service_provider_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (service_provider_id) REFERENCES service_providers(id),
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- Refresh tokens table
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    INDEX idx_expiry (expiry_date)
);
```

---

## CONFIGURATION IN application.properties

```properties
# JWT Configuration
jwt.secret=replace_this_with_a_very_long_random_secret_of_at_least_256_bits
jwt.issuer=ServiceLink
jwt.access-token-expiration-ms=900000        # 15 minutes
jwt.refresh-token-expiration-ms=2592000000  # 30 days
jwt.header=Authorization                     # Header name
jwt.token-prefix=Bearer                      # Token prefix

# Security is configured in SecurityConfig.java
```

---

## API RESPONSE STRUCTURE

All endpoints return standardized responses:

```json
{
  "status": "success|error",
  "message": "Human readable message",
  "data": {},
  "timestamp": "2024-07-10T10:30:00",
  "path": "/api/v1/endpoint"
}
```

### Example Responses:

**Success (Login)**:
```json
{
  "status": "success",
  "message": "Logged in",
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc..."
  },
  "timestamp": "2024-07-10T10:30:00"
}
```

**Error (Invalid Credentials)**:
```json
{
  "status": "error",
  "message": "Invalid credentials",
  "data": null,
  "timestamp": "2024-07-10T10:30:00"
}
```

---

## AUTHENTICATION vs AUTHORIZATION

### Authentication (Who are you?)
- Verified in AuthenticationService.login()
- User provides username + password
- Backend verifies credentials match database
- Returns tokens (proof of authentication)

### Authorization (What can you do?)
- Verified by SecurityConfig and @Secured annotations
- Based on user's role (ROLE_CUSTOMER, ROLE_SERVICE_PROVIDER, ROLE_ADMIN)
- Endpoint access controlled by role
- Method-level security possible with @PreAuthorize

### Current Authorization Rules:
- `/api/v1/auth/**` → Public (all roles)
- `/api/v1/categories/**` → Public (all roles)
- Other endpoints → Authenticated (any valid token)

---

## KEY TAKEAWAYS FOR REACT FRONTEND

1. **Authentication is stateless**
   - Backend doesn't maintain sessions
   - Frontend must send token with every request

2. **Two-token system**
   - Access token: Use for API requests (15 min)
   - Refresh token: Use to get new access token (30 days)

3. **Token location**
   - Always in Authorization header
   - Format: `Authorization: Bearer <TOKEN>`

4. **Error handling**
   - 401: Token expired or invalid → Refresh and retry
   - 403: User doesn't have permission → Show error
   - 400: Invalid input → Show validation errors
   - 500: Server error → Show generic message

5. **User context**
   - After login, call `/api/v1/auth/me` to get user details
   - Store in frontend state (Zustand)
   - Use role for conditional rendering (Customer vs Provider views)

---

END OF AUTHENTICATION LAYER ANALYSIS
