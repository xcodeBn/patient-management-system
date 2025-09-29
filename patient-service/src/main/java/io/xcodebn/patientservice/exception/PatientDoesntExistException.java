package io.xcodebn.patientservice.exception;

public class PatientDoesntExistException extends RuntimeException {
    public PatientDoesntExistException(String message) {
        super(message);
    }
}
