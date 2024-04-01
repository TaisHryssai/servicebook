package br.edu.utfpr.servicebook.model.repository;

import br.edu.utfpr.servicebook.model.entity.JobRequest;
import br.edu.utfpr.servicebook.model.entity.PaymentJobRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentJobRepository extends JpaRepository<PaymentJobRequest, Long> {
    List<PaymentJobRequest> findAll();

    Page<PaymentJobRequest> findAll(Pageable pageable);

    List<PaymentJobRequest> findAllById(Iterable<Long> longs);

    Optional<PaymentJobRequest> findByJobRequest(JobRequest id);
}
