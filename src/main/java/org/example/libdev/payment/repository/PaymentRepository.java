package org.example.libdev.payment.repository;

import org.example.libdev.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findPaymentByImpUid(String uid);
}
