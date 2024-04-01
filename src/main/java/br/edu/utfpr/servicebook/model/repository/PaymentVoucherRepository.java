package br.edu.utfpr.servicebook.model.repository;

import br.edu.utfpr.servicebook.model.entity.PaymentVoucher;
import br.edu.utfpr.servicebook.model.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentVoucherRepository  extends JpaRepository<PaymentVoucher, Long> {

    List<PaymentVoucher> findAll();


    Page<PaymentVoucher> findAll(Pageable pageable);

    List<PaymentVoucher> findAllById(Iterable<Long> longs);
}
