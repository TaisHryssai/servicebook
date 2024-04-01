package br.edu.utfpr.servicebook.service;

import br.edu.utfpr.servicebook.model.entity.JobRequest;
import br.edu.utfpr.servicebook.model.entity.PaymentJobRequest;
import br.edu.utfpr.servicebook.model.repository.PaymentJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentJobService {
    @Autowired
    private PaymentJobRepository paymentJobRepository;

    public void save(PaymentJobRequest entity) {
        paymentJobRepository.save(entity);
    }

    public List<PaymentJobRequest> findAll() {
        return this.paymentJobRepository.findAll();
    }

    public Optional<PaymentJobRequest> findById(Long id) {
        return this.paymentJobRepository.findById(id);
    }

    public Optional<PaymentJobRequest> findByJobRequest(JobRequest id) {
        return this.paymentJobRepository.findByJobRequest(id);
    }
}
