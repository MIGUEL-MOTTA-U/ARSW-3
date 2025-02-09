package edu.eci.arso.blacklist;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConcurrentIPGenerator {
    private final int totalIPs;
    private final int threadCount;
    private final List<String> ipList;

    public ConcurrentIPGenerator(int totalIPs, int threadCount) {
        this.totalIPs = totalIPs;
        this.threadCount = threadCount;
        this.ipList = new CopyOnWriteArrayList<>();
    }

    public List<String> generateIPs() {
        CountDownLatch latch = new CountDownLatch(threadCount);
        int ipsPerThread = totalIPs / threadCount;
        int remainingIPs = totalIPs % threadCount;

        for (int i = 0; i < threadCount; i++) {
            int ipsToGenerate = ipsPerThread + (i == 0 ? remainingIPs : 0);
            Thread thread = new Thread(new IPGeneratorWorker(ipsToGenerate, latch));
            thread.start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        return new ArrayList<>(ipList);
    }

    private class IPGeneratorWorker implements Runnable {
        private final int count;
        private final CountDownLatch latch;
        private final Random rand;

        public IPGeneratorWorker(int count, CountDownLatch latch) {
            this.count = count;
            this.latch = latch;
            this.rand = new Random();
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < count; i++) {
                    String ip = rand.nextInt(256) + "." +
                            rand.nextInt(256) + "." +
                            rand.nextInt(256) + "." +
                            rand.nextInt(256);
                    ipList.add(ip);
                }
            } finally {
                latch.countDown();
            }
        }
    }
}
