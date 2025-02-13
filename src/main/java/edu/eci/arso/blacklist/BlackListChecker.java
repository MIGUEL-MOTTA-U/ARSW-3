package edu.eci.arso.blacklist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@code BlackListChecker} class is responsible for checking a list of IP addresses
 * against a blacklist using multiple threads. If the number of insecure addresses found
 * exceeds a predefined threshold, the system identifies the situation as insecure.
 *
 * <p>The process is distributed across multiple threads to improve efficiency when checking
 * large lists of addresses.</p>
 */
public class BlackListChecker extends Thread {
    /** Counter for the number of insecure addresses found. */
    private AtomicInteger unsecureDirections = new AtomicInteger(0);
    private final CountDownLatch latch;

    /** Threshold for triggering an alarm when the number of blacklisted addresses is exceeded. */
    public static int BLACK_LIST_ALARM_COUNT;

    /** List of blacklisted IP addresses. */
    private List<String> blackList;

    /** List of IP addresses to check against the blacklist. */
    private List<String> directionsToCheck;

    /** Number of threads to be used for the verification process. */
    private final int NUMBER_THREADS;

    /** List of worker threads (Checker instances) performing the validation. */
    private List<Checker> checkers;

    /** Flag indicating whether the system has detected an insecure situation. */
    private AtomicBoolean isInsecure = new AtomicBoolean(false);

    /**
     * Constructs a new {@code BlackListChecker} instance.
     *
     * @param blackList         The list of blacklisted IP addresses.
     * @param directionsToCheck The list of IP addresses to verify.
     * @param NUMBER_THREADS    The number of threads to be used for checking.
     * @param BLACK_LIST_ALARM_COUNT The threshold for flagging an insecure situation.
     */
    public BlackListChecker(List<String> blackList, List<String> directionsToCheck, int NUMBER_THREADS, int BLACK_LIST_ALARM_COUNT, CountDownLatch latch) {
        this.blackList = blackList;
        this.directionsToCheck = directionsToCheck;
        this.checkers = new ArrayList<>();
        this.BLACK_LIST_ALARM_COUNT = BLACK_LIST_ALARM_COUNT;
        this.NUMBER_THREADS = NUMBER_THREADS;
        this.latch = latch;
    }

    /**
     * Executes the blacklist verification process in multiple threads.
     * Each thread is responsible for checking a subset of the IP addresses.
     */
    @Override
    public void run() {
        int chunkSize = directionsToCheck.size() / NUMBER_THREADS;

        for (int i = 0; i < NUMBER_THREADS; i++) {
            int start = i * chunkSize;
            int end = (i + 1) * chunkSize;

            // Ensure the last thread processes any remaining elements
            if (i == NUMBER_THREADS - 1) {
                end = directionsToCheck.size();
            }

            // Create and start a new checker thread
            Checker checker = new Checker(blackList, directionsToCheck.subList(start, end), unsecureDirections, isInsecure, latch);
            checkers.add(checker);
            checker.start();


        }

        // Wait for the threads to finish before proceeding with sequential execution of the thread
        try{
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        // Output the results of the verification process
        System.out.println("Finished...");
        System.out.println("Found " + unsecureDirections.get() + " unsecure directions.");
    }
}
