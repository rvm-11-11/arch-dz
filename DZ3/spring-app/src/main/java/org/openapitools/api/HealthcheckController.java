package org.openapitools.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class HealthcheckController {

    @Value("${my.version}")
    private String version;

    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        Map<String, String> response = new HashMap();
        response.put("status", "OK");
        return response;
    }

    @RequestMapping("/version")
    public Map<String, String> version() {
        log.info("Calling version()");
        Map<String, String> response = new HashMap();
        response.put("version", version);
        return  response;
    }
}
