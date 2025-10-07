# ---- Builder stage ----
FROM gradle:8.10.2-jdk21 AS builder

# Build proto-definitions first
WORKDIR /proto-definitions
COPY proto-definitions/ ./

# Now build patient-service
WORKDIR /app
COPY patient-service/build.gradle.kts patient-service/settings.gradle.kts ./
COPY patient-service/gradle ./gradle
COPY patient-service/gradlew* ./

# Make gradlew executable
RUN if [ -f gradlew ]; then chmod +x gradlew; fi

# Pre-download dependencies
RUN if [ -f gradlew ]; then ./gradlew --no-daemon dependencies; fi

# Copy the source code
COPY patient-service/src ./src

# Package the Spring Boot JAR
RUN if [ -f gradlew ]; then ./gradlew clean bootJar -x test --no-daemon; fi

# ---- Runner stage ----
FROM openjdk:21 AS runner
WORKDIR /app

# Copy only the built JAR from builder stage
COPY --from=builder /app/build/libs/patient-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 4000
ENTRYPOINT ["java","-jar","app.jar"]