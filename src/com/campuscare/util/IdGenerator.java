package com.campuscare.util;

import java.time.Year;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe ID and ticket generator for all CampusCare+ entities.
 */
public final class IdGenerator {

    private static final AtomicInteger ticketCounter = new AtomicInteger(100);
    private static final AtomicInteger studentCounter = new AtomicInteger(10);
    private static final AtomicInteger walletCounter = new AtomicInteger(10);

    private IdGenerator() {}

    /**
     * Generates a readable ticket number e.g. CMP-2026-101.
     */
    public static synchronized String nextTicketNumber() {
        int currentYear = Year.now().getValue();
        int counter = ticketCounter.incrementAndGet();
        return String.format("CMP-%d-%04d", currentYear, counter);
    }

    /**
     * Generates UUID string for primary keys.
     */
    public static String nextUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generates formatted student ID e.g. STU-011.
     */
    public static synchronized String nextStudentId() {
        return String.format("STU-%03d", studentCounter.incrementAndGet());
    }

    /**
     * Generates formatted wallet ID e.g. WLT-011.
     */
    public static synchronized String nextWalletId() {
        return String.format("WLT-%03d", walletCounter.incrementAndGet());
    }
}
