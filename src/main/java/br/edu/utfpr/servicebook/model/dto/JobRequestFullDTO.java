package br.edu.utfpr.servicebook.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRequestFullDTO implements Serializable {

    private Long id;
    private ExpertiseMinDTO expertise;
    private ClientMinDTO client;
    private String dateCreated;
    private String dateExpired;
    private Integer quantityCandidatorsMax;
    private String description;
    private Long totalCandidates;
    private Long intervalOfDays;

}
