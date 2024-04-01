package br.edu.utfpr.servicebook.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Table(name = "payment_vouchers")
@Entity
public class PaymentVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String code;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne
    @JoinColumn(name = "professional_id")
    private User professional;

    @ManyToOne
    @JoinColumn(name = "jobrequest_id")
    private JobRequest jobRequest;
    
}
