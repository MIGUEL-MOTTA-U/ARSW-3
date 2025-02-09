package edu.eci.arso.blacklist;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@code Checker} class represents a thread responsible for checking a subset of IP addresses
 * against a blacklist. If the number of insecure addresses found exceeds a defined threshold,
 * all threads will stop execution.
 */
public class Checker extends Thread {

    /** Counter for the number of insecure directions found. */
    private AtomicInteger unsecureDirectionsFound;

    /** List of blacklisted IP addresses. */
    private List<String> blackList;

    /** List of IP addresses to be checked. */
    private List<String> directionsToCheck;

    /** Flag to indicate if the threshold for insecure addresses has been reached. */
    private AtomicBoolean isInsecure;

    /**
     * Constructs a new {@code Checker} instance.
     *
     * @param blackList The list of blacklisted IP addresses.
     * @param directionsToCheck The list of IP addresses to verify.
     * @param unsecureDirectionsFound The counter for found insecure addresses.
     * @param isInsecure Shared flag to indicate if the insecure threshold has been reached.
     */
    public Checker(List<String> blackList, List<String> directionsToCheck, AtomicInteger unsecureDirectionsFound, AtomicBoolean isInsecure) {
        this.blackList = blackList;
        this.directionsToCheck = directionsToCheck;
        this.unsecureDirectionsFound = unsecureDirectionsFound;
        this.isInsecure = isInsecure;
    }

    /**
     * Executes the checking process. Each thread iterates through its assigned list of IP addresses,
     * verifying if they appear in the blacklist. If the insecure threshold is reached,
     * all threads stop execution.
     */
    @Override
    public void run() {
        for (String direction : directionsToCheck) {
            if (isInsecure.get()) {
                // If another thread has already reached the threshold, stop execution
                System.out.println("Stopping thread...");
                return;
            }
            if (blackList.contains(direction)) {
                System.out.println(this.getName() + " Found insecure direction: " + direction);
                unsecureDirectionsFound.incrementAndGet();

                // If the threshold is reached, stop all threads
                if (unsecureDirectionsFound.get() >= BlackListChecker.BLACK_LIST_ALARM_COUNT) {
                    isInsecure.set(true);
                    System.out.println("Stopping thread...");
                    return;
                }
            }
        }
    }
}
