package edu.eci.arso.blacklist;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The {@code StartChecker} class initializes and executes the blacklist verification process.
 * It generates random IP addresses, includes some blacklisted IPs for testing, and runs
 * the {@code BlackListChecker} using multiple threads to detect insecure addresses.
 */
public class StartChecker {

    /** Number of threads to be used for checking the blacklist. */
    private static final int NUMBER_THREADS = 3;

    /** Threshold for triggering an alarm when the number of blacklisted addresses is exceeded. */
    private static final int BLACK_LIST_ALARM_COUNT = 10;

    /** Number of IP addresses to be generated for verification. */
    private static final int NUMBER_DIRECTIONS = 1_000_000;

    /** Number of blacklisted IP addresses to be generated. */
    private static final int NUMBER_BLACKLIST = 50;

    /** Number of blacklisted addresses that will be inserted into the list to check. */
    private static final int PRESENT_BLACKLIST_NUMBERS_ON_LIST_TO_CHECK = 10;

    /**
     * The main method that initiates the blacklist verification process.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        List<String> blackList = generateRandomIPs(NUMBER_BLACKLIST);
        List<String> directionsToCheck = generateRandomIPs(NUMBER_DIRECTIONS, blackList, PRESENT_BLACKLIST_NUMBERS_ON_LIST_TO_CHECK);

        System.out.println("Starting verification with " + NUMBER_THREADS + " threads...");
        long startTime = System.currentTimeMillis();

        // Create and execute the checker
        BlackListChecker blackListChecker = new BlackListChecker(blackList, directionsToCheck, NUMBER_THREADS, BLACK_LIST_ALARM_COUNT);
        blackListChecker.start();

        // Wait for execution to complete
        try {
            blackListChecker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Verification process completed in " + (endTime - startTime) + " ms.");
    }

    /**
     * Generates a list of random IP addresses.
     *
     * @param count Number of IP addresses to generate.
     * @return A list containing randomly generated IP addresses.
     */
    private static List<String> generateRandomIPs(int count) {
        List<String> ipList = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            String ip = rand.nextInt(256) + "." + rand.nextInt(256) + "." + rand.nextInt(256) + "." + rand.nextInt(256);
            ipList.add(ip);
        }
        return ipList;
    }

    /**
     * Generates a list of random IP addresses and ensures that some blacklisted IPs are included.
     *
     * @param count Total number of IP addresses to generate.
     * @param blackList List of blacklisted IPs that should be inserted into the generated list.
     * @param presentNumbersInBlackList Number of blacklisted IPs to be included in random positions.
     * @return A list of random IP addresses, including some from the blacklist.
     */
    private static List<String> generateRandomIPs(int count, List<String> blackList, int presentNumbersInBlackList) {
        List<String> ipList = new ArrayList<>(generateRandomIPs(count));
        Random rand = new Random();

        // Insert some blacklisted addresses at random positions
        for (int i = 0; i < presentNumbersInBlackList; i++) {
            int position = rand.nextInt(blackList.size());
            int ipPosition = rand.nextInt(ipList.size());
            ipList.set(ipPosition, blackList.get(position));
        }
        return ipList;
    }
}
