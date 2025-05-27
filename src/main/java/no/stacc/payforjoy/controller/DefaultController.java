package no.stacc.payforjoy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DefaultController {

    private final RequestMappingHandlerMapping handlerMapping;
    private final ObjectMapper objectMapper;

    @Autowired
    public DefaultController(RequestMappingHandlerMapping handlerMapping, ObjectMapper objectMapper) {
        this.handlerMapping = handlerMapping;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public ResponseEntity<String> listEndpoints() throws JsonProcessingException {
        List<Map<String, Object>> endpoints = handlerMapping.getHandlerMethods().entrySet().stream()
                .map(entry -> Map.of(
                        "controller", (Object) entry.getValue().getMethod().getDeclaringClass().getSimpleName(),
                        "method", (Object) entry.getValue().getMethod().getName(),
                        "path", (Object) extractPath(entry.getKey())
                ))
                .collect(Collectors.toList());

        // Convert the list to a pretty-printed JSON string
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(endpoints);

        // Return the response with the correct Content-Type
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(prettyJson);
    }

    private String extractPath(org.springframework.web.servlet.mvc.method.RequestMappingInfo info) {
        if (info.getPatternsCondition() != null && !info.getPatternsCondition().getPatterns().isEmpty()) {
            return info.getPatternsCondition().getPatterns().iterator().next();
        } else if (info.getPathPatternsCondition() != null && !info.getPathPatternsCondition().getPatterns().isEmpty()) {
            return info.getPathPatternsCondition().getPatterns().iterator().next().toString();
        }
        return "N/A";
    }
}