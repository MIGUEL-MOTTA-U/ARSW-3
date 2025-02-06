package edu.eci.arso.blacklist;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Checker extends Thread{
    private AtomicInteger unsecureDirectionsFound;
    private List<String> blackList;
    private List<String> directionsToCheck;
    private final Object monitor;

    public Checker(List<String> blackList, List<String> directionsToCheck, AtomicInteger unsecureDirectionsFound, Object monitor){
        this.blackList = blackList;
        this.directionsToCheck = directionsToCheck;
        this.unsecureDirectionsFound = unsecureDirectionsFound;
        this.monitor = monitor;
    }

    @Override
    public void run(){
        for (String direction : directionsToCheck){
            if (blackList.contains(direction)){
                unsecureDirectionsFound.incrementAndGet();
            }
        }
    }

}
