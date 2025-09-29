package io.xcodebn.patientservice.dto;

import io.xcodebn.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientRequestDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 255, message = "Name can't exceed 255 characters")
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @Builder.Default
    @NotBlank(groups = CreatePatientValidationGroup.class)
    private String registeredDate = ""; // or use LocalDate.now().toString() if you want current date

    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth;
}