package com.campuscare.service;

import com.campuscare.dao.ComplaintDao;
import com.campuscare.dao.DaoFactory;
import com.campuscare.exception.DuplicateComplaintException;
import com.campuscare.model.Complaint;
import com.campuscare.util.TextSimilarityUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Service providing smart duplicate complaint detection across open tickets.
 * Prevents repetitive reporting, spamming, and duplicate credit exploitation.
 */
public class DuplicateDetectionService {

    public static final double DUPLICATE_THRESHOLD = 0.70; // 70% composite similarity threshold

    private final ComplaintDao complaintDao;

    public DuplicateDetectionService() {
        this.complaintDao = DaoFactory.getInstance().getComplaintDao();
    }

    public DuplicateDetectionService(ComplaintDao complaintDao) {
        this.complaintDao = complaintDao;
    }

    /**
     * Finds potential duplicate complaints among active, unresolved tickets.
     */
    public List<DuplicateMatch> findDuplicates(Complaint candidate) {
        List<DuplicateMatch> matches = new ArrayList<>();
        if (candidate == null) return matches;

        List<Complaint> activeTickets = complaintDao.findActiveComplaints();

        for (Complaint existing : activeTickets) {
            // Skip comparing against itself if already persisted
            if (candidate.getComplaintId() != null && candidate.getComplaintId().equals(existing.getComplaintId())) {
                continue;
            }

            double score = TextSimilarityUtil.calculateDuplicateScore(candidate, existing);
            if (score >= DUPLICATE_THRESHOLD) {
                matches.add(new DuplicateMatch(existing, score));
            }
        }

        // Sort descending by similarity score
        matches.sort((a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore()));
        return matches;
    }

    /**
     * Validates that the candidate complaint is not an existing duplicate.
     * Throws DuplicateComplaintException if high-confidence match found.
     */
    public void validateNoDuplicates(Complaint candidate) throws DuplicateComplaintException {
        List<DuplicateMatch> duplicates = findDuplicates(candidate);
        if (!duplicates.isEmpty()) {
            DuplicateMatch topMatch = duplicates.get(0);
            int percent = (int) Math.round(topMatch.getSimilarityScore() * 100);
            throw new DuplicateComplaintException(
                    String.format("Potential duplicate of existing ticket #%s (\"%s\") detected with %d%% similarity. Please track the existing ticket.",
                            topMatch.getExistingComplaint().getTicketNumber(),
                            topMatch.getExistingComplaint().getTitle(),
                            percent),
                    topMatch.getExistingComplaint().getTicketNumber(),
                    topMatch.getSimilarityScore()
            );
        }
    }

    public static class DuplicateMatch {
        private final Complaint existingComplaint;
        private final double similarityScore;

        public DuplicateMatch(Complaint existingComplaint, double similarityScore) {
            this.existingComplaint = existingComplaint;
            this.similarityScore = similarityScore;
        }

        public Complaint getExistingComplaint() {
            return existingComplaint;
        }

        public double getSimilarityScore() {
            return similarityScore;
        }
    }
}
