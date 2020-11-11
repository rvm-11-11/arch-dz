package rvm.arch.dz.dz1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Dz1Application {

	@RequestMapping("/health")
	public String health() {
		return " {\"status\": \"OK\"}";
	}

	public static void main(String[] args) {
		SpringApplication.run(Dz1Application.class, args);
	}

}
