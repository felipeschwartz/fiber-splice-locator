package github.felipeschwartz.fiber_splice_locator;

import me.paulschwarz.springdotenv.spring.DotenvApplicationInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FiberSpliceLocatorApplication {

	public static void main(String[] args) {
		// spring-dotenv não se registra sozinho (sem spring.factories no jar) —
		// precisa desse initializer manual pra carregar o .env antes do resto.
		new SpringApplicationBuilder(FiberSpliceLocatorApplication.class)
				.initializers(new DotenvApplicationInitializer())
				.run(args);
	}

}
