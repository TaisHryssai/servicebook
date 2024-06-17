package br.edu.utfpr.servicebook.service;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
//import br.edu.utfpr.servicebook.model.entity.Payment;
import br.edu.utfpr.servicebook.exception.MercadoPagoException;
import br.edu.utfpr.servicebook.model.dto.CardPaymentDTO;
import br.edu.utfpr.servicebook.model.dto.PaymentResponseDTO;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
// SDK do Mercado Pago
import com.mercadopago.MercadoPagoConfig;
import br.edu.utfpr.servicebook.model.repository.PaymentRepository;


@Service
public class PaymentService {

    private final Environment environment;

    @Autowired
    private PaymentRepository paymentRepository;

    private static final String API_URL = "https://api.mercadopago.com/v1/payments";
    private static final String ACCESS_TOKEN = "TEST-2738533774159236-052518-f4e09de99516f8d7b2adb21186313d1b-494777183";  // Substitua pelo seu token de acesso

    @Value("TEST-2738533774159236-052518-f4e09de99516f8d7b2adb21186313d1b-494777183")
    private String mercadoPagoAccessToken;

    @Autowired
    public PaymentService(Environment environment){
        this.environment = environment;
    }

    public PaymentResponseDTO pay(CardPaymentDTO cardPaymentDTO){
        try {
            MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);

            System.out.println("cardPaymentDTO");
            System.out.println(cardPaymentDTO);

            PaymentClient paymentClient = new PaymentClient();

            PaymentCreateRequest paymentCreateRequest =
                    PaymentCreateRequest.builder()
                            .transactionAmount(BigDecimal.valueOf(1000))
                            .token(cardPaymentDTO.getToken())
                            .installments(cardPaymentDTO.getInstallments())
                            .paymentMethodId(cardPaymentDTO.getPaymentMethodId())
                            .payer(
                                    PaymentPayerRequest.builder()
                                            .email(cardPaymentDTO.getPayer().getEmail())
                                            .identification(
                                                    IdentificationRequest.builder()
                                                            .type(cardPaymentDTO.getPayer().getIdentification().getType())
                                                            .number(cardPaymentDTO.getPayer().getIdentification().getNumber())
                                                            .build())
                                            .build())
                            .build();

            Payment createdPayment = paymentClient.create(paymentCreateRequest);

            return new PaymentResponseDTO(
                    createdPayment.getId(),
                    String.valueOf(createdPayment.getStatus()),
                    createdPayment.getStatusDetail());
        } catch (MPApiException apiException) {
            System.out.println(apiException.getApiResponse().getContent());
            throw new MercadoPagoException(apiException.getApiResponse().getContent());
        } catch (MPException exception) {
            System.out.println(exception.getMessage());
            throw new MercadoPagoException(exception.getMessage());
        }
    }

    public br.edu.utfpr.servicebook.model.entity.Payment save(br.edu.utfpr.servicebook.model.entity.Payment entity){ return paymentRepository.save(entity); }

    public Optional<br.edu.utfpr.servicebook.model.entity.Payment> find(Long payment){ return paymentRepository.findById(payment); }

    public Optional<br.edu.utfpr.servicebook.model.entity.Payment> findByPaymentId(Long payment){ return paymentRepository.findByPaymentId(payment); }
}
