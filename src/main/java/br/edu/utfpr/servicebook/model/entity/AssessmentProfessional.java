package br.edu.utfpr.servicebook.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Table(name = "assessment_professionals")
@Entity
public class AssessmentProfessional {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float quality;

    private String comment;

    private LocalDate date;

    @PrePersist
    public void onPersist() {
        this.date = LocalDate.now();
    }

    @ManyToOne
    @JoinColumn(name = "professional_id")
    private User professional;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne
    @JoinColumn(name = "jobRequest_id")
    private JobRequest jobRequest;

    @ManyToOne
    @JoinColumn(name = "assessment_response")
    private AssessmentResponse assessmentResponse;

    @ManyToOne
    @JoinColumn(name = "assessment_files")
    private AssessmentProfessionalFiles assessmentProfessionalFiles;

//    @OneToMany(mappedBy = "assessmentProfessional", cascade = CascadeType.REMOVE)
//    Set<AssessmentResponse> assessmentResponses = new HashSet<>();

}