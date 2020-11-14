package rvm.arch.dz.dz1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Slf4j
public class Dz1Application {

	@RequestMapping("/health")
	public String health() {
		log.info("Calling health()");
		return "{\"status\": \"OK\"}";
	}

	@RequestMapping("/")
	public String hello() {
		log.info("Calling hello()");
		return "Hello world!";
	}

	public static void main(String[] args) {
		SpringApplication.run(Dz1Application.class, args);
	}

}
