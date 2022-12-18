package br.edu.utfpr.servicebook.controller;

import br.edu.utfpr.servicebook.exception.InvalidParamsException;
import br.edu.utfpr.servicebook.model.dto.*;
import br.edu.utfpr.servicebook.model.entity.*;
import br.edu.utfpr.servicebook.model.mapper.*;
import br.edu.utfpr.servicebook.service.*;
import br.edu.utfpr.servicebook.sse.EventSse;
import br.edu.utfpr.servicebook.sse.EventSseDTO;
import br.edu.utfpr.servicebook.sse.EventSseMapper;
import br.edu.utfpr.servicebook.sse.SSEService;
import br.edu.utfpr.servicebook.util.CurrentUserUtil;
import br.edu.utfpr.servicebook.util.pagination.PaginationDTO;
import br.edu.utfpr.servicebook.util.pagination.PaginationUtil;
import br.edu.utfpr.servicebook.util.sidePanel.SidePanelIndividualDTO;
import br.edu.utfpr.servicebook.util.sidePanel.SidePanelItensDTO;
import br.edu.utfpr.servicebook.util.sidePanel.SidePanelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/minha-conta/profissional")
@Controller
public class ProfessionalHomeController {

    public static final Logger log = LoggerFactory.getLogger(ProfessionalHomeController.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Autowired
    private IndividualService individualService;

    @Autowired
    private IndividualMapper individualMapper;

    @Autowired
    private CityService cityService;

    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private ProfessionalExpertiseService professionalExpertiseService;

    @Autowired
    private ExpertiseService expertiseService;

    @Autowired
    private ExpertiseMapper expertiseMapper;

    @Autowired
    private JobContractedService jobContractedService;

    @Autowired
    private JobContractedMapper jobContractedMapper;

    @Autowired
    private JobRequestService jobRequestService;

    @Autowired
    private JobRequestMapper jobRequestMapper;

    @Autowired
    private JobCandidateService jobCandidateService;

    @Autowired
    private JobCandidateMapper jobCandidateMapper;

    @Autowired
    private StateService stateService;

    @Autowired
    private SidePanelUtil sidePanelUtil;

    @Autowired
    private SSEService sseService;

    @Autowired
    private EventSseMapper eventSseMapper;

    @Autowired
    private JobAvailableToHideService jobAvailableToHideService;

    @GetMapping
    public ModelAndView showMyAccountProfessional(@RequestParam(required = false, defaultValue = "0") Optional<Long> id) throws Exception {
        log.debug("ServiceBook: Minha conta.");
   
        Optional<Individual> oProfessional = (individualService.findByEmail(CurrentUserUtil.getCurrentUserEmail()));

        System.err.println("l103 TESTE PARA VER O OBJETO OpROFESSIONAL..." + oProfessional);
        System.err.println("l104 TESTE PARA VER O OBJETO individual service..." + individualService.findByEmail(CurrentUserUtil.getCurrentUserEmail()));

        if (!oProfessional.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        ModelAndView mv = new ModelAndView("professional/my-account");
    
        List<ProfessionalExpertise> professionalExpertises = professionalExpertiseService.findByProfessional(oProfessional.get());
        List<ExpertiseDTO> expertiseDTOs = professionalExpertises.stream()
                .map(professionalExpertise -> professionalExpertise.getExpertise())
                .map(expertise -> expertiseMapper.toDto(expertise))
                .collect(Collectors.toList());

        mv.addObject("expertises", expertiseDTOs);

        boolean isClient = false;
        mv.addObject("isClient", isClient);

        IndividualDTO professionalMinDTO = individualMapper.toDto(oProfessional.get());

        System.err.println("l124 TESTE PARA VER O OBJETO professionalMinDTo..." + professionalMinDTO);
        System.err.println("l125 TESTE PARA VER O OBJETO indicidual mapper..." + individualMapper.toResponseDto(oProfessional.get()));

        SidePanelIndividualDTO sidePanelIndividualDTO = SidePanelUtil.getSidePanelDTO(professionalMinDTO);

        System.err.println(" l128 TESTE PARA VER O OBJETO sidePanelIndividualDTO..." + sidePanelIndividualDTO);


        mv.addObject("user", sidePanelIndividualDTO);

        SidePanelItensDTO sidePanelItensDTO = sidePanelUtil.getSidePanelStats(oProfessional.get(), id.get());

        System.err.println(" l128 TESTE PARA VER O OBJETO sidePanelItensDTO..." + sidePanelItensDTO);


        mv.addObject("dataIndividual", sidePanelItensDTO);
        mv.addObject("id", id.orElse(0L));

        //FAZER ENVIO DE NOTIFICAÇÃO PARA O PROFISSIONAL
        List<EventSse> eventSsesList = sseService.findPendingEventsByEmail(CurrentUserUtil.getCurrentUserEmail());
        List<EventSseDTO> eventSseDTOS = eventSsesList.stream()
                .map(eventSse -> {
                    return eventSseMapper.toFullDto(eventSse);
                })
                .collect(Collectors.toList());
        mv.addObject("eventsse", eventSseDTOS);
        //FAZER ENVIO DE NOTIFICAÇÃO PARA O PROFISSIONAL ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


        return mv;
    }

    /**
     * Retorna a lista de jobs no estado de disponíveis. Se o profissional estiver com o filtro de especialidade, busca
     * por especialidade ou então, por todas as especialidades.
     * Se o profissional marcou alguns anúncios para não aparecer, estes não aparecerão. Há um filtro na busca no BD.
     * @param request
     * @param id
     * @param page
     * @param size
     * @param order
     * @param direction
     * @return
     * @throws Exception
     */
    @GetMapping("/disponiveis")
    public ModelAndView showAvailableJobs(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Long id,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "5") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Optional<Individual> oIndividual = (individualService.findByEmail(CurrentUserUtil.getCurrentUserEmail()));

        if (!oIndividual.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("dateExpired").ascending());
        Page<JobRequest> jobRequestPage = null;
        List<JobRequestFullDTO> jobRequestFullDTOs = null;

        if (id == 0) {
            jobRequestPage = jobRequestService.findAvailableAllExpertises(JobRequest.Status.AVAILABLE, oIndividual.get().getId(), pageRequest);
        } else {

            if (id < 0) {
                throw new InvalidParamsException("O identificador da especialidade não pode ser negativo. Por favor, tente novamente.");
            }

            Optional<Expertise> oExpertise = expertiseService.findById(id);

            if (!oExpertise.isPresent()) {
                throw new EntityNotFoundException("A especialidade não foi encontrada pelo id informado. Por favor, tente novamente.");
            }

            Optional<ProfessionalExpertise> oProfessionalExpertise = professionalExpertiseService.findByProfessionalAndExpertise(oIndividual.get(), oExpertise.get());

            if (!oProfessionalExpertise.isPresent()) {
                throw new InvalidParamsException("A especialidade profissional não foi encontrada. Por favor, tente novamente.");
            }

            jobRequestPage = jobRequestService.findAvailableByExpertise(JobRequest.Status.AVAILABLE, oExpertise.get(), oIndividual.get().getId(), pageRequest);
        }

        jobRequestFullDTOs = jobRequestPage.stream().distinct()
                .map(jobRequest -> {
                    Optional<Long> totalCandidates = jobCandidateService.countByJobRequest(jobRequest);

                    if (totalCandidates.isPresent()) {
                        return jobRequestMapper.toFullDto(jobRequest, totalCandidates);
                    }

                    return jobRequestMapper.toFullDto(jobRequest, Optional.ofNullable(0L));
                }).collect(Collectors.toList());

        PaginationDTO paginationDTO = PaginationUtil.getPaginationDTO(jobRequestPage, "/minha-conta/profissional/disponiveis");

        ModelAndView mv = new ModelAndView("professional/available-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobRequestFullDTOs);

        return mv;
    }

    @GetMapping("/em-disputa")
    public ModelAndView showDisputedJobs(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Long id,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Optional<Individual> oProfessional = (individualService.findByEmail(CurrentUserUtil.getCurrentUserEmail()));

        if (!oProfessional.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("date").descending());
        Page<JobCandidate> jobCandidatePage = null;
        List<JobCandidateMinDTO> jobCandidateDTOs = null;

        if (id == 0) {
            jobCandidatePage = jobCandidateService.findByJobRequest_StatusAndProfessional(JobRequest.Status.AVAILABLE, oProfessional.get(), pageRequest);
        } else {
            if (id < 0) {
                throw new InvalidParamsException("O identificador da especialidade não pode ser negativo. Por favor, tente novamente.");
            }

            Optional<Expertise> oExpertise = expertiseService.findById(id);

            if (!oExpertise.isPresent()) {
                throw new EntityNotFoundException("A especialidade não foi encontrada pelo id informado. Por favor, tente novamente.");
            }

            Optional<ProfessionalExpertise> oProfessionalExpertise = professionalExpertiseService.findByProfessionalAndExpertise(oProfessional.get(), oExpertise.get());

            if (!oProfessionalExpertise.isPresent()) {
                throw new InvalidParamsException("A especialidade profissional não foi encontrada. Por favor, tente novamente.");
            }

            jobCandidatePage = jobCandidateService.findByJobRequest_StatusAndJobRequest_ExpertiseAndProfessional(JobRequest.Status.AVAILABLE, oExpertise.get(), oProfessional.get(), pageRequest);
        }

        jobCandidateDTOs = jobCandidatePage.stream()
                .map(jobCandidate -> {
                    Optional<Long> totalCandidates = jobCandidateService.countByJobRequest(jobCandidate.getJobRequest());

                    if (totalCandidates.isPresent()) {
                        return jobCandidateMapper.toMinDto(jobCandidate, totalCandidates);
                    }

                    return jobCandidateMapper.toMinDto(jobCandidate, Optional.ofNullable(0L));
                }).collect(Collectors.toList());

        PaginationDTO paginationDTO = PaginationUtil.getPaginationDTO(jobCandidatePage, "/minha-conta/profissional/em-disputa");

        ModelAndView mv = new ModelAndView("professional/disputed-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobCandidateDTOs);

        return mv;
    }

    @GetMapping("/para-contratar")
    public ModelAndView showForHiredJobs(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Long id,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Optional<Individual> oProfessional = (individualService.findByEmail(CurrentUserUtil.getCurrentUserEmail()));

        if (!oProfessional.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("date").descending());
        Page<JobCandidate> jobCandidatePage = null;
        List<JobCandidateMinDTO> jobCandidateDTOs = null;

        if (id == 0) {
            jobCandidatePage = jobCandidateService.findByJobRequest_StatusAndProfessional(JobRequest.Status.TO_HIRED, oProfessional.get(), pageRequest);
        } else {
            if (id < 0) {
                throw new InvalidParamsException("O identificador da especialidade não pode ser negativo. Por favor, tente novamente.");
            }

            Optional<Expertise> oExpertise = expertiseService.findById(id);

            if (!oExpertise.isPresent()) {
                throw new EntityNotFoundException("A especialidade não foi encontrada pelo id informado. Por favor, tente novamente.");
            }

            Optional<ProfessionalExpertise> oProfessionalExpertise = professionalExpertiseService.findByProfessionalAndExpertise(oProfessional.get(), oExpertise.get());

            if (!oProfessionalExpertise.isPresent()) {
                throw new InvalidParamsException("A especialidade profissional não foi encontrada. Por favor, tente novamente.");
            }

            jobCandidatePage = jobCandidateService.findByJobRequest_StatusAndJobRequest_ExpertiseAndProfessional(JobRequest.Status.TO_HIRED, oExpertise.get(), oProfessional.get(), pageRequest);
        }

        jobCandidateDTOs = jobCandidatePage.stream()
                .map(jobCandidate -> {
                    Optional<Long> totalCandidates = jobCandidateService.countByJobRequest(jobCandidate.getJobRequest());

                    if (totalCandidates.isPresent()) {
                        return jobCandidateMapper.toMinDto(jobCandidate, totalCandidates);
                    }

                    return jobCandidateMapper.toMinDto(jobCandidate, Optional.ofNullable(0L));
                }).collect(Collectors.toList());

        PaginationDTO paginationDTO = PaginationUtil.getPaginationDTO(jobCandidatePage, "/minha-conta/profissional/para-contratar");

        ModelAndView mv = new ModelAndView("professional/to-hired-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobCandidateDTOs);

        return mv;
    }

    @GetMapping("/para-fazer")
    public ModelAndView showTodoJobs(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Long id,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Optional<Individual> oProfessional = (individualService.findByEmail(CurrentUserUtil.getCurrentUserEmail()));

        if (!oProfessional.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("dateExpired").ascending());
        Page<JobRequest> jobRequestPage = null;
        List<JobRequestFullDTO> jobRequestFullDTOs = null;

        if (id == 0) {
            jobRequestPage = jobRequestService.findByStatusAndJobContracted_Professional(JobRequest.Status.TO_DO, oProfessional.get(), pageRequest);
        } else {
            if (id < 0) {
                throw new InvalidParamsException("O identificador da especialidade não pode ser negativo. Por favor, tente novamente.");
            }

            Optional<Expertise> oExpertise = expertiseService.findById(id);

            if (!oExpertise.isPresent()) {
                throw new EntityNotFoundException("A especialidade não foi encontrada pelo id informado. Por favor, tente novamente.");
            }

            Optional<ProfessionalExpertise> oProfessionalExpertise = professionalExpertiseService.findByProfessionalAndExpertise(oProfessional.get(), oExpertise.get());

            if (!oProfessionalExpertise.isPresent()) {
                throw new InvalidParamsException("A especialidade profissional não foi encontrada. Por favor, tente novamente.");
            }

            jobRequestPage = jobRequestService.findByStatusAndExpertiseAndJobContracted_Professional(JobRequest.Status.TO_DO, oExpertise.get(), oProfessional.get(), pageRequest);
        }

        jobRequestFullDTOs = jobRequestPage.stream()
                .map(jobRequest -> {
                    Optional<Long> totalCandidates = jobCandidateService.countByJobRequest(jobRequest);

                    if (totalCandidates.isPresent()) {
                        return jobRequestMapper.toFullDto(jobRequest, totalCandidates);
                    }

                    return jobRequestMapper.toFullDto(jobRequest, Optional.ofNullable(0L));
                }).collect(Collectors.toList());

        PaginationDTO paginationDTO = PaginationUtil.getPaginationDTO(jobRequestPage, "/minha-conta/profissional/para-fazer");

        ModelAndView mv = new ModelAndView("professional/todo-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobRequestFullDTOs);

        return mv;
    }

    @GetMapping("/fazendo")
    public ModelAndView showWorkingJobs(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Long id,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Optional<Individual> oProfessional = (individualService.findByEmail(CurrentUserUtil.getCurrentUserEmail()));

        if (!oProfessional.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("dateExpired").ascending());
        Page<JobRequest> jobRequestPage = null;
        List<JobRequestFullDTO> jobRequestFullDTOs = null;

        if (id == 0) {
            jobRequestPage = jobRequestService.findByStatusAndJobContracted_Professional(JobRequest.Status.DOING, oProfessional.get(), pageRequest);
        } else {
            if (id < 0) {
                throw new InvalidParamsException("O identificador da especialidade não pode ser negativo. Por favor, tente novamente.");
            }

            Optional<Expertise> oExpertise = expertiseService.findById(id);

            if (!oExpertise.isPresent()) {
                throw new EntityNotFoundException("A especialidade não foi encontrada pelo id informado. Por favor, tente novamente.");
            }

            Optional<ProfessionalExpertise> oProfessionalExpertise = professionalExpertiseService.findByProfessionalAndExpertise(oProfessional.get(), oExpertise.get());

            if (!oProfessionalExpertise.isPresent()) {
                throw new InvalidParamsException("A especialidade profissional não foi encontrada. Por favor, tente novamente.");
            }

            jobRequestPage = jobRequestService.findByStatusAndExpertiseAndJobContracted_Professional(JobRequest.Status.DOING, oExpertise.get(), oProfessional.get(), pageRequest);
        }

        jobRequestFullDTOs = jobRequestPage.stream()
                .map(jobRequest -> {
                    Optional<Long> totalCandidates = jobCandidateService.countByJobRequest(jobRequest);

                    if (totalCandidates.isPresent()) {
                        return jobRequestMapper.toFullDto(jobRequest, totalCandidates);
                    }

                    return jobRequestMapper.toFullDto(jobRequest, Optional.ofNullable(0L));
                }).collect(Collectors.toList());

        PaginationDTO paginationDTO = PaginationUtil.getPaginationDTO(jobRequestPage, "/minha-conta/profissional/para-fazer");

        ModelAndView mv = new ModelAndView("professional/todo-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobRequestFullDTOs);

        return mv;
    }

    @GetMapping("/executados")
    public ModelAndView showJobsPerformed(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Long id,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Optional<Individual> oProfessional = (individualService.findByEmail(CurrentUserUtil.getCurrentUserEmail()));

        if (!oProfessional.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<JobContracted> jobContractedPage = null;
        List<JobContractedFullDTO> jobContractedDTOs = null;

        if (id == 0) {
            jobContractedPage = jobContractedService.findByJobRequest_StatusAndProfessional(JobRequest.Status.CLOSED, oProfessional.get(), pageRequest);
        } else {
            if (id < 0) {
                throw new InvalidParamsException("O identificador da especialidade não pode ser negativo. Por favor, tente novamente.");
            }

            Optional<Expertise> oExpertise = expertiseService.findById(id);

            if (!oExpertise.isPresent()) {
                throw new EntityNotFoundException("A especialidade não foi encontrada pelo id informado. Por favor, tente novamente.");
            }

            Optional<ProfessionalExpertise> oProfessionalExpertise = professionalExpertiseService.findByProfessionalAndExpertise(oProfessional.get(), oExpertise.get());

            if (!oProfessionalExpertise.isPresent()) {
                throw new InvalidParamsException("A especialidade profissional não foi encontrada. Por favor, tente novamente.");
            }

            jobContractedPage = jobContractedService.findByJobRequest_StatusAndJobRequest_ExpertiseAndProfessional(JobRequest.Status.CLOSED, oExpertise.get(), oProfessional.get(), pageRequest);
        }

        jobContractedDTOs = jobContractedPage.stream()
                .map(jobContracted -> {
                    Optional<Long> totalCandidates = jobCandidateService.countByJobRequest(jobContracted.getJobRequest());

                    if (totalCandidates.isPresent()) {
                        return jobContractedMapper.toFullDto(jobContracted, totalCandidates);
                    }

                    return jobContractedMapper.toFullDto(jobContracted, Optional.ofNullable(0L));
                })
                .collect(Collectors.toList());

        PaginationDTO paginationDTO = PaginationUtil.getPaginationDTO(jobContractedPage, "/minha-conta/profissional/executados");

        ModelAndView mv = new ModelAndView("professional/jobs-performed-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobContractedDTOs);

        return mv;
    }

    @GetMapping("/cancelados")
    public ModelAndView showCanceledJobs(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Long id,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Optional<Individual> oProfessional = (individualService.findByEmail(CurrentUserUtil.getCurrentUserEmail()));

        if (!oProfessional.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("dateExpired").ascending());
        Page<JobRequest> jobRequestPage = null;
        List<JobRequestFullDTO> jobRequestFullDTOs = null;

        if (id == 0) {
            jobRequestPage = jobRequestService.findByStatusAndJobContracted_Professional(JobRequest.Status.CANCELED, oProfessional.get(), pageRequest);
        } else {
            if (id < 0) {
                throw new InvalidParamsException("O identificador da especialidade não pode ser negativo. Por favor, tente novamente.");
            }

            Optional<Expertise> oExpertise = expertiseService.findById(id);

            if (!oExpertise.isPresent()) {
                throw new EntityNotFoundException("A especialidade não foi encontrada pelo id informado. Por favor, tente novamente.");
            }

            Optional<ProfessionalExpertise> oProfessionalExpertise = professionalExpertiseService.findByProfessionalAndExpertise(oProfessional.get(), oExpertise.get());

            if (!oProfessionalExpertise.isPresent()) {
                throw new InvalidParamsException("A especialidade profissional não foi encontrada. Por favor, tente novamente.");
            }

            jobRequestPage = jobRequestService.findByStatusAndExpertiseAndJobContracted_Professional(JobRequest.Status.CANCELED, oExpertise.get(), oProfessional.get(), pageRequest);
        }

        jobRequestFullDTOs = jobRequestPage.stream()
                .map(jobRequest -> {
                    Optional<Long> totalCandidates = jobCandidateService.countByJobRequest(jobRequest);

                    if (totalCandidates.isPresent()) {
                        return jobRequestMapper.toFullDto(jobRequest, totalCandidates);
                    }

                    return jobRequestMapper.toFullDto(jobRequest, Optional.ofNullable(0L));
                }).collect(Collectors.toList());

        PaginationDTO paginationDTO = PaginationUtil.getPaginationDTO(jobRequestPage, "/minha-conta/profissional/para-fazer");

        ModelAndView mv = new ModelAndView("professional/todo-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobRequestFullDTOs);

        return mv;
    }

    @GetMapping("/detalhes")
    public ModelAndView showMyDetails() throws Exception {
        ModelAndView mv = new ModelAndView("client/details-contact");

        Optional<Individual> oProfessional = (individualService.findByEmail(CurrentUserUtil.getCurrentUserEmail()));

        if (!oProfessional.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        IndividualDTO professionalDTO = individualMapper.toDto(oProfessional.get());
        IndividualDTO professionalMinDTO = individualMapper.toDto(oProfessional.get());

        List<JobContracted> jobContracted = jobContractedService.findByIdProfessional(professionalDTO.getId());
        List<JobContractedDTO> jobContractedDTOs = jobContracted.stream()
                .map(contracted -> jobContractedMapper.toResponseDto(contracted))
                .collect(Collectors.toList());

        mv.addObject("individual", professionalMinDTO);
        mv.addObject("jobContracted", jobContractedDTOs);

        return mv;
    }


    @GetMapping("/detalhes-servico/{id}")
    public ModelAndView showAvailableDetailJobs(
            @PathVariable Long id
    ) throws Exception {

        Optional<Individual> oProfessional = (individualService.findByEmail(CurrentUserUtil.getCurrentUserEmail()));

        if (!oProfessional.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        ModelAndView mv = new ModelAndView("professional/detail-service");
        Optional<JobRequest> oJob = jobRequestService.findById(id);

        if (!oJob.isPresent()) {
            throw new Exception("O trabalho não foi encontrado em nosso sistema!");
        }

        JobRequest jb = oJob.get();

        JobRequestDetailsDTO jobFull = jobRequestMapper.jobRequestDetailsDTO(jb);











        //TESTE PUSH NOTIFICATION
        System.err.println("DETALHE DO SERVIÇO (JOBFULL)....   " + jobFull);
        System.err.println("DETALHE DO CLIENTE SERVIÇO......  " + jobFull.getIndividual().getEmail());












        Optional oClient, oCity, oState;

        oClient = individualService.findById(jobFull.getIndividual().getId());
        Individual client = (Individual) oClient.get();

        oCity = cityService.findById(jobFull.getIndividual().getAddress().getCity().getId());

        City city = (City) oCity.get();
        oState = stateService.findById(city.getState().getId());

        State state = (State) oState.get();

        int maxCandidates = jb.getQuantityCandidatorsMax();
        int currentCandidates = jb.getJobCandidates().size();
        int percentCandidatesApplied = (int)(((double)currentCandidates / (double)maxCandidates) * 100);

        boolean isAvailableJobRequest = jb.getStatus().equals(JobRequest.Status.AVAILABLE) && jb.isClientConfirmation();
        boolean isJobToHired = jb.getStatus().equals(JobRequest.Status.TO_HIRED);

        Optional<JobCandidate> oJobCandidate = jobCandidateService.findById(id, oProfessional.get().getId());

        if (oJobCandidate.isPresent()) {
            JobCandidate jobCandidate = oJobCandidate.get();
            boolean hasHiredDate = false;

            if (jobCandidate.getHiredDate() != null) {
                String date = this.dateFormat.format(jobCandidate.getHiredDate());
                hasHiredDate = true;

                mv.addObject("jobCandidateHiredDate",  date);
            }

            mv.addObject("hasHiredDate",  hasHiredDate);
        }

        mv.addObject("job", jobFull);
        mv.addObject("client", client);
        mv.addObject("city", city.getName());
        mv.addObject("state", state.getName());
        mv.addObject("candidatesApplied", currentCandidates);
        mv.addObject("maxCandidates", maxCandidates);
        mv.addObject("percentCandidatesApplied", percentCandidatesApplied);
        mv.addObject("isAvailableJobRequest", isAvailableJobRequest);
        mv.addObject("isJobToHired", isJobToHired);
        return mv;
    }

}
