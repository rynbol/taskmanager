#!/bin/bash

# Stop and remove any old containers
echo "--- Stopping and removing old containers ---"
docker-compose down

# Build the Spring Boot application using Gradle
echo "--- Building the Spring Boot application ---"
./gradlew clean build --console=plain

# Start the services using Docker Compose
echo "--- Starting application and database with Docker Compose ---"
docker-compose up --build