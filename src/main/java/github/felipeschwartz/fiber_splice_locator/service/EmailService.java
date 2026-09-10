package github.felipeschwartz.fiber_splice_locator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final RestClient restClient = RestClient.create("https://api.resend.com");

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${resend.from}")
    private String from;

    @Async
    public void sendPasswordResetCode(String to, String code) {
        send(to, "Fiber Splice Locator - Código de redefinição de senha",
                "Use o código abaixo para redefinir sua senha. Ele expira em 15 minutos:\n\n" + code);
    }

    @Async
    public void sendServiceOrderAssignedEmail(String to, Long serviceOrderId, String ceoBoxNumber) {
        send(to, "Fiber Splice Locator - Nova ordem de serviço atribuída a você",
                "Você recebeu uma nova ordem de serviço.\n\n" +
                        "OS #" + serviceOrderId + " - CEO " + ceoBoxNumber + "\n\n" +
                        "Acesse o aplicativo para ver os detalhes e iniciar o atendimento.");
    }

    private void send(String to, String subject, String text) {
        try {
            restClient.post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "from", from,
                            "to", List.of(to),
                            "subject", subject,
                            "text", text
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
