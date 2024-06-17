package br.edu.utfpr.servicebook.model.dto;

import br.edu.utfpr.servicebook.model.entity.AssessmentProfessional;
import br.edu.utfpr.servicebook.model.entity.JobImages;
import br.edu.utfpr.servicebook.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AssessmentResponseDTO implements Serializable {
    private Long id;

    private AssessmentProfessional assessmentProfessional;

    private User professional;

    private String date;

    private String response;

}
