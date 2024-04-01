package br.edu.utfpr.servicebook.service;

import br.edu.utfpr.servicebook.model.entity.PaymentVoucher;
import br.edu.utfpr.servicebook.model.repository.PaymentVoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentVoucherService {

    @Autowired
    private PaymentVoucherRepository paymentVoucherRepository;

    public void save(PaymentVoucher entity) {
        paymentVoucherRepository.save(entity);
    }

    public List<PaymentVoucher> findAll() {
        return this.paymentVoucherRepository.findAll();
    }

    public Optional<PaymentVoucher> findById(Long id) {
        return this.paymentVoucherRepository.findById(id);
    }

}
