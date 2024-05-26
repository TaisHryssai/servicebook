package br.edu.utfpr.servicebook.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobContractedFullDTO {

    private Long id;
    private String comments;
    private int rating;
    private JobRequestFullDTO jobRequest;
    private String todoDate;

}
