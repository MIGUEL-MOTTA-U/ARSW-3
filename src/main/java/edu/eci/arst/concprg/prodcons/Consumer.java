/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author hcadavid
 */
public class Consumer extends Thread{
    
    private final LinkedBlockingQueue<Integer> queue;
    private final Object monitor;
    public Consumer(LinkedBlockingQueue<Integer> queue, Object monitor){
        this.queue=queue;
        this.monitor = monitor;
    }
    
    @Override
    public void run() {
        while (true) {
            while (queue.isEmpty()){
                synchronized (monitor){
                    try {
                        monitor.wait();
                        System.out.println("Stopping the thread consumer...");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                }
            }


            int elem=queue.poll();
            System.out.println("Consumer consumes "+elem);
        }

    }
}
