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
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;

@SpringBootApplication
@RestController
public class SimCardActivator {

    private final ActivationRecordRepository activationRecordRepository;

    @Autowired
    public SimCardActivator(ActivationRecordRepository activationRecordRepository) {
        this.activationRecordRepository = activationRecordRepository;
    }

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

        // Save activation record
        ActivationRecord record = new ActivationRecord(iccid, customerEmail, success, LocalDateTime.now());
        activationRecordRepository.save(record);

        return ResponseEntity.status(HttpStatus.OK).body(success ? "Activation successful" : "Activation failed");
    }

    @GetMapping("/activations")
    public List<ActivationRecord> getAllActivations() {
        return activationRecordRepository.findAll();
    }

    @GetMapping("/activation")
    public Map<String, Object> getActivationById(@RequestParam long simCardId) {
        ActivationRecord record = activationRecordRepository.findById(simCardId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activation record not found"));
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("iccid", record.getIccid());
        response.put("customerEmail", record.getCustomerEmail());
        response.put("active", record.isSuccess());
        return response;
    }
}
