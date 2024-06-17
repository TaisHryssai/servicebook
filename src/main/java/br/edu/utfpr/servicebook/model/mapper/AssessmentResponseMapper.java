package br.edu.utfpr.servicebook.model.mapper;

import br.edu.utfpr.servicebook.model.dto.AssessmentProfessionalDTO;
import br.edu.utfpr.servicebook.model.dto.AssessmentResponseDTO;
import br.edu.utfpr.servicebook.model.entity.AssessmentProfessional;
import br.edu.utfpr.servicebook.model.entity.AssessmentResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class AssessmentResponseMapper {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private ModelMapper mapper;

    public AssessmentResponseDTO toFullDto(AssessmentProfessional entity) {
        AssessmentResponseDTO dto = mapper.map(entity, AssessmentResponseDTO.class);

        return dto;
    }
}
