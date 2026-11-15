package com.campuscare.service;

import com.campuscare.dao.ComplaintDao;
import com.campuscare.dao.DaoFactory;
import com.campuscare.model.Complaint;
import com.campuscare.util.DateTimeUtil;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service managing File Handling, CSV export, and Java Object Serialization for audit backups.
 * Demonstrates Core Java File I/O (BufferedReader, BufferedWriter) and Serialization.
 */
public class ReportExportService {

    private final ComplaintDao complaintDao;
    private final AnalyticsService analyticsService;

    public ReportExportService() {
        this.complaintDao = DaoFactory.getInstance().getComplaintDao();
        this.analyticsService = new AnalyticsService();
    }

    /**
     * Exports all complaints to a standard CSV file.
     */
    public int exportComplaintsToCsv(File targetFile) throws IOException {
        List<Complaint> complaints = complaintDao.findAll();
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(targetFile)))) {
            // Write CSV Header
            writer.println("TicketNumber,Category,Title,Location,Priority,Status,IsAnonymous,Department,Staff,CreatedAt,DeadlineAt,ResolvedAt");

            for (Complaint c : complaints) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        escapeCsv(c.getTicketNumber()),
                        escapeCsv(c.getCategory().getDisplayName()),
                        escapeCsv(c.getTitle()),
                        escapeCsv(c.getLocation()),
                        c.getPriority().name(),
                        c.getStatus().name(),
                        c.isAnonymous() ? "YES" : "NO",
                        escapeCsv(c.getAssignedDepartmentName() != null ? c.getAssignedDepartmentName() : "Unassigned"),
                        escapeCsv(c.getAssignedStaffName() != null ? c.getAssignedStaffName() : "None"),
                        DateTimeUtil.formatDisplay(c.getCreatedAt()),
                        DateTimeUtil.formatDisplay(c.getDeadlineAt()),
                        DateTimeUtil.formatDisplay(c.getResolvedAt())
                );
            }
        }
        return complaints.size();
    }

    /**
     * Generates a formal printable text audit report.
     */
    public void exportDepartmentAuditReport(File targetFile) throws IOException {
        AnalyticsService.OverallStats stats = analyticsService.getOverallStats();
        List<AnalyticsService.DepartmentMetric> metrics = analyticsService.getDepartmentMetrics();
        List<AnalyticsService.LocationDensity> heatmap = analyticsService.getCampusHeatmap();

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(targetFile)))) {
            writer.println("================================================================================");
            writer.println("            CAMPUSCARE+ GRIEVANCE & RESOLUTION AUDIT REPORT                     ");
            writer.println("            Generated On: " + DateTimeUtil.formatDisplay(LocalDateTime.now()));
            writer.println("================================================================================");
            writer.println();
            writer.println("--- 1. OVERALL SYSTEM METRICS ---");
            writer.printf("Total Grievances Registered : %d%n", stats.getTotalComplaints());
            writer.printf("Currently Pending / Active  : %d%n", stats.getPendingComplaints());
            writer.printf("Successfully Resolved       : %d%n", stats.getResolvedComplaints());
            writer.printf("Declined / Rejected         : %d%n", stats.getRejectedComplaints());
            writer.printf("SLA Escalated Grievances    : %d%n", stats.getEscalatedComplaints());
            writer.printf("Average Resolution Time     : %.1f hours%n", stats.getAvgResolutionHours());
            writer.printf("Student Satisfaction Index  : %.1f%% (Based on %d ratings)%n",
                    stats.getSatisfactionPercentage(), stats.getFeedbackCount());
            writer.println();

            writer.println("--- 2. DEPARTMENT PERFORMANCE ROSTER ---");
            writer.printf("%-30s | %-8s | %-8s | %-12s | %-10s%n", "Department Name", "Total", "Resolved", "Res. Rate %", "Avg. Hours");
            writer.println("--------------------------------------------------------------------------------");
            for (AnalyticsService.DepartmentMetric m : metrics) {
                writer.printf("%-30s | %-8d | %-8d | %-12.1f | %-10.1f%n",
                        m.getDepartmentName(), m.getTotalAssigned(), m.getResolvedCount(), m.getResolutionRate(), m.getAvgResolutionHours());
            }
            writer.println();

            writer.println("--- 3. CAMPUS PROBLEM HOTSPOT HEATMAP ---");
            for (AnalyticsService.LocationDensity h : heatmap) {
                writer.printf("%-35s %s %3d complaints (%4.1f%%)%n",
                        h.getLocation(), h.getTextBar(), h.getCount(), h.getPercentage());
            }
            writer.println();
            writer.println("================================================================================");
            writer.println("                    END OF AUDIT REPORT                                         ");
            writer.println("================================================================================");
        }
    }

    /**
     * Demonstrates Java Object Serialization by backing up complaints to a binary file.
     */
    public void serializeBackup(File targetFile) throws IOException {
        List<Complaint> complaints = new ArrayList<>(complaintDao.findAll());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(targetFile))) {
            oos.writeObject(complaints);
        }
    }

    /**
     * Demonstrates Java Object Deserialization by reading complaints from a binary file.
     */
    @SuppressWarnings("unchecked")
    public List<Complaint> deserializeBackup(File sourceFile) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(sourceFile))) {
            return (List<Complaint>) ois.readObject();
        }
    }

    private String escapeCsv(String str) {
        if (str == null) return "";
        return str.replace("\"", "\"\"");
    }
}
