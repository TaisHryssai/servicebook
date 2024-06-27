package br.edu.utfpr.servicebook.jobs;

import br.edu.utfpr.servicebook.service.EmailSenderService;
import br.edu.utfpr.servicebook.service.UserService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Optional;
import br.edu.utfpr.servicebook.model.entity.User;
import java.io.ByteArrayOutputStream;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendEmailVoucher implements Job {

    public static final String PAYMENT_KEY = "token";
    public static final Long CLIENT_ID = Long.valueOf(0);
    public static final Long PROFESSIONAL_ID = Long.valueOf(0);
    public static final String SERVICE_KEY = "recipient_service";
    public static final String DATE_KEY = "recipient_date";

    public static final String VOUCHER_KEY = "recipient_voucher";

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private UserService userService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        String email1 = "thryssai@gmail.com";

        String code = (String) jobDataMap.get(SendEmailVoucher.PAYMENT_KEY);
        String voucher = (String) jobDataMap.get(SendEmailVoucher.VOUCHER_KEY);

        Long clientId = (Long) jobDataMap.get(SendEmailVoucher.CLIENT_ID.toString());
        Optional<User> oUser = userService.findById(clientId);

        Long professionalId = (Long) jobDataMap.get(SendEmailVoucher.PROFESSIONAL_ID.toString());
        Optional<User> oProfessional = userService.findById(professionalId);

        String service = (String) jobDataMap.get(SendEmailVoucher.SERVICE_KEY);

        String date = (String) jobDataMap.get(SendEmailVoucher.DATE_KEY);

        String emailClient = (String) oUser.get().getEmail();
        String emailProfessional = (String) oProfessional.get().getEmail();

        String text = "<html><body>" +
                "<h1>Olá, "+ oUser.get().getName() +"</h1>" +
                "<p>Segue abaixo o link para acessar o seu voucher. Com ele é possível verificar os dados relacionados ao seu pedido.</p>" +
                "<p>Qualquer informação pode ser obtida diretamente com o profissional, através do contato disponibilizado na plataforma.</p>" +
                "<p><strong> Clique no link para acessar o seu voucher: </strong></p>" +
                "<p><a href=\"" + voucher + "\" target=\"_blank\">Ver o meu Voucher</a></p>"+
                "<p>Atenciosamente, </p>" +
                "<p>Equipe ServiceBook. </p>" +
                "</body></html>";

        String textProfessional = "<html><body>" +
                "<h1>Olá, "+ oProfessional.get().getName() +"</h1>" +
                "<p>Segue o voucher emitido pelo cliente referente ao pedido de serviço.</p>" +
                "<p>Serviço solicitado por: <strong>"+ oUser.get().getName() +"</strong>.</p>" +
                "<p>Serviço solicitado: <strong>"+ service +"</strong>.</p>" +
                "<p><strong> Clique no link para acessar o voucher disponível: </strong></p>" +
                "<p><a href=\"" + voucher + "\" target=\"_blank\">Ver o Voucher</a></p>"+
                "<p>Atenciosamente, </p>" +
                "<p>Equipe ServiceBook. </p>" +
                "</body></html>";
        try {

            emailSenderService.sendHTMLEmail(emailClient, "Service Book - Voucher de Pagamento", text);
            emailSenderService.sendHTMLEmail(emailProfessional, "Service Book - Voucher de Pagamento", textProfessional);

        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
    }
}
