package com.pm.stack;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.route53.CfnHealthCheck;
import software.amazon.awscdk.services.route53.CfnHealthCheckProps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LocalStack CDK Stack for Patient Management System
 *
 * This stack creates a local development environment using LocalStack to simulate AWS services.
 * It sets up the entire microservices architecture including networking, databases,
 * message queues, and containerized services.
 *
 * @author xcodeBn@github
 */
public class LocalStack extends Stack {

    private final Vpc vpc;
    private final Cluster ecsCluster;

    public LocalStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Foundation: Network and container orchestration
        this.vpc = createVpc();
        this.ecsCluster = creaateEcsCluster();

        // Databases: Each service gets its own PostgreSQL instance for data isolation
        DatabaseInstance authServiceDb = createDatabase("AuthServiceDb", "auth-service-db");
        DatabaseInstance patientServiceDb = createDatabase("PatientServiceDb", "patient-service-db");

        // Health checks: Monitor database availability before starting dependent services
        CfnHealthCheck authServiceDbHealthCheck = createDbHealthCheck(authServiceDb, "AuthServiceDbHealthCheck");
        CfnHealthCheck patientServiceDbHealthCheck = createDbHealthCheck(patientServiceDb, "PatientServiceDbHealthCheck");

        // Message queue: Kafka cluster for async event streaming
        // Note: Requires LocalStack Pro - consider using Docker Compose Kafka for free tier
        CfnCluster mskCluster = createMskCluster();

        // Auth Service: Handles user authentication and JWT generation
        FargateService authService = createFargateService(
                "AuthService",
                "auth-service",
                List.of(4005),
                authServiceDb,
                Map.of("JWT_SECRET","${jwt.secret}")
        );
        authService.getNode().addDependency(authServiceDbHealthCheck);
        authService.getNode().addDependency(authServiceDb);

        // Billing Service: Stateless service with gRPC endpoint
        FargateService billingService = createFargateService("BillingService",
               "billing-service",
               List.of(4001, 9001), // HTTP API + gRPC
               null, // No database needed
               null);

        // Analytics Service: Processes events from Kafka
        FargateService analyticsService = createFargateService("AnalyticsService",
               "analytics-service",
               List.of(4002),
               null,
               null);
        analyticsService.getNode().addDependency(mskCluster); // Wait for Kafka

        // Patient Service: Main service managing patient records
        FargateService patientService = createFargateService("PatientService",
                "patient-service",
                List.of(4000),
                patientServiceDb,
                Map.of("BILLING_SERVICE_ADDRESS","host.docker.internal","BILLING_SERVICE_GRPC_PORT","9001")
        );
        // Dependencies: Patient service needs DB, billing, and Kafka to be ready
        patientService.getNode().addDependency(patientServiceDb);
        patientService.getNode().addDependency(patientServiceDbHealthCheck);
        patientService.getNode().addDependency(billingService);
        patientService.getNode().addDependency(mskCluster);

        createApiGatewayService();
    }

    /**
     * Creates an ECS cluster for running containerized services
     * Includes service discovery via Cloud Map for inter-service communication
     */
    private Cluster creaateEcsCluster() {
        return Cluster.Builder.create(this, "PatientManagementCluster")
                .vpc(vpc)
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                        .name("patient-management-local").build())
                .build();
    }

    /**
     * Creates a TCP health check for database instances
     * Ensures databases are accepting connections before services start
     */
    private CfnHealthCheck createDbHealthCheck(DatabaseInstance db, String id) {
        return CfnHealthCheck.Builder
                .create(this, id)
                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                        .type("TCP")
                        .port(Token.asNumber(db.getDbInstanceEndpointPort()))
                        .ipAddress(db.getDbInstanceEndpointAddress())
                        .requestInterval(30) // Check every 30 seconds
                        .failureThreshold(3) // Fail after 3 consecutive failures
                        .build())
                .build();
    }

    /**
     * Creates a VPC spanning 2 availability zones for high availability
     */
    private Vpc createVpc() {
        return Vpc.Builder.create(this, "patient-management-vpc")
                .vpcName("patient-management-vpc")
                .maxAzs(2)
                .build();
    }

    /**
     * Creates a managed Kafka cluster for event streaming
     * Note: MSK might not work in LocalStack free tier
     */
    private CfnCluster createMskCluster() {
        return CfnCluster.Builder.create(this, "MskCluster")
                .clusterName("kafka-cluster")
                .kafkaVersion("2.8.0")
                .numberOfBrokerNodes(1)
                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                        .instanceType("kafka.m5.xlarge")
                        .clientSubnets(vpc.getPrivateSubnets().stream().map(
                                ISubnet::getSubnetId
                        ).collect(Collectors.toList()))
                        .brokerAzDistribution("DEFAULT")
                        .build())
                .build();
    }

    /**
     * Creates a PostgreSQL database instance
     * Uses auto-generated credentials stored in Secrets Manager
     */
    private DatabaseInstance createDatabase(String id, String dbName) {
        return DatabaseInstance.Builder
                .create(this, id)
                .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                        .version(PostgresEngineVersion.VER_17_2).build()))
                .vpc(vpc)
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .allocatedStorage(20)
                .credentials(Credentials.fromGeneratedSecret("admin_user"))
                .databaseName(dbName)
                .removalPolicy(RemovalPolicy.DESTROY) // Auto-delete on stack deletion
                .build();
    }

    /**
     * Creates a Fargate service to run a containerized microservice
     *
     * @param id CDK construct ID
     * @param imageName Docker image name (also used as service name)
     * @param ports List of ports to expose
     * @param db Database instance (null if service doesn't need a database)
     * @param additionalEnvVars Extra environment variables specific to this service
     * @return Configured Fargate service
     */
    private FargateService createFargateService(
            String id,
            String imageName,
            List<Integer> ports,
            DatabaseInstance db,
            Map<String, String> additionalEnvVars
    ) {
        // Task definition: Blueprint for the container (CPU, memory, etc.)
        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder.create(this, id + "Task")
                .cpu(256)
                .memoryLimitMiB(512)
                .build();

        // Container configuration: Image, ports, logging
        ContainerDefinitionOptions.Builder containerDefinitionOptions = ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry(imageName))
                .portMappings(ports.stream()
                        .map(
                                port -> PortMapping.builder()
                                        .containerPort(port)
                                        .hostPort(port)
                                        .protocol(Protocol.TCP)
                                        .build())
                        .toList())
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                        .logGroup(LogGroup.Builder.create(this, id + "LogGroup")
                                .logGroupName("/ecs/" + imageName)
                                .removalPolicy(RemovalPolicy.DESTROY)
                                .retention(RetentionDays.ONE_DAY) // Keep logs for 1 day
                                .build())
                                .streamPrefix(imageName)
                        .build()));

        // Environment variables: Common config for all Spring Boot services
        Map<String,String> envVars = new HashMap<>();

        // Kafka configuration (LocalStack endpoints)
        envVars.put("SPRING_KAFKA_BOOTSTRAP_SERVERS","localhost.localstack.cloud:4510, localhost.localstack.cloud:4511,localhost.localstack.cloud:4512");

        // Merge in any service-specific env vars
        if(additionalEnvVars != null && !additionalEnvVars.isEmpty()) {
            envVars.putAll(additionalEnvVars);
        }

        // Database configuration (only if service has a database)
        if(db != null){
            envVars.put("SPRING_DATASOURCE_URL","jdbc:postgresql://%s:%s/%s-db".formatted(
                    db.getDbInstanceEndpointAddress(),
                    db.getDbInstanceEndpointPort(),
                    imageName
            ) + db.getDbInstanceEndpointAddress());

            // Database credentials from Secrets Manager
            envVars.put("SPRING_DATASOURCE_USERNAME","admin_user");
            envVars.put("SPRING_DATASOURCE_PASSWORD", db.getSecret().secretValueFromJson("password").toString());

            // JPA/Hibernate configuration
            envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO","update");
            envVars.put("SPRING_SQL_INIT_MODE","always");
            envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT","60000");
        }

        containerDefinitionOptions.environment(envVars);

        // Add container to task definition
        taskDefinition.addContainer(imageName + "Container", containerDefinitionOptions.build());

        // Create the Fargate service
        return FargateService.Builder.create(this, id)
                .cluster(ecsCluster)
                .taskDefinition(taskDefinition)
                .assignPublicIp(false) // Private subnet
                .serviceName(imageName)
                .build();
    }

    private void createApiGatewayService(){
// Task definition: Blueprint for the container (CPU, memory, etc.)
        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder.create(this, "ApiGatewayTaskDefinition" + "Task")
                .cpu(256)
                .memoryLimitMiB(512)
                .build();


        ContainerDefinitionOptions containerDefinitionOptions = ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry("api-gateway"))
                .environment(Map.of("STRING_PROFILE_ACTIVE","prod",
                        "AUTH_SERVICE_URL","http://host.docker.internat:4005"))
                .portMappings(Stream.of(4004)
                        .map(
                                port -> PortMapping.builder()
                                        .containerPort(port)
                                        .hostPort(port)
                                        .protocol(Protocol.TCP)
                                        .build())
                        .toList())
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                        .logGroup(LogGroup.Builder.create(this,  "ApiGatewayLogGroup")
                                .logGroupName("/ecs/api-gateway")
                                .removalPolicy(RemovalPolicy.DESTROY)
                                .retention(RetentionDays.ONE_DAY) // Keep logs for 1 day
                                .build())
                        .streamPrefix("api-gateway")
                        .build()))
                .build();

        taskDefinition.addContainer("ApiGatewayContainer",containerDefinitionOptions);

         ApplicationLoadBalancedFargateService.Builder.create(this,"ApiGatewayService")
                 .cluster(ecsCluster)
                 .serviceName("api-gateway")
                 .taskDefinition(taskDefinition)
                 .desiredCount(1)
                 .healthCheckGracePeriod(Duration.seconds(60))
                 .build();
    }

    public static void main(final String[] args) {
        App app = new App(AppProps.builder().outdir("Infrastructure/cdk.out").build());
        StackProps props = StackProps.builder()
                .synthesizer(new BootstraplessSynthesizer()) // No AWS bootstrapping needed for LocalStack
                .build();
        new LocalStack(app, "localstack2", props);
        app.synth();
        System.out.println("App synthesizing in progress...");
    }
}