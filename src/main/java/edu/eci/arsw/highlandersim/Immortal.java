package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    private ControlFrame controller;
    private final Object monitor;
    private final AtomicInteger health;
    private volatile boolean alive;
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb, ControlFrame controller, Object monitor) {
        super(name);
        this.alive = true;
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = new AtomicInteger(health);
        this.defaultDamageValue=defaultDamageValue;
        this.controller = controller;
        this.monitor = monitor;
    }

    public void stopThread(){
        this.alive = false;
    }

    @Override
    public void run() {
        // evitar esperas innecesarias
        while (health.get() > 0 && alive) {
            // pausar la ejecución
            while (controller.isPaused()) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                }
            }
            if (!alive) break;
            // Realizar ataque mientras no este pausado
            Immortal im;
            int myIndex = immortalsPopulation.indexOf(this);
            int nextFighterIndex = r.nextInt(immortalsPopulation.size());
            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }
            im = immortalsPopulation.get(nextFighterIndex);

            this.fight(im);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        immortalsPopulation.remove(this);
    }

    public void fight(Immortal i2) {
        Immortal firstInmortal = this;
        Immortal secondInmortal = i2;
        // Asegurar un orden consistente durante el bloqueo de objetos
        if (firstInmortal.getName().compareTo(secondInmortal.getName()) > 0) {
            firstInmortal = i2;
            secondInmortal = this;
        }
        synchronized(firstInmortal) {
            synchronized (secondInmortal) {
                if (i2.getHealth() > 0) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    this.changeHealth(getHealth() + defaultDamageValue);
                    updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
                } else {
                    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                }
            }
        }
    }

    public void changeHealth(int v) {
        health.set(v);
    }

    public int getHealth() {
        return health.get();
    }

    @Override
    public String toString() {
        return name + "[" + health + "]";
    }

}
