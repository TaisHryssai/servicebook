package br.edu.utfpr.servicebook.model.dto;

import br.edu.utfpr.servicebook.model.entity.JobImages;
import br.edu.utfpr.servicebook.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRequestFullDTO implements Serializable {

    private Long id;
    private ExpertiseMinDTO expertise;
    private String dateCreated;
    private ClientMinDTO individual;
    private String dateTarget;
    private Integer quantityCandidatorsMax;
    private String description;
    private Long totalCandidates;
    private String textualDate;
    private Long amountOfCandidates;
    private User user;

    public String status;
    private MultipartFile imageFile;
    private Set<JobImages> jobImages = new HashSet<>();


}
