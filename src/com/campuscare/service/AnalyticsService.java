package com.campuscare.service;

import com.campuscare.dao.ComplaintDao;
import com.campuscare.dao.DaoFactory;
import com.campuscare.dao.DepartmentDao;
import com.campuscare.dao.FeedbackDao;
import com.campuscare.model.Complaint;
import com.campuscare.model.Department;
import com.campuscare.model.Feedback;
import com.campuscare.model.enums.ComplaintStatus;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service calculating campus complaint analytics, department resolution performance,
 * student satisfaction indexes, and text-based campus heatmaps.
 * Demonstrates extensive usage of Java 8+ Streams, Lambda expressions, and Date/Time Duration.
 */
public class AnalyticsService {

    private final ComplaintDao complaintDao;
    private final DepartmentDao departmentDao;
    private final FeedbackDao feedbackDao;

    public AnalyticsService() {
        DaoFactory factory = DaoFactory.getInstance();
        this.complaintDao = factory.getComplaintDao();
        this.departmentDao = factory.getDepartmentDao();
        this.feedbackDao = factory.getFeedbackDao();
    }

    public AnalyticsService(ComplaintDao complaintDao, DepartmentDao departmentDao, FeedbackDao feedbackDao) {
        this.complaintDao = complaintDao;
        this.departmentDao = departmentDao;
        this.feedbackDao = feedbackDao;
    }

    /**
     * Calculates overall system KPI counts.
     */
    public OverallStats getOverallStats() {
        List<Complaint> all = complaintDao.findAll();
        int total = all.size();
        int pending = (int) all.stream().filter(c -> !c.getStatus().isTerminal()).count();
        int resolved = (int) all.stream().filter(c -> c.getStatus() == ComplaintStatus.RESOLVED || c.getStatus() == ComplaintStatus.CLOSED).count();
        int rejected = (int) all.stream().filter(c -> c.getStatus() == ComplaintStatus.REJECTED).count();
        int escalated = (int) all.stream().filter(c -> c.getStatus() == ComplaintStatus.ESCALATED).count();

        // Calculate average resolution time across resolved tickets
        double avgHours = all.stream()
                .filter(c -> c.getResolvedAt() != null && c.getCreatedAt() != null)
                .mapToLong(c -> Duration.between(c.getCreatedAt(), c.getResolvedAt()).toHours())
                .average()
                .orElse(0.0);

        // Student satisfaction rating
        List<Feedback> feedbacks = feedbackDao.findAll();
        double avgRating = feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(5.0);
        double satisfactionPercent = (avgRating / 5.0) * 100.0;

        return new OverallStats(total, pending, resolved, rejected, escalated, avgHours, satisfactionPercent, feedbacks.size());
    }

    /**
     * Calculates resolution performance metrics for every campus department.
     */
    public List<DepartmentMetric> getDepartmentMetrics() {
        List<Department> departments = departmentDao.findAll();
        List<Complaint> allComplaints = complaintDao.findAll();
        List<DepartmentMetric> metrics = new ArrayList<>();

        for (Department dept : departments) {
            List<Complaint> deptComplaints = allComplaints.stream()
                    .filter(c -> c.getAssignedDepartmentId() != null && c.getAssignedDepartmentId() == dept.getDepartmentId())
                    .collect(Collectors.toList());

            int total = deptComplaints.size();
            int resolved = (int) deptComplaints.stream()
                    .filter(c -> c.getStatus() == ComplaintStatus.RESOLVED || c.getStatus() == ComplaintStatus.CLOSED)
                    .count();
            int pending = (int) deptComplaints.stream()
                    .filter(c -> !c.getStatus().isTerminal())
                    .count();

            double resolutionRate = total > 0 ? ((double) resolved / total) * 100.0 : 100.0;

            double avgHours = deptComplaints.stream()
                    .filter(c -> c.getResolvedAt() != null)
                    .mapToLong(c -> Duration.between(c.getCreatedAt(), c.getResolvedAt()).toHours())
                    .average()
                    .orElse(0.0);

            metrics.add(new DepartmentMetric(
                    dept.getDepartmentId(),
                    dept.getName(),
                    dept.getCode(),
                    total,
                    resolved,
                    pending,
                    resolutionRate,
                    avgHours
            ));
        }

        // Sort descending by total complaints
        metrics.sort((a, b) -> Integer.compare(b.getTotalAssigned(), a.getTotalAssigned()));
        return metrics;
    }

    /**
     * Generates Campus Heatmap location density rankings.
     */
    public List<LocationDensity> getCampusHeatmap() {
        List<Complaint> all = complaintDao.findAll();
        if (all.isEmpty()) return Collections.emptyList();

        int totalCount = all.size();

        // Group by location and count
        Map<String, Long> countByLocation = all.stream()
                .collect(Collectors.groupingBy(Complaint::getLocation, Collectors.counting()));

        long maxCount = countByLocation.values().stream().max(Long::compareTo).orElse(1L);

        List<LocationDensity> heatmap = new ArrayList<>();
        for (Map.Entry<String, Long> entry : countByLocation.entrySet()) {
            String location = entry.getKey();
            long count = entry.getValue();
            double percentage = ((double) count / totalCount) * 100.0;
            String textBar = generateTextBar((int) count, (int) maxCount);

            heatmap.add(new LocationDensity(location, (int) count, percentage, textBar));
        }

        heatmap.sort((a, b) -> Integer.compare(b.getCount(), a.getCount()));
        return heatmap;
    }

    private String generateTextBar(int value, int max) {
        int barLength = 12;
        int filled = max > 0 ? (int) Math.round(((double) value / max) * barLength) : 0;
        filled = Math.min(barLength, Math.max(1, filled));
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < filled; i++) sb.append("█");
        for (int i = filled; i < barLength; i++) sb.append("░");
        sb.append("]");
        return sb.toString();
    }

    // Metric DTOs
    public static class OverallStats {
        private final int totalComplaints;
        private final int pendingComplaints;
        private final int resolvedComplaints;
        private final int rejectedComplaints;
        private final int escalatedComplaints;
        private final double avgResolutionHours;
        private final double satisfactionPercentage;
        private final int feedbackCount;

        public OverallStats(int total, int pending, int resolved, int rejected, int escalated,
                            double avgResolutionHours, double satisfactionPercentage, int feedbackCount) {
            this.totalComplaints = total;
            this.pendingComplaints = pending;
            this.resolvedComplaints = resolved;
            this.rejectedComplaints = rejected;
            this.escalatedComplaints = escalated;
            this.avgResolutionHours = avgResolutionHours;
            this.satisfactionPercentage = satisfactionPercentage;
            this.feedbackCount = feedbackCount;
        }

        public int getTotalComplaints() { return totalComplaints; }
        public int getPendingComplaints() { return pendingComplaints; }
        public int getResolvedComplaints() { return resolvedComplaints; }
        public int getRejectedComplaints() { return rejectedComplaints; }
        public int getEscalatedComplaints() { return escalatedComplaints; }
        public double getAvgResolutionHours() { return avgResolutionHours; }
        public double getSatisfactionPercentage() { return satisfactionPercentage; }
        public int getFeedbackCount() { return feedbackCount; }
    }

    public static class DepartmentMetric {
        private final int departmentId;
        private final String departmentName;
        private final String departmentCode;
        private final int totalAssigned;
        private final int resolvedCount;
        private final int pendingCount;
        private final double resolutionRate;
        private final double avgResolutionHours;

        public DepartmentMetric(int id, String name, String code, int total, int resolved, int pending, double rate, double avgHours) {
            this.departmentId = id;
            this.departmentName = name;
            this.departmentCode = code;
            this.totalAssigned = total;
            this.resolvedCount = resolved;
            this.pendingCount = pending;
            this.resolutionRate = rate;
            this.avgResolutionHours = avgHours;
        }

        public int getDepartmentId() { return departmentId; }
        public String getDepartmentName() { return departmentName; }
        public String getDepartmentCode() { return departmentCode; }
        public int getTotalAssigned() { return totalAssigned; }
        public int getResolvedCount() { return resolvedCount; }
        public int getPendingCount() { return pendingCount; }
        public double getResolutionRate() { return resolutionRate; }
        public double getAvgResolutionHours() { return avgResolutionHours; }
    }

    public static class LocationDensity {
        private final String location;
        private final int count;
        private final double percentage;
        private final String textBar;

        public LocationDensity(String location, int count, double percentage, String textBar) {
            this.location = location;
            this.count = count;
            this.percentage = percentage;
            this.textBar = textBar;
        }

        public String getLocation() { return location; }
        public int getCount() { return count; }
        public double getPercentage() { return percentage; }
        public String getTextBar() { return textBar; }
    }
}
