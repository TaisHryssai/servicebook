package br.edu.utfpr.servicebook.model.mapper;

import br.edu.utfpr.servicebook.model.dto.*;
import br.edu.utfpr.servicebook.model.entity.*;
import br.edu.utfpr.servicebook.util.DateUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class AssessmentProfessionalMapper {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private AssessmentProfessionalFileMapper assessmentProfessionalFileMapper;
    public AssessmentProfessionalDTO toDTO(AssessmentProfessional entity) {
        return mapper.map(entity, AssessmentProfessionalDTO.class);
    }

    public AssessmentProfessionalDTO toResponseDTO(AssessmentProfessional entity) {
        AssessmentProfessionalDTO dto = mapper.map(entity, AssessmentProfessionalDTO.class);
        return dto;
    }

    public AssessmentProfessional toEntity(AssessmentProfessionalDTO dto) {
        return mapper.map(dto, AssessmentProfessional.class);
    }

    public AssessmentProfessionalDTO toFullDto(AssessmentProfessional entity) {
        AssessmentProfessionalDTO dto = mapper.map(entity, AssessmentProfessionalDTO.class);
//        dto.setAssessmentProfessionalFiles(entity.getAssessmentProfessionalFiles());
        dto.setDate(this.dateTimeFormatter.format(entity.getDate()));
        dto.setAssessmentProfessionalFiles(dto.getAssessmentProfessionalFiles());

        dto.setAssessmentResponses(dto.getAssessmentResponses());
        return dto;
    }


    public AssessmentResponseDTO toResponseDto(AssessmentResponse entity) {
        AssessmentResponseDTO dto = mapper.map(entity, AssessmentResponseDTO.class);
        if(!entity.getResponse().isEmpty() ){
            dto.setResponse(entity.getResponse());
        }

        return dto;
    }

    public AssessmentProfessionalFileDTO toFilesDto(AssessmentProfessionalFiles entity) {
        AssessmentProfessionalFileDTO dto = mapper.map(entity, AssessmentProfessionalFileDTO.class);
        dto.setImage(entity.getPathImage());

        return dto;
    }
}