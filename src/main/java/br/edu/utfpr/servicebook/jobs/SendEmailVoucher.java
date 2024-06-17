package br.edu.utfpr.servicebook.jobs;

import br.edu.utfpr.servicebook.service.EmailSenderService;
import br.edu.utfpr.servicebook.service.UserService;
import com.itextpdf.layout.properties.TextAlignment;
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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
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

            emailSenderService.sendHTMLEmail(email1, "Service Book - Voucher de Pagamento", text);
            emailSenderService.sendHTMLEmail(email1, "Service Book - Voucher de Pagamento", textProfessional);

        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
    }

    public byte[] generatePDF(String code, String service, String date, String professional, String client) throws JobExecutionException {
        // criar PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter pdfWriter = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        Text titleText = new Text("Comprovante de Pagamento\n\n")
                .setFontSize(20)
                .setBold();
        Paragraph titleParagraph = new Paragraph(titleText)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(titleParagraph);

        Paragraph contentParagraph = new Paragraph()
                .add(new Text("Voucher: ").setBold())
                .add(new Text(code))
                .add("\n")
                .add(new Text("Serviço: ").setBold())
                .add(new Text(service))
                .add("\n")
                .add(new Text("Data de Validade: ").setBold())
                .add(new Text(date))
                .add("\n\n")
                .add(new Text("Profissional: : ").setBold())
                .add(new Text(professional))
                .add("\n\n")
                .add(new Text("Cliente: ").setBold())
                .add(new Text(client))
                .add("\n\n");
        document.add(contentParagraph);

        document.close();
        return outputStream.toByteArray();
    }
}
