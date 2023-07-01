package br.edu.utfpr.servicebook.controller.admin;

import br.edu.utfpr.servicebook.exception.InvalidParamsException;
import br.edu.utfpr.servicebook.model.dto.ExpertiseDTO;
import br.edu.utfpr.servicebook.model.dto.ServiceDTO;
import br.edu.utfpr.servicebook.model.entity.Expertise;
import br.edu.utfpr.servicebook.model.entity.Service;
import br.edu.utfpr.servicebook.model.mapper.ExpertiseMapper;
import br.edu.utfpr.servicebook.model.mapper.ServiceMapper;
import br.edu.utfpr.servicebook.security.RoleType;
import br.edu.utfpr.servicebook.service.ExpertiseService;
import br.edu.utfpr.servicebook.service.ServiceService;
import br.edu.utfpr.servicebook.util.pagination.PaginationDTO;
import br.edu.utfpr.servicebook.util.pagination.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/a/servicos")
@Controller
public class ServiceController {
    public static final Logger log =
            LoggerFactory.getLogger(ExpertiseController.class);

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private PaginationUtil paginationUtil;

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private ExpertiseService expertiseService;

    @Autowired
    private ExpertiseMapper expertiseMapper;

    @GetMapping
    @PermitAll
    public ModelAndView showForm(HttpServletRequest request,
                                 @RequestParam(value = "pag", defaultValue = "1") int page,
                                 @RequestParam(value = "siz", defaultValue = "5") int size,
                                 @RequestParam(value = "ord", defaultValue = "name") String order,
                                 @RequestParam(value = "dir", defaultValue = "ASC") String direction){

        ModelAndView mv = new ModelAndView("admin/service-register");

        //paginação de serviços
        PageRequest pageRequest = PageRequest.of(page-1, size, Sort.Direction.valueOf(direction), order);

        List<Expertise> expertises = expertiseService.findAll();
        List<ExpertiseDTO> expertiseDTOs = expertises.stream()
                .map(s -> expertiseMapper.toDto(s))
                .collect(Collectors.toList());
        mv.addObject("expertises", expertiseDTOs);

        Page<Service> servicePage = serviceService.findAll(pageRequest);
        List<ServiceDTO> serviceDTOS = servicePage.stream()
                .map(s -> serviceMapper.toDto(s))
                .collect(Collectors.toList());
        mv.addObject("services", serviceDTOS);

        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(servicePage);
        mv.addObject("pagination", paginationDTO);

        return mv;
    }

    /**
     * @param dto
     * @param errors
     * @param redirectAttributes
     * @return
     */
    @PostMapping
    public ModelAndView save(@Valid ServiceDTO dto, BindingResult errors, RedirectAttributes redirectAttributes){

        for(FieldError e: errors.getFieldErrors()){
            log.info(e.getField() + " -> " + e.getCode());
        }

        if(errors.hasErrors()){
            return errorFowarding(dto, errors);
        }

        Optional<Expertise> oExpertise = expertiseService.findById(dto.getExpertiseId());
        if(!oExpertise.isPresent()){
            throw new EntityNotFoundException("A especialidade não foi encontrada!");
        }

        //verifica se o id é nulo, se for, é um novo registro
        if(dto.getId() == null){
            //verifica se já existe um serviço com o mesmo nome e especialidade
            Optional<Service> oService = serviceService.findByNameAndExpertise(dto.getName(), oExpertise.get());
            if (oService.isPresent()) {
                errors.rejectValue("name", "error.dto", "O serviço já está cadastrado!");
                return errorFowarding(dto, errors);
            }
        }

        //verifica se o id é diferente de nulo, se for, é uma atualização
        if(dto.getId() != null){
            // Lógica para atualização de um serviço existente
            Optional<Service> oExistingService = serviceService.findById(dto.getId());

            if (!oExistingService.isPresent()) {
                throw new EntityNotFoundException("O serviço não foi encontrado!");
            }

            // Atualize as propriedades necessárias do serviço existente com base nos dados do DTO
            Service service = oExistingService.get();

            //verifica se o usuário mudou o nome para um serviço existente
            Optional<Service> otherService = serviceService.findByNameAndExpertise(dto.getName(), oExpertise.get());
            if (otherService.isPresent()) {
                if(service.getId() != otherService.get().getId()) {
                    errors.rejectValue("name", "error.dto", "O serviço já está cadastrado!");
                    return errorFowarding(dto, errors);
                }
            }
        }

        Service service = serviceMapper.toEntity(dto);
        service.setExpertise(oExpertise.get());
        serviceService.save(service);

        redirectAttributes.addFlashAttribute("msg", "Serviço salvo com sucesso!");

        return new ModelAndView("redirect:/a/servicos");
    }

    /**
     * Mostra o formulário para atualizar uma especialidade.
     * @param id
     * @param request
     * @param page
     * @param size
     * @param order
     * @param direction
     * @return
     */
    @GetMapping("/{id}")
    @RolesAllowed({RoleType.ADMIN})
    public ModelAndView showFormForUpdate(@PathVariable("id") Long id, HttpServletRequest request,
                                          @RequestParam(value = "pag", defaultValue = "1") int page,
                                          @RequestParam(value = "siz", defaultValue = "4") int size,
                                          @RequestParam(value = "ord", defaultValue = "name") String order,
                                          @RequestParam(value = "dir", defaultValue = "ASC") String direction){

        ModelAndView mv = new ModelAndView("admin/service-register");

        if(id < 0){
            throw new InvalidParamsException("O identificador não pode ser negativo.");
        }

        Optional<Service> oService = serviceService.findById(id);

        if(!oService.isPresent()){
            throw new EntityNotFoundException("O serviço não foi encontrado!");
        }

        ServiceDTO serviceDTO = serviceMapper.toDto(oService.get());
        mv.addObject("dto", serviceDTO);

        PageRequest pageRequest = PageRequest.of(page-1, size, Sort.Direction.valueOf(direction), order);

        List<Expertise> expertises = expertiseService.findAll();
        List<ExpertiseDTO> expertiseDTOs = expertises.stream()
                .map(s -> expertiseMapper.toDto(s))
                .collect(Collectors.toList());
        mv.addObject("expertises", expertiseDTOs);

        Page<Service> servicePage = serviceService.findAll(pageRequest);
        List<ServiceDTO> serviceDTOS = servicePage.stream()
                .map(s -> serviceMapper.toDto(s))
                .collect(Collectors.toList());
        mv.addObject("services", serviceDTOS);

        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(servicePage, "/servicos/" + id);
        mv.addObject("pagination", paginationDTO);

        return mv;
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({RoleType.ADMIN})
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) throws IOException {
        log.debug("Removendo um serviço com id {}", id);
        Optional <Service> optionalService = this.serviceService.findById(id);
        ServiceDTO serviceDTO = serviceMapper.toDto(optionalService.get());

        if(!optionalService.isPresent()){
            throw new EntityNotFoundException("Erro ao remover, registro não encontrado para o id " + id);
        }

        try{
            this.serviceService.delete(id);
            redirectAttributes.addFlashAttribute("msg", "Serviço removido com sucesso!");
            return "redirect:/a/servicos";
        }catch (Exception exception) {
            redirectAttributes.addFlashAttribute("msgError", "Serviço não pode ser removido pois já esta sendo utilizado!");
            return "redirect:/a/servicos";
        }
    }

    private ModelAndView errorFowarding(ServiceDTO dto, BindingResult errors) {
        ModelAndView mv = new ModelAndView("admin/service-register");
        mv.addObject("dto", dto);
        mv.addObject("errors", errors.getAllErrors());

        List<Expertise> expertises = expertiseService.findAll();
        List<ExpertiseDTO> expertiseDTOs = expertises.stream()
                .map(s -> expertiseMapper.toDto(s))
                .collect(Collectors.toList());
        mv.addObject("expertises", expertiseDTOs);

        //paginação de serviços
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Service> servicePage = serviceService.findAll(pageRequest);
        List<ServiceDTO> serviceDTOS = servicePage.stream()
                .map(s -> serviceMapper.toDto(s))
                .collect(Collectors.toList());
        mv.addObject("services", serviceDTOS);

        //carrega a paginação
        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(servicePage);
        mv.addObject("pagination", paginationDTO);

        return mv;
    }
}