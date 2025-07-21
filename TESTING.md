# Testing Guide

This document provides comprehensive information about the testing strategy and how to run tests for the Task Management API.

## Test Structure

The project includes comprehensive testing at multiple levels:

### 1. Unit Tests
- **TaskEntityTest**: Tests the JPA entity, lifecycle callbacks, and Lombok-generated methods
- **TasksApiControllerTest**: Tests the REST controller with mocked dependencies using `@WebMvcTest`
- **TaskRepositoryTest**: Tests the JPA repository layer using `@DataJpaTest`

### 2. Integration Tests
- **TaskManagerApplicationTests**: Full end-to-end tests with real database using `@SpringBootTest`

### 3. Application Tests
- **TaskmanagerApplicationTest**: Tests Spring Boot application context loading

## Running Tests

### Run All Tests
```bash
./gradlew test
```

### Run Tests with Coverage Report
```bash
./gradlew test jacocoTestReport
```

### View Coverage Report
After running tests with coverage, open the HTML report:
```bash
open build/jacocoHtml/index.html
```

### Run Specific Test Classes
```bash
# Run only unit tests
./gradlew test --tests "*Test"

# Run only integration tests
./gradlew test --tests "*Tests"

# Run specific test class
./gradlew test --tests "TasksApiControllerTest"
```

### Verify Coverage Threshold
```bash
./gradlew jacocoTestCoverageVerification
```
This will fail if coverage is below 80%.

## Test Coverage Goals

The project aims for **80% code coverage** as specified in the assignment requirements.

### Coverage Breakdown
- **Controller Layer**: Comprehensive unit tests with mocked dependencies
- **Repository Layer**: JPA-specific tests with in-memory H2 database
- **Entity Layer**: Tests for lifecycle callbacks and data integrity
- **Integration Layer**: End-to-end API tests with full Spring context

## Test Data Strategy

### Unit Tests
- Use mocked dependencies (`@MockBean`)
- Test individual components in isolation
- Fast execution with no external dependencies

### Integration Tests
- Use H2 in-memory database for repository tests
- Use `@SpringBootTest` for full application context
- Clean database state between tests using `@BeforeEach`

## Key Test Scenarios Covered

### Happy Path
- ✅ Create, read, update, delete operations
- ✅ Multiple tasks management
- ✅ Timestamp handling (createdAt, updatedAt)

### Edge Cases
- ✅ Empty database queries
- ✅ Non-existent resource access (404 scenarios)
- ✅ Null and empty string handling
- ✅ Large string inputs
- ✅ Invalid JSON requests

### Error Scenarios
- ✅ Resource not found (404)
- ✅ Bad request format (400)
- ✅ Database constraint violations

## Continuous Integration

Tests are designed to run in CI/CD environments:
- No external dependencies required
- Uses H2 in-memory database for fast execution
- Deterministic test results
- Proper test isolation

## Test Best Practices Implemented

1. **AAA Pattern**: Arrange, Act, Assert structure
2. **Test Isolation**: Each test is independent
3. **Descriptive Names**: Test method names clearly describe what is being tested
4. **Comprehensive Coverage**: Tests cover both success and failure scenarios
5. **Mocking Strategy**: Appropriate use of mocks vs real implementations
6. **Data Cleanup**: Proper cleanup between tests

## Running Tests in Docker

Tests can also be run within the Docker environment:
```bash
# Build and run tests in container
docker-compose run --rm app ./gradlew test

# Run with coverage
docker-compose run --rm app ./gradlew test jacocoTestReport
```
