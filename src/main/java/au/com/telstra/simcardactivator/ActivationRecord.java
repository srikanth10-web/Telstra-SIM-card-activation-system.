package au.com.telstra.simcardactivator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ActivationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String iccid;
    private String customerEmail;
    private boolean success;
    private LocalDateTime timestamp;

    public ActivationRecord() {}

    public ActivationRecord(String iccid, String customerEmail, boolean success, LocalDateTime timestamp) {
        this.iccid = iccid;
        this.customerEmail = customerEmail;
        this.success = success;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getIccid() { return iccid; }
    public void setIccid(String iccid) { this.iccid = iccid; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
} 