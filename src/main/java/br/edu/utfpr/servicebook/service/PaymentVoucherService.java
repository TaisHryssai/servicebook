package br.edu.utfpr.servicebook.service;

import br.edu.utfpr.servicebook.model.entity.PaymentVoucher;
import br.edu.utfpr.servicebook.model.repository.PaymentVoucherRepository;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.fop.configuration.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.fop.activity.ContainerUtil;
import org.apache.fop.configuration.Configuration;
import org.apache.fop.configuration.ConfigurationException;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.apache.fop.svg.PDFTranscoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
@Service
public class PaymentVoucherService {

    @Autowired
    private PaymentVoucherRepository paymentVoucherRepository;

    public void save(PaymentVoucher entity) {
        paymentVoucherRepository.save(entity);
    }

    public List<PaymentVoucher> findAll() {
        return this.paymentVoucherRepository.findAll();
    }

    public Optional<PaymentVoucher> findById(Long id) {
        return this.paymentVoucherRepository.findById(id);
    }

    /**
     * Recebe a URL do template em SVG e gera o certificado em PDF, com as alterações devidas, retornando
     * o File do PDF armazenado no diretório temporário local.
     * @param svgCertificateTemplate
     * @param name
     * @param year
     * @return
     * @throws TranscoderException
     * @throws IOException
     * @throws ConfigurationException
     * @throws TransformerException
     */
    public File generateCertificate(String svgCertificateTemplate, String voucher, String service, String date, String date_due,
                                    String name_client, String document_client, String fone_client, String mail_client,
                                    String name_professional, String document_professional,String mail_professional,
                                    String fone_professional, String endereco_pro, String payment_type, String payment_value, String qrCode) throws TranscoderException, IOException, ConfigurationException, TransformerException {

        //busca o template de certificado em SVG na nuvem
        URL url = new URL(svgCertificateTemplate);

        //realiza a customização
        Document doc = updateCertificate(url.openStream(), voucher, service,date, date_due,name_client,document_client,fone_client,mail_client,name_professional, document_professional,
                mail_professional, fone_professional, endereco_pro, payment_type, payment_value, qrCode);

        //persiste as alterções em um SVG temporário
        File fileSVG = convertDocumentToFile(doc);

        File filePDF = convertSVGToPDF(fileSVG);

        return filePDF;
    }

    /**
     * Recebe o SVG de certificado e realiza as edições de texto no DOM
     * @param svgInputStream
     * @return
     * @throws IOException
     */
    private Document updateCertificate(InputStream svgInputStream, String voucher, String service, String date, String date_due,
                                       String name_client, String document_client, String fone_client, String mail_client,
                                       String name_professional, String document_professional,String mail_professional,
                                       String fone_professional, String endereco_pro, String payment_type, String payment_value, String qrCodeBase64) throws IOException {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);

        Document doc = f.createDocument(null, svgInputStream);
        Element numberVoucher = doc.getElementById("number-voucher");
        numberVoucher.setTextContent(voucher);

        Element nameService = doc.getElementById("service-name");
        nameService.setTextContent(service);

        Element datePurchase = doc.getElementById("date-service");
        datePurchase.setTextContent(date);

        Element dateDue = doc.getElementById("date-due");
        dateDue.setTextContent(date_due);

        Element nameClient = doc.getElementById("name-client");
        nameClient.setTextContent(name_client);

        Element documentClient = doc.getElementById("document-client");
        documentClient.setTextContent(document_client);

        Element foneClient = doc.getElementById("fone-client");
        foneClient.setTextContent(fone_client);

        Element emailClient = doc.getElementById("mail_client");
        emailClient.setTextContent(mail_client);

        Element nameProfessional = doc.getElementById("name_professional");
        nameProfessional.setTextContent(name_professional);

        Element documentProfessional = doc.getElementById("document-professional");
        documentProfessional.setTextContent(document_professional);

        Element emailProfessional = doc.getElementById("mail_professional");
        emailProfessional.setTextContent(mail_professional);

        Element foneProfessional = doc.getElementById("fone-professional");
        foneProfessional.setTextContent(fone_professional);

        Element endereco = doc.getElementById("endereco");
        endereco.setTextContent(endereco_pro);

        Element valor = doc.getElementById("valor");
        valor.setTextContent(payment_value);

        Element payment = doc.getElementById("payment");
        payment.setTextContent(payment_type);


//        Element qrElement = doc.getElementById("image0");
//        qrElement.setAttribute("xlink:href", qrCodeBase64);

        return doc;
    }

    /**
     * Recebe um document DOM e realiza a transformação em um arquivo persistido temporáriamente
     * @param doc
     * @return
     * @throws IOException
     * @throws TransformerException
     */
    private File convertDocumentToFile(Document doc) throws IOException, TransformerException {
        Path pathSVG = Files.createTempFile("certificado", ".svg");
        File fileSVG = pathSVG.toFile();
        FileWriter fileWriter = new FileWriter(fileSVG);

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(fileWriter);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(source, result);
        return fileSVG;
    }

    /**
     * Converte um SVG para PDF
     * @param fileSVG
     * @return
     * @throws IOException
     * @throws ConfigurationException
     * @throws TranscoderException
     */
    private File convertSVGToPDF(File fileSVG) throws IOException, ConfigurationException, TranscoderException {

        File filePDF = null;

        PDFTranscoder transcoder = new PDFTranscoder();

        //carrega o arquivo de configuração
        DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
        //File configFile = ResourceUtils.getFile("classpath:fop-config.xml");
        Configuration cfg = cfgBuilder.buildFromFile(ResourceUtils.getFile("classpath:fop-config.xml"));
        ContainerUtil.configure(transcoder, cfg);

        //Faz a leitura do novo SVG
        TranscoderInput transcoderInput = new TranscoderInput(new FileInputStream(fileSVG));

        //define o diretório temporário e o prefixo e sufixo do arquivo
        Path pathPDF = Files.createTempFile("certificado", ".pdf");
        filePDF = pathPDF.toFile();

        //realiza a transformação de SVG para PDF e guarda na pasta temporária
        OutputStream outputStream = new FileOutputStream(filePDF);
        TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);
        transcoder.transcode(transcoderInput, transcoderOutput);
        outputStream.flush();
        outputStream.close();


        return filePDF;
    }
}
