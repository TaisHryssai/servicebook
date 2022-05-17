package br.edu.utfpr.servicebook.model.mapper;

import br.edu.utfpr.servicebook.model.dto.ExpertiseDTO;
import br.edu.utfpr.servicebook.model.dto.ProfessionalExpertiseDTO;
import br.edu.utfpr.servicebook.model.dto.ProfessionalExpertiseDTO2;
import br.edu.utfpr.servicebook.model.dto.ProfessionalSearchItemDTO;
import br.edu.utfpr.servicebook.model.entity.Expertise;
import br.edu.utfpr.servicebook.model.entity.Individual;
import br.edu.utfpr.servicebook.model.entity.ProfessionalExpertise;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProfessionalExpertiseMapper {
    @Autowired
    private ModelMapper mapper;

    public ProfessionalExpertiseDTO toDTO(ProfessionalExpertise entity) {
        return mapper.map(entity, ProfessionalExpertiseDTO.class);
    }

    public ProfessionalExpertiseDTO2 toResponseDTO(ProfessionalExpertise entity) {
        ProfessionalExpertiseDTO2 dto = mapper.map(entity, ProfessionalExpertiseDTO2.class);
        dto.setName(entity.getExpertise().getName());
        return dto;
    }

    public ProfessionalExpertise toEntity(ProfessionalExpertiseDTO dto) {
        return mapper.map(dto, ProfessionalExpertise.class);
    }

    public ProfessionalSearchItemDTO toSearchItemDto(Expertise entity, List<ExpertiseDTO> expertises){
        ProfessionalSearchItemDTO dto = mapper.map(entity, ProfessionalSearchItemDTO.class);
        dto.setExpertises(expertises);
        return dto;
    }
}
