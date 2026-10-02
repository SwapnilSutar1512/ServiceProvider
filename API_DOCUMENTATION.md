# Local Service Provider Platform - API Documentation

## Project Overview
This is a production-ready REST API backend for a Local Service Provider Platform built with Spring Boot. The platform connects customers with nearby local service providers such as plumbers, electricians, carpenters, painters, and other professionals.

## Technology Stack
- Java 17
- Spring Boot 4.1.0
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- Lombok
- Jakarta Validation

## Project Structure
```
com.localservice
├── controller          # REST API endpoints
├── service            # Business logic interfaces
│   └── impl           # Service implementations
├── repository         # Data access layer
├── entity             # JPA entities
├── dto                # Request/Response DTOs
├── mapper             # Entity to DTO mappings
├── exception          # Custom exceptions and handlers
├── config             # Configuration classes
├── util               # Utility classes
└── constants          # Application constants
```

## Prerequisites
- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.8.1 or higher

## Setup Instructions

### 1. Database Setup
```bash
# Create PostgreSQL database
createdb servicelink_db

# Update application.properties with your credentials
spring.datasource.url=jdbc:postgresql://localhost:5432/servicelink_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 2. Build and Run
```bash
# Build the project
mvn clean build

# Run the application
mvn spring-boot:run
```

The application will start on http://localhost:8081

### 3. Database Initialization
On first run, 18 service categories are automatically initialized in the database.

## API Endpoints

### Base URL
```
http://localhost:8081/api/v1
```

## Category APIs

### 1. Create Category
```http
POST /api/v1/categories
Content-Type: application/json

{
  "categoryName": "Plumber",
  "description": "Professional plumbing services",
  "active": true
}
```
**Response (201 Created):**
```json
{
  "status": "Success",
  "message": "Category created successfully",
  "data": {
    "id": 1,
    "categoryName": "Plumber",
    "description": "Professional plumbing services",
    "active": true,
    "createdAt": "2026-07-06T14:58:35",
    "updatedAt": "2026-07-06T14:58:35"
  }
}
```

### 2. Get All Categories (Paginated)
```http
GET /api/v1/categories?page=0&size=10&sort=categoryName,asc
```
**Response (200 OK):**
```json
{
  "status": "Success",
  "message": "Categories retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "categoryName": "AC Repair",
        "description": "Air conditioning repair and maintenance",
        "active": true,
        "createdAt": "2026-07-06T14:58:35",
        "updatedAt": "2026-07-06T14:58:35"
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 18,
    "totalPages": 2,
    "isFirst": true,
    "isLast": false
  }
}
```

### 3. Get Category by ID
```http
GET /api/v1/categories/1
```

### 4. Get All Active Categories
```http
GET /api/v1/categories/active/list
```

### 5. Update Category
```http
PUT /api/v1/categories/1
Content-Type: application/json

{
  "categoryName": "Plumber Updated",
  "description": "Updated description",
  "active": true
}
```

### 6. Delete Category
```http
DELETE /api/v1/categories/1
```

---

## Service Provider APIs

### 1. Create Service Provider
```http
POST /api/v1/providers
Content-Type: application/json

{
  "fullName": "John Doe",
  "businessName": "John's Plumbing",
  "phoneNumber": "9876543210",
  "email": "john@example.com",
  "experience": 10,
  "street": "123 Main St",
  "locality": "Downtown",
  "city": "New York",
  "state": "NY",
  "pincode": "100001",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "workingHours": "9 AM - 6 PM",
  "categoryId": 1,
  "active": true
}
```
**Response (201 Created):**
```json
{
  "status": "Success",
  "message": "Service provider created successfully",
  "data": {
    "id": 1,
    "fullName": "John Doe",
    "businessName": "John's Plumbing",
    "phoneNumber": "9876543210",
    "email": "john@example.com",
    "experience": 10,
    "street": "123 Main St",
    "locality": "Downtown",
    "city": "New York",
    "state": "NY",
    "pincode": "100001",
    "latitude": 40.7128,
    "longitude": -74.0060,
    "workingHours": "9 AM - 6 PM",
    "rating": 0.0,
    "active": true,
    "categoryId": 1,
    "categoryName": "Plumber",
    "createdAt": "2026-07-06T14:58:35",
    "updatedAt": "2026-07-06T14:58:35"
  }
}
```

### 2. Get All Service Providers
```http
GET /api/v1/providers?page=0&size=10&sort=id,asc
```

### 3. Get Service Provider by ID
```http
GET /api/v1/providers/1
```

### 4. Update Service Provider
```http
PUT /api/v1/providers/1
Content-Type: application/json

{
  "fullName": "John Doe Updated",
  "businessName": "John's Plumbing Updated",
  "phoneNumber": "9876543210",
  "email": "john@example.com",
  "experience": 12,
  "street": "123 Main St",
  "locality": "Downtown",
  "city": "New York",
  "state": "NY",
  "pincode": "100001",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "workingHours": "8 AM - 8 PM",
  "categoryId": 1,
  "active": true
}
```

### 5. Delete Service Provider
```http
DELETE /api/v1/providers/1
```

---

## Search APIs

### 1. Search by Category
```http
GET /api/v1/providers/search/category/1?page=0&size=10
```

### 2. Search by City
```http
GET /api/v1/providers/search/city/New%20York?page=0&size=10
```

### 3. Search by Locality
```http
GET /api/v1/providers/search/locality/Downtown?page=0&size=10
```

### 4. Search by Category and City
```http
GET /api/v1/providers/search/category/1/city/New%20York?page=0&size=10
```

### 5. Search by Category and Locality
```http
GET /api/v1/providers/search/category/1/locality/Downtown?page=0&size=10
```

---

## Pagination Parameters

For all list endpoints, the following query parameters are supported:

| Parameter | Type    | Default | Description              |
|-----------|---------|---------|--------------------------|
| page      | Integer | 0       | Zero-based page number   |
| size      | Integer | 10      | Number of records        |
| sort      | String  | id,asc  | Sort by field,direction  |

**Example:**
```
GET /api/v1/providers?page=0&size=20&sort=businessName,asc
```

---

## Error Responses

### 400 Bad Request - Validation Error
```json
{
  "status": "Validation Error",
  "message": "Validation failed",
  "data": {
    "fieldErrors": {
      "phoneNumber": "Phone number must be exactly 10 digits",
      "email": "Email should be valid"
    }
  },
  "timestamp": "2026-07-06T14:58:35",
  "path": "/api/v1/providers"
}
```

### 404 Not Found
```json
{
  "status": "Error",
  "message": "Service provider not found",
  "timestamp": "2026-07-06T14:58:35",
  "path": "/api/v1/providers/999"
}
```

### 409 Conflict - Resource Already Exists
```json
{
  "status": "Error",
  "message": "Service provider with this email already exists",
  "timestamp": "2026-07-06T14:58:35",
  "path": "/api/v1/providers"
}
```

### 500 Internal Server Error
```json
{
  "status": "Error",
  "message": "Internal server error: [error details]",
  "timestamp": "2026-07-06T14:58:35",
  "path": "/api/v1/providers"
}
```

---

## Validation Rules

### Category
- `categoryName`: Required, 2-100 characters, unique
- `description`: Optional, max 500 characters
- `active`: Required, boolean

### Service Provider
- `fullName`: Required, 2-100 characters
- `businessName`: Required, 2-100 characters
- `phoneNumber`: Required, exactly 10 digits
- `email`: Required, valid email format, unique
- `experience`: Required, 0-70 years
- `street`: Required
- `locality`: Required
- `city`: Required
- `state`: Required
- `pincode`: Required, exactly 6 digits
- `latitude`: Optional, GPS coordinate
- `longitude`: Optional, GPS coordinate
- `workingHours`: Optional, max 500 characters
- `categoryId`: Required, valid category ID
- `active`: Required, boolean

---

## Initial Service Categories

The following 18 categories are initialized on first run:

1. Plumber
2. Electrician
3. Carpenter
4. Painter
5. AC Repair
6. Refrigerator Repair
7. Washing Machine Repair
8. RO Service
9. House Cleaning
10. Pest Control
11. CCTV Installation
12. Internet Technician
13. Driver
14. Tutor
15. Cook
16. Mechanic
17. Movers & Packers
18. Salon at Home

---

## HTTP Status Codes

| Status | Meaning                                      |
|--------|----------------------------------------------|
| 200    | OK - Request succeeded                       |
| 201    | Created - Resource successfully created     |
| 400    | Bad Request - Invalid request data          |
| 404    | Not Found - Resource not found              |
| 409    | Conflict - Resource already exists          |
| 500    | Internal Server Error                       |

---

## Features Implemented

### Phase 1 - Category Module ✓
- [x] Create Category
- [x] Update Category
- [x] Delete Category
- [x] Get Category by ID
- [x] Get All Categories with Pagination

### Phase 2 - Service Provider Module ✓
- [x] Create Service Provider
- [x] Update Service Provider
- [x] Delete Service Provider
- [x] Get Service Provider by ID
- [x] Get All Service Providers with Pagination
- [x] Search by Category
- [x] Search by City
- [x] Search by Locality
- [x] Search by Category and City
- [x] Search by Category and Locality

### Phase 3 - Advanced Features ✓
- [x] Pagination
- [x] Sorting
- [x] Validation (Jakarta Validation)
- [x] Exception Handling (Global Exception Handler)
- [x] Standard API Response Structure
- [x] DTO Pattern
- [x] Layered Architecture

---

## Future Enhancements

- Spring Security & JWT Authentication
- Customer module with booking system
- Reviews and Ratings
- Google Maps Integration
- Nearby search using Latitude/Longitude
- Chat module
- Payment Gateway
- Push Notifications
- Admin Dashboard

---

## Best Practices Implemented

1. **Layered Architecture**: Separation of concerns with controller, service, and repository layers
2. **DTOs**: Request and response DTOs to prevent exposing entity classes
3. **Constructor Injection**: All dependencies injected via constructor for better testability
4. **Validation**: Jakarta Validation annotations on DTO fields
5. **Exception Handling**: Custom exceptions and global exception handler
6. **Pagination**: Efficient data retrieval with Spring Data Pageable
7. **Sorting**: Flexible sorting with multiple criteria support
8. **CORS Configuration**: Cross-origin requests enabled for frontend integration
9. **Database Initialization**: Automatic category seeding on startup
10. **Clean Code**: Following Spring Boot and Java naming conventions

---

## Testing the API

You can test the APIs using:
- Postman
- Curl
- Thunder Client
- REST Client IDE

Example curl commands:

```bash
# Create a category
curl -X POST http://localhost:8081/api/v1/categories \
  -H "Content-Type: application/json" \
  -d '{"categoryName":"Plumber","description":"Professional plumbing","active":true}'

# Get all categories
curl http://localhost:8081/api/v1/categories

# Search providers by city
curl http://localhost:8081/api/v1/providers/search/city/NewYork
```

---

## Database Schema

The application uses PostgreSQL with Hibernate auto-schema generation (`ddl-auto=update`).

**Tables Created:**
- `categories` - Service categories
- `service_providers` - Service provider information
- `addresses` - Address information (prepared for future use)

---

## Configuration

Default configuration in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/servicelink_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
server.port=8081
```

---

## Support

For issues or questions, please refer to the code comments or the BRD document.

---

**Last Updated:** 2026-07-06
**Status:** Production Ready
