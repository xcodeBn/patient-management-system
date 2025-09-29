package io.xcodebn.patientservice.mapper;

import io.xcodebn.patientservice.dto.PatientRequestDTO;
import io.xcodebn.patientservice.dto.PatientResponseDTO;
import io.xcodebn.patientservice.model.Patient;

import java.time.LocalDate;


public class PatientMapper {
   public static PatientResponseDTO  toPatientResponseDTO(Patient patient) {
       PatientResponseDTO patientResponseDTO = new PatientResponseDTO();
       patientResponseDTO.setAddress(patient.getAddress());
       patientResponseDTO.setId(patient.getId().toString());
       patientResponseDTO.setEmail(patient.getEmail());
       patientResponseDTO.setName(patient.getName());
       patientResponseDTO.setDateOfBirth(patient.getDateOfBirth().toString());
       return patientResponseDTO;

   }

    public static PatientResponseDTO toDTO(Patient patient) {
        return toPatientResponseDTO(patient);
    }

   public static Patient toPatient(PatientRequestDTO patientRequestDTO){
       Patient patient = new Patient();
       patient.setName(patientRequestDTO.getName());
       patient.setAddress(patientRequestDTO.getAddress());
       patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
       patient.setRegisteredDate(
               patientRequestDTO.getRegisteredDate().isBlank()
                       ? LocalDate.now()
                       : LocalDate.parse(patientRequestDTO.getRegisteredDate())
       );
       patient.setEmail(patientRequestDTO.getEmail());
       return patient;
   }

    public static Patient toModel(PatientRequestDTO patientRequestDTO){
        return toPatient(patientRequestDTO);
    }
}
