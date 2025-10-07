# FinTrack Backend API

REST API for the FinTrack personal finance management system.

## 🚀 Quick Start

### With Docker Compose (Recommended)
```bash
docker-compose up -d
```

Access:
- API: http://localhost:8080/api
- Swagger: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

### Local Development

#### Prerequisites
- Java 21+
- PostgreSQL 14+
- Gradle 8.5+

#### Setup Database
```sql
CREATE DATABASE fintrack;
CREATE USER fintrack_user WITH PASSWORD 'fintrack_pass';
GRANT ALL PRIVILEGES ON DATABASE fintrack TO fintrack_user;
```

#### Run Application
```bash
./gradlew bootRun
```

## 📡 API Endpoints

### Authentication (Public)
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login

### Users (Authenticated)
- `GET /api/users/me` - Get current user

### Categories (Authenticated)
- `GET /api/categories` - List categories
- `POST /api/categories` - Create category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

### Transactions (Authenticated)
- `GET /api/transactions` - List transactions (with filters & pagination)
- `GET /api/transactions/{id}` - Get transaction
- `POST /api/transactions` - Create transaction
- `PUT /api/transactions/{id}` - Update transaction
- `DELETE /api/transactions/{id}` - Delete transaction
- `GET /api/transactions/summary` - Get financial summary

### Budgets (Authenticated)
- `GET /api/budgets` - List budgets
- `GET /api/budgets/{id}` - Get budget
- `POST /api/budgets` - Create budget
- `PUT /api/budgets/{id}` - Update budget
- `DELETE /api/budgets/{id}` - Delete budget
- `GET /api/budgets/progress` - Get budget progress

### Reports (Authenticated)
- `GET /api/reports/month-summary` - Current month summary
- `GET /api/reports/category-summary` - Expenses by category
- `GET /api/reports/monthly-evolution` - Monthly evolution
- `GET /api/reports/overview` - Financial overview

## 🏗️ Architecture

```
Controller Layer → Service Layer → Repository Layer → Database
```

### Key Components
- **Security**: JWT-based authentication
- **Validation**: Jakarta Validation on all inputs
- **Exception Handling**: Global exception handler
- **Database**: PostgreSQL with Flyway migrations

## 🛠️ Technology Stack

- Java 21
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Flyway
- Gradle
- Lombok
- Docker

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport
```

## 📦 Build

```bash
# Build JAR
./gradlew build

# Skip tests
./gradlew build -x test
```

## 🔒 Environment Variables

### Required for Production
```
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/fintrack
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
JWT_SECRET=your-256-bit-secret-key
JWT_EXPIRATION=3600000
CORS_ORIGINS=https://your-frontend-url.com
```

## 📊 Database Schema

### Tables
- `users` - User accounts
- `categories` - Transaction categories
- `transactions` - Financial transactions
- `budgets` - Budget management

### Migrations
Located in `src/main/resources/db/migration/`
- V1: Create users table
- V2: Create categories table
- V3: Create transactions table
- V4: Create budgets table

## 🔐 Security

- Password encryption with BCrypt
- JWT token authentication
- Stateless session management
- CORS configuration
- Request validation at all layers
- Ownership validation for all resources

## 📝 API Documentation

Visit `/swagger-ui.html` when application is running for interactive API documentation.

## 🐳 Docker

### Build Image
```bash
docker build -t fintrack-backend .
```

### Run with Docker Compose
```bash
docker-compose up -d
```

### View Logs
```bash
docker-compose logs -f backend
```

## 🔍 Health Check

```bash
curl http://localhost:8080/actuator/health
```

## 📚 Project Structure

```
src/main/java/com/fintrack/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/             # Data Transfer Objects
├── exception/       # Custom exceptions
├── model/           # JPA entities
├── repository/      # Data repositories
├── security/        # Security & JWT
└── service/         # Business logic
```

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'feat: add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

MIT License

---

**Author**: Matheus Mafioletti  
**Version**: 1.0.0  
**Status**: ✅ Production Ready
