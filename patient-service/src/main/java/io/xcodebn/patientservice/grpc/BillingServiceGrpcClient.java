package io.xcodebn.patientservice.grpc;


import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BillingServiceGrpcClient {
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;



    // local host
    public BillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String serverAddress,
            @Value("${billing.service.grpc.port:9001}") int serverPort
    ){

        log.info("Creating grpc service client");
        log.info("Connecting to grpc billing server at {}:{}", serverAddress, serverPort);


        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress,serverPort).usePlaintext().build();
        blockingStub = BillingServiceGrpc.newBlockingStub(channel);

    }

    public BillingResponse createBillingAccount(String patientId,String name , String email){

        BillingRequest  request =  BillingRequest.newBuilder().setName(name).setEmail(email).build();

       BillingResponse response =  blockingStub.createBillingAccount(request);
       log.info("Recieved response from billing service {}",response);
       return response;
    }
}
