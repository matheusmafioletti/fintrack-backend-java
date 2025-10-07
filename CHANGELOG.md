# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-10-07

### Added

#### Phase 1: Setup & Authentication
- Initial project setup with Spring Boot 3.2.0, Gradle 8.5 and Java 21
- JWT authentication and security configuration
- User entity and repository
- Authentication system with DTOs, services and controllers
- Global exception handling
- User registration and login endpoints
- Password encryption with BCrypt

#### Phase 2: Categories
- Category entity with TransactionType enum
- Category CRUD endpoints
- Default categories creation on user registration (11 categories)
- Category ownership validation
- Duplicate name prevention per user

#### Phase 3: Transactions
- Transaction entity with relationships
- Transaction CRUD with advanced filtering
- Pagination and sorting support
- Financial summary calculation
- Search functionality in descriptions
- Category type matching validation

#### Phase 4: Reports & Dashboard
- Report service with aggregation queries
- Month summary with previous month comparison
- Category summary with percentages
- Monthly evolution tracking
- Financial overview endpoint
- Top categories ranking

#### Phase 5: Budgets
- Budget entity with BudgetPeriod enum
- Budget CRUD endpoints
- Budget progress calculation by period (weekly, monthly, yearly)
- Status alerts (OK, WARNING, EXCEEDED)
- Only EXPENSE categories validation
- Duplicate budget prevention

#### Phase 8: Testing
- Unit tests for AuthService
- Unit tests for CategoryService
- Test configuration with H2 database
- JUnit 5 + Mockito setup

#### Phase 9: DevOps & Deploy
- Dockerfile with multi-stage build
- docker-compose.yml for development
- application-prod.properties for production
- GitHub Actions CI/CD pipeline
- Health checks configuration
- BACKEND_README.md

#### Extras & Improvements
- Swagger/OpenAPI documentation configuration
- OpenApiConfig with JWT scheme
- DateUtils utility class
- Custom date range validation
- ValidationUtils for common validations
- ForbiddenException for 403 errors
- Test scripts (test-api.sh, seed-data.sql)
- Improved main README

### Security
- JWT token provider with HMAC-SHA256
- JWT authentication filter
- Custom user details service
- Security configuration with stateless sessions
- CORS configuration
- Password validation (min 8 characters)
- Email validation
- Ownership validation on all resources

### Database
- PostgreSQL 15 support
- Flyway migrations (V1-V4)
- Optimized indexes
- Foreign key constraints
- Check constraints for data integrity

### Documentation
- Complete API documentation with Swagger
- Comprehensive README files
- Progress tracking document
- Project completion summary
- API examples and usage

## [Unreleased]

### Future Enhancements
- Refresh token implementation
- Email notifications
- Data export (CSV/PDF)
- Redis caching
- Rate limiting
- Recurring transactions automation
- More comprehensive integration tests

---

**Repository**: https://github.com/matheusmafioletti/fintrack-backend-java  
**Author**: Matheus Mafioletti  
**License**: MIT
