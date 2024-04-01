package br.edu.utfpr.servicebook.model.mapper;

import br.edu.utfpr.servicebook.model.dto.PaymentJobDTO;
import br.edu.utfpr.servicebook.model.dto.PaymentVoucherDTO;
import br.edu.utfpr.servicebook.model.entity.PaymentJobRequest;
import br.edu.utfpr.servicebook.model.entity.PaymentVoucher;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentJobMapper {
    @Autowired
    private ModelMapper mapper;

    public PaymentJobDTO toDTO(PaymentJobRequest entity) {
        return mapper.map(entity, PaymentJobDTO.class);
    }

    public PaymentJobRequest toEntity(PaymentJobDTO dto) {
        PaymentJobRequest entity = mapper.map(dto, PaymentJobRequest.class);
        return entity;
    }
}
