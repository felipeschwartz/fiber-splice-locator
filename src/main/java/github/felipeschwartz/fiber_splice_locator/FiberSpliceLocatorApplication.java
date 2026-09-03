package github.felipeschwartz.fiber_splice_locator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FiberSpliceLocatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(FiberSpliceLocatorApplication.class, args);
	}

}
