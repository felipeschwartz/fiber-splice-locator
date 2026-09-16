package github.felipeschwartz.fiber_splice_locator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class PushNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationService.class);

    private final RestClient restClient = RestClient.create("https://exp.host/--/api/v2/push/send");

    @Async
    public void sendPush(String pushToken, String title, String body) {
        if (pushToken == null || pushToken.isBlank()) return;

        try {
            // Expo responde 200 mesmo em falha de entrega (credencial FCM inválida etc.);
            // o erro de verdade só aparece no corpo, por isso logamos ele.
            String response = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("to", pushToken, "title", title, "body", body))
                    .retrieve()
                    .body(String.class);
            logger.info("Push notification response for {}: {}", pushToken, response);
        } catch (Exception e) {
            logger.warn("Failed to send push notification to {}: {}", pushToken, e.getMessage());
        }
    }

}
