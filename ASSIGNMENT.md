# Software Engineer Intern Assignment

## Objective
Build a comprehensive Task Management RESTful API using modern software engineering practices, demonstrating proficiency in Spring Boot, database integration, testing, and DevOps practices.

## Assignment Overview
You will implement a complete task management system with CRUD operations, following an OpenAPI specification, and deploy it using containerization with a single command execution.

## Technical Requirements

### Technology Stack
- **Java**: 21
- **Framework**: Spring Boot 3.5+
- **Database**: MySQL 8+
- **Build Tool**: Gradle (already configured)
- **Containerization**: Docker & Docker Compose
- **Version Control**: Git

### Core Features Required
1. **Task Management CRUD Operations**
   - Create new tasks
   - Retrieve all tasks
   - Retrieve single task by ID
   - Update existing tasks
   - Delete tasks

2. **Task Data Model**
   ```
   Task:
   - id: Long (unique identifier)
   - title: String (task title)
   - description: String (task description)
   - completed: Boolean (completion status)
   - createdAt: DateTime (creation timestamp)
   - updatedAt: DateTime (last update timestamp)
   ```

## Must-Have Deliverables

### 1. OpenAPI-First Development
- Use the provided `openapi.yaml` specification
- Generate server code using OpenAPI Generator
- Implement all endpoints as specified

### 2. Single Command Deployment
- Create a `run.sh` script that:
  - Starts MySQL database in Docker container
  - Sets up database schema
  - Builds and runs the Spring Boot application
  - Makes the API accessible at `http://localhost:8080`

### 3. Comprehensive Testing
- **Unit Tests**: Test individual components and services
- **Integration Tests**: Test API endpoints with database
- Minimum 80% code coverage
- All tests should pass

### 4. CI/CD Pipeline
- Set up GitHub Actions (or similar) workflow
- Automated testing on every push/PR
- Build and containerization process
- Include documentation on your CI/CD approach in README.md

### 5. Database Integration
- MySQL database with proper schema
- Connection pooling
- Proper error handling for database operations
- Database should run in Docker container

### 6. Documentation
- Complete README.md following the provided template
- API documentation available via Swagger UI
- Clear instructions for running and testing

### 7. Postman Collection
- Working Postman collection for all endpoints
- Include example requests and responses
- Export as JSON file in repository

## Bonus Features (Nice-to-Have)

### Observability Stack
- **OpenTelemetry**: Auto-instrumentation for traces and metrics
- **Prometheus**: Metrics collection
- **Grafana**: Dashboard with system and business metrics
- Access points:
  - Grafana: `http://localhost:3000`
  - Prometheus: `http://localhost:9090`

## Evaluation Criteria

### Primary (Must Work)
1. **Functionality**: `run.sh` successfully starts the application
2. **API Compliance**: All endpoints work as per OpenAPI spec
3. **Data Persistence**: CRUD operations work with MySQL
4. **Testing**: All tests pass

### Secondary (Quality Assessment)
1. **Code Quality**: Clean, maintainable, well-structured code
2. **Documentation**: Clear, comprehensive documentation
3. **Error Handling**: Proper error responses and logging
4. **Best Practices**: Following Spring Boot and Java conventions

### Bonus Points
1. **Observability Implementation**: Working telemetry stack
2. **Security**: Authentication and authorization

## Submission Requirements

### Timeline
**Deadline**: 7 days from assignment receipt

### Deliverables
Submit a ZIP file of this repo with completed assignment containing:

1. **Source Code**: Complete Spring Boot application
2. **Configuration**: `run.sh`, Docker configurations
3. **Documentation**: Updated README.md
4. **Tests**: Unit and integration test suites
5. **CI/CD Documentation**: Description of your CI/CD pipeline setup
6. **Git history** : `.git` directory should also be part of the zip

### Submission Format
- **Deliverable**: ZIP file containing complete git repository
- **Email**: Send ZIP file or download link via email reply
- **README**: Must include your assumptions and architectural decisions

## Getting Started

1. Extract the provided baseline code ZIP file
2. Review the provided `openapi.yaml` specification
3. Import the `postman_collection.json` for API testing
4. Implement the solution following the requirements
5. Ensure `run.sh` works end-to-end
6. Create ZIP file of your complete solution

## Support
If you have questions or need clarification on any requirements, make reasonable assumptions and document them in your README.md file.

## Success Criteria
Your submission will be considered successful if:
- `run.sh` executes without errors
- All API endpoints are functional
- Tests pass
- Documentation is complete and clear
- Code demonstrates good engineering practices

Good luck! 🚀