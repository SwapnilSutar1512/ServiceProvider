# ServiceLink Backend - Quick API Reference Guide

## Base URL
```
http://localhost:8081/api/v1
```

## Authentication Header Format
```
Authorization: Bearer <ACCESS_TOKEN>
```

---

## AUTH ENDPOINTS

### 1. Register as Customer
```http
POST /auth/register/customer

Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```

**Success (201):**
```json
{
  "status": "success",
  "message": "Customer registered",
  "data": null,
  "timestamp": "2024-07-10T10:30:00"
}
```

**Error (400):**
```json
{
  "status": "error",
  "message": "Username or email already exists",
  "data": null,
  "timestamp": "2024-07-10T10:30:00"
}
```

---

### 2. Register as Service Provider
```http
POST /auth/register/provider

Content-Type: application/json

{
  "username": "provider_name",
  "email": "provider@example.com",
  "password": "SecurePassword123!",
  "fullName": "John Provider"
}
```

**Success (201):**
```json
{
  "status": "success",
  "message": "Service provider registered",
  "data": null,
  "timestamp": "2024-07-10T10:30:00"
}
```

**Error (400):**
```json
{
  "status": "error",
  "message": "Username or email already exists",
  "data": null,
  "timestamp": "2024-07-10T10:30:00"
}
```

---

### 3. Login
```http
POST /auth/login

Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePassword123!"
}
```

**Success (200):**
```json
{
  "status": "success",
  "message": "Logged in",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huX2RvZSIsImlzcyI6IlNlcnZpY2VMAW5rIiwiaWF0IjoxNzIwNTg0NjAwLCJleHAiOjE3MjA1ODUyMDAsInJvbGUiOiJST0xFX0NVU1RPTUVSIn0.XXX",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huX2RvZSIsImlzcyI6IlNlcnZpY2VMAW5rIiwiaWF0IjoxNzIwNTg0NjAwLCJleHAiOjE3MjMxNzY2MDB9.YYY"
  },
  "timestamp": "2024-07-10T10:30:00"
}
```

**Error (401):**
```json
{
  "status": "error",
  "message": "Invalid credentials",
  "data": null,
  "timestamp": "2024-07-10T10:30:00"
}
```

---

### 4. Refresh Access Token
```http
POST /auth/refresh-token

Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Success (200):**
```json
{
  "status": "success",
  "message": "Token refreshed",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "timestamp": "2024-07-10T10:30:00"
}
```

**Error (401):**
```json
{
  "status": "error",
  "message": "Invalid refresh token",
  "data": null,
  "timestamp": "2024-07-10T10:30:00"
}
```

---

### 5. Get Current User Profile
```http
GET /auth/me

Authorization: Bearer <ACCESS_TOKEN>
```

**Success (200):**
```json
{
  "status": "success",
  "message": "User profile",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "role": "ROLE_CUSTOMER"
  },
  "timestamp": "2024-07-10T10:30:00"
}
```

**Error (401):**
```json
{
  "status": "error",
  "message": "Unauthorized",
  "data": null,
  "timestamp": "2024-07-10T10:30:00"
}
```

---

### 6. Logout
```http
POST /auth/logout

Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Success (200):**
```json
{
  "status": "success",
  "message": "Logged out",
  "data": null,
  "timestamp": "2024-07-10T10:30:00"
}
```

---

## CATEGORIES ENDPOINTS (Public)

### Get All Categories
```http
GET /categories
```

**Response:**
```json
{
  "status": "success",
  "message": "Categories retrieved",
  "data": [
    {
      "id": 1,
      "name": "Plumbing",
      "description": "Plumbing services"
    }
  ],
  "timestamp": "2024-07-10T10:30:00"
}
```

---

## PROVIDERS ENDPOINTS

### Create Service Provider Profile
```http
POST /providers

Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json

{
  "name": "John's Plumbing Service",
  "description": "Professional plumbing services",
  "categoryId": 1,
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "latitude": 40.7128,
    "longitude": -74.0060
  }
}
```

**Success (201):**
```json
{
  "status": "success",
  "message": "Provider created successfully",
  "data": {
    "id": 1,
    "name": "John's Plumbing Service",
    "description": "Professional plumbing services",
    "categoryId": 1,
    "address": {...}
  },
  "timestamp": "2024-07-10T10:30:00"
}
```

---

## HTTP STATUS CODES

| Code | Meaning | Scenario |
|------|---------|----------|
| 200 | OK | Successful GET, POST, PUT, DELETE |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Invalid input (validation error) |
| 401 | Unauthorized | Missing/invalid token, expired token |
| 403 | Forbidden | User doesn't have permission |
| 404 | Not Found | Resource not found |
| 500 | Server Error | Internal server error |

---

## TOKEN STRUCTURE (Reference)

When you decode the JWT token, you'll see:

```json
{
  "sub": "username",
  "iss": "ServiceLink",
  "iat": 1720584600,
  "exp": 1720585500,
  "role": "ROLE_CUSTOMER"
}
```

- `sub`: Subject (username)
- `iss`: Issuer (ServiceLink)
- `iat`: Issued at (Unix timestamp)
- `exp`: Expiration (Unix timestamp)
- `role`: User role

---

## FRONTEND IMPLEMENTATION CHECKLIST

### Token Management
- [ ] Store accessToken in localStorage after login
- [ ] Store refreshToken in localStorage after login
- [ ] Add Authorization header to all API requests: `Authorization: Bearer <accessToken>`
- [ ] Implement token refresh logic when getting 401
- [ ] Clear tokens on logout

### Error Handling
- [ ] On 401: Call /refresh-token with refreshToken
- [ ] On 403: Show permission denied message
- [ ] On 400: Show validation error messages
- [ ] On 500: Show generic "Server error" message

### User Context
- [ ] After login, call /auth/me to get user details
- [ ] Store user data in state management (Zustand/Redux)
- [ ] Use user.role to conditionally render Customer vs Provider views
- [ ] Redirect unauthenticated users to login

### Protected Routes
- [ ] Create ProtectedRoute component
- [ ] Check for accessToken before allowing access
- [ ] Redirect to login if token missing
- [ ] Handle expired tokens gracefully

---

## CURL EXAMPLES FOR TESTING

### Register Customer
```bash
curl -X POST http://localhost:8081/api/v1/auth/register/customer \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePassword123!"
  }'
```

### Login
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePassword123!"
  }'
```

### Get User Profile (Replace with actual token)
```bash
curl -X GET http://localhost:8081/api/v1/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Refresh Token
```bash
curl -X POST http://localhost:8081/api/v1/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

### Logout
```bash
curl -X POST http://localhost:8081/api/v1/auth/logout \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

---

## POSTMAN COLLECTION

Import this into Postman for easy testing:

### Variables (Set in Postman Environment)
- `baseUrl`: http://localhost:8081/api/v1
- `accessToken`: (auto-set after login)
- `refreshToken`: (auto-set after login)

### Requests

**Register Customer** - POST {{baseUrl}}/auth/register/customer
**Register Provider** - POST {{baseUrl}}/auth/register/provider
**Login** - POST {{baseUrl}}/auth/login
**Get Profile** - GET {{baseUrl}}/auth/me (Header: Authorization: Bearer {{accessToken}})
**Refresh Token** - POST {{baseUrl}}/auth/refresh-token
**Logout** - POST {{baseUrl}}/auth/logout

---

## COMMON ISSUES & SOLUTIONS

### Issue: 401 Unauthorized on /auth/me
**Solution**: Token is missing or expired. Call /refresh-token or login again.

### Issue: "Username or email already exists"
**Solution**: Try a different username/email. Check database for existing user.

### Issue: Invalid credentials on login
**Solution**: Verify username exists and password is correct.

### Issue: Refresh token returns 401
**Solution**: Refresh token has expired (30 days) or was blacklisted. Must login again.

### Issue: CORS error from frontend
**Solution**: Backend CORS not configured for frontend domain. Add frontend URL to CORS whitelist.

---

## SECURITY REMINDERS

✅ DO:
- Always use HTTPS in production
- Keep tokens secure (don't expose in URLs)
- Validate user input on frontend
- Refresh tokens before they expire
- Clear tokens on logout
- Use environment variables for API URL

❌ DON'T:
- Hard-code API URL in frontend
- Store tokens in URL or query parameters
- Display raw tokens to users
- Log tokens in console (production)
- Use localStorage for extremely sensitive data
- Trust only frontend validation (validate on backend too)

---

END OF QUICK REFERENCE
