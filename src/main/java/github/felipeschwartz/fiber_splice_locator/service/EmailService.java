package github.felipeschwartz.fiber_splice_locator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
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
                "Use o código abaixo para redefinir sua senha. Ele expira em 15 minutos:\n\n" + code,
                null);
    }

    @Async
    public void sendServiceOrderAssignedEmail(String to, Long serviceOrderId, String ceoBoxNumber) {
        // Deep link — só abre a tela da OS se o app estiver instalado no
        // aparelho (esquema registrado em app.json/App.js do mobile).
        String link = "fibersplicelocator://service-orders/" + serviceOrderId;

        String text = "Você recebeu uma nova ordem de serviço.\n\n" +
                "OS #" + serviceOrderId + " - " + ceoBoxNumber + "\n\n" +
                "Acesse pelo aplicativo: " + link;

        String html = "<p>Você recebeu uma nova ordem de serviço.</p>" +
                "<p><strong>OS #" + serviceOrderId + "</strong> - " + ceoBoxNumber + "</p>" +
                "<p><a href=\"" + link + "\">Abrir ordem de serviço no aplicativo</a></p>";

        send(to, "Fiber Splice Locator - Nova ordem de serviço atribuída a você", text, html);
    }

    private void send(String to, String subject, String text, String html) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("from", from);
            payload.put("to", List.of(to));
            payload.put("subject", subject);
            payload.put("text", text);
            if (html != null) {
                payload.put("html", html);
            }

            restClient.post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
