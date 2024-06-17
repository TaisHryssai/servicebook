package br.edu.utfpr.servicebook.service;

import br.edu.utfpr.servicebook.model.entity.*;
import br.edu.utfpr.servicebook.model.repository.AssessmentProfessionalRepository;
import br.edu.utfpr.servicebook.model.repository.ProfessionalExpertiseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssessmentProfessionalService {

    @Autowired
    private AssessmentProfessionalRepository assessmentProfessionalRepository;

    public List<AssessmentProfessional> findAll() {
        return this.assessmentProfessionalRepository.findAll();
    }

    public AssessmentProfessional save(AssessmentProfessional assessmentProfessional) {
        return this.assessmentProfessionalRepository.save(assessmentProfessional);
    }

    public Optional<AssessmentProfessional> findById(Long assessmentProfessional){
        return this.assessmentProfessionalRepository.findById(assessmentProfessional);
    }

    public List<AssessmentProfessional> findByProfessionalId(Long professional_id){
        return this.assessmentProfessionalRepository.findAssessmentProfessionalById(professional_id);
    }

    public List<AssessmentProfessional> findAssessmentProfessionalByIdAndJobRequest(Long professional_id, Long job_id){
        return this.assessmentProfessionalRepository.findAssessmentProfessionalByIdAndJobRequest(professional_id, job_id);
    }

    public List<Object[]> findAssessmentProfessionalByAssessmentResponses(Long professional_id, Long job_id){
        return this.assessmentProfessionalRepository.findAssessmentProfessionalByAssessmentResponses(professional_id, job_id);
    }

    public List<Object[]> findAssessmentProfessionalByProfessional(Long professional_id){
        return this.assessmentProfessionalRepository.findAssessmentProfessionalByProfessional(professional_id);
    }
}