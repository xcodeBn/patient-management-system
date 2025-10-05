plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "io.xcodebn"
version = "0.0.1-SNAPSHOT"
description = "analytics-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // --- Proto definitions ---
    implementation("io.xcodebn:proto-patient-api:1.0-SNAPSHOT")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.kafka:spring-kafka")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
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
