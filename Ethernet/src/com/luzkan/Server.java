package com.luzkan;

import java.util.Random;

public class Server {

    boolean working;
    boolean errDetected;
    boolean collDetected;

    boolean confirmed;
    boolean receiving;
    int signal = 0;

    public boolean isReceiving() {
        return receiving;
    }

    public void setReceiving(boolean receiving) {
        this.receiving = receiving;
    }

    public Server(){
        working = false;
        errDetected = false;
        confirmed = false;
        signal = 0;
        receiving = false;
    }

    void serverSimulation(int signalMark){
        Random rand = new Random();
        int rand1 = rand.nextInt(100);
        //System.out.println("Rand: " + rand1);

        if(rand1 > 95){
            signal = signalMark;
            setWorking(true);
        }
    }


    public boolean isWorking() {
        return working;
    }

    public boolean isErrDetected() {
        return errDetected;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public void setErrDetected(boolean errDetected) {
        this.errDetected = errDetected;
    }

    public boolean isCollDetected() {
        return collDetected;
    }

    public void setCollDetected(boolean collDetected) {
        this.collDetected = collDetected;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

}
