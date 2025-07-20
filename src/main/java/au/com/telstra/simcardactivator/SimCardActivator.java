package au.com.telstra.simcardactivator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;

@SpringBootApplication
@RestController
public class SimCardActivator {

    public static void main(String[] args) {
        SpringApplication.run(SimCardActivator.class, args);
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activateSim(@RequestBody Map<String, String> payload) {
        String iccid = payload.get("iccid");
        String customerEmail = payload.get("customerEmail");
        if (iccid == null || customerEmail == null) {
            return ResponseEntity.badRequest().body("Missing iccid or customerEmail");
        }

        // Prepare request to actuator
        RestTemplate restTemplate = new RestTemplate();
        String actuatorUrl = "http://localhost:8444/actuate";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String actuatorPayload = String.format("{\"iccid\":\"%s\"}", iccid);
        HttpEntity<String> request = new HttpEntity<>(actuatorPayload, headers);
        boolean success = false;
        try {
            Map response = restTemplate.postForObject(actuatorUrl, request, Map.class);
            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                success = true;
            }
        } catch (Exception e) {
            System.out.println("Error calling actuator: " + e.getMessage());
        }
        System.out.println("Activation for ICCID " + iccid + " was " + (success ? "successful" : "unsuccessful"));
        return ResponseEntity.status(HttpStatus.OK).body(success ? "Activation successful" : "Activation failed");
    }
}
