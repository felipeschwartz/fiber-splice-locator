package github.felipeschwartz.fiber_splice_locator.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("FIBER SPLICE LOCATOR")
                        .version("V1")
                        .description("APPLICATION FOR LOCATE FIBER SPLICE LOCATOR, REST API'S RESTFUL, WITH JAVA, SPRING BOOT AND DOCKER")
                        .termsOfService("https://github.com/felipeschwartz/fiber-splice-locator")
                        .license(new License()
                            .name("Apache 2.0")
                            .url("https://github.com/felipeschwartz/fiber-splice-locator"))
                )
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT returned by POST /api/auth/v1/login")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }
}
