package io.xcodebn.billingservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.xcodebn.billingservice.dto.BillingAccountResponse;
import io.xcodebn.billingservice.dto.CreateBillingAccountRequest;
import io.xcodebn.billingservice.exception.BillingAccountAlreadyExistsException;
import io.xcodebn.billingservice.service.BillingAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class BillingGrpcService extends BillingServiceImplBase {

    private final BillingAccountService billingAccountService;

    @Override
    public void createBillingAccount(BillingRequest request, StreamObserver<BillingResponse> responseObserver) {
        try {
            log.info("createBillingAccount request received for patient: {}", request.getPatientId());

            CreateBillingAccountRequest accountRequest = CreateBillingAccountRequest.builder()
                    .patientId(request.getPatientId())
                    .patientName(request.getName())
                    .patientEmail(request.getEmail())
                    .build();

            BillingAccountResponse account = billingAccountService.createBillingAccount(accountRequest);

            BillingResponse response = BillingResponse.newBuilder()
                    .setAccountId(account.getPatientId())
                    .setStatus(account.getStatus().toString())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Billing account created successfully for patient: {}", account.getPatientId());

        } catch (BillingAccountAlreadyExistsException e) {
            log.warn("Billing account already exists for patient: {}", request.getPatientId());
            responseObserver.onError(Status.ALREADY_EXISTS
                    .withDescription(e.getMessage())
                    .asRuntimeException());

        } catch (Exception e) {
            log.error("Error creating billing account: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to create billing account: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
