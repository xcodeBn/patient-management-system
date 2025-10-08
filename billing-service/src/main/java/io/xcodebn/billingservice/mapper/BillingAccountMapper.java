package io.xcodebn.billingservice.mapper;

import io.xcodebn.billingservice.dto.BillingAccountResponse;
import io.xcodebn.billingservice.dto.CreateBillingAccountRequest;
import io.xcodebn.billingservice.model.BillingAccount;

public class BillingAccountMapper {

    public static BillingAccount toEntity(CreateBillingAccountRequest request) {
        if (request == null) {
            return null;
        }

        return BillingAccount.builder()
                .patientId(request.getPatientId())
                .patientName(request.getPatientName())
                .patientEmail(request.getPatientEmail())
                .build();
    }

    public static BillingAccountResponse toResponse(BillingAccount account) {
        if (account == null) {
            return null;
        }

        return BillingAccountResponse.builder()
                .patientId(account.getPatientId())
                .patientName(account.getPatientName())
                .patientEmail(account.getPatientEmail())
                .balance(account.getBalance())
                .status(account.getStatus())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}