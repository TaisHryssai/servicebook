package br.edu.utfpr.servicebook.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentVoucherDTO implements Serializable {
    private Long id;
    private Long paymentId;
    private Long clientId;
    private Long professionalId;
    private Long jobRequestId;

    private String code;
}
