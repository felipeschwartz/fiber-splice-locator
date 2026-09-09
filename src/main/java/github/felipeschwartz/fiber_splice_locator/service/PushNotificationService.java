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

    private final RestClient restClient = RestClient.create("http://exp.host/--/api/v2/push/send");

    @Async
    public void sendPush(String pushToken, String title, String body) {
        if (pushToken == null || pushToken.isBlank()) return;

        try {
            restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("to", pushToken, "title", title, "body", body))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            logger.warn("Failed to send push notification to {}: {}", pushToken, e.getMessage());
        }
    }

}
