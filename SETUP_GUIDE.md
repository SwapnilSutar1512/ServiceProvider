# Local Service Provider Platform - Setup Guide

## Complete Setup Instructions

This guide will help you set up and run the Local Service Provider Platform backend.

## Prerequisites

### System Requirements
- **Operating System**: Windows, macOS, or Linux
- **Java**: Java 17 or higher
- **Database**: PostgreSQL 12 or higher
- **Build Tool**: Maven 3.8.1 or higher
- **IDE**: IntelliJ IDEA, VS Code, or Eclipse (recommended: IntelliJ)

### Installation Steps

### 1. Install Java 17
Download and install Java 17 from [oracle.com](https://www.oracle.com/java/technologies/downloads/#java17)

**Verify Installation:**
```bash
java -version
javac -version
```

### 2. Install Maven
Download and install Maven from [maven.apache.org](https://maven.apache.org/download.cgi)

**Verify Installation:**
```bash
mvn -version
```

### 3. Install PostgreSQL
Download and install PostgreSQL from [postgresql.org](https://www.postgresql.org/download/)

**Default Port:** 5432
**Default User:** postgres

### 4. Create Database

**Using PostgreSQL Command Line:**

```bash
# Windows
psql -U postgres

# macOS/Linux
sudo -u postgres psql
```

**Execute SQL Commands:**
```sql
-- Create database
CREATE DATABASE servicelink_db;

-- Verify creation
\l

-- Exit psql
\q
```

**Alternative - Using pgAdmin:**
1. Open pgAdmin
2. Right-click on Databases → Create → Database
3. Name it: `servicelink_db`
4. Click Save

### 5. Configure Application

**File:** `src/main/resources/application.properties`

```properties
spring.application.name=ServiceLink
server.port=8081

# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/servicelink_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# Update password if you set a custom one during PostgreSQL installation
```

### 6. Build the Project

```bash
# Navigate to project directory
cd c:\LocalServiceAPP\ServiceLink

# Clean and build
mvn clean install

# Or just compile
mvn clean compile
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
[INFO] Finished at: YYYY-MM-DDTHH:MM:SS±HH:MM
```

### 7. Run the Application

**Using Maven:**
```bash
mvn spring-boot:run
```

**Using IDE:**
1. Right-click on `ServiceLinkApplication.java`
2. Select "Run" or "Run As" → "Java Application"

**Using Command Line:**
```bash
java -jar target/localserve-0.0.1-SNAPSHOT.jar
```

### 8. Verify Application Started

**Expected Console Output:**
```
Tomcat started on port(s): 8081 (http)
Started ServiceLinkApplication in X.XXX seconds
Service categories initialized successfully
```

**Test Endpoint:**
```bash
curl http://localhost:8081/api/v1/categories
```

---

## Troubleshooting

### Issue 1: Maven Build Fails
**Error:** "Cannot find symbol"

**Solution:**
```bash
# Clear Maven cache
mvn clean install -DskipTests

# Update Maven dependencies
mvn dependency:tree
```

### Issue 2: PostgreSQL Connection Error
**Error:** "org.postgresql.util.PSQLException: Connection refused"

**Solutions:**
1. Verify PostgreSQL is running
2. Check database URL in `application.properties`
3. Verify credentials
4. Check if port 5432 is accessible

**Windows - Start PostgreSQL Service:**
```bash
net start postgresql-x64-15
```

**macOS - Start PostgreSQL:**
```bash
brew services start postgresql
```

**Linux - Start PostgreSQL:**
```bash
sudo systemctl start postgresql
```

### Issue 3: Port 8081 Already in Use
**Error:** "Port 8081 already in use"

**Solution:**
Change the port in `application.properties`:
```properties
server.port=8082
```

Or stop the process using port 8081:

**Windows:**
```bash
netstat -ano | findstr :8081
taskkill /PID <PID> /F
```

**macOS/Linux:**
```bash
lsof -i :8081
kill -9 <PID>
```

### Issue 4: Lombok Not Working
**Problem:** Lombok annotations not recognized

**Solution:**
1. Install Lombok plugin in IDE
2. Enable annotation processing in IDE settings
3. Clean and rebuild the project

### Issue 5: Tests Failing
**Solution:**
```bash
# Skip tests during build
mvn clean install -DskipTests

# Run specific test
mvn test -Dtest=CategoryServiceImplTest
```

---

## Project Structure Overview

```
ServiceLink/
├── src/
│   ├── main/
│   │   ├── java/com/localservice/
│   │   │   ├── controller/          # REST controllers
│   │   │   ├── service/             # Business logic
│   │   │   │   └── impl/            # Service implementations
│   │   │   ├── repository/          # Data access
│   │   │   ├── entity/              # JPA entities
│   │   │   ├── dto/                 # Request/Response objects
│   │   │   ├── mapper/              # Entity mappers
│   │   │   ├── exception/           # Exception classes
│   │   │   ├── config/              # Configuration
│   │   │   ├── util/                # Utility classes
│   │   │   ├── constants/           # Constants
│   │   │   └── ServiceLinkApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
├── target/                          # Compiled files
├── pom.xml                          # Maven configuration
├── mvnw                             # Maven wrapper (Unix)
├── mvnw.cmd                         # Maven wrapper (Windows)
└── README.md
```

---

## Development Workflow

### 1. Start Development Environment

```bash
# Terminal 1: Start PostgreSQL
psql -U postgres -d servicelink_db

# Terminal 2: Start Spring Boot application
cd c:\LocalServiceAPP\ServiceLink
mvn spring-boot:run
```

### 2. Making Code Changes

1. Edit the code
2. Save the file
3. Spring Boot DevTools automatically reloads (if enabled)

### 3. Testing Endpoints

```bash
# Get all categories
curl http://localhost:8081/api/v1/categories

# Create a category
curl -X POST http://localhost:8081/api/v1/categories \
  -H "Content-Type: application/json" \
  -d '{
    "categoryName": "Plumber",
    "description": "Professional plumbing services",
    "active": true
  }'

# Get specific category
curl http://localhost:8081/api/v1/categories/1
```

---

## IDE Setup

### IntelliJ IDEA

1. **Import Project:**
   - File → Open → Select project directory
   - Select "Maven" when prompted

2. **Enable Lombok:**
   - File → Settings → Plugins → Search "Lombok"
   - Install and restart IDE

3. **Configure Annotation Processing:**
   - File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Check "Enable annotation processing"

4. **Run Configuration:**
   - Right-click `ServiceLinkApplication.java` → Run

### VS Code

1. **Install Extensions:**
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - REST Client

2. **Create `.vscode/launch.json`:**
   ```json
   {
     "version": "0.2.0",
     "configurations": [
       {
         "name": "Spring Boot App",
         "type": "java",
         "name": "Spring Boot App",
         "request": "launch",
         "mainClass": "com.localservice.ServiceLinkApplication",
         "projectName": "localserve",
         "cwd": "${workspaceFolder}",
         "console": "integratedTerminal"
       }
     ]
   }
   ```

3. **Run Application:**
   - Press F5 or click Run

---

## Database Management

### Using PostgreSQL CLI

```bash
# Connect to database
psql -U postgres -d servicelink_db

# List tables
\dt

# View table structure
\d service_providers

# Run SQL query
SELECT * FROM categories;

# Exit
\q
```

### Using pgAdmin GUI

1. Open pgAdmin (usually at http://localhost:5050)
2. Navigate to: Servers → PostgreSQL → Databases → servicelink_db → Tables
3. Right-click on table → View/Edit Data

### Reset Database

```bash
# Drop and recreate database
dropdb -U postgres servicelink_db
createdb -U postgres servicelink_db

# Restart application (it will recreate tables)
mvn spring-boot:run
```

---

## Performance Tips

1. **Enable Query Caching:**
   ```properties
   spring.jpa.properties.hibernate.cache.use_second_level_cache=true
   ```

2. **Optimize Database Connection Pool:**
   ```properties
   spring.datasource.hikari.maximum-pool-size=20
   ```

3. **Use Read-Only Transactions:**
   Already implemented in service methods with `@Transactional(readOnly = true)`

4. **Index Frequently Searched Columns:**
   - categoryId
   - city
   - locality
   - email

---

## Production Deployment

### Before Deploying

1. **Update Configuration:**
   ```properties
   spring.jpa.hibernate.ddl-auto=validate
   spring.jpa.show-sql=false
   logging.level.org.springframework=WARN
   ```

2. **Security:**
   - Use environment variables for sensitive data
   - Enable HTTPS
   - Configure firewall rules

3. **Performance:**
   - Enable caching
   - Optimize database queries
   - Use CDN for static files

### Build JAR

```bash
mvn clean package -DskipTests
```

### Deploy JAR

```bash
java -jar target/localserve-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:postgresql://prod-host:5432/servicelink_db \
  --spring.datasource.username=prod_user \
  --spring.datasource.password=prod_password
```

---

## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Jakarta Validation](https://jakarta.ee/specifications/validation/)

---

## Support and Help

For issues:
1. Check the troubleshooting section
2. Review Spring Boot logs
3. Check PostgreSQL logs
4. Refer to project API documentation

---

**Last Updated:** 2026-07-06
**Version:** 1.0.0
