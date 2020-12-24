package rvm.dz.dz4;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@Slf4j
@RestController
public class Dz4Application {

	public static void main(String[] args) {
		SpringApplication.run(Dz4Application.class, args);
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
}
