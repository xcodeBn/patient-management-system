package io.xcodebn.billingservice.service;

import io.xcodebn.billingservice.dto.BalanceResponse;
import io.xcodebn.billingservice.dto.BillingAccountResponse;
import io.xcodebn.billingservice.dto.ChargeRequest;
import io.xcodebn.billingservice.dto.CreateBillingAccountRequest;
import io.xcodebn.billingservice.dto.PaymentRequest;
import io.xcodebn.billingservice.exception.BillingAccountAlreadyExistsException;
import io.xcodebn.billingservice.exception.BillingAccountNotFoundException;
import io.xcodebn.billingservice.exception.InvalidBalanceOperationException;
import io.xcodebn.billingservice.mapper.BillingAccountMapper;
import io.xcodebn.billingservice.model.BillingAccount;
import io.xcodebn.billingservice.repository.BillingAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingAccountService {
    private final BillingAccountRepository billingAccountRepository;

    // ========== Account Management ==========

    @Transactional
    public BillingAccountResponse createBillingAccount(CreateBillingAccountRequest request) {
        log.info("Creating billing account for patient: {}", request.getPatientId());

        // Check if account already exists (idempotency)
        if (billingAccountRepository.existsByPatientId(request.getPatientId())) {
            throw new BillingAccountAlreadyExistsException(
                    "Billing account already exists for patient: " + request.getPatientId());
        }

        BillingAccount billingAccount = BillingAccountMapper.toEntity(request);
        BillingAccount savedAccount = billingAccountRepository.save(billingAccount);

        log.info("Billing account created successfully: {}", savedAccount.getId());

        return BillingAccountMapper.toResponse(savedAccount);
    }

    @Transactional(readOnly = true)
    public BillingAccountResponse getBillingAccountByPatientId(String patientId) {
        BillingAccount account = billingAccountRepository.findByPatientId(patientId)
                .orElseThrow(() -> new BillingAccountNotFoundException(
                        "Billing account not found for patient: " + patientId));
        return BillingAccountMapper.toResponse(account);
    }

    @Transactional(readOnly = true)
    public BillingAccountResponse getBillingAccountById(UUID id) {
        BillingAccount account = billingAccountRepository.findById(id)
                .orElseThrow(() -> new BillingAccountNotFoundException(
                        "Billing account not found with id: " + id));
        return BillingAccountMapper.toResponse(account);
    }

    @Transactional(readOnly = true)
    public List<BillingAccountResponse> getAllBillingAccounts() {
        return billingAccountRepository.findAll().stream()
                .map(BillingAccountMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ========== Balance Management ==========

    @Transactional
    public BillingAccountResponse addCharge(ChargeRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBalanceOperationException("Charge amount must be positive");
        }

        BillingAccount account = billingAccountRepository.findByPatientId(request.getPatientId())
                .orElseThrow(() -> new BillingAccountNotFoundException(
                        "Billing account not found for patient: " + request.getPatientId()));

        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);

        log.info("Added charge of {} to patient {}. New balance: {}",
                request.getAmount(), request.getPatientId(), newBalance);

        BillingAccount savedAccount = billingAccountRepository.save(account);
        return BillingAccountMapper.toResponse(savedAccount);
    }

    @Transactional
    public BillingAccountResponse addPayment(PaymentRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBalanceOperationException("Payment amount must be positive");
        }

        BillingAccount account = billingAccountRepository.findByPatientId(request.getPatientId())
                .orElseThrow(() -> new BillingAccountNotFoundException(
                        "Billing account not found for patient: " + request.getPatientId()));

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(newBalance);

        log.info("Added payment of {} to patient {}. New balance: {}",
                request.getAmount(), request.getPatientId(), newBalance);

        BillingAccount savedAccount = billingAccountRepository.save(account);
        return BillingAccountMapper.toResponse(savedAccount);
    }

    @Transactional(readOnly = true)
    public BalanceResponse getBalance(String patientId) {
        BillingAccount account = billingAccountRepository.findByPatientId(patientId)
                .orElseThrow(() -> new BillingAccountNotFoundException(
                        "Billing account not found for patient: " + patientId));

        return BalanceResponse.builder()
                .patientId(patientId)
                .balance(account.getBalance())
                .status(account.getStatus().toString())
                .build();
    }

    // ========== Status Management ==========

    @Transactional
    public BillingAccountResponse updateAccountStatus(String patientId, BillingAccount.AccountStatus status) {
        BillingAccount account = billingAccountRepository.findByPatientId(patientId)
                .orElseThrow(() -> new BillingAccountNotFoundException(
                        "Billing account not found for patient: " + patientId));

        account.setStatus(status);
        log.info("Updated account status for patient {} to {}", patientId, status);

        BillingAccount savedAccount = billingAccountRepository.save(account);
        return BillingAccountMapper.toResponse(savedAccount);
    }

    @Transactional
    public BillingAccountResponse suspendAccount(String patientId) {
        return updateAccountStatus(patientId, BillingAccount.AccountStatus.SUSPENDED);
    }

    @Transactional
    public BillingAccountResponse activateAccount(String patientId) {
        return updateAccountStatus(patientId, BillingAccount.AccountStatus.ACTIVE);
    }

    @Transactional
    public BillingAccountResponse closeAccount(String patientId) {
        BillingAccount account = billingAccountRepository.findByPatientId(patientId)
                .orElseThrow(() -> new BillingAccountNotFoundException(
                        "Billing account not found for patient: " + patientId));

        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new InvalidBalanceOperationException(
                    "Cannot close account with outstanding balance: " + account.getBalance());
        }

        account.setStatus(BillingAccount.AccountStatus.CLOSED);
        log.info("Closed account for patient {}", patientId);

        BillingAccount savedAccount = billingAccountRepository.save(account);
        return BillingAccountMapper.toResponse(savedAccount);
    }

    // ========== Query Operations ==========

    @Transactional(readOnly = true)
    public long getTotalAccounts() {
        return billingAccountRepository.count();
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalOutstandingBalance() {
        return billingAccountRepository.findAll().stream()
                .map(BillingAccount::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
