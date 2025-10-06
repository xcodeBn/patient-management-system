# ---- Builder stage ----
FROM gradle:8.10.2-jdk21 AS builder

# Build and publish core-pms to mavenLocal first
WORKDIR /core-pms
COPY core-pms/build.gradle.kts core-pms/settings.gradle.kts ./
COPY core-pms/gradle ./gradle
COPY core-pms/gradlew* ./
RUN if [ -f gradlew ]; then chmod +x gradlew && ./gradlew --no-daemon dependencies; fi
COPY core-pms/src ./src
RUN if [ -f gradlew ]; then ./gradlew publishToMavenLocal --no-daemon; fi

# Now build auth-service
WORKDIR /app
ENV DOCKER_BUILD=true

# Copy Gradle files first for dependency caching
COPY auth-service/build.gradle.kts auth-service/settings.gradle.kts ./
COPY auth-service/gradle ./gradle
COPY auth-service/gradlew* ./

# Make gradlew executable
RUN if [ -f gradlew ]; then chmod +x gradlew; fi

# Pre-download dependencies
RUN if [ -f gradlew ]; then ./gradlew --no-daemon dependencies; fi

# Copy the source code
COPY auth-service/src ./src

# Package the Spring Boot JAR
RUN if [ -f gradlew ]; then ./gradlew clean bootJar -x test --no-daemon; fi

# ---- Runner stage ----
FROM openjdk:21 AS runner
WORKDIR /app

# Copy only the built JAR from builder stage
COPY --from=builder /app/build/libs/auth-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 4005

ENTRYPOINT ["java","-jar","app.jar"]