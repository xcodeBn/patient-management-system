package io.xcodebn.patientservice.service;


import billing.BillingResponse;
import io.xcodebn.patientservice.dto.PatientRequestDTO;
import io.xcodebn.patientservice.dto.PatientResponseDTO;
import io.xcodebn.patientservice.exception.EmailAlreadyExistsException;
import io.xcodebn.patientservice.exception.PatientNotFoundException;
import io.xcodebn.patientservice.grpc.BillingServiceGrpcClient;
import io.xcodebn.patientservice.mapper.PatientMapper;
import io.xcodebn.patientservice.model.Patient;
import io.xcodebn.patientservice.repository.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class PatientService {
    private final PatientRepository patientRepository;

    private final BillingServiceGrpcClient billingServiceGrpcClient;


    public PatientService(PatientRepository patientRepository,BillingServiceGrpcClient billingServiceGrpcClient) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;

    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();

        return patients.stream()
                .map(PatientMapper::toPatientResponseDTO).toList();

    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("A patient of this email already exists:" + patientRequestDTO.getEmail());
        }
        Patient patient = patientRepository.save(
                PatientMapper.toModel(patientRequestDTO)
        );


         billingServiceGrpcClient.createBillingAccount(
                patient.getId().toString(),
                patient.getName(),
                patient.getEmail()
        );

        return PatientMapper.toPatientResponseDTO(patient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {


        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException("Patient not found with ID:  " + id)
        );

//        if(patientRepository.existsByEmail(patientRequestDTO.getEmail()) && !Objects.equals(patientRequestDTO.getEmail(), patientRepository.getReferenceById(id).getEmail())){
//            throw new EmailAlreadyExistsException("A patient of this email already exists:" + patientRequestDTO.getEmail());
//        }
        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)){
            throw new EmailAlreadyExistsException("A patient of this email already exists:" + patientRequestDTO.getEmail());
        }


        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setEmail(patientRequestDTO.getEmail());

       Patient updatedPatient =  patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }


    public void deletePatient(UUID id) {
        if(!patientRepository.existsById(id)){
            throw new PatientNotFoundException("Patient with id " + id + " doesn't exist" );
        }
        patientRepository.deleteById(id);
    }



}
