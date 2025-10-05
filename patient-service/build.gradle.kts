plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "io.xcodebn"
version = "0.0.1-SNAPSHOT"
description = "patient-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {
    // --- Proto definitions ---
    implementation("io.xcodebn:proto-billing-api:1.0-SNAPSHOT")
    implementation("io.xcodebn:proto-patient-api:1.0-SNAPSHOT")

    // --- Spring ---
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")

    // --- Lombok ---
    implementation("org.projectlombok:lombok")
    implementation("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    annotationProcessor("org.projectlombok:lombok")

    // --- Database ---
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")

    // --- Dev tools ---
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // --- Testing ---
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // --- gRPC + Protobuf ---
    implementation("io.grpc:grpc-netty-shaded:1.69.0")
    implementation("io.grpc:grpc-protobuf:1.69.0")
    implementation("io.grpc:grpc-stub:1.69.0")
    implementation("net.devh:grpc-spring-boot-starter:3.1.0.RELEASE")
    implementation("com.google.protobuf:protobuf-java:4.29.1")
    compileOnly("org.apache.tomcat:annotations-api:6.0.53") // For Java 9+
}

tasks.withType<Test> {
    useJUnitPlatform()
}
