package io.xcodebn.billingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBillingAccountRequest {
    private String patientId;
    private String patientName;
    private String patientEmail;
}