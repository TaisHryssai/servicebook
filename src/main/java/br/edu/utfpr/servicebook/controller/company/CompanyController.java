package br.edu.utfpr.servicebook.controller.company;

import br.edu.utfpr.servicebook.exception.InvalidParamsException;
import br.edu.utfpr.servicebook.service.FollowsService;
import br.edu.utfpr.servicebook.model.dto.*;
import br.edu.utfpr.servicebook.model.entity.*;
import br.edu.utfpr.servicebook.model.mapper.*;
import br.edu.utfpr.servicebook.security.IAuthentication;
import br.edu.utfpr.servicebook.security.RoleType;
import br.edu.utfpr.servicebook.service.*;
import br.edu.utfpr.servicebook.sse.EventSSE;
import br.edu.utfpr.servicebook.sse.EventSSEDTO;
import br.edu.utfpr.servicebook.sse.EventSseMapper;
import br.edu.utfpr.servicebook.sse.SSEService;
import br.edu.utfpr.servicebook.util.SessionNames;
import br.edu.utfpr.servicebook.util.pagination.PaginationDTO;
import br.edu.utfpr.servicebook.util.pagination.PaginationUtil;
import br.edu.utfpr.servicebook.util.UserTemplateInfo;
import br.edu.utfpr.servicebook.util.UserTemplateStatisticInfo;
import br.edu.utfpr.servicebook.util.TemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@RequestMapping("/minha-conta/empresa")
@Controller
public class CompanyController {

    public static final Logger log = LoggerFactory.getLogger(CompanyController.class);
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private FollowsService followsService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IndividualMapper individualMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private ProfessionalExpertiseService professionalExpertiseService;

    @Autowired
    private ExpertiseService expertiseService;

    @Autowired
    private ExpertiseMapper expertiseMapper;

    @Autowired
    private JobRequestService jobRequestService;

    @Autowired
    private JobRequestMapper jobRequestMapper;

    @Autowired
    private JobCandidateService jobCandidateService;

    private JobCandidateMapper jobCandidateMapper;

    @Autowired
    private StateService stateService;

    @Autowired
    private TemplateUtil templateUtil;

    @Autowired
    private SSEService sseService;

    @Autowired
    private EventSseMapper eventSseMapper;

    @Autowired
    private PaginationUtil paginationUtil;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private JobContractedService jobContractedService;

    @Autowired
    private JobContractedMapper jobContractedMapper;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private IAuthentication authentication;

    @GetMapping
    @RolesAllowed({RoleType.COMPANY})
    public ModelAndView showMyAccountCompany(@RequestParam(required = false, defaultValue = "0") Optional<Long> expertiseId
    ) throws Exception {
        Optional<User> oProfessional = (userService.findByEmail(authentication.getEmail()));

        log.debug("ServiceBook: Minha conta.");

        if (!oProfessional.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        ModelAndView mv = new ModelAndView("company/my-account-menu");

        UserDTO professionalDTO = userMapper.toDto(oProfessional.get());

        Optional<Long> oProfessionalFollowingAmount = followsService.countByProfessional(oProfessional.get());
        professionalDTO.setFollowingAmount(oProfessionalFollowingAmount.get());

        List<ProfessionalExpertise> professionalExpertises = professionalExpertiseService.findByProfessional(oProfessional.get());
        List<ExpertiseDTO> expertiseDTOs = professionalExpertises.stream()
                .map(professionalExpertise -> professionalExpertise.getExpertise())
                .map(expertise -> expertiseMapper.toDto(expertise))
                .collect(Collectors.toList());

        //envia a notificação ao usuário
        List<EventSSE> eventSsesList = sseService.findPendingEventsByEmail(authentication.getEmail());
        List<EventSSEDTO> eventSSEDTOs = eventSsesList.stream()
                .map(eventSse -> {
                    return eventSseMapper.toFullDto(eventSse);
                })
                .collect(Collectors.toList());

        mv.addObject("eventsse", eventSSEDTOs);
        mv.addObject("expertises", expertiseDTOs);
        mv.addObject("company", true);
        return mv;
    }

    /**
     * Apresenta a tela para adição de um novo funcionário a uma empresa.
     * @param expertiseId
     * @return
     * @throws Exception
     */
    @GetMapping("/adicionar-profissional")
    @RolesAllowed({RoleType.COMPANY})
    public ModelAndView newProfessional(@RequestParam(required = false, defaultValue = "0") Optional<Long> expertiseId
    ) throws Exception {
        ModelAndView mv = new ModelAndView("company/new-professional");
        Optional<User> oProfessional = (userService.findByEmail(authentication.getEmail()));

        UserDTO professionalDTO = userMapper.toDto(oProfessional.get());

        Optional<Long> oProfessionalFollowingAmount = followsService.countByProfessional(oProfessional.get());
        professionalDTO.setFollowingAmount(oProfessionalFollowingAmount.get());
        UserTemplateInfo individualInfo = templateUtil.getUserInfo(professionalDTO);
        UserTemplateStatisticInfo statisticInfo = templateUtil.getProfessionalStatisticInfo(oProfessional.get(), expertiseId.get());
        mv.addObject("userInfo", individualInfo);

        List<ProfessionalExpertise> professionalExpertises = professionalExpertiseService.findByProfessional(oProfessional.get());
        List<ExpertiseDTO> expertiseDTOs = professionalExpertises.stream()
                .map(professionalExpertise -> professionalExpertise.getExpertise())
                .map(expertise -> expertiseMapper.toDto(expertise))
                .collect(Collectors.toList());

        //envia a notificação ao usuário
        List<EventSSE> eventSsesList = sseService.findPendingEventsByEmail(authentication.getEmail());
        List<EventSSEDTO> eventSSEDTOs = eventSsesList.stream()
                .map(eventSse -> {
                    return eventSseMapper.toFullDto(eventSse);
                })
                .collect(Collectors.toList());

        UserDTO professionalDTO1 = userMapper.toDto(oProfessional.get());
        mv.addObject("eventsse", eventSSEDTOs);
        mv.addObject("expertises", expertiseDTOs);
        mv.addObject("userInfo", individualInfo);
        mv.addObject("professionalDTO1", professionalDTO1);
        mv.addObject("statisticInfo", statisticInfo);
        mv.addObject("company", true);
        return mv;
    }

    @GetMapping("/disponiveis")
    @RolesAllowed({RoleType.COMPANY})
    public ModelAndView showAvailableJobs(
            HttpServletRequest request,
            HttpSession httpSession,
            @RequestParam(required = false, defaultValue = "0") Long id,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "5") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {
        //altera o tipo de usuário para empresa
        httpSession.setAttribute(SessionNames.ACCESS_USER_KEY, SessionNames.ACCESS_USER_COMPANY_VALUE);

        Page<JobRequest> jobRequestPage = findJobRequests(id, JobRequest.Status.AVAILABLE, page, size);

        List<JobRequestFullDTO> jobRequestFullDTOs = generateJobRequestDTOList(jobRequestPage);

        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(jobRequestPage, "/minha-conta/empresa/disponiveis");

        ModelAndView mv = new ModelAndView("professional/professional-index");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobRequestFullDTOs);

        return mv;
    }
    @RolesAllowed({RoleType.COMPANY})
    private Page<JobRequest> findJobRequests(Long expertiseId, JobRequest.Status status, int page, int size){
        Optional<User> oProfessional = (userService.findByEmail(authentication.getEmail()));

        if (!oProfessional.isPresent()) {
            throw new EntityNotFoundException("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("dateTarget").ascending());
        Page<JobRequest> jobRequestPage = null;
        List<JobRequestFullDTO> jobRequestFullDTOs = null;

        if (expertiseId == 0) {
            if(status == JobRequest.Status.AVAILABLE){
                return jobRequestPage = jobRequestService.findAvailableAllExpertises(JobRequest.Status.AVAILABLE, oProfessional.get().getId(), pageRequest);
            }
            else if(status == JobRequest.Status.TO_DO){
                return jobRequestPage = jobRequestService.findByStatusAndJobContracted_User(JobRequest.Status.TO_DO, oProfessional.get(), pageRequest);
            }
            else if(status == JobRequest.Status.DOING){
                return jobRequestPage = jobRequestService.findByStatusAndJobContracted_User(JobRequest.Status.DOING, oProfessional.get(), pageRequest);
            }
            else if(status == JobRequest.Status.CANCELED){
                return jobRequestPage = jobRequestService.findByStatusAndJobContracted_User(JobRequest.Status.CANCELED, oProfessional.get(), pageRequest);
            }
            return null;
        } else {

            if (expertiseId < 0) {
                throw new InvalidParamsException("O identificador da especialidade não pode ser negativo. Por favor, tente novamente.");
            }

            Optional<Expertise> oExpertise = expertiseService.findById(expertiseId);

            if (!oExpertise.isPresent()) {
                throw new EntityNotFoundException("A especialidade não foi encontrada pelo id informado. Por favor, tente novamente.");
            }

            Optional<ProfessionalExpertise> oProfessionalExpertise = professionalExpertiseService.findByProfessionalAndExpertise(oProfessional.get(), oExpertise.get());

            if (!oProfessionalExpertise.isPresent()) {
                throw new InvalidParamsException("A especialidade profissional não foi encontrada. Por favor, tente novamente.");
            }

            if(status == JobRequest.Status.AVAILABLE){
                return jobRequestPage = jobRequestService.findAvailableByExpertise(JobRequest.Status.AVAILABLE, oExpertise.get(), oProfessional.get().getId(), pageRequest);
            }
            else if(status == JobRequest.Status.TO_DO){
                return jobRequestPage = jobRequestService.findByStatusAndExpertiseAndJobContracted_Professional(JobRequest.Status.TO_DO, oExpertise.get(), oProfessional.get(), pageRequest);
            }
            else if(status == JobRequest.Status.DOING){
                return jobRequestPage = jobRequestService.findByStatusAndExpertiseAndJobContracted_Professional(JobRequest.Status.DOING, oExpertise.get(), oProfessional.get(), pageRequest);
            }
            else if(status == JobRequest.Status.CANCELED){
                return jobRequestPage = jobRequestService.findByStatusAndExpertiseAndJobContracted_Professional(JobRequest.Status.CANCELED, oExpertise.get(), oProfessional.get(), pageRequest);
            }
            return null;
        }
    }
    @RolesAllowed({RoleType.COMPANY})
    private List<JobRequestFullDTO> generateJobRequestDTOList(Page<JobRequest> jobRequestPage){
        return jobRequestPage.stream().distinct()
                .map(jobRequest -> {
                    Optional<Long> totalCandidates = jobCandidateService.countByJobRequest(jobRequest);

                    if (totalCandidates.isPresent()) {
                        return jobRequestMapper.toFullDto(jobRequest, totalCandidates);
                    }

                    return jobRequestMapper.toFullDto(jobRequest, Optional.ofNullable(0L));
                }).collect(Collectors.toList());
    }

    /**
     * Apresenta a tela para o profissional adicionar especialidades.
     * @return
     * @throws Exception
     */
    @GetMapping("/especialidades")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public ModelAndView showExpertises()  throws Exception {

        User professional = this.getAuthenticatedUser();

        //lista de categorias
        List<Category> categories = categoryService.findAll();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(s -> categoryMapper.toDto(s))
                .collect(Collectors.toList());

        List<ProfessionalExpertise> professionalExpertises = professionalExpertiseService.findByProfessional(professional);

        List<ExpertiseDTO> professionalExpertiseDTOs = professionalExpertises.stream()
                .map(professionalExpertise -> professionalExpertise.getExpertise())
                .map(expertise -> expertiseMapper.toDto(expertise))
                .collect(Collectors.toList());

        List<Expertise> professionPage = expertiseService.findExpertiseNotExist(getAuthenticatedUser().getId());

        List<ExpertiseDTO> otherExpertisesDTOs = professionPage.stream()
                .map(s -> expertiseMapper.toDto(s))
                .collect(Collectors.toList());

        ModelAndView mv = new ModelAndView("professional/my-expertises");
        mv.addObject("categories", categoryDTOs);
        mv.addObject("otherExpertises", otherExpertisesDTOs);
        mv.addObject("professionalExpertises", professionalExpertiseDTOs);
        return mv;
    }

    @GetMapping("/especialidades/novo")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public ModelAndView showFormToRegister()  throws Exception {

        User professional = this.getAuthenticatedUser();

        //lista de categorias
        List<Category> categories = categoryService.findAll();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(s -> categoryMapper.toDto(s))
                .collect(Collectors.toList());

        ModelAndView mv = new ModelAndView("professional/my-expertises-register");
        mv.addObject("categories", categoryDTOs);

        return mv;
    }

    @GetMapping("/meus-jobs")
    @RolesAllowed({RoleType.COMPANY})
    public ModelAndView showMyJobsProfessional(@RequestParam(required = false, defaultValue = "0") Optional<Long> expertiseId) throws Exception {
        log.debug("ServiceBook: Minha conta.");

        Optional<User> oProfessional = (userService.findByEmail(authentication.getEmail()));

        if (!oProfessional.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        List<ProfessionalExpertise> professionalExpertises = professionalExpertiseService.findByProfessional(oProfessional.get());
        List<ExpertiseDTO> professionalExpertiseDTOs = professionalExpertises.stream()
                .map(professionalExpertise -> professionalExpertise.getExpertise())
                .map(expertise -> expertiseMapper.toDto(expertise))
                .collect(Collectors.toList());

        //envia a notificação ao usuário
        List<EventSSE> eventSsesList = sseService.findPendingEventsByEmail(authentication.getEmail());
        List<EventSSEDTO> eventSSEDTOs = eventSsesList.stream()
                .map(eventSse -> {
                    return eventSseMapper.toFullDto(eventSse);
                })
                .collect(Collectors.toList());

        ModelAndView mv = new ModelAndView("professional/my-account");
        mv.addObject("eventsse", eventSSEDTOs);
        mv.addObject("professionalExpertises", professionalExpertiseDTOs);

        return mv;
    }

    /**
     * Mostra os serviços em que o profissional se candidatou.
     * @param request
     * @param id
     * @param page
     * @param size
     * @param order
     * @param direction
     * @return
     * @throws Exception
     */
    @GetMapping("/em-disputa")
    @RolesAllowed({RoleType.COMPANY})
    public ModelAndView showDisputedJobs(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Long id,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

//        Optional<Individual> oProfessional = (individualService.findByEmail(authentication.getEmail()));
        User oProfessional = this.getAuthenticatedUser();

//        if (!oProfessional.isPresent()) {
//            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
//        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("dateCreated").descending());
        Page<JobCandidate> jobCandidatePage = null;
        List<JobCandidateMinDTO> jobCandidateDTOs = null;

        if (id == 0) {
            jobCandidatePage = jobCandidateService.findByJobRequest_StatusAndProfessional(JobRequest.Status.AVAILABLE, oProfessional, pageRequest);
        } else {
            if (id < 0) {
                throw new InvalidParamsException("O identificador da especialidade não pode ser negativo. Por favor, tente novamente.");
            }

            Optional<Expertise> oExpertise = expertiseService.findById(id);

            if (!oExpertise.isPresent()) {
                throw new EntityNotFoundException("A especialidade não foi encontrada pelo id informado. Por favor, tente novamente.");
            }

            Optional<ProfessionalExpertise> oProfessionalExpertise = professionalExpertiseService.findByProfessionalAndExpertise(oProfessional, oExpertise.get());

            if (!oProfessionalExpertise.isPresent()) {
                throw new InvalidParamsException("A especialidade profissional não foi encontrada. Por favor, tente novamente.");
            }

            jobCandidatePage = jobCandidateService.findByJobRequest_StatusAndJobRequest_ExpertiseAndProfessional(JobRequest.Status.AVAILABLE, oExpertise.get(), oProfessional, pageRequest);
        }

        jobCandidateDTOs = generateJobCandidateDTOList(jobCandidatePage);

        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(jobCandidatePage, "/minha-conta/profissional/em-disputa");

        ModelAndView mv = new ModelAndView("professional/disputed-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobCandidateDTOs);

        return mv;
    }

    @GetMapping("/em-orcamento")
    @RolesAllowed({RoleType.COMPANY})
    public ModelAndView showBudgetJobs(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Long id,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

            User oProfessional = this.getAuthenticatedUser();

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("dateCreated").descending());
        Page<JobCandidate> jobCandidatePage = null;
        List<JobCandidateMinDTO> jobCandidateDTOs = null;

        if (id == 0) {
            jobCandidatePage = jobCandidateService.findByJobRequest_StatusAndProfessional(JobRequest.Status.BUDGET, oProfessional, pageRequest);
        } else {
            if (id < 0) {
                throw new InvalidParamsException("O identificador da especialidade não pode ser negativo. Por favor, tente novamente.");
            }

            Optional<Expertise> oExpertise = expertiseService.findById(id);

            if (!oExpertise.isPresent()) {
                throw new EntityNotFoundException("A especialidade não foi encontrada pelo id informado. Por favor, tente novamente.");
            }

            Optional<ProfessionalExpertise> oProfessionalExpertise = professionalExpertiseService.findByProfessionalAndExpertise(oProfessional, oExpertise.get());

            if (!oProfessionalExpertise.isPresent()) {
                throw new InvalidParamsException("A especialidade profissional não foi encontrada. Por favor, tente novamente.");
            }

            jobCandidatePage = jobCandidateService.findByJobRequest_StatusAndJobRequest_ExpertiseAndProfessional(JobRequest.Status.BUDGET, oExpertise.get(), oProfessional, pageRequest);
        }

        jobCandidateDTOs = generateJobCandidateDTOList(jobCandidatePage);

        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(jobCandidatePage, "/minha-conta/profissional/em-orcamento");

        ModelAndView mv = new ModelAndView("professional/budget-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobCandidateDTOs);

        return mv;
    }

    @GetMapping("/para-contratar")
    @RolesAllowed({RoleType.COMPANY})
    public ModelAndView showForHiredJobs(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Long id,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Page<JobCandidate> jobCandidatePage = findJobCandidates(id, JobRequest.Status.TO_HIRED, page, size);

        List<JobCandidateMinDTO> jobCandidateDTOs = generateJobCandidateDTOList(jobCandidatePage);

        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(jobCandidatePage, "/minha-conta/profissional/para-contratar");

        ModelAndView mv = new ModelAndView("professional/to-hired-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobCandidateDTOs);

        return mv;
    }

    /**
     * Mostra as ordens de serviços para serem feitas, ou seja, que o profissional já foi contratado.
     * @param request
     * @param expertiseId
     * @param page
     * @param size
     * @param order
     * @param direction
     * @return
     * @throws Exception
     */
    @GetMapping("/para-fazer")
    @RolesAllowed({RoleType.COMPANY})
    public ModelAndView showTodoJobs(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Long expertiseId,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Page<JobRequest> jobRequestPage = findJobRequests(expertiseId, JobRequest.Status.TO_DO, page, size);

        List<JobRequestFullDTO> jobRequestFullDTOs = generateJobRequestDTOList(jobRequestPage);

        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(jobRequestPage, "/minha-conta/profissional/para-fazer");

        ModelAndView mv = new ModelAndView("professional/todo-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobRequestFullDTOs);

        return mv;
    }

    @GetMapping("/fazendo")
    @RolesAllowed({RoleType.COMPANY})
    public ModelAndView showWorkingJobs(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Long id,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Page<JobRequest> jobRequestPage = findJobRequests(id, JobRequest.Status.DOING, page, size);
        List<JobRequestFullDTO> jobRequestFullDTOs = generateJobRequestDTOList(jobRequestPage);

        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(jobRequestPage, "/minha-conta/profissional/para-fazer");

        ModelAndView mv = new ModelAndView("professional/todo-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobRequestFullDTOs);

        return mv;
    }

    @GetMapping("/executados")
    @RolesAllowed({RoleType.COMPANY})
    public ModelAndView showJobsPerformed(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Long id,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Page<JobContracted> jobContractedPage = findJobContracted(id, JobRequest.Status.CLOSED, page, size);
        List<JobContractedFullDTO> jobContractedDTOs = generateJobContractedDTOList(jobContractedPage);

        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(jobContractedPage, "/minha-conta/profissional/executados");

        ModelAndView mv = new ModelAndView("professional/jobs-performed-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobContractedDTOs);

        return mv;
    }

    @GetMapping("/cancelados")
    @RolesAllowed({RoleType.COMPANY})
    public ModelAndView showCanceledJobs(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Long id,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Page<JobRequest> jobRequestPage = findJobRequests(id, JobRequest.Status.CANCELED, page, size);
        List<JobRequestFullDTO> jobRequestFullDTOs = generateJobRequestDTOList(jobRequestPage);

        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(jobRequestPage, "/minha-conta/profissional/para-fazer");

        ModelAndView mv = new ModelAndView("professional/todo-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobRequestFullDTOs);

        return mv;
    }

    private List<JobCandidateMinDTO> generateJobCandidateDTOList(Page<JobCandidate> jobCandidatePage){
        return jobCandidatePage.stream()
                .map(jobCandidate -> {
                    Optional<Long> totalCandidates = jobCandidateService.countByJobRequest(jobCandidate.getJobRequest());

                    if (totalCandidates.isPresent()) {
                        return jobCandidateMapper.toMinDto(jobCandidate, totalCandidates);
                    }

                    return jobCandidateMapper.toMinDto(jobCandidate, Optional.ofNullable(0L));
                }).collect(Collectors.toList());
    }


    private Page<JobContracted> findJobContracted(Long expertiseId, JobRequest.Status status, int page, int size){
        Optional<User> oProfessional = (userService.findByEmail(authentication.getEmail()));

        if (!oProfessional.isPresent()) {
            throw new EntityNotFoundException("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<JobContracted> jobContractedPage = null;
        List<JobContractedFullDTO> jobContractedDTOs = null;

        if (expertiseId == 0) {
            if(status == JobRequest.Status.CLOSED){
                return jobContractedPage = jobContractedService.findByJobRequest_StatusAndProfessional(JobRequest.Status.CLOSED, oProfessional.get(), pageRequest);
            }
            return null;
        } else {

            if (expertiseId < 0) {
                throw new InvalidParamsException("O identificador da especialidade não pode ser negativo. Por favor, tente novamente.");
            }

            Optional<Expertise> oExpertise = expertiseService.findById(expertiseId);

            if (!oExpertise.isPresent()) {
                throw new EntityNotFoundException("A especialidade não foi encontrada pelo id informado. Por favor, tente novamente.");
            }

            Optional<ProfessionalExpertise> oProfessionalExpertise = professionalExpertiseService.findByProfessionalAndExpertise(oProfessional.get(), oExpertise.get());

            if (!oProfessionalExpertise.isPresent()) {
                throw new InvalidParamsException("A especialidade profissional não foi encontrada. Por favor, tente novamente.");
            }

            if(status == JobRequest.Status.CLOSED){
                jobContractedPage = jobContractedService.findByJobRequest_StatusAndJobRequest_ExpertiseAndProfessional(JobRequest.Status.CLOSED, oExpertise.get(), oProfessional.get(), pageRequest);
            }
            return null;
        }
    }

    private List<JobContractedFullDTO> generateJobContractedDTOList(Page<JobContracted> jobContractedPage) {
        return jobContractedPage.stream()
                .map(jobContracted -> {
                    Optional<Long> totalCandidates = jobCandidateService.countByJobRequest(jobContracted.getJobRequest());

                    if (totalCandidates.isPresent()) {
                        return jobContractedMapper.toFullDto(jobContracted, totalCandidates);
                    }

                    return jobContractedMapper.toFullDto(jobContracted, Optional.ofNullable(0L));
                })
                .collect(Collectors.toList());
    }

    private Page<JobCandidate> findJobCandidates(Long expertiseId, JobRequest.Status status, int page, int size){
        Optional<User> oProfessional = (userService.findByEmail(authentication.getEmail()));

        if (!oProfessional.isPresent()) {
            throw new EntityNotFoundException("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("dateCreated").descending());
        Page<JobCandidate> jobCandidatePage = null;
        List<JobCandidateMinDTO> jobCandidateDTOs = null;

        if (expertiseId == 0) {
            if(status == JobRequest.Status.TO_HIRED){
                return jobCandidatePage = jobCandidateService.findByJobRequest_StatusAndProfessional(status, oProfessional.get(), pageRequest);
            }
            return null;
        } else {
            if (expertiseId < 0) {
                throw new InvalidParamsException("O identificador da especialidade não pode ser negativo. Por favor, tente novamente.");
            }

            Optional<Expertise> oExpertise = expertiseService.findById(expertiseId);

            if (!oExpertise.isPresent()) {
                throw new EntityNotFoundException("A especialidade não foi encontrada pelo id informado. Por favor, tente novamente.");
            }

            Optional<ProfessionalExpertise> oProfessionalExpertise = professionalExpertiseService.findByProfessionalAndExpertise(oProfessional.get(), oExpertise.get());

            if (!oProfessionalExpertise.isPresent()) {
                throw new InvalidParamsException("A especialidade profissional não foi encontrada. Por favor, tente novamente.");
            }

            if(status == JobRequest.Status.TO_HIRED){
                return jobCandidatePage = jobCandidateService.findByJobRequest_StatusAndJobRequest_ExpertiseAndProfessional(JobRequest.Status.TO_HIRED, oExpertise.get(), oProfessional.get(), pageRequest);
            }

            return null;
        }
    }

    /**
     * Retorna a empresa logada.
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
