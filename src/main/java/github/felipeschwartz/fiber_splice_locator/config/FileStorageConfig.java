package github.felipeschwartz.fiber_splice_locator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.storage")
public class FileStorageConfig {
    private String service_order_photos;

    public FileStorageConfig() {
    }

    public String getService_order_photos() {
        return service_order_photos;
    }

    public void setService_order_photos(String service_order_photos) {
        this.service_order_photos = service_order_photos;
    }
}
