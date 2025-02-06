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
        try{
            while (true){
                int elem=queue.take();
                System.out.println("Consumer consumes " + elem);
                Thread.sleep(2000); // 2s
                //int elem=queue.take(); // Así estaba
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
}
