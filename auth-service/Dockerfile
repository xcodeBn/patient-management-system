# ---- Builder stage ----
FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /app

# Copy Gradle files first for dependency caching (equivalent to copying pom.xml)
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY gradlew gradlew.bat ./

# Make gradlew executable
RUN chmod +x gradlew

# Pre-download dependencies (equivalent to mvn dependency:go-offline)
RUN ./gradlew --no-daemon dependencies

# Copy the source code
COPY src ./src

# Package the Spring Boot JAR (equivalent to mvn clean package -DskipTests)
RUN ./gradlew clean bootJar -x test --no-daemon

# ---- Runner stage ----
FROM openjdk:21 AS runner
WORKDIR /app

# Copy only the built JAR from builder stage
COPY --from=builder /app/build/libs/auth-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 4005

ENTRYPOINT ["java","-jar","app.jar"]