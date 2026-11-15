package com.campuscare.util;

import com.campuscare.model.Complaint;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Advanced text similarity and natural language heuristic utility for Duplicate Complaint Detection.
 * Utilizes Jaccard token set similarity, Levenshtein edit distance, and location/category weighting.
 */
public final class TextSimilarityUtil {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but",
            "by", "could", "did", "do", "does", "doing", "down", "during", "each", "few", "for", "from",
            "further", "had", "has", "have", "having", "he", "her", "here", "hers", "herself", "him",
            "himself", "his", "how", "i", "if", "in", "into", "is", "it", "its", "itself", "just", "me",
            "more", "most", "my", "myself", "no", "nor", "not", "now", "of", "off", "on", "once", "only",
            "or", "other", "our", "ours", "ourselves", "out", "over", "own", "same", "she", "should", "so",
            "some", "such", "than", "that", "the", "their", "theirs", "them", "themselves", "then", "there",
            "these", "they", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was",
            "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why", "with", "would", "you"
    ));

    private TextSimilarityUtil() {}

    /**
     * Extracts meaningful keyword tokens from text by lowercasing, stripping punctuation,
     * and filtering common stop words.
     */
    public static Set<String> extractKeywords(String text) {
        Set<String> keywords = new HashSet<>();
        if (text == null || text.trim().isEmpty()) {
            return keywords;
        }
        String clean = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        String[] tokens = clean.split("\\s+");
        for (String token : tokens) {
            String trimmed = token.trim();
            if (trimmed.length() > 2 && !STOP_WORDS.contains(trimmed)) {
                keywords.add(trimmed);
            }
        }
        return keywords;
    }

    /**
     * Computes Jaccard Similarity between two text passages based on keyword tokens.
     * J(A, B) = |A ∩ B| / |A ∪ B|
     */
    public static double computeJaccardSimilarity(String text1, String text2) {
        Set<String> set1 = extractKeywords(text1);
        Set<String> set2 = extractKeywords(text2);

        if (set1.isEmpty() && set2.isEmpty()) return 1.0;
        if (set1.isEmpty() || set2.isEmpty()) return 0.0;

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

    /**
     * Computes Levenshtein Distance between two strings.
     */
    public static int computeLevenshteinDistance(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";
        s1 = s1.trim().toLowerCase();
        s2 = s2.trim().toLowerCase();

        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + cost
                    );
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }

    /**
     * Returns a normalized Levenshtein similarity ratio between 0.0 and 1.0.
     */
    public static double computeLevenshteinRatio(String s1, String s2) {
        int maxLen = Math.max(s1 != null ? s1.length() : 0, s2 != null ? s2.length() : 0);
        if (maxLen == 0) return 1.0;
        int distance = computeLevenshteinDistance(s1, s2);
        return 1.0 - ((double) distance / maxLen);
    }

    /**
     * Calculates composite duplicate score between a new complaint and an existing complaint.
     * Weights:
     * - Category Match: 25%
     * - Location Proximity / Match: 25%
     * - Title & Description Content Similarity: 50%
     */
    public static double calculateDuplicateScore(Complaint newComplaint, Complaint existing) {
        if (newComplaint == null || existing == null) return 0.0;

        // 1. Category comparison (25%)
        double categoryScore = (newComplaint.getCategory() == existing.getCategory()) ? 1.0 : 0.0;

        // 2. Location comparison (25%)
        double locationScore = computeLevenshteinRatio(newComplaint.getLocation(), existing.getLocation());

        // 3. Text description & title similarity (50%)
        String text1 = (newComplaint.getTitle() + " " + newComplaint.getDescription()).trim();
        String text2 = (existing.getTitle() + " " + existing.getDescription()).trim();
        double contentScore = computeJaccardSimilarity(text1, text2);

        // Weighted sum
        return (0.25 * categoryScore) + (0.25 * locationScore) + (0.50 * contentScore);
    }
}
