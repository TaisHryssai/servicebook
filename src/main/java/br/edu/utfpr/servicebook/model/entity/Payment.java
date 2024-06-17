package br.edu.utfpr.servicebook.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "payments")
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true)
    private Long paymentId;

    @NonNull
    @Column()
    private String status;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<PaymentJobRequest> paymentJobs;
}