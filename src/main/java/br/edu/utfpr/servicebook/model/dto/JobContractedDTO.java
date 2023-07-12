package br.edu.utfpr.servicebook.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobContractedDTO {

    private Long id;

    private String status;

    private String comments;

    private int rating;

    private Long idJobRequest;

    private Long idProfessional;

    private LocalDate todoDate;

    private LocalDate hiredDate;


}
