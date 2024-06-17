package br.edu.utfpr.servicebook.model.dto;

import br.edu.utfpr.servicebook.model.entity.AssessmentProfessional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AssessmentProfessionalFileDTO implements Serializable {
    private Long id;

//    private String path;

    //    private String video;
    private String image;

    private MultipartFile pathImage;

    private String comment;

    private Long assessmentProfessionalId;

    private AssessmentProfessional assessmentProfessional;
}