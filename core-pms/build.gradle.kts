plugins {
    id("java-library")
}

group = "io.xcodebn"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Optional dependencies - consumers choose what they need
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // For JSON annotations (optional - Jackson used by both Spring and Ktor)
    compileOnly("com.fasterxml.jackson.core:jackson-annotations:2.15.3")

    // Optional Spring Boot 3.5.6 support (for Spring services only)
    // Using Spring Boot 3.5.6 compatible versions (Spring Framework 6.2.x)
    compileOnly("org.springframework.boot:spring-boot-starter-web:3.5.6")
    compileOnly("org.springframework:spring-web:6.2.3")
    compileOnly("org.springframework:spring-context:6.2.3")

    // Jakarta servlet API (for Spring Boot 3.x)
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Test Spring support
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.6")
    testImplementation("org.springframework:spring-test:6.2.3")
}

tasks.test {
    useJUnitPlatform()
}