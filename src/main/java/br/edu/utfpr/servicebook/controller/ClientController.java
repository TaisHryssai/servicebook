package br.edu.utfpr.servicebook.controller;

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
import br.edu.utfpr.servicebook.util.DateUtil;
import br.edu.utfpr.servicebook.util.pagination.PaginationDTO;
import br.edu.utfpr.servicebook.util.pagination.PaginationUtil;
import br.edu.utfpr.servicebook.util.UserTemplateInfo;
import br.edu.utfpr.servicebook.util.TemplateUtil;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mercadopago.client.common.AddressRequest;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.common.PhoneRequest;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.preference.Preference;
import org.apache.batik.transcoder.TranscoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import com.google.zxing.WriterException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.fop.configuration.ConfigurationException;
import com.mercadopago.MercadoPagoConfig;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("/minha-conta/cliente")
@Controller
public class ClientController {

    public static final Logger log =
            LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private IndividualService individualService;

    @Autowired
    private UserService userService;

    @Autowired
    private IndividualMapper individualMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JobCandidateService jobCandidateService;

    @Autowired
    private JobRequestService jobRequestService;

    @Autowired
    private JobCandidateMapper jobCandidateMapper;

    @Autowired
    private JobRequestMapper jobRequestMapper;

    @Autowired
    private ExpertiseService expertiseService;

    @Autowired
    private ExpertiseMapper expertiseMapper;

    @Autowired
    private JobContractedService jobContractedService;

    @Autowired
    private JobContractedMapper jobContractedMapper;

    @Autowired
    private QuartzService quartzService;

    @Autowired
    private SSEService sseService;

    @Autowired
    private EventSseMapper eventSseMapper;

    @Autowired
    private IAuthentication authentication;

    @Autowired
    private FollowsService followsService;

    @Autowired
    private TemplateUtil templateUtil;

    @Autowired
    private PaginationUtil paginationUtil;

    @Autowired
    private ProfessionalMapper professionalMapper;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private PaymentVoucherService paymentVoucherService;

    @Autowired
    private PaymentVoucherMapper paymentVoucherMapper;

    @Autowired
    private PaymentJobService paymentJobService;

    @Autowired
    private PaymentJobMapper paymentJobMapper;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private ProfessionalServiceOfferingService professionalServiceOfferingService;

    @Autowired
    private AssessmentProfessionalService assessmentProfessionalService;

    @Autowired
    private  AssessmentProfessionalFileService assessmentProfessionalFileService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AssessmentProfessionalFileMapper assessmentProfessionalFileMapper;

    @Autowired
    private ModerateService moderateService;

    @Autowired
    private AssessmentResponseService assessmentResponseService;

    @Autowired
    private PerspectiveAPIService perspectiveAPIService;

    @Value("${svg.certificate.template}")
    private String svgCertificateTemplate;


    /**
     * Método que apresenta a tela inicial do cliente
     * @return
     * @throws Exception
     */
    @GetMapping
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public ModelAndView show() throws Exception {

        Optional<User> oClient = userService.findByEmail(authentication.getEmail());

        if (!oClient.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        //apresenta os eventos de notificação ao cliente que ainda não foram lidos
        List<EventSSE> eventSsesList = sseService.findPendingEventsByEmail(authentication.getEmail());
        List<EventSSEDTO> eventSseDTOs = eventSsesList.stream()
                .map(eventSse -> {
                    return eventSseMapper.toFullDto(eventSse);
                })
                .collect(Collectors.toList());

        //lista os pedidos de serviço do cliente
        List<JobRequest> jobRequests = jobRequestService.findByClientOrderByDateCreatedDesc(oClient.get());
        List<JobRequestMinDTO> jobRequestDTOs = jobRequests.stream()
                .map(job -> {
                    Optional<Long> amountOfCandidates = jobCandidateService.countByJobRequest(job);

                    if (amountOfCandidates.isPresent()) {
                        return jobRequestMapper.toMinDto(job, amountOfCandidates.get());
                    }
                    return jobRequestMapper.toMinDto(job, 0L);
                })
                .collect(Collectors.toList());

        ModelAndView mv = new ModelAndView("client/my-requests");
        mv.addObject("jobRequests", jobRequestDTOs);
        mv.addObject("eventsse", eventSseDTOs);

        return mv;
    }

    /**
     * Cliente remove o seu próprio anúncio.
     * @param id
     * @param redirectAttributes
     * @return
     * @throws IOException
     */
    @DeleteMapping("/meus-pedidos/{id}")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public String delete (@PathVariable Long id, RedirectAttributes redirectAttributes) throws IOException {

        Optional<User> oUser = (userService.findByEmail(authentication.getEmail()));

        if (!oUser.isPresent()) {
            throw new IOException("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        Optional<JobRequest> jobRequest = this.jobRequestService.findById(id);

        if (!jobRequest.isPresent()) {
            throw new EntityNotFoundException("Solicitação não foi encontrada pelo id informado.");
        }

        Long jobRequestClientId = jobRequest.get().getUser().getId();
        Long clientId = oUser.get().getId();

        if (jobRequestClientId != clientId) {
            throw new EntityNotFoundException("Você não ter permissão para deletar essa solicitação.");
        }

        this.jobRequestService.delete(id);
        redirectAttributes.addFlashAttribute("msg", "Solicitação deletada!");

        return "redirect:/minha-conta/meus-pedidos";
    }

    /**
     * Mostra a tela de detalhes de um anúncio do cliente, ou seja, com os candidatos.
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("/meus-pedidos/{id}")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public ModelAndView showDetailsRequest(@PathVariable Optional<Long> id,
                                           @RequestParam(value = "pag", defaultValue = "1") int page,
                                           @RequestParam(value = "siz", defaultValue = "4") int size) throws Exception {

        ModelAndView mv = new ModelAndView("client/details-request");

        Optional<JobRequest> jobRequest = jobRequestService.findById(id.get());
        Optional<JobContracted> oJobContracted = jobContractedService.findByJobRequest(jobRequest.get());

        if (!jobRequest.isPresent()) {
            throw new EntityNotFoundException("Solicitação de serviço não encontrado. Por favor, tente novamente.");
        }

        JobRequestFullDTO jobDTO = jobRequestMapper.toFullDto(jobRequest.get());

        Long expertiseId = jobRequest.get().getExpertise().getId();

        Optional<Expertise> expertise = expertiseService.findById(expertiseId);

        if (!expertise.isPresent()) {
            throw new EntityNotFoundException("A especialidade não foi encontrada. Por favor, tente novamente.");
        }

        ExpertiseMinDTO expertiseDTO = expertiseMapper.toMinDto(expertise.get());

        Optional<User> client = (userService.findByEmail(authentication.getEmail()));

        if (!client.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(1 - 1, 4, Sort.by("dateTarget").ascending());
        Page<JobRequest> jobRequestPage = null;
        List<JobRequestFullDTO> jobRequestFullDTOs = null;

        List<JobCandidate> jobCandidates = jobCandidateService.findByJobRequestAndChosenByBudget(jobRequest.get(), true);

        jobRequestPage = jobRequestService.findByStatusAndClient(JobRequest.Status.DOING, client.get(), pageRequest);

        jobRequestFullDTOs = jobRequestPage.stream()
                .map(jobRequest1 -> {
                    Optional<JobContracted> totalCandidates = jobContractedService.findByJobRequest(jobRequest1);

                    if (totalCandidates.isPresent()) {
                        return jobRequestMapper.toFullDto(jobRequest1);
                    }

                    return jobRequestMapper.toFullDto(jobRequest1, Optional.ofNullable(0L));
                }).collect(Collectors.toList());


        List<JobCandidateDTO> jobCandidatesDTOs = jobCandidates.stream()
                .map(candidate -> {
                    return jobCandidateMapper.toDto(candidate);
                })
                .collect(Collectors.toList());

        if (jobRequest.get().getServiceOffering() != null) {
            Optional<ProfessionalServiceOffering> pso = professionalServiceOfferingService.findById(jobRequest.get().getServiceOffering().getId());
            mv.addObject("service_professional", pso.get());
        }


        mv.addObject("candidates", jobCandidatesDTOs);
        mv.addObject("jobCandidates12", jobRequestFullDTOs);
        mv.addObject("expertise", expertiseDTO);
        mv.addObject("jobRequest", jobDTO);
        mv.addObject("jobContracted", oJobContracted.get());

        return mv;
    }

    /**
     * Apresenta a tela de detalhes de um candidato para um serviço.
     * @param jobId
     * @param candidateId
     * @return
     * @throws Exception
     */
    @GetMapping("/meus-pedidos/{jobId}/detalhes/{candidateId}")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public ModelAndView showDetailsRequestCandidate(@PathVariable Long jobId, @PathVariable Long candidateId) throws Exception {
        ModelAndView mv = new ModelAndView("client/details-request-candidate");

        System.out.println("DADOSSS");
        System.out.println(jobId);
        System.out.println(candidateId);

        Optional<User> oCandidate = userService.findById(candidateId);
        Optional<Individual> oIndividual = individualService.findById(candidateId);

        System.out.println(oCandidate);
        System.out.println(oIndividual.get());

        if (!oCandidate.isPresent()) {
            throw new EntityNotFoundException("O candidato não foi encontrado!");
        }

        Optional<JobRequest> oJobRequest = jobRequestService.findById(jobId);
        System.out.println("oJobRequest");
        System.out.println(oJobRequest);

        if (!oJobRequest.isPresent()) {
            throw new EntityNotFoundException("O candidato não foi encontrado!");
        }

        Optional<JobCandidate> jobCandidate = jobCandidateService.findById(oJobRequest.get().getId(), oIndividual.get().getId());

        if (!jobCandidate.isPresent()) {
            throw new EntityNotFoundException("Candidato não encontrado. Por favor, tente novamente.");
        }

        Optional<User> oClient = (userService.findByEmail(authentication.getEmail()));
        if (!oClient.isPresent()) {
            throw new EntityNotFoundException("Usuário não encontrado. Por favor, tente novamente.");
        }

        JobCandidateDTO jobCandidateDTO = jobCandidateMapper.toDto(jobCandidate.get());
        UserDTO clientDTO = userMapper.toDto(oClient.get());

        List<Follows> follows = followsService.findFollowProfessionalClient(oCandidate.get(), oClient.get());
        boolean isFollow = !follows.isEmpty();
        System.out.println(jobCandidateDTO.getId());
        mv.addObject("jobCandidate", jobCandidateDTO);
        mv.addObject("isFollow", isFollow);
        mv.addObject("client", clientDTO);

        return mv;
    }

    @GetMapping("/meus-pedidos/disponiveis")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public ModelAndView showAvailableJobs(
            HttpServletRequest request,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "4") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Optional<User> oUser = (userService.findByEmail(authentication.getEmail()));

        if (!oUser.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("dateTarget").ascending());
        Page<JobRequest> jobRequestPage = null;
        List<JobRequestFullDTO> jobRequestFullDTOs = null;

        jobRequestPage = jobRequestService.findByStatusAndClient(JobRequest.Status.AVAILABLE, oUser.get(), pageRequest);

        jobRequestFullDTOs = jobRequestPage.stream()
                .map(jobRequest -> {
                    Optional<Long> totalCandidates = jobCandidateService.countByJobRequest(jobRequest);

                    if (totalCandidates.isPresent()) {
                        return jobRequestMapper.toFullDto(jobRequest, totalCandidates);
                    }

                    return jobRequestMapper.toFullDto(jobRequest, Optional.ofNullable(0L));
                }).collect(Collectors.toList());

        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(jobRequestPage, "/minha-conta/cliente/meus-pedidos/disponiveis");

        ModelAndView mv = new ModelAndView("client/job-request/tabs/available-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobRequestFullDTOs);

        return mv;
    }

    /**
     * O cliente exclui um anúncio.
     * O cliente pode ter contratado de alguma outra forma, não necessitando mais do anúncio.
     * @param id
     * @param redirectAttributes
     * @return
     */
    @DeleteMapping("/desistir/{id}")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public String desist(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String currentUserEmail = authentication.getEmail();

        Optional<User> oUser = userService.findByEmail(currentUserEmail);
        if(!oUser.isPresent()){
            throw new EntityNotFoundException("O usuário não foi encontrado!");
        }

        Optional<JobRequest> oJobRequest = jobRequestService.findById(id);
        if(!oJobRequest.isPresent()) {
            throw new EntityNotFoundException("O anúncio não foi encontrado!");
        }

        jobRequestService.delete(id);
        quartzService.sendEmailToConfirmationStatus(id);
        redirectAttributes.addFlashAttribute("msg", "O pedido foi excluído com sucesso!");

        return "redirect:/minha-conta/cliente#disponiveis";
    }

    @GetMapping("/meus-pedidos/para-orcamento")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public ModelAndView showDisputedJobs(
            HttpServletRequest request,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Optional<User> oUser = (userService.findByEmail(authentication.getEmail()));

        if (!oUser.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("dateTarget").ascending());
        Page<JobRequest> jobRequestPage = null;
        List<JobRequestFullDTO> jobRequestFullDTOs = null;

        jobRequestPage = jobRequestService.findByStatusAndClient(JobRequest.Status.BUDGET, oUser.get(), pageRequest);

        jobRequestFullDTOs = jobRequestPage.stream()
                .map(jobRequest -> {
                    Optional<Long> totalCandidates = jobCandidateService.countByJobRequest(jobRequest);

                    if (totalCandidates.isPresent()) {
                        return jobRequestMapper.toFullDto(jobRequest, totalCandidates);
                    }

                    return jobRequestMapper.toFullDto(jobRequest, Optional.ofNullable(0L));
                }).collect(Collectors.toList());

        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(jobRequestPage, "/minha-conta/cliente/meus-pedidos/disponiveis");

        ModelAndView mv = new ModelAndView("client/job-request/tabs/available-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobRequestFullDTOs);

        return mv;
    }

    /**
     * Retorna os serviços para fazer, ou seja, que já foram confirmados pelo profissional.
     * @param request
     * @param page
     * @param size
     * @param order
     * @param direction
     * @return
     * @throws Exception
     */
    @GetMapping("/meus-pedidos/para-fazer")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public ModelAndView showTodoJobs(
            HttpServletRequest request,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Optional<User> oUser = (userService.findByEmail(authentication.getEmail()));

        if (!oUser.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("dateTarget").ascending());
        Page<JobRequest> jobRequestPage = null;
        List<JobRequestFullDTO> jobRequestFullDTOs = null;

        jobRequestPage = jobRequestService.findByStatusAndClient(JobRequest.Status.TO_DO, oUser.get(), pageRequest);

        jobRequestFullDTOs = jobRequestPage.stream()
                .map(jobRequest -> {
                    Optional<Long> totalCandidates = jobCandidateService.countByJobRequest(jobRequest);

                    if (totalCandidates.isPresent()) {
                        return jobRequestMapper.toFullDto(jobRequest, totalCandidates);
                    }

                    return jobRequestMapper.toFullDto(jobRequest, Optional.ofNullable(0L));
                }).collect(Collectors.toList());

        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(jobRequestPage, "/minha-conta/cliente/meus-pedidos/disponiveis");

        ModelAndView mv = new ModelAndView("client/job-request/tabs/available-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobRequestFullDTOs);

        return mv;
    }

    /**
     * Retorna os serviços que aguardam confirmação do profissional para serem realizados
     * @param request
     * @param page
     * @param size
     * @param order
     * @param direction
     * @return
     * @throws Exception
     */
    @GetMapping("/meus-pedidos/para-confirmar")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public ModelAndView showForHiredJobs(
            HttpServletRequest request,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Optional<User> client = (userService.findByEmail(authentication.getEmail()));

        if (!client.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("dateCreated").descending());
        Page<JobCandidate> jobCandidatePage = null;
        List<JobCandidateMinDTO> jobCandidateDTOs = null;

        jobCandidatePage = jobCandidateService.findByJobRequest_StatusAndJobRequest_Client(JobRequest.Status.TO_HIRED, client.get(),pageRequest);

        jobCandidateDTOs = jobCandidatePage.stream()
                .map(jobCandidate -> {
                    Optional<Long> totalCandidates = jobCandidateService.countByJobRequest(jobCandidate.getJobRequest());

                    if (totalCandidates.isPresent()) {
                        return jobCandidateMapper.toMinDto(jobCandidate, totalCandidates);
                    }

                    return jobCandidateMapper.toMinDto(jobCandidate, Optional.ofNullable(0L));
                }).collect(Collectors.toList());

        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(jobCandidatePage, "/minha-conta/cliente/meus-pedidos/para-confirmar");

        ModelAndView mv = new ModelAndView("client/job-request/tabs/to-hired-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobCandidateDTOs);

        return mv;
    }

    @GetMapping("/meus-pedidos/fazendo")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public ModelAndView showDoingJobs(
            HttpServletRequest request,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Optional<User> oUser = (userService.findByEmail(authentication.getEmail()));

        if (!oUser.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<JobContracted> jobContractedPage = null;
        List<JobContractedFullDTO> jobContractedDTOs = null;

        jobContractedPage = jobContractedService.findByJobRequest_StatusAndJobRequest_Client(JobRequest.Status.DOING, oUser.get(), pageRequest);

        jobContractedDTOs = jobContractedPage.stream()
                .map(jobContracted -> {

                    Optional<Long> totalCandidates = jobCandidateService.countByJobRequest(jobContracted.getJobRequest());

                    if (totalCandidates.isPresent()) {
                        return jobContractedMapper.toFullDto(jobContracted, totalCandidates);
                    }

                    return jobContractedMapper.toFullDto(jobContracted, Optional.ofNullable(0L));
                })
                .collect(Collectors.toList());

        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(jobContractedPage, "/minha-conta/cliente/meus-pedidos/fazendo");

        ModelAndView mv = new ModelAndView("client/job-request/tabs/doing-jobs-report");
        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobContractedDTOs);

        quartzService.updateJobRequestStatusWhenIsHiredDateExpired();

        return mv;
    }

    /**
     * Retorna uma lista de pedidos criados pelo cliente que já foram finalizados.
     * @param request
     * @param page
     * @param size
     * @param order
     * @param direction
     * @return
     * @throws Exception
     */
    @GetMapping("/meus-pedidos/executados")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public ModelAndView showJobsPerformed(
            HttpServletRequest request,
            @RequestParam(value = "pag", defaultValue = "1") int page,
            @RequestParam(value = "siz", defaultValue = "3") int size,
            @RequestParam(value = "ord", defaultValue = "id") String order,
            @RequestParam(value = "dir", defaultValue = "ASC") String direction
    ) throws Exception {

        Optional<User> oUser = (userService.findByEmail(authentication.getEmail()));

        if (!oUser.isPresent()) {
            throw new Exception("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<JobContracted> jobContractedPage = null;
        List<JobContractedFullDTO> jobContractedDTOs = null;

        jobContractedPage = jobContractedService.findByJobRequest_StatusAndJobRequest_Client(JobRequest.Status.CLOSED, oUser.get(), pageRequest);
        System.out.println("Quantos: " + jobContractedPage.getTotalElements());
        jobContractedDTOs = jobContractedPage.stream()
                .map(jobContracted -> {

                    System.out.println("ID: " + jobContracted.getJobRequest().getId());

                    Optional<Long> totalCandidates = jobCandidateService.countByJobRequest(jobContracted.getJobRequest());

                    if (totalCandidates.isPresent()) {
                        return jobContractedMapper.toFullDto(jobContracted, totalCandidates);
                    }

                    return jobContractedMapper.toFullDto(jobContracted, Optional.ofNullable(0L));
                })
                .collect(Collectors.toList());

        PaginationDTO paginationDTO = paginationUtil.getPaginationDTO(jobContractedPage, "/minha-conta/cliente/meus-pedidos/executados");

        ModelAndView mv = new ModelAndView("client/job-request/tabs/executed-jobs-report");

        mv.addObject("pagination", paginationDTO);
        mv.addObject("jobs", jobContractedDTOs);

        return mv;
    }

    /**
     * Encerra o recebimento de candidaturas antes de receber o total de candidaturas esperado.
     * Basicamente, muda o estado para BUDGET.
     * @param id
     * @param redirectAttributes
     * @return
     * @throws IOException
     */
    @PatchMapping("/encerra-pedido/{id}")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public String updateRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) throws IOException {

        Optional<User> oClient = (userService.findByEmail(authentication.getEmail()));

        if (!oClient.isPresent()) {
            throw new AuthenticationCredentialsNotFoundException("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        Optional<JobRequest> oJobRequest = this.jobRequestService.findById(id);

        if (!oJobRequest.isPresent()) {
            throw new EntityNotFoundException("Solicitação não foi encontrada pelo id informado.");
        }

        JobRequest jobRequest = oJobRequest.get();

        Long jobRequestClientId = jobRequest.getUser().getId();
        Long clientId = oClient.get().getId();

        if (jobRequestClientId != clientId) {
            throw new EntityNotFoundException("Você não tem permissão para alterar essa solicitação.");
        }

        if (!jobRequest.getStatus().equals(JobRequest.Status.AVAILABLE)) {
            throw new InvalidParamsException("O status da solicitação não pode ser alterado.");
        }

        jobRequest.setStatus(JobRequest.Status.BUDGET);
        this.jobRequestService.save(jobRequest);

        redirectAttributes.addFlashAttribute("msg", "Solicitação alterada!");
        return "redirect:/minha-conta/meus-pedidos?tab=paraOrcamento";
    }

    /**
     * O cliente escolhe um profissional para realizar o orçamento ou cancela a escolha do profissional para orçamento.
     * *
     * @param jobId
     * @param candidateId
     * @param redirectAttributes
     * @return
     * @throws IOException
     */
    @PatchMapping("/orcamento-ao/{candidateId}/para/{jobId}")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public String markAsBudget(@PathVariable Long jobId, @PathVariable Long candidateId, RedirectAttributes redirectAttributes) throws IOException {

        Optional<JobCandidate> oJobCandidate = jobCandidateService.findById(jobId, candidateId);

        if (!oJobCandidate.isPresent()) {
            throw new EntityNotFoundException("Candidato não encontrado");
        }

        Optional<JobRequest> oJobRequest = jobRequestService.findById(jobId);
        if(!oJobRequest.isPresent()) {
            throw new EntityNotFoundException("Pedido não encontrado!");
        }
        JobRequest jobRequest = oJobRequest.get();

        //verifica se o usuário logado é o dono do dado
        User user = userService.getAuthenticated();

        if(jobRequest.getUser().getId() != user.getId()){
            throw new InvalidParamsException("O usuário não tem permissão de alterar este dado!");
        }

        JobCandidate jobCandidate = oJobCandidate.get();
        jobCandidate.setChosenByBudget(!jobCandidate.isChosenByBudget());
        jobCandidateService.save(jobCandidate);

        //caso o candidato seja o primeiro escolhido para orçamento
        if (jobCandidate.isChosenByBudget() && jobRequest.getStatus() == JobRequest.Status.AVAILABLE) {
            jobRequest.setStatus(JobRequest.Status.BUDGET);
            jobRequestService.save(jobRequest);
        }

        //caso o candidato seja o último ou único e o cliente cancelou o orçamento, muda o job para AVAILABLE novamente
        if(!jobCandidate.isChosenByBudget()){
            List<JobCandidate> jobCandidates = jobCandidateService.findByJobRequestAndChosenByBudget(jobRequest, true);
            if(jobCandidates.isEmpty()) {
                jobRequest.setStatus(JobRequest.Status.AVAILABLE);
            }
        }

        return "redirect:/minha-conta/cliente/meus-pedidos/" + jobId;
    }

    /**
     * Altera o estado para finalizado, ou seja, o cliente verifica que o serviço foi finalizado
     * e então sinaliza manualmete esta informação na plataforma.
     * O estado do JobRequest é mudado para CLOSED e a data da finalização é guardada em JobContracted.
     * @param jobId
     * @param dto
     * @param redirectAttributes
     * @return
     * @throws IOException
     */
    @PatchMapping("/informa-finalizado/{jobId}")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public String markAsClose(
            @PathVariable Long jobId,
            JobCandidateMinDTO dto,
            RedirectAttributes redirectAttributes) throws IOException {

        Optional<JobRequest> oJobRequest = jobRequestService.findById(jobId);

        if(!oJobRequest.isPresent()) {
            throw new EntityNotFoundException("Pedido não encontrado!");
        }

        JobRequest jobRequest = oJobRequest.get();

        //verifica se o usuário é realmente o dono do anúncio
        User user = jobRequest.getUser();

        Optional<User> oClient = userService.findByEmail(authentication.getEmail());

        if (!oClient.isPresent()) {
            throw new AuthenticationCredentialsNotFoundException("Usuário não autenticado! Por favor, realize sua autenticação no sistema.");
        }

        jobRequest.setStatus(JobRequest.Status.CLOSED);
        jobRequestService.save(jobRequest);

        //busca pelo job que foi contratado, ao finalizar é adicionado no campo data e horário em que foi concluido.
        Optional<JobContracted> oJobContracted = jobContractedService.findByJobRequest(jobRequest);
        if(!oJobContracted.isPresent()) {
            throw new EntityNotFoundException("O serviço não pode ser finalizado!");
        }

        JobContracted jobContracted = oJobContracted.get();
        jobContracted.setFinishDate(LocalDate.now());
        jobContractedService.save(jobContracted);

        return "redirect:/minha-conta/cliente#executados";
    }

    /**
     * Altera o estado de um serviço para TO_HIRED, ou seja, o cliente contratou um serviço mas
     * fica no estado de espera da confirmação do profissional.
     * @param jobId
     * @param userId
     * @param redirectAttributes
     * @return
     * @throws IOException
     */
    @PatchMapping("/contrata/{userId}/para/{jobId}")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public String markAsHided(@PathVariable Long jobId, @PathVariable Long userId, RedirectAttributes redirectAttributes) throws IOException {
        Optional<JobCandidate> oJobCandidate = jobCandidateService.findById(jobId, userId);

        if (!oJobCandidate.isPresent()) {
            throw new EntityNotFoundException("Candidato não encontrado");
        }

        Optional<JobRequest> oJobRequest = jobRequestService.findById(jobId);

        if(!oJobRequest.isPresent()) {
            throw new EntityNotFoundException("Pedido não encontrado!");
        }

        //muda o estado para contratado, mas a espera de confirmação do profissional
        JobRequest jobRequest = oJobRequest.get();
        jobRequest.setStatus(JobRequest.Status.TO_HIRED);
        jobRequestService.save(jobRequest);

        System.out.println("JOBBBBBBBBB" + oJobRequest.get());

        //guarda a data de contratação
        Optional<JobContracted> oJobContracted = jobContractedService.findByJobRequest(oJobRequest.get());
//        if(!oJobContracted.isPresent()) {
//            throw new EntityNotFoundException("O profissional não pode ser contratado!");
//        }
        Optional<User> oUser = userService.findById(userId);

        JobContracted jobContracted = new JobContracted();
        jobContracted.setHiredDate(LocalDate.now());
        jobContracted.setJobRequest(oJobRequest.get());
        jobContracted.setUser(oUser.get());
        jobContractedService.save(jobContracted);

        return "redirect:/minha-conta/cliente/meus-pedidos/"+jobId;
    }

    @Autowired
    public ClientController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Responsável por realizar o pagamento via API Mercado Pago.
     */
    @PostMapping("/pagamento")
    @RolesAllowed({RoleType.USER})
    public ResponseEntity<PaymentResponseDTO> createPayment(@RequestBody CardPaymentDTO cardPaymentDTO) {
        log.info("Received payment request: {}", cardPaymentDTO);

            Optional<User> oUser = (userService.findByEmail(authentication.getEmail()));

//
////            if(!paymentResponse.getStatusCode().is2xxSuccessful()){
////                response.setMessage("Erro ao processar pagamento. Tente novamente");
////                return ResponseEntity.status(paymentResponse.getStatusCode()).body(response);
////            }
//
//            Map<?, ?> responseMap = (Map<?, ?>) responseBody;

//            Integer paymentId = (Integer) responseMap.get("id");
//            String status = (String) responseMap.get("status");
////
//            PaymentDTO paymentDTO = new PaymentDTO(paymentId, status);
//            Payment payment = paymentMapper.toEntity(paymentDTO);
////
//            paymentService.save(payment);
//
//            response.setData(paymentResponse.getBody());
//
//            System.out.println("SATTSUS PAGAMENTO.....");
////            System.out.println(status);
//
//
//            return ResponseEntity.ok(response);
//
//
//        } catch (Exception e) {
//            response.setMessage("Erro ao fazer pagamento. Por favor, tente novamente.");
//            return ResponseEntity.status(400).body(response);
//        }


        PaymentResponseDTO payment = paymentService.pay(cardPaymentDTO);

        System.out.println("paymentResponse");
        System.out.println(payment.getStatus());
        System.out.println(payment.getId());

        if(Objects.equals(payment.getStatus(), "approved")){
            Payment payment1 = new Payment();
            payment1.setStatus(payment.getStatus());
            payment1.setPaymentId(payment.getId());
            paymentService.save(payment1);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    /**
     * Apos processar o pagamento, ele é inserido na tabela payments_jobRequests
     * contendo o id do pagamento e do serviço que foi realizado o pagamento.
     * Finalizado este processo é preciso gerar um comprovante do pagamento
     * tanto para cliente quanto profissional, que sera enviado por email
     * **/
    @PostMapping("/pagamento/jobRequest")
    @RolesAllowed({RoleType.USER})
    public ModelAndView savePaymentJob(@RequestBody  PaymentJobDTO dto, BindingResult errors, RedirectAttributes redirectAttributes) throws ConfigurationException, TranscoderException, IOException, TransformerException, WriterException {
        ModelAndView modelAndView = new ModelAndView();
        final Date now = new Date();

        Optional<Payment> oPayment = paymentService.findByPaymentId(dto.getPaymentId());
        Optional<JobRequest> oJobRequest = jobRequestService.findById(dto.getJobRequestId());

        PaymentJobRequest paymentJob = new PaymentJobRequest();
        paymentJob.setJobRequestId(dto.getJobRequestId());
        paymentJob.setPayment(oPayment.get());
        paymentJob.setJobRequest(oJobRequest.get());
        paymentJob.setDateCreated(now);

        paymentJobService.save(paymentJob);

        createVoucherPayment(paymentJob); // GERA O COMPROVANTE DE PAGAMENTO

        modelAndView.addObject("mensagem", "Pagamento processado com sucesso!");

        return modelAndView;
    }

    public String createVoucherPayment(PaymentJobRequest paymentJob) throws IOException, TranscoderException, ConfigurationException, TransformerException, WriterException, TransformerException {
        /*Busca o job request - cliente*/
        Optional<JobRequest> oJobRequest = jobRequestService.findById(paymentJob.getJobRequestId());

        /*Traz o profissional*/
        Optional<JobContracted> oJobContracted = jobContractedService.findByJobRequest(oJobRequest.get());

        /*Traz o servico*/
        Optional<Expertise> oExpertise = expertiseService.findById(oJobRequest.get().getExpertise().getId());

        PaymentVoucher paymentVoucher = new PaymentVoucher();
        paymentVoucher.setClient(oJobRequest.get().getUser());
        paymentVoucher.setProfessional(oJobContracted.get().getUser());
        paymentVoucher.setCode(gerarCodigoAlfanumerico());
        paymentVoucher.setJobRequest(oJobRequest.get());

        String emailProfissional = oJobContracted.get().getUser().getEmail();
        String emailClient = oJobRequest.get().getUser().getEmail();

        paymentVoucherService.save(paymentVoucher);
        Date dataAtual = new Date();

        // Converter LocalDate para Date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataAtual);

        // Incrementando 15 dias
        calendar.add(Calendar.DATE, 15);

        // Obtendo a nova data após o incremento
        Date dataFinal = calendar.getTime();
        SimpleDateFormat sdfNovo = new SimpleDateFormat("dd/MM/yyyy");

        String client_name = paymentVoucher.getClient().getName(); // NOME DO CLIENTE
        String client_fone = paymentVoucher.getClient().getPhoneNumber(); // TELEFONE DO CLIENTE
        String client_email = paymentVoucher.getClient().getEmail(); // EMAIL DO CLIENTE
        String client_address_name = paymentVoucher.getClient().getAddress().getNeighborhood(); // ENDEREÇO DO CLIENTE
        String client_address_number = paymentVoucher.getClient().getAddress().getNumber(); // NUMERO ENDEREÇO DO CLIENTE
        String client_address = client_address_name + " - " + client_address_number; // ENDEREÇO DO CLIENTE

        String profes_name = paymentVoucher.getProfessional().getName(); // NOME DO PROFISSIONAL
        String profes_fone = paymentVoucher.getProfessional().getPhoneNumber(); // TELEFONE DO PROFISSIONAL
        String profes_email = paymentVoucher.getProfessional().getEmail(); // EMAIL DO PROFISSIONAL
        String profes_address_name = paymentVoucher.getProfessional().getAddress().getNeighborhood(); // ENDEREÇO DO PROFISSIONAL
        String profes_address_number = paymentVoucher.getProfessional().getAddress().getNumber(); // NUMERO ENDEREÇO DO PROFISSIONAL
        String profes_address = profes_address_name + " - " + profes_address_number; // ENDEREÇO DO PROFISSIONAL

        String service_name = oJobRequest.get().getExpertise().getName();

//        Optional<ProfessionalServiceOffering> optionalProfessionalServiceOffering = professionalServiceOfferingService.findProfessionalServiceOfferingByExpertiseAAndUser(paymentVoucher.getProfessional().getId(), oJobRequest.get().getExpertise().getId());
        Optional<ProfessionalServiceOffering> optionalProfessionalServiceOffering = professionalServiceOfferingService.findProfessionalServiceOfferingByIdAndUser(oJobRequest.get().getServiceOffering().getId(), paymentVoucher.getProfessional().getId());

        String value_service = "0,00";
        if (optionalProfessionalServiceOffering.isPresent()){
            value_service = String.valueOf(optionalProfessionalServiceOffering.get().getPrice());
        }

        File pdfFile = paymentVoucherService.generateCertificate(svgCertificateTemplate, paymentVoucher.getCode(),service_name, sdfNovo.format(dataAtual), sdfNovo.format(dataFinal),
                client_name, "111111111", client_fone, client_email, profes_name, "10444444440",
                profes_email, profes_fone, profes_address, "PIX", value_service, "QRDCOD", sdfNovo.format(dataAtual));
        Map uploadResult = cloudinary.uploader().upload(pdfFile, ObjectUtils.asMap("folder", "certificates"));

        String uploadURL = (String)uploadResult.get("url");

        //envia um email com a URL do certificado
        /*Envio de email para o profissional*/
        quartzService.sendEmailPaymentVoucher(paymentVoucher.getCode(),
                oJobRequest.get().getUser().getId(), oJobContracted.get().getUser().getId(), oExpertise.get().getName(), sdf.format(dataFinal), (String)uploadResult.get("url"));
        pdfFile.delete();

//        (url.openStream(), voucher, service,date, date_due,name_client,document_client,fone_client,mail_client,name_professional, document_professional,
//                mail_professional, fone_professional, endereco_pro, payment_type, payment_value, qrCode);

        return "redirect:/minha-conta/cliente#executados";
    }

    /* Gera código aleatorico e unico para gravação de comprovante */
    private String gerarCodigoAlfanumerico() {
        UUID uuid = UUID.randomUUID();
        String codigo = uuid.toString().replace("-", "").substring(0, 10);
        return codigo.toUpperCase();
    }

    /**
     * Avalia o serviço depois de finalizado
     * @param jobId
     * @param redirectAttributes
     * @return
     * @throws IOException
     */
    @GetMapping("/avaliar/servico/{jobId}/profissional/{profissionalId}")
    @RolesAllowed({RoleType.USER, RoleType.COMPANY})
    public ModelAndView evaluateService(@PathVariable Long jobId, @PathVariable Long profissionalId, RedirectAttributes redirectAttributes) throws IOException {
        Optional<User> oClientAuthenticated = (userService.findByEmail(authentication.getEmail()));
        ModelAndView mv = new ModelAndView("client/evaluate-jobs");

        if(!oClientAuthenticated.isPresent()){
            return mv.addObject("visitor/login");
        }

        Optional<JobRequest> oJobRequest = jobRequestService.findById(jobId);

        Optional<User> oIndividual = userService.findById(profissionalId);

        System.out.println("INDIVIDUAL");
        System.out.println(oIndividual.get().getName());

        Optional<JobContracted> oJobContracted = jobContractedService.findByJobRequest(oJobRequest.get());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


        mv.addObject("job", oJobRequest.get());
        mv.addObject("professional", oIndividual.get());
        mv.addObject("jobContracted", oJobContracted.get());
        mv.addObject("hiredDate", dateTimeFormatter.format(oJobContracted.get().getHiredDate()));
        mv.addObject("finishDate", dateTimeFormatter.format(oJobContracted.get().getFinishDate()));

        return mv;
    }

    /**
     * @param jobId
     * @param profissionalId
     * @param dto
     * @param redirectAttributes
     * @return
     * @throws IOException
     * @throws ParseException
     */
    @PostMapping("/avaliar/servico/{jobId}/profissional/{profissionalId}")
    @RolesAllowed({RoleType.USER})
    public ModelAndView  saveAssessmentProfessional(
            @PathVariable Long profissionalId,
            @PathVariable Long jobId,
            AssessmentProfessionalDTO dto,
            AssessmentProfessionalFileDTO assessmentProfessionalFileDTO,
            @RequestParam("pathImage") MultipartFile file,
            RedirectAttributes redirectAttributes,
            BindingResult errors
    ) throws IOException, ParseException {
        String currentUserEmail = authentication.getEmail();

        Optional<Individual> oindividual = individualService.findById(profissionalId);
        Optional<Individual> oClliente = individualService.findByEmail(currentUserEmail);

        if(!oindividual.isPresent()){
            throw new EntityNotFoundException("O usuário não foi encontrado!");
        }

        Optional<JobRequest> oJobRequest = jobRequestService.findById(jobId);

        AssessmentProfessional assessmentProfessional = new AssessmentProfessional();
        String comment = dto.getComment();

        if (!comment.isEmpty()){
            String analyzedComment  = analyzeComment(comment);
            if ("Comentário Ofensivo".equals(analyzedComment)) {
                errors.rejectValue("comment", "error.dto", "Proibido comentários ofensivos!.");
                return errorFowarding(dto, assessmentProfessionalFileDTO, errors);
            }
        }

        assessmentProfessional.setComment(dto.getComment());
        assessmentProfessional.setProfessional(oindividual.get());
        assessmentProfessional.setQuality(dto.getQuality());
        assessmentProfessional.setDate(DateUtil.getToday());

        assessmentProfessional.setClient(oClliente.get());
        assessmentProfessional.setJobRequest(oJobRequest.get());
        assessmentProfessionalService.save(assessmentProfessional);

        if (!assessmentProfessionalFileDTO.getPathImage().isEmpty() ) {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            String imageUrl = (String) uploadResult.get("url");

            if(moderateService.isNsfwImage(imageUrl)){
                String publicId = (String) uploadResult.get("public_id");
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                redirectAttributes.addFlashAttribute("msgError", "A imagem enviada contém conteúdo impróprio. Por favor, envie outra foto.");
                return new ModelAndView("redirect:/minha-conta/cliente/avaliar/servico/"+jobId+"/profissional/"+profissionalId);
            }

//            AssessmentProfessionalFiles assessmentProfessionalFiles = assessmentProfessionalFileMapper.toEntity(assessmentProfessionalFileDTO);
//            assessmentProfessionalFiles.setAssessmentProfessional(assessmentProfessional);
            AssessmentProfessionalFiles assessmentProfessionalFiles1 = new AssessmentProfessionalFiles();
            assessmentProfessionalFiles1.setPathImage(imageUrl);
            assessmentProfessionalFiles1.setAssessmentProfessional(assessmentProfessional);
            assessmentProfessionalFileService.save(assessmentProfessionalFiles1);
        }

        redirectAttributes.addFlashAttribute("msg", "Avaliação realizada com sucesso!");

        return new ModelAndView("redirect:/minha-conta/cliente#executados");
    }

    public boolean isValidateImage(MultipartFile image){
        List<String> contentTypes = Arrays.asList("image/png", "image/jpg", "image/jpeg");

        for(int i = 0; i < contentTypes.size(); i++){
            if(image.getContentType().toLowerCase().startsWith(contentTypes.get(i))){
                return true;
            }
        }

        return false;
    }

    /*Método responsavel em chamar a api e então validar comentários*/
    @GetMapping("/analyze-comment")
    public String analyzeComment(String coment){
        return perspectiveAPIService.analyzeComment(coment);
    }

    private ModelAndView errorFowarding(AssessmentProfessionalDTO dto, AssessmentProfessionalFileDTO dtoFiles,BindingResult errors) {
        ModelAndView mv = new ModelAndView("client/evaluate-jobs");
        mv.addObject("dto", dto);
        mv.addObject("errors", errors.getAllErrors());
        return mv;
    }

    private ModelAndView errorFowardingResponse(AssessmentResponseDTO dto,BindingResult errors) {
        ModelAndView mv = new ModelAndView("professional/detail-service");
        mv.addObject("dto", dto);
        mv.addObject("errors", errors.getAllErrors());
        return mv;
    }

    /**
     * @param profissionalId
     * @param dto
     * @param redirectAttributes
     * @return
     * @throws IOException
     * @throws ParseException
     */
    @PostMapping("/contratar-servico/profissional/{profissionalId}")
    @RolesAllowed({RoleType.USER})
    public ModelAndView  saveContratedServiceProfessional(
            JobRequestDTO dto,
            RedirectAttributes redirectAttributes,
            @PathVariable Long profissionalId,
            BindingResult errors
    ) throws IOException, ParseException {
        String currentUserEmail = authentication.getEmail();

        System.out.println(dto);

        Optional<Individual> oCliente = individualService.findByEmail(currentUserEmail);

        if(!oCliente.isPresent()){
            throw new EntityNotFoundException("O usuário não foi encontrado!");
        }

        Optional<Expertise> oExpertise = expertiseService.findById(dto.getExpertiseId());

        if(!oExpertise.isPresent()){
            throw new EntityNotFoundException("Especialidade não foi encontrada!");
        }

        JobRequest jobRequest = new JobRequest();
        jobRequest.setExpertise(oExpertise.get());
        jobRequest.setUser(oCliente.get());
        jobRequest.setClientConfirmation(true);
        jobRequest.setQuantityCandidatorsMax(1);
        jobRequest.setDateCreated(LocalDate.now());
        jobRequest.setDateTarget(DateUtil.getThisMonth()); // para este mês
        jobRequest.setDescription(oExpertise.get().getDescription());
        jobRequest.setProfessionalConfirmation(true);
        jobRequest.setStatus(JobRequest.Status.TO_DO);

        Optional<User> oUser = userService.findById(profissionalId);

        Optional<ProfessionalServiceOffering> oProfessionalServiceOffering = professionalServiceOfferingService.findById(dto.getProfessionalServiceOfferingId());
        if(!oProfessionalServiceOffering.isPresent()){
            throw new EntityNotFoundException("Serviço do profissional não foi encontrada!");
        }
        jobRequest.setServiceOffering(oProfessionalServiceOffering.get());

        jobRequestService.save(jobRequest);

        JobCandidate jobCandidate = new JobCandidate(jobRequest, oUser.get());
        jobCandidateService.save(jobCandidate);

        if(!oUser.isPresent()){
            throw new EntityNotFoundException("Profissional não foi encontrado!");
        }

        JobContracted jobContracted = new JobContracted();
        jobContracted.setJobRequest(jobRequest);
        jobContracted.setUser(oUser.get());
        jobContracted.setHiredDate(LocalDate.now());

        jobContractedService.save(jobContracted);

        jobRequest.setStatus(JobRequest.Status.TO_DO);
        jobRequestService.save(jobRequest);

        redirectAttributes.addFlashAttribute("msg", "Pedido foi enviado, o profissional receberá uma notificação!!");

        return new ModelAndView("redirect:/minha-conta/cliente#paraFazer");
    }
}