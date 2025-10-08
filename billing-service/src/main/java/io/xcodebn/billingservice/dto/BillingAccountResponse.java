package io.xcodebn.billingservice.dto;

import io.xcodebn.billingservice.model.BillingAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingAccountResponse {
    private String patientId;
    private String patientName;
    private String patientEmail;
    private BigDecimal balance;
    private BillingAccount.AccountStatus status;
    private LocalDateTime updatedAt;
}