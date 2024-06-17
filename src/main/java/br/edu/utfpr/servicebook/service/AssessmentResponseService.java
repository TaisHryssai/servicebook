package br.edu.utfpr.servicebook.service;

import br.edu.utfpr.servicebook.model.entity.AssessmentProfessional;
import br.edu.utfpr.servicebook.model.entity.AssessmentResponse;
import br.edu.utfpr.servicebook.model.repository.AssessmentResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AssessmentResponseService {
    @Autowired

    private AssessmentResponseRepository assessmentResponseRepository;

    public List<AssessmentResponse> findAll() {
        return this.assessmentResponseRepository.findAll();
    }

    public AssessmentResponse save(AssessmentResponse assessmentProfessional) {
        return this.assessmentResponseRepository.save(assessmentProfessional);
    }

    public List<AssessmentResponse> findAssessmentProfessionalById(Long id) {
        return this.assessmentResponseRepository.findAssessmentProfessionalById(id);
    }

    public Set<AssessmentResponse> findAllByAssessmentProfessional(AssessmentProfessional assessmentProfessional) {
        return this.assessmentResponseRepository.findAllByAssessmentProfessional(assessmentProfessional);
    }


    public Set<AssessmentResponse> findAssessmentProfessionalById(AssessmentProfessional assessmentProfessional) {
        return this.assessmentResponseRepository.findAssessmentProfessionalById(assessmentProfessional);
    }
}
