# 🚀 Getting Started with FinTrack Backend

## Quick Start Guide (5 minutes)

### Prerequisites Check

```bash
# Verify Java 21
java -version
# Expected: openjdk version "21.x.x"

# Verify Docker
docker --version
# Expected: Docker version 24.x.x or higher

# Verify PostgreSQL (if running locally)
psql --version
# Expected: psql (PostgreSQL) 15.x
```

---

## 🎯 Option 1: Docker Compose (Fastest)

### Step 1: Start Everything
```bash
docker-compose up -d
```

This will:
- Start PostgreSQL database
- Start Backend API
- Run Flyway migrations
- Configure networking

### Step 2: Wait for Startup (~30 seconds)
```bash
# Check if backend is ready
curl http://localhost:8080/actuator/health
```

### Step 3: Access the API
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Base**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/actuator/health

### Step 4: Test the API

#### Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Response** will include:
- JWT token
- User information
- 11 default categories created automatically

#### Save the Token
```bash
# Copy the token from the response
TOKEN="eyJhbGci..."
```

#### List Categories
```bash
curl http://localhost:8080/api/categories \
  -H "Authorization: Bearer $TOKEN"
```

You should see 11 default categories!

#### Create a Transaction
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "description": "Lunch at restaurant",
    "amount": 45.50,
    "type": "EXPENSE",
    "categoryId": 1,
    "date": "2025-10-07"
  }'
```

#### Get Financial Summary
```bash
curl http://localhost:8080/api/transactions/summary \
  -H "Authorization: Bearer $TOKEN"
```

### Docker Compose Commands

```bash
# View logs
docker-compose logs -f backend

# Stop all containers
docker-compose down

# Restart
docker-compose restart

# Remove everything (including data)
docker-compose down -v
```

---

## 🎯 Option 2: Local Development

### Step 1: Setup Database

```bash
# Start PostgreSQL (if not running)
# Windows: services.msc → PostgreSQL
# Mac: brew services start postgresql
# Linux: sudo systemctl start postgresql

# Create database and user
psql -U postgres
```

```sql
CREATE DATABASE fintrack;
CREATE USER fintrack_user WITH PASSWORD 'fintrack_pass';
GRANT ALL PRIVILEGES ON DATABASE fintrack TO fintrack_user;
\c fintrack
GRANT ALL ON SCHEMA public TO fintrack_user;
\q
```

### Step 2: Configure Application

The `application.properties` is already configured for local development:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fintrack
spring.datasource.username=fintrack_user
spring.datasource.password=fintrack_pass
```

If you need different credentials, edit `src/main/resources/application.properties`

### Step 3: Run Application

```bash
# Option A: Using Gradle wrapper
./gradlew bootRun

# Option B: Build and run JAR
./gradlew build
java -jar build/libs/fintrack-backend-1.0.0.jar
```

### Step 4: Verify

```bash
curl http://localhost:8080/actuator/health
```

Expected: `{"status":"UP"}`

---

## 🧪 Running Tests

```bash
# Run all tests
./gradlew test

# Run with detailed output
./gradlew test --info

# Run specific test class
./gradlew test --tests AuthServiceTest

# Run specific test method
./gradlew test --tests "AuthServiceTest.shouldRegisterUserSuccessfully"
```

### Test Results
After running tests, view report at:
`build/reports/tests/test/index.html`

---

## 📚 Using the API

### Interactive Documentation

Visit **Swagger UI** at: http://localhost:8080/swagger-ui.html

Benefits:
- ✅ Interactive API testing
- ✅ Try endpoints directly in browser
- ✅ See request/response schemas
- ✅ No need for Postman
- ✅ Auto-generated from code

### Authentication Flow

1. **Register** → Get token
2. **Click "Authorize"** in Swagger
3. **Paste token** (without "Bearer " prefix)
4. **Test all endpoints** with authentication

### Sample Workflow

```
1. POST /api/auth/register
   └─> Returns token + user + 11 categories created

2. GET /api/categories
   └─> See your 11 default categories

3. POST /api/transactions
   └─> Create a transaction

4. GET /api/transactions/summary
   └─> See your financial summary

5. POST /api/budgets
   └─> Create a budget

6. GET /api/budgets/progress
   └─> Track your budget progress

7. GET /api/reports/overview
   └─> Complete financial overview
```

---

## 🔧 Troubleshooting

### Port 8080 Already in Use

**Windows**:
```bash
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Mac/Linux**:
```bash
lsof -ti:8080 | xargs kill -9
```

### Database Connection Failed

Check if PostgreSQL is running:
```bash
# Windows
services.msc  # Look for PostgreSQL service

# Mac
brew services list

# Linux
sudo systemctl status postgresql
```

### Docker Container Won't Start

```bash
# Check logs
docker-compose logs backend

# Restart
docker-compose restart backend

# Reset everything
docker-compose down -v
docker-compose up -d
```

### Flyway Migration Failed

```bash
# Connect to database
psql -U fintrack_user -d fintrack

# Drop and recreate schema
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO fintrack_user;
\q

# Restart application
```

### Tests Failing

```bash
# Clean and rebuild
./gradlew clean

# Run tests with full output
./gradlew test --info

# Check specific test
./gradlew test --tests AuthServiceTest --info
```

---

## 📊 Verify Installation

Run this checklist:

```bash
# 1. Health check
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}

# 2. Swagger accessible
curl -I http://localhost:8080/swagger-ui.html
# Expected: HTTP/1.1 200

# 3. Can register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@test.com","password":"password123"}'
# Expected: JSON with token

# 4. Database has tables
psql -U fintrack_user -d fintrack -c "\dt"
# Expected: List of 4 tables
```

All checks pass? ✅ **You're ready to go!**

---

## 🎓 Next Steps

### Explore the API
1. Open Swagger UI: http://localhost:8080/swagger-ui.html
2. Register a user
3. Authorize in Swagger
4. Try all endpoints

### Read Documentation
- [BACKEND_README.md](BACKEND_README.md) - Complete API guide
- [PROJECT_COMPLETE.md](PROJECT_COMPLETE.md) - What was built
- [PROGRESS.md](PROGRESS.md) - Implementation details

### Integrate Frontend
- API is ready for any frontend
- All endpoints are RESTful
- CORS is configured
- JWT authentication ready

### Deploy to Production
- Use provided Dockerfile
- Set environment variables
- Deploy to your preferred platform
- Configure domain and SSL

---

## 💡 Tips

### Development
- Use Swagger UI for testing (easier than cURL)
- Check logs: `docker-compose logs -f backend`
- Use H2 console for debugging (if enabled)

### Testing
- Run tests before commits
- Write new tests for new features
- Use test-api.sh for automation

### Deployment
- Change JWT_SECRET in production
- Use strong database password
- Enable HTTPS
- Set up monitoring

---

## 📞 Need Help?

- 📖 Read [BACKEND_README.md](BACKEND_README.md)
- 🔍 Check [troubleshooting section](#-troubleshooting)
- 📝 See [PROGRESS.md](PROGRESS.md) for implementation details
- 💬 Open an issue on GitHub

---

**🎉 Enjoy using FinTrack Backend API!**

*Total setup time: ~5 minutes with Docker*  
*Status: ✅ Ready to use*
