package br.edu.utfpr.servicebook.model.mapper;

import br.edu.utfpr.servicebook.model.dto.PaymentVoucherDTO;
import br.edu.utfpr.servicebook.model.dto.ProfessionalExpertiseDTO;
import br.edu.utfpr.servicebook.model.entity.PaymentVoucher;
import br.edu.utfpr.servicebook.model.entity.ProfessionalExpertise;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentVoucherMapper {
    @Autowired
    private ModelMapper mapper;

    public PaymentVoucherDTO toDTO(PaymentVoucher entity) {
        return mapper.map(entity, PaymentVoucherDTO.class);
    }

    public PaymentVoucher toEntity(PaymentVoucherDTO dto) {
        PaymentVoucher entity = mapper.map(dto, PaymentVoucher.class);
        return entity;
    }
}
