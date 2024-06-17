package br.edu.utfpr.servicebook.model.dto;

import br.edu.utfpr.servicebook.model.entity.AssessmentProfessionalFiles;
import br.edu.utfpr.servicebook.model.entity.AssessmentResponse;
import br.edu.utfpr.servicebook.model.entity.JobRequest;
import br.edu.utfpr.servicebook.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AssessmentProfessionalDTO implements Serializable {
    private Long id;

    private float quality;

    private String comment;

    private String date;

    private Long professionalId;

    private User professional;

    private Long clientId;

    private User client;

    private Long jobRequestId;

    private JobRequest jobRequest;

    private AssessmentProfessionalFileDTO assessmentProfessionalFiles;

    private AssessmentResponseDTO assessmentResponses;

}