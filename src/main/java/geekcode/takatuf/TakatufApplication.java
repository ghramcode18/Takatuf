package geekcode.takatuf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "geekcode.takatuf")
@EnableScheduling
public class TakatufApplication {

	public static void main(String[] args) {
		SpringApplication.run(TakatufApplication.class, args);
	}

}
