package org.openapitools.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HealthcheckController {

    @RequestMapping("/health")
    public String health() {
        log.info("Calling health()");
        return "{\"status\": \"OK\", \"version\": \"v3\"}";
    }
}
