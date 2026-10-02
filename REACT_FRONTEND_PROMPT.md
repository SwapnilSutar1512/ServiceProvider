# ServiceLink React Frontend - Comprehensive Generation Prompt

## Project Overview & Architecture

This is a **ServiceLink** platform - a location-based local service provider platform with JWT-based authentication. The backend runs on **Spring Boot 3.5.6** (Java 17) with MySQL database at `http://localhost:8081`.

### Backend Technology Stack:
- Spring Boot 3.5.6
- Spring Security with JWT authentication
- MySQL Database
- REST API with standardized ApiResponse wrapper
- Role-based access control (3 roles: ADMIN, CUSTOMER, SERVICE_PROVIDER)

---

## AUTHENTICATION LAYER DEEP DIVE

### 1. **JWT Token Management System**

#### Token Structure:
- **Access Token**: 15 minutes expiration
  - Contains: username, role (ROLE_ADMIN/ROLE_CUSTOMER/ROLE_SERVICE_PROVIDER)
  - Used for API authentication
  - Sent in header: `Authorization: Bearer <ACCESS_TOKEN>`

- **Refresh Token**: 30 days expiration
  - Stored in database (RefreshToken entity)
  - Used only to generate new access tokens
  - Returned after login and refresh operations

#### Security Features:
- HS256 signing algorithm
- Token validation on each request via `JwtAuthenticationFilter`
- Stateless session management (SessionCreationPolicy.STATELESS)
- CSRF disabled for API-first design
- Password encoded with BCrypt

### 2. **Three User Roles**

```
ROLE_CUSTOMER          → Regular service consumers
ROLE_SERVICE_PROVIDER  → Service providers/professionals  
ROLE_ADMIN            → System administrators
```

### 3. **User Entity Structure**

```json
{
  "id": Long,
  "username": String (unique, required),
  "email": String (unique, required),
  "password": String (BCrypt encoded),
  "role": Enum(ROLE_ADMIN, ROLE_CUSTOMER, ROLE_SERVICE_PROVIDER),
  "enabled": Boolean,
  "accountNonLocked": Boolean,
  "serviceProvider": ServiceProvider (optional, OneToOne relation),
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### 4. **API Response Wrapper**

All API responses follow this standardized structure:

```json
{
  "status": "success|error",
  "message": "Human readable message",
  "data": {
    // endpoint-specific data
  },
  "timestamp": "ISO-8601 LocalDateTime",
  "path": "optional - request path"
}
```

---

## AUTHENTICATION ENDPOINTS SPECIFICATION

### API Base URL: `http://localhost:8081/api/v1/auth`

All endpoints accept and return JSON with `Content-Type: application/json`

#### 1. **REGISTER AS CUSTOMER**
- **Endpoint**: `POST /api/v1/auth/register/customer`
- **Authentication**: Not Required (Public)
- **Request Body**:
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```
- **Success Response** (201 Created):
```json
{
  "status": "success",
  "message": "Customer registered",
  "data": null,
  "timestamp": "2024-07-10T10:30:00"
}
```
- **Error Response** (400 Bad Request):
```json
{
  "status": "error",
  "message": "Username or email already exists",
  "data": null,
  "timestamp": "2024-07-10T10:30:00"
}
```
- **Validation Rules**:
  - Username: Required, must be unique
  - Email: Required, must be unique, valid email format
  - Password: Required, minimum 8 characters recommended

---

#### 2. **REGISTER AS SERVICE PROVIDER**
- **Endpoint**: `POST /api/v1/auth/register/provider`
- **Authentication**: Not Required (Public)
- **Request Body**:
```json
{
  "username": "provider_name",
  "email": "provider@example.com",
  "password": "SecurePassword123!",
  "fullName": "John Provider Name"
}
```
- **Success Response** (201 Created):
```json
{
  "status": "success",
  "message": "Service provider registered",
  "data": null,
  "timestamp": "2024-07-10T10:30:00"
}
```
- **Error Response** (400 Bad Request):
```json
{
  "status": "error",
  "message": "Username or email already exists",
  "data": null,
  "timestamp": "2024-07-10T10:30:00"
}
```

---

#### 3. **LOGIN**
- **Endpoint**: `POST /api/v1/auth/login`
- **Authentication**: Not Required (Public)
- **Request Body**:
```json
{
  "username": "john_doe",
  "password": "SecurePassword123!"
}
```
- **Success Response** (200 OK):
```json
{
  "status": "success",
  "message": "Logged in",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "timestamp": "2024-07-10T10:30:00"
}
```
- **Error Response** (401 Unauthorized):
```json
{
  "status": "error",
  "message": "Invalid credentials",
  "data": null,
  "timestamp": "2024-07-10T10:30:00"
}
```
- **Frontend Action**: 
  - Store both tokens in localStorage
  - accessToken should be used for all subsequent API requests
  - refreshToken should be stored securely for token refresh

---

#### 4. **REFRESH ACCESS TOKEN**
- **Endpoint**: `POST /api/v1/auth/refresh-token`
- **Authentication**: Not Required (Public)
- **Purpose**: Get new access token using refresh token (when access token expires)
- **Request Body**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
- **Success Response** (200 OK):
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
- **Error Response** (401 Unauthorized):
```json
{
  "status": "error",
  "message": "Invalid refresh token" OR "Refresh token not recognized" OR "User not found",
  "data": null,
  "timestamp": "2024-07-10T10:30:00"
}
```
- **Frontend Action**:
  - Call this endpoint automatically when access token expires (15 minutes)
  - Update stored tokens with new ones
  - Retry original failed request with new access token

---

#### 5. **GET USER PROFILE (ME)**
- **Endpoint**: `GET /api/v1/auth/me`
- **Authentication**: Required (Bearer Token)
- **Header**: `Authorization: Bearer <ACCESS_TOKEN>`
- **Success Response** (200 OK):
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
- **Error Responses**:
  - (401 Unauthorized): Invalid or missing token
  - (404 Not Found): User not found in database
- **Frontend Use**: 
  - Call after login/app startup to hydrate user context
  - Display user information in navigation/profile
  - Confirm token is still valid

---

#### 6. **LOGOUT**
- **Endpoint**: `POST /api/v1/auth/logout`
- **Authentication**: Not Required (Public)
- **Request Body**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
- **Success Response** (200 OK):
```json
{
  "status": "success",
  "message": "Logged out",
  "data": null,
  "timestamp": "2024-07-10T10:30:00"
}
```
- **Frontend Action**:
  - Clear all tokens from localStorage
  - Clear user context/state
  - Redirect to login page

---

## ADDITIONAL ENDPOINTS (For Complete Integration)

### 1. **GET ALL CATEGORIES** (Public)
- **Endpoint**: `GET /api/v1/categories`
- **Authentication**: Not Required
- **Response**: List of service categories

### 2. **CREATE SERVICE PROVIDER PROFILE**
- **Endpoint**: `POST /api/v1/providers`
- **Authentication**: Required (Bearer Token)
- **Response**: Created service provider details

---

## REACT FRONTEND REQUIREMENTS

### Project Setup
- **Framework**: React 18+ with TypeScript
- **Build Tool**: Create React App or Vite
- **Node Version**: 16+ (LTS recommended)
- **Package Manager**: npm or yarn

### Core Dependencies to Install
```json
{
  "react": "^18.0.0",
  "react-dom": "^18.0.0",
  "react-router-dom": "^6.0.0",
  "axios": "^1.0.0",
  "zustand": "^4.0.0",
  "react-hook-form": "^7.0.0",
  "zod": "^3.0.0",
  "@hookform/resolvers": "^3.0.0"
}
```

### Project Structure
```
src/
├── components/
│   ├── Auth/
│   │   ├── LoginForm.tsx          # Login form with validation
│   │   ├── RegisterForm.tsx       # Registration form (customer/provider)
│   │   ├── LoginPage.tsx          # Full login page layout
│   │   ├── RegisterPage.tsx       # Full registration page layout
│   │   └── AuthGuard.tsx          # Protected route wrapper
│   ├── Navigation/
│   │   ├── Header.tsx             # Top navigation with user menu
│   │   ├── Sidebar.tsx            # Side navigation (role-based)
│   │   └── NavBar.tsx             # Main navigation component
│   ├── Common/
│   │   ├── LoadingSpinner.tsx
│   │   ├── ErrorAlert.tsx
│   │   ├── SuccessAlert.tsx
│   │   └── ApiResponseHandler.tsx # Handle standard API responses
│   └── Dashboard/
│       ├── CustomerDashboard.tsx  # ROLE_CUSTOMER view
│       └── ProviderDashboard.tsx  # ROLE_SERVICE_PROVIDER view
├── pages/
│   ├── LoginPage.tsx
│   ├── RegisterPage.tsx
│   ├── DashboardPage.tsx
│   └── ProfilePage.tsx
├── stores/
│   ├── authStore.ts              # Zustand store for auth state
│   ├── userStore.ts              # Zustand store for user data
│   └── types.ts                  # TypeScript interfaces
├── services/
│   ├── api/
│   │   ├── authService.ts        # All auth API calls
│   │   ├── categoryService.ts    # Category API calls
│   │   ├── providerService.ts    # Provider API calls
│   │   └── apiClient.ts          # Axios instance with JWT config
│   └── tokenService.ts           # Token storage and refresh logic
├── hooks/
│   ├── useAuth.ts               # Custom hook for auth state
│   ├── useUser.ts               # Custom hook for user profile
│   └── useTokenRefresh.ts       # Custom hook for token refresh logic
├── utils/
│   ├── tokenUtils.ts            # JWT token parsing and validation
│   ├── storageUtils.ts          # localStorage utilities
│   └── validators.ts            # Form validation utilities
├── types/
│   └── index.ts                 # All TypeScript types and interfaces
├── App.tsx                       # Main app component with routing
├── App.css
└── index.tsx                     # React entry point
```

---

## STATE MANAGEMENT (Using Zustand)

### Auth Store (`stores/authStore.ts`)
```typescript
// State
{
  accessToken: string | null,
  refreshToken: string | null,
  isAuthenticated: boolean,
  isLoading: boolean,
  error: string | null,
}

// Actions
{
  setTokens(access: string, refresh: string): void,
  clearTokens(): void,
  setLoading(loading: boolean): void,
  setError(error: string | null): void,
  logout(): void,
  refreshAccessToken(refreshToken: string): Promise<void>,
}
```

### User Store (`stores/userStore.ts`)
```typescript
// State
{
  user: {
    id: number,
    username: string,
    email: string,
    role: 'ROLE_CUSTOMER' | 'ROLE_SERVICE_PROVIDER' | 'ROLE_ADMIN',
  } | null,
  loading: boolean,
  error: string | null,
}

// Actions
{
  setUser(user: User): void,
  clearUser(): void,
  fetchUserProfile(accessToken: string): Promise<void>,
  updateUser(user: User): void,
}
```

---

## API INTEGRATION DETAILS

### 1. **Axios Client Configuration** (`services/api/apiClient.ts`)

```typescript
// Key Features:
// - Base URL: http://localhost:8081/api/v1
// - Request interceptor: Add Authorization header with access token
// - Response interceptor: 
//   * Handle 401 errors (token expired)
//   * Automatically call refresh-token endpoint
//   * Retry original request with new token
//   * Handle 403 (access denied) with error modal
// - Handle network errors gracefully
```

### 2. **Token Management Strategy**

```
LOGIN SUCCESS
    ↓
Store accessToken & refreshToken in localStorage
Store tokens in Zustand state
Set Authorization header for all requests
    ↓
REQUEST WITH TOKEN
    ↓
IF 401 RESPONSE (Token Expired)
    ↓
Call POST /api/v1/auth/refresh-token with refreshToken
    ↓
IF REFRESH SUCCESS
    ↓
Update tokens in localStorage & Zustand
Retry original request with new accessToken
    ↓
IF REFRESH FAILS
    ↓
Clear all tokens
Redirect to login
Show error message
```

### 3. **CORS Configuration Requirement**
Backend must have CORS enabled for frontend domain (localhost:3000/3001)

---

## AUTHENTICATION FLOW UI/UX

### Login Flow
1. User navigates to `/login`
2. LoginForm component displays username/password fields
3. User submits form
4. Frontend validates inputs (client-side)
5. POST request to `/api/v1/auth/login` with credentials
6. On success:
   - Save tokens to localStorage
   - Save tokens to Zustand store
   - Fetch user profile via `/api/v1/auth/me`
   - Store user data in Zustand
   - Redirect to dashboard based on role
7. On error:
   - Display error message (invalid credentials, server error, etc.)
   - Allow retry

### Register Flow
1. User navigates to `/register`
2. ShowRegisterForm with:
   - Role selector (Customer / Service Provider)
   - Username input
   - Email input
   - Password input
   - Password confirmation
   - Full Name (only for Service Provider)
3. Client-side validation:
   - Username not empty, 3+ chars
   - Email valid format
   - Password 8+ chars with requirements
   - Passwords match
4. User submits form
5. POST request to `/api/v1/auth/register/customer` OR `/api/v1/auth/register/provider`
6. On success:
   - Show success message
   - Redirect to login after 2 seconds
7. On error:
   - Display error (username exists, email exists, server error)
   - Allow user to correct and retry

### Protected Routes
- All routes except `/login` and `/register` require valid accessToken
- ProtectedRoute/AuthGuard component checks:
  - Token exists in localStorage
  - Token is not expired
  - User profile loaded
- If any check fails: redirect to login

---

## ERROR HANDLING STRATEGY

### API Error Response Format (Backend Returns)
```json
{
  "status": "error",
  "message": "Human readable error message",
  "data": null,
  "timestamp": "ISO-8601",
  "path": "/api/v1/endpoint"
}
```

### Frontend Error Handling
- **401 Unauthorized**: Auto-refresh token, retry request
- **403 Forbidden**: Show permission denied message, redirect to dashboard
- **400 Bad Request**: Display validation errors
- **500 Server Error**: Show generic error, allow retry
- **Network Error**: Show offline/connection error
- **Invalid Token**: Clear tokens, redirect to login

---

## FORM VALIDATION STRATEGY

### Login Form
```
Fields: username, password
Rules:
  - username: required, 3-100 chars
  - password: required, 6+ chars
Validation: Client-side with react-hook-form
```

### Register Form
```
Fields: username, email, password, confirmPassword, fullName (provider only)
Rules:
  - username: required, 3-100 chars, alphanumeric + underscore
  - email: required, valid email format
  - password: required, 8+ chars
  - confirmPassword: must match password
  - fullName: optional, 3-100 chars
Validation: Client-side with react-hook-form + Zod schema
Real-time feedback on each field
```

---

## TOKEN STORAGE & SECURITY

### Storage Strategy
- **Access Token**: localStorage (short-lived, 15 min)
  - Rationale: Quick access for every API call
  - Risk: Vulnerable to XSS
  - Mitigation: Sanitize all user inputs, use CSP headers
  
- **Refresh Token**: localStorage (long-lived, 30 days)
  - Rationale: Persist login session
  - Risk: Vulnerable to XSS, CSRF
  - Mitigation: CSRF tokens for state-changing requests, proper CORS

### XSS Prevention
- Sanitize all user inputs before rendering
- Never use `dangerouslySetInnerHTML` unless absolutely necessary
- Use Content-Security-Policy headers

### CSRF Prevention
- Backend handles with CSRF tokens (if needed)
- Frontend sends tokens in headers for state-changing requests

---

## RESPONSIVE DESIGN REQUIREMENTS

- **Mobile First**: Design for mobile, scale up
- **Breakpoints**:
  - Mobile: < 640px
  - Tablet: 640px - 1024px
  - Desktop: > 1024px
- **Navigation**: Hamburger menu on mobile, full nav on desktop
- **Forms**: Stack on mobile, side-by-side on desktop
- **Components**: Use flexbox/grid for responsiveness

---

## ACCESSIBILITY REQUIREMENTS

- ARIA labels on all form inputs
- Keyboard navigation support (Tab, Enter, Escape)
- Color contrast ratio 4.5:1 minimum for text
- Error messages linked to form fields
- Loading states for async operations
- Semantic HTML (button, form, nav, etc.)

---

## COMPONENT SPECIFICATIONS

### LoginForm Component
```typescript
Props: {
  onSuccess?: (tokens: { accessToken, refreshToken }) => void,
  onError?: (error: string) => void,
}
State: {
  username: string,
  password: string,
  isLoading: boolean,
  error: string | null,
}
Behavior:
  - Validate form before submit
  - Show loading spinner during API call
  - Display error messages below fields
  - Success: show success message, call onSuccess callback
  - Forgot password link (placeholder for future)
```

### RegisterForm Component
```typescript
Props: {
  role: 'customer' | 'provider',
  onSuccess?: () => void,
  onError?: (error: string) => void,
}
State: {
  username: string,
  email: string,
  password: string,
  confirmPassword: string,
  fullName: string,
  isLoading: boolean,
  errors: { field: string }[],
}
Behavior:
  - Role selector at top
  - Validate all fields with Zod schema
  - Show real-time validation feedback
  - Password strength indicator
  - Success: redirect to login
  - Error: show below relevant field
```

### AuthGuard Component
```typescript
Props: {
  children: ReactNode,
  requiredRole?: 'ROLE_CUSTOMER' | 'ROLE_SERVICE_PROVIDER' | 'ROLE_ADMIN',
}
Behavior:
  - Check if user authenticated (token in store)
  - Check if token not expired
  - If role required, verify user has role
  - If checks fail: redirect to login
  - If checks pass: render children
```

### Header/Navigation Component
```typescript
Shows:
  - App logo/title
  - Navigation links (role-based)
  - User menu (dropdown)
    - View Profile
    - Settings
    - Logout
  - Loading indicator during API calls
  - Mobile hamburger menu
```

---

## ENVIRONMENT VARIABLES

Create `.env` file in React project root:
```
REACT_APP_API_BASE_URL=http://localhost:8081/api/v1
REACT_APP_APP_NAME=ServiceLink
REACT_APP_TOKEN_EXPIRY_MS=900000
REACT_APP_REFRESH_TOKEN_EXPIRY_MS=2592000000
```

---

## BUILD & DEPLOYMENT

### Development
```bash
npm install
npm start
# Runs on http://localhost:3000
```

### Production Build
```bash
npm run build
# Creates optimized build in build/ folder
```

### Deployment
- Frontend can be deployed to Vercel, Netlify, AWS S3+CloudFront, etc.
- Ensure backend CORS allows frontend domain
- Update `.env` with production API URL

---

## TESTING REQUIREMENTS

### Unit Tests
- Auth service functions
- Token utility functions
- Form validation functions

### Integration Tests
- Login flow end-to-end
- Register flow end-to-end
- Protected route access
- Token refresh flow

### E2E Tests (Optional)
- Cypress or Playwright
- Test complete user journeys

---

## FUTURE ENHANCEMENTS (Not in MVP)

1. Forgot Password flow
2. Email verification
3. Two-factor authentication
4. Social login (Google, Facebook)
5. User profile update
6. Service provider profile details
7. Search/filter services
8. Booking management
9. Ratings and reviews
10. Payment integration

---

## KEY IMPLEMENTATION NOTES

### DO's
✅ Always include Authorization header with Bearer token in requests
✅ Handle 401 responses by attempting token refresh
✅ Store tokens in both localStorage and Zustand (sync them)
✅ Validate forms before sending to API
✅ Show loading states during API calls
✅ Display user-friendly error messages
✅ Use TypeScript for type safety
✅ Create reusable API service methods
✅ Implement proper error boundaries

### DON'Ts
❌ Never hard-code API URLs (use environment variables)
❌ Never store sensitive data in session storage
❌ Never expose tokens in URLs or query parameters
❌ Never use localStorage to store sensitive user data (use state)
❌ Never make API calls in render method
❌ Never forget to cleanup listeners in useEffect
❌ Never display raw error objects to users
❌ Never skip token validation checks

---

## DELIVERABLES FOR REACT PROJECT

1. ✅ Complete React project with TypeScript
2. ✅ All components specified above
3. ✅ Authentication service with all API endpoints
4. ✅ Zustand stores for auth and user state
5. ✅ Token refresh interceptor logic
6. ✅ Protected route component
7. ✅ Form validation with react-hook-form + Zod
8. ✅ Error handling and user feedback
9. ✅ Responsive design (mobile, tablet, desktop)
10. ✅ Environment variable configuration
11. ✅ README with setup and deployment instructions

---

## GETTING STARTED CHECKLIST

Before running React frontend:
- [ ] Backend running on http://localhost:8081
- [ ] MySQL database running and populated
- [ ] Backend CORS configured to allow localhost:3000
- [ ] `.env` file created with API base URL
- [ ] `npm install` completed
- [ ] `npm start` runs successfully

---

## JWT TOKEN PAYLOAD EXAMPLE

When you decode the access token (for reference):
```json
{
  "sub": "john_doe",
  "iss": "ServiceLink",
  "iat": 1720584600,
  "exp": 1720585500,
  "role": "ROLE_CUSTOMER"
}
```

Use token decoding library (jwt-decode) to extract claims for frontend logic.

---

END OF PROMPT
This prompt is complete and production-ready for generating a full-featured React frontend.
