package br.edu.utfpr.servicebook.controller;

import br.edu.utfpr.servicebook.model.dto.ExpertiseDTO;
import br.edu.utfpr.servicebook.model.dto.ProfessionalServiceOfferingDTO;
import br.edu.utfpr.servicebook.model.entity.Expertise;
import br.edu.utfpr.servicebook.model.entity.ProfessionalServiceOffering;
import br.edu.utfpr.servicebook.model.entity.User;
import br.edu.utfpr.servicebook.model.mapper.*;
import br.edu.utfpr.servicebook.security.IAuthentication;
import br.edu.utfpr.servicebook.security.RoleType;
import br.edu.utfpr.servicebook.service.*;
import br.edu.utfpr.servicebook.util.TemplateUtil;
import br.edu.utfpr.servicebook.util.UserTemplateStatisticInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RequestMapping("/minha-conta/profissional/servicos")
@Controller
public class ProfessionalServiceOfferingController {
    public static final Logger log = LoggerFactory.getLogger(ProfessionalHomeController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private ProfessionalServiceOfferingService professionalServiceOfferingService;

    @Autowired
    private ExpertiseService expertiseService;

    @Autowired
    private ExpertiseMapper expertiseMapper;

    @Autowired
    private ProfessionalServiceOfferingMapper ProfessionalServiceOfferingMapper;

    @Autowired
    private TemplateUtil templateUtil;

    @Autowired
    private IAuthentication authentication;

    @GetMapping("/{id}")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public ModelAndView showExpertises(@PathVariable Long id)  throws Exception {

        User user = this.getAuthenticatedUser();

        Expertise expertise = expertiseService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Especialidade não encontrada"));

        ExpertiseDTO expertiseDTO = expertiseMapper.toDto(expertise);

        List<ProfessionalServiceOffering> professionalServiceOfferings = professionalServiceOfferingService.findProfessionalServiceOfferingByUserAndService_Expertise(user, expertise);

        //transforma a lista de ofertas de serviços em uma lista de DTOs com stream
        List<ProfessionalServiceOfferingDTO> professionalServiceOfferingsDTO = professionalServiceOfferings.stream()
                .map(professionalServiceOffering -> ProfessionalServiceOfferingMapper.toDTO(professionalServiceOffering))
                .toList();

        ModelAndView mv = new ModelAndView("professional/my-services");
        mv.addObject("expertise", expertiseDTO);
        mv.addObject("professionalServiceOfferings", professionalServiceOfferingsDTO);

        return mv;
    }


    /**
     * Retorna o usuário logado.
     * @return
     * @throws Exception
     */
    private User getAuthenticatedUser() throws Exception {
        Optional<User> oProfessional = (userService.findByEmail(authentication.getEmail()));

        if (!oProfessional.isPresent()) {
            throw new EntityNotFoundException("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        return oProfessional.get();
    }

}