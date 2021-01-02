package rvm.dz.dz5;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
@Slf4j
public class Dz5AuthenticationApplication {

	public static void main(String[] args) {
		SpringApplication.run(Dz5AuthenticationApplication.class, args);
	}


	@Value("${my.version: v0}")
	private String version;

	@RequestMapping("/version")
	public Map<String, String> version() {
		log.info("Calling version(), version is {}", version);
		Map<String, String> response = new HashMap();
		response.put("version", version);
		return  response;
	}

	@RequestMapping("/health")
	public Map<String, String> health() {
		log.info("Calling health()");
		Map<String, String> response = new HashMap();
		response.put("status", "OK");
		return response;
	}
}
