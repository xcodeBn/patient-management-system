package io.xcodebn.patientservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PatientResponseDTO {

    private String id;
    private String email;
    private String name;
    private String address;
    private String dateOfBirth;


}
