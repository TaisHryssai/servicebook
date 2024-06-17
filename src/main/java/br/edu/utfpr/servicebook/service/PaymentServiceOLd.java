package br.edu.utfpr.servicebook.service;

import br.edu.utfpr.servicebook.model.entity.Payment;
import br.edu.utfpr.servicebook.model.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;


@Service
public class PaymentServiceOLd {

    private final Environment environment;

    @Autowired
    private PaymentRepository paymentRepository;

    private static final String API_URL = "https://api.mercadopago.com/v1/payments";
    private static final String ACCESS_TOKEN = "TEST-2738533774159236-052518-f4e09de99516f8d7b2adb21186313d1b-494777183";  // Substitua pelo seu token de acesso

    @Autowired
    public PaymentServiceOLd(Environment environment){
        this.environment = environment;
    }

    public ResponseEntity<?> pay(Map<String, Object> paymentData){
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://api.mercadopago.com/v1/payments";

        System.out.println("restTemplate");
        System.out.println(restTemplate);

        try {
            String accessToken = "TEST-2738533774159236-052518-f4e09de99516f8d7b2adb21186313d1b-494777183";

            System.out.println("accessToken");
            System.out.println(accessToken);


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(paymentData, headers);

            System.out.println("requestEntity");
            System.out.println(requestEntity);

            ResponseEntity<?> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Object.class);

            System.out.println("response");
            System.out.println(response);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Algo deu errado! Tente novamente.");
        }

    }

    public Payment save(Payment entity){ return paymentRepository.save(entity); }

    public Optional<Payment> find(Long payment){ return paymentRepository.findById(payment); }
}
