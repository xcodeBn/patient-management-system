rootProject.name = "auth-service"

// Only use composite build for local development (not in Docker)
val corePmsPath = file("../core-pms")
if (corePmsPath.exists() && System.getenv("DOCKER_BUILD") == null) {
    includeBuild("../core-pms")
}
