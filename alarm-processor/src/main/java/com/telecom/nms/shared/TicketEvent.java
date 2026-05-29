package com.telecom.nms.shared.event;

public class TicketEvent {
    private String ticketId;
    private String equipmentId;
    private String issueDescription;
    private String status;

    // 🚀 CRITICAL: Default constructor required by Jackson for JSON deserialization
    public TicketEvent() {}

    public TicketEvent(String ticketId, String equipmentId, String issueDescription, String status) {
        this.ticketId = ticketId;
        this.equipmentId = equipmentId;
        this.issueDescription = issueDescription;
        this.status = status;
    }

    // Getters and Setters
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getEquipmentId() { return equipmentId; }
    public void setEquipmentId(String equipmentId) { this.equipmentId = equipmentId; }

    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}