package io.xcodebn.patientservice.exception;

import java.util.UUID;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(String exception ){
        super(exception);
    }
}
