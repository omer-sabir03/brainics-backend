# Brainics Backend - Build & Testing Instructions

## ⚠️ Java Version Requirement

**This project requires Java 11 or higher** (Java 21 recommended for best performance).

The codebase uses modern Java features including:
- Text blocks (Java 13+)
- Records (Java 14+)
- Modern Spring Boot patterns (Java 11+)

### Check Your Java Version
```bash
java -version
javac -version
```

### Install Java 11+ or Higher

**Windows:**
- Download from https://www.oracle.com/java/technologies/downloads/
- Or use Windows Package Manager: `winget install OpenJDK.JDK.21`
- Set `JAVA_HOME` environment variable

**macOS:**
```bash
brew install openjdk@21
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt-get install openjdk-21-jdk
```

### Verify Installation
```bash
java -version
# Expected output: openjdk version "21..." or similar (11+)
```

---

## Building the Project

### Step 1: Clone Repository
```bash
git clone https://github.com/omer-sabir03/brainics-backend.git
cd brainics-backend
```

### Step 2: Set Environment Variables
Create a `.env` file in the project root:
```properties
# Database
DB_URL=jdbc:postgresql://localhost:5432/brainics
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Email (Resend)
RESEND_API_KEY=re_xxxxxxxxxxxxxxxxxxxxx
BRAINICS_EMAIL_FROM=noreply@brainics.com
BRAINICS_EMAIL_REPLY_TO=support@brainics.com

# Admin Settings
ADMIN_ENQUIRY_EMAIL=admin@brainics.com
INITIAL_ADMIN_USERNAME=admin
INITIAL_ADMIN_PASSWORD=changeme

# JWT & Security
JWT_ACCESS_TOKEN_MINUTES=15
CORS_ALLOWED_ORIGINS=http://localhost:3000

# Frontend URL
BRAINICS_FRONTEND_URL=http://localhost:3000
```

### Step 3: Clean & Compile
```bash
mvn clean compile
```

Expected output:
```
[INFO] BUILD SUCCESS
```

### Step 4: Run Tests
```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=EnquiryControllerIntegrationTest

# With coverage report
mvn clean test jacoco:report
# Report: target/site/jacoco/index.html
```

### Step 5: Package Application
```bash
mvn clean package
# Creates: target/brainics-backend-0.0.1-SNAPSHOT.jar
```

### Step 6: Run Application
```bash
# Option A: Via Maven
mvn spring-boot:run

# Option B: Via JAR file
java -jar target/brainics-backend-0.0.1-SNAPSHOT.jar

# With custom port
java -Dserver.port=9000 -jar target/brainics-backend-0.0.1-SNAPSHOT.jar
```

Server will start on: `http://localhost:8080`

---

## Running Tests

### Prerequisites for Tests
- Java 11+
- Maven 3.6+
- Docker (for Testcontainers PostgreSQL) - optional, can use local PostgreSQL

### Test Commands

**Run All Tests**
```bash
mvn clean test
```

**Run Specific Test Suite**
```bash
# Enquiry tests
mvn test -Dtest=EnquiryServiceIntegrationTest
mvn test -Dtest=EnquiryControllerIntegrationTest

# Academy tests
mvn test -Dtest=AcademyControllerIntegrationTest

# Notification tests
mvn test -Dtest=NotificationServiceTest
```

**Skip Tests During Build**
```bash
mvn clean package -DskipTests
```

**Run Tests with Detailed Output**
```bash
mvn test -X
```

**Run Tests with Code Coverage**
```bash
mvn clean test jacoco:report
# View report: target/site/jacoco/index.html
```

### Expected Test Results

| Test Class | Tests | Status |
|-----------|-------|--------|
| EnquiryServiceIntegrationTest | 5 | ✅ Pass |
| EnquiryControllerIntegrationTest | 6 | ✅ Pass |
| AcademyControllerIntegrationTest | 6 | ✅ Pass |
| NotificationServiceTest | 4 | ✅ Pass |
| **Total** | **21** | **✅ All Pass** |

---

## Docker Setup (Optional)

### Start PostgreSQL with Docker
```bash
docker run -d \
  --name brainics-db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=brainics \
  -p 5432:5432 \
  postgres:15-alpine

# Verify connection
docker exec brainics-db psql -U postgres -d brainics -c "\dt"
```

### Connect to Database
```bash
# Using psql
psql -h localhost -U postgres -d brainics

# Useful commands
\dt                    # List tables
\d admission_enquiries # Describe table
SELECT * FROM classes; # Query data
```

---

## Troubleshooting

### Build Fails with "invalid flag: --release"
**Solution:** Update Java to 11+
```bash
java -version  # Check current version
# If Java 8, download and install Java 11+
```

### Tests Hang or Timeout
**Solutions:**
1. Increase timeout: `-Dorg.apache.maven.surefire.timeout=120`
2. Disable parallel tests: `-DthreadCount=1`
3. Check Docker is running (if using Testcontainers)

### Cannot Connect to Database
**Check:**
```bash
# Verify database is running
psql -h localhost -U postgres -c "SELECT version();"

# Verify connection string in .env
DB_URL=jdbc:postgresql://localhost:5432/brainics

# Check PostgreSQL port
netstat -an | grep 5432
```

### Port 8080 Already in Use
```bash
# On Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# On macOS/Linux
lsof -i :8080
kill -9 <PID>

# Or use different port
java -Dserver.port=9000 -jar target/brainics-backend-0.0.1-SNAPSHOT.jar
```

### Lombok Not Generating Getters/Setters
**Solution:** Enable Annotation Processing in your IDE
- **IntelliJ:** Settings → Build → Compiler → Annotation Processors → Enable
- **VS Code:** Install Lombok extension
- **Eclipse:** Install Lombok

---

## Continuous Integration (GitHub Actions)

The project includes GitHub Actions workflows (can be added):

```yaml
name: Build & Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
      - run: mvn clean test
      - run: mvn clean package -DskipTests
```

---

## Performance Benchmarks

After successful build & test:

**Startup Time**
```bash
# Measure startup time
time java -jar target/brainics-backend-0.0.1-SNAPSHOT.jar
# Expected: 3-5 seconds
```

**API Response Time**
```bash
# Test endpoint response
time curl http://localhost:8080/api/public/academy/classes
# Expected: <100ms
```

**Database Queries**
```bash
# Query execution time logged in application.yml
# Enable debug logging:
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

---

## IDE Setup

### IntelliJ IDEA
1. Open project: File → Open → brainics-backend
2. Configure JDK: Project Structure → Project → JDK → Java 21
3. Enable Lombok: Settings → Plugins → search "Lombok" → Install
4. Enable Annotation Processing: Settings → Build → Compiler → Annotation Processors → ✓ Enable

### VS Code
1. Install: "Extension Pack for Java" (Microsoft)
2. Install: "Lombok Annotations Support" (Pivotal)
3. Open project folder

### Eclipse
1. File → Import → Maven → Existing Maven Projects
2. Install Lombok: help → Install New Software → https://projectlombok.org/p2
3. Enable Annotation Processing: Project → Properties → Java Compiler → Annotation Processing

---

## Next Steps After Build Success

1. **Test Locally**
   ```bash
   mvn spring-boot:run
   # Navigate to http://localhost:8080/api/public/academy/classes
   ```

2. **Test APIs** (see TESTING_GUIDE.md)
   ```bash
   curl -X POST http://localhost:8080/api/public/enquiry/submit \
     -H "Content-Type: application/json" \
     -d '{...}'
   ```

3. **Deploy to Staging**
   ```bash
   mvn clean package
   # Deploy target/brainics-backend-0.0.1-SNAPSHOT.jar
   ```

4. **Monitor Application**
   - Application logs
   - Database connections
   - API response times
   - Email delivery

---

## Useful Commands

```bash
# Clean build
mvn clean install

# Build without tests
mvn clean package -DskipTests

# Run specific main class
mvn exec:java -Dexec.mainClass="com.brainics.backend.BrainicsBackendApplication"

# Generate dependency tree
mvn dependency:tree

# Check for security vulnerabilities
mvn org.owasp:dependency-check-maven:check

# Update dependencies
mvn versions:display-dependency-updates

# Format code
mvn spotless:apply
```

---

## Support

- See `TESTING_GUIDE.md` for comprehensive testing procedures
- See `PHASE1_SUMMARY.md` for project overview
- See README.md for general information
