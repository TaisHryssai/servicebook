package br.edu.utfpr.servicebook.model.repository;

import br.edu.utfpr.servicebook.model.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentProfessionalRepository extends JpaRepository<AssessmentProfessional, Long> {

    List<AssessmentProfessional> findAll();

    List<AssessmentProfessional> findAssessmentProfessionalById(Long id);
    @Query("SELECT j FROM AssessmentProfessional j WHERE j.professional.id = :user_id AND j.jobRequest.id = :job_id")
    List<AssessmentProfessional> findAssessmentProfessionalByIdAndJobRequest(Long user_id, Long job_id);

//    @Query("SELECT j FROM AssessmentProfessional j  WHERE j.professional.id = :user_id AND j.jobRequest.id = :job_id")
//    List<AssessmentProfessional> findAssessmentProfessionalByAssessmentResponses(Long user_id, Long job_id);

    @Query("SELECT j, ar FROM AssessmentProfessional j LEFT JOIN AssessmentResponse ar on ar.assessmentProfessional.id = j.id WHERE j.professional.id = :user_id AND j.jobRequest.id = :job_id")
    List<Object[]> findAssessmentProfessionalByAssessmentResponses(Long user_id, Long job_id);

    @Query("SELECT j, ar, apf FROM AssessmentProfessional j LEFT JOIN AssessmentResponse ar on ar.assessmentProfessional.id = j.id "+
    " LEFT JOIN AssessmentProfessionalFiles apf on apf.assessmentProfessional.id = j.id WHERE j.professional.id = :user_id")
    List<Object[]> findAssessmentProfessionalByProfessional(Long user_id);

}