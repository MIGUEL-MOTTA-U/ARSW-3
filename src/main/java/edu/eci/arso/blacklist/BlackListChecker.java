package edu.eci.arso.blacklist;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BlackListChecker extends Thread {
    private static AtomicInteger unsecureDirections = new AtomicInteger(0);
    private static final int BLACK_LIST_ALARM_COUNT = 5;
    private List<String> blackList;
    private List<String> directionsToCheck;
    private final int NUMBER_THREADS = 5;
    private List<Checker> checkers;
    private final Object monitor = new Object();
    private AtomicBoolean isInsecure = new AtomicBoolean(false);

    public BlackListChecker(List<String> blackList, List<String> directionsToCheck, int NUMBER_THREADS, int BLACK_LIST_ALARM_COUNT) {
        this.blackList = blackList;
    }

    @Override
    public void run() {
        int chunkSize = directionsToCheck.size() / NUMBER_THREADS;
        for (int i = 0; i < NUMBER_THREADS; i++) {
            int start = i * chunkSize;
            int end = (i + 1) * chunkSize;
            if (i == NUMBER_THREADS - 1) {
                end = directionsToCheck.size();
            }
            Checker checker = new Checker(blackList, directionsToCheck.subList(start, end), unsecureDirections, monitor);
            checkers.add(checker);
            checker.start();
        }
        while (isInsecure.get()) {
            System.out.println("Checking...");
            System.out.println("Found " + unsecureDirections.get() + " unsecure directions.");
            isInsecure.set(unsecureDirections.get() < BLACK_LIST_ALARM_COUNT);
        }
    }
}
