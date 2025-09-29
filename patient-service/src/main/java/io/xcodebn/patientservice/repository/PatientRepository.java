package io.xcodebn.patientservice.repository;

import io.xcodebn.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    boolean existsByEmail(String email);
    Patient findByEmail(String email);
    //this could replace my code
    boolean existsByEmailAndIdNot(String email, UUID id);
}
