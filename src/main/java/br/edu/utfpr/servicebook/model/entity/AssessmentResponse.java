package br.edu.utfpr.servicebook.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude={"assessmentProfessional"})

@Table(name = "assessment_responses")
@Entity
public class AssessmentResponse{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;

//    @EmbeddedId
//    private AssessmentResponsePK id;

    @ManyToOne
    @JoinColumn(name = "professional_id")
    private User professional;

    private String response;

//    @ManyToOne
//    @MapsId("assessmentProfessionalId")
//    @JoinColumn(name = "assessment_professional")
//    private AssessmentProfessional assessmentProfessional;

    @ManyToOne
    @JoinColumn(name = "assessment_professional")
    private AssessmentProfessional assessmentProfessional;


    @PrePersist
    public void onPersist() {
        this.date = LocalDate.now();
    }

//    public AssessmentResponse(AssessmentProfessional assessmentProfessional) {
//        this.assessmentProfessional = assessmentProfessional;
//        this.id = new AssessmentResponsePK(assessmentProfessional.getId());
//    }
}
