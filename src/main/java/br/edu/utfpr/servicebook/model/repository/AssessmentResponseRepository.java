package br.edu.utfpr.servicebook.model.repository;

import br.edu.utfpr.servicebook.model.entity.AssessmentProfessional;
import br.edu.utfpr.servicebook.model.entity.AssessmentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface AssessmentResponseRepository  extends JpaRepository<AssessmentResponse, Long> {

    List<AssessmentResponse> findAll();

    List<AssessmentResponse> findAssessmentProfessionalById(Long id);

    Set<AssessmentResponse> findAllByAssessmentProfessional(AssessmentProfessional professional);

    @Query("SELECT j FROM AssessmentResponse j WHERE j.assessmentProfessional = :professional")
    Set<AssessmentResponse> findAssessmentProfessionalById(AssessmentProfessional professional);

}
