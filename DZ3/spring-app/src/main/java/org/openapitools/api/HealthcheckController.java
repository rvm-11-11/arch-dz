package org.openapitools.api;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
//@Timed
public class HealthcheckController {

    public HealthcheckController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

    }

    private MeterRegistry meterRegistry;

    @Value("${my.version}")
    private String version;

    @Timed(percentiles = {0.5, 0.95, 0.99}, histogram = true)
    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        Map<String, String> response = new HashMap();
        response.put("status", "OK");
        meterRegistry.counter("rvm.counter.health").increment();
        return response;
    }

    @Timed(percentiles = {0.5, 0.95, 0.99}, histogram = true)
    @RequestMapping("/version")
    public Map<String, String> version() {
        log.info("Calling version()");
        Map<String, String> response = new HashMap();
        response.put("version", version);
        return  response;
    }

    @RequestMapping("/exception")
    public Map<String, String> exception() {
        log.info("Calling exception()");
        if (1 == 1)
            throw new RuntimeException();
        return  null;
    }
}
