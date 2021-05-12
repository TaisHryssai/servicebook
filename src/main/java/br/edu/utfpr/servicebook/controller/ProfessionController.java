package br.edu.utfpr.servicebook.controller;

import br.edu.utfpr.servicebook.exception.InvalidParamsException;
import br.edu.utfpr.servicebook.model.dto.ProfessionDTO;
import br.edu.utfpr.servicebook.model.entity.Profession;
import br.edu.utfpr.servicebook.model.mapper.ProfessionMapper;
import br.edu.utfpr.servicebook.service.ProfessionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/profissoes")
@Controller
public class ProfessionController {

    public static final Logger log =
            LoggerFactory.getLogger(ProfessionController.class);

    @Autowired
    private ProfessionService professionService;

    @Autowired
    private ProfessionMapper professionMapper;

    @GetMapping
    public ModelAndView showForm(){
        ModelAndView mv = new ModelAndView("admin/profession-registration");

        mv.addObject("professions", listProfessionDTO());

        return mv;
    }

    @PostMapping
    public ModelAndView save(@Valid ProfessionDTO dto, BindingResult errors, RedirectAttributes redirectAttributes){
        for(FieldError e: errors.getFieldErrors()){
            log.info(e.getField() + " -> " + e.getCode());
        }

        if(errors.hasErrors()){
            return errorFowarding(dto, errors);
        }

        Optional<Profession> optionalProfession = professionService.findByName(dto.getName());

        if (!optionalProfession.isPresent()) {
            Profession profession = professionMapper.toEntity(dto);
            professionService.save(profession);
            List<Profession> professions = professionService.findAll();
            List<ProfessionDTO> professionDTOs = professions.stream()
                    .map(s -> professionMapper.toDto(s))
                    .collect(Collectors.toList());
            redirectAttributes.addFlashAttribute("msg", "Profissão salva com sucesso!");
        } else {
            errors.rejectValue("name", "error.dto", "A profissão já está cadastrada.");
            return errorFowarding(dto, errors);
        }
        return new ModelAndView("redirect:profissoes");
    }

    @GetMapping("/{id}")
    public ModelAndView showFormForUpdate(@PathVariable("id") Long id){
        ModelAndView mv = new ModelAndView("admin/profession-registration");

        if(id < 0){
            throw new InvalidParamsException("O identificador não pode ser negativo.");
        }

        Optional<Profession> optionalProfession = professionService.findById(id);

        if(!optionalProfession.isPresent()){
            throw new EntityNotFoundException("O aluno não foi encontrado pelo id informado.");
        }

        ProfessionDTO professionDTO = professionMapper.toDto(optionalProfession.get());

        mv.addObject("professions", listProfessionDTO());
        mv.addObject("dto", professionDTO);
        return mv;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.debug("Removendo uma profissão com id {}", id);
        Optional <Profession> optionalProfession = this.professionService.findById(id);

        if(!optionalProfession.isPresent()){
            throw new EntityNotFoundException("Erro ao remover, registro não encontrado para o id " + id);
        }

        this.professionService.delete(id);
        redirectAttributes.addFlashAttribute("msg", "Profissão removida com sucesso!");
        return "redirect:/profissoes";
    }

    public ModelAndView errorFowarding(ProfessionDTO dto, BindingResult errors) {
        ModelAndView mv = new ModelAndView("admin/profession-registration");
        mv.addObject("dto", dto);
        mv.addObject("errors", errors.getAllErrors());

        return mv;
    }

    public List<ProfessionDTO> listProfessionDTO (){
        List<Profession> professions = professionService.findAll();

        List<ProfessionDTO> professionDTOs = professions.stream().map(s -> professionMapper.toDto(s)).collect(Collectors.toList());
        return professionDTOs;
    }
}