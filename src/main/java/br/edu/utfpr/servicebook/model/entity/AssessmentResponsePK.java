package br.edu.utfpr.servicebook.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AssessmentResponsePK implements Serializable {

    Long assessmentProfessionalId;

}
