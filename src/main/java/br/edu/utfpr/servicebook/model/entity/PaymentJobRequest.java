package br.edu.utfpr.servicebook.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@Table(name = "payments_jobRequests")
@Entity
public class PaymentJobRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private Long jobRequestId;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "jobrequest_id")
    private JobRequest jobRequest;

    @Temporal(TemporalType.DATE)
    private Date dateCreated;
}
