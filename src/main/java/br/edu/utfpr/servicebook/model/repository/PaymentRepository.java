package br.edu.utfpr.servicebook.model.repository;

import br.edu.utfpr.servicebook.model.entity.JobRequest;
import br.edu.utfpr.servicebook.model.entity.Payment;

import br.edu.utfpr.servicebook.model.entity.PaymentJobRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>{

    Optional<Payment> findByPaymentId(Long id);

}

