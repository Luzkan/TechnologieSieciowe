package com.luzkan;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Main {

    private static int tickTime = 10;
    private static int cableLength = 20;

    private static ArrayList<Integer> ethernetCable = new ArrayList<>();

    private static ArrayList<Integer> serv1EthSpots = new ArrayList<>();
    private static Server server1 = new Server();
    private static int serv1Push = 0;
    private static boolean server1clearing = false;
    private static int serv1ClearSpot = 0;
    private static int serv1clearAmount = 0;

    private static ArrayList<Integer> serv2EthSpots = new ArrayList<>();
    private static Server server2 = new Server();
    private static int serv2Push = 0;
    private static boolean server2clearing = false;
    private static int serv2ClearSpot = 0;
    private static int serv2clearAmount = 0;


    private static int statsServ1DataGet = 0;
    private static int statsServ1Received = 0;
    private static int statsServ2DataGet = 0;
    private static int statsServ2Received = 0;
    private static int statsServ1NotReceived = 0;
    private static int statsServ2NotReceived = 0;


    public static void main(String[] args) throws Exception {

        server1.setSignal(1);
        server2.setSignal(2);

        for(int n = 0; n < cableLength; n++){
            ethernetCable.add(0);
        }

        while(true){

            // Refresh
            if(!server1.isReceiving() && ethernetCable.get(1) == 0) server1.serverSimulation(1);
            if(!server2.isReceiving() && ethernetCable.get(cableLength - 2) == 0) server2.serverSimulation(2);
            ethernetCableUpdate();
            status();
            summary();

            System.out.println("\t\t\t-- -- -- -- --\n");

            // Sleep
            try {
                TimeUnit.MILLISECONDS.sleep(tickTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void ethernetCableUpdate() {

        // Check is server receives information
        if(ethernetCable.get(0) == 2) server1.setReceiving(true); else server1.setReceiving(false);
        if(ethernetCable.get(cableLength-1) == 1) server2.setReceiving(true); else server2.setReceiving(false);

        // Check if server receives error
        if(ethernetCable.get(0) == 9) server1.setErrDetected(true); else server1.setErrDetected(false);
        if(ethernetCable.get(cableLength-1) == 9) server2.setErrDetected(true); else server2.setErrDetected(false);

        // Reset whole cable to 0's
        for(int n = 0; n < cableLength; n++){
            ethernetCable.set(n, 0);
        }


        // If Error detected
        if(server1.errDetected){
            server1.setErrDetected(true);
            //if(serv1Push <= cableLength) serv1clearAmount = serv1Push; else serv1Push = cableLength;
            serv1clearAmount = cableLength;
            server1clearing = true;
        }

        if(server2.errDetected){
            server2.setErrDetected(true);
            //if(serv2Push <= cableLength) serv2clearAmount = serv2Push; else serv2Push = cableLength;
            serv2clearAmount = cableLength;
            server2clearing = true;
        }


        // [S1] Push data further
        if(server1.isWorking()){
            if(serv1Push < cableLength) serv1EthSpots.add(serv1Push);
            serv1Push++;
            if(!(server1.errDetected)) {
                if (serv1Push == 2 * cableLength) {
                    server1clearing = true;
                    serv1clearAmount = cableLength;
                    serv1Push = 0;
                    server1.setWorking(false);
                }
            }else{
                if(serv1Push >= cableLength){
                    serv1clearAmount = cableLength;
                    serv1Push = 0;
                    server1.setWorking(false);
                }
            }
        }
        // Clear data after send
        if(server1clearing){
            System.out.println("Cleared Index: " + serv1ClearSpot + " \tServer [1] Spots: " + serv1EthSpots);
            serv1EthSpots.remove(0);
            serv1ClearSpot++;
            if(serv1ClearSpot == (serv1clearAmount)){
                serv1ClearSpot = 0;
                server1clearing = false;
                server1.setErrDetected(false);
                server1.setCollDetected(false);
            }
        }


        // [S2] Push data further
        if(server2.isWorking()){
            if(serv2Push < cableLength) serv2EthSpots.add(serv2Push);
            serv2Push++;
            if(!(server2.errDetected)) {
                if (serv2Push == 2 * cableLength) {
                    server2clearing = true;
                    serv2clearAmount = cableLength;
                    serv2Push = 0;
                    server2.setWorking(false);
                }
            }else{
                if(serv2Push >= cableLength){
                    serv2clearAmount = cableLength;
                    serv2Push = 0;
                    server2.setWorking(false);
                }
            }
        }
        // Clear data after send
        if(server2clearing){
            System.out.println("Cleared Index: " + serv2ClearSpot + " \tServer [2] Spots: " + serv2EthSpots);
            serv2EthSpots.remove(0);
            serv2ClearSpot++;
            if(serv2ClearSpot == (serv2clearAmount)){
                serv2ClearSpot = 0;
                server2clearing = false;
                server2.setErrDetected(false);
                server2.setCollDetected(false);
            }
        }



        // Populate cable
        for(int k = 0; k < cableLength; k++){
            if(serv1EthSpots.contains(k) && serv2EthSpots.contains(cableLength-1-k)){
                ethernetCable.set(k, 9);
                server1.setCollDetected(true);
                server2.setCollDetected(true);
            }else if(serv1EthSpots.contains(k)) {
                ethernetCable.set(k, server1.signal);
            }else if(serv2EthSpots.contains(cableLength-1-k)){
                ethernetCable.set(k, server2.signal);
            }
        }

        for(int k = 0; k < cableLength; k++){
            for(int n = 0; n < cableLength; n++){
                if(ethernetCable.get(k) == 1){
                    if((n < k) && ((ethernetCable.get(n) == 2) || ethernetCable.get(n) == 9)) ethernetCable.set(k, 9);
                }else if(ethernetCable.get(k) == 2){
                    if((n > k) && ((ethernetCable.get(n) == 1) || ethernetCable.get(n) == 9)) ethernetCable.set(k, 9);
                }
            }
        }

        // Check is server receives information
        if(ethernetCable.get(0) == 2) server1.setReceiving(true); else server1.setReceiving(false);
        if(ethernetCable.get(cableLength-1) == 1) server2.setReceiving(true); else server2.setReceiving(false);

        // Check if server receives error
        if(ethernetCable.get(0) == 9) server1.setErrDetected(true); else server1.setErrDetected(false);
        if(ethernetCable.get(cableLength-1) == 9) server2.setErrDetected(true); else server2.setErrDetected(false);

    }



    private static void status(){
        System.out.println("Eth Cable: \t" + ethernetCable.toString());
        System.out.println("Server #1: \tSend: " + server1.isWorking() + "\t\tCollision: " + server1.isCollDetected() + "\t\tConfirmed: " + server1.isErrDetected() + "\t\tReceiving: " + server1.isReceiving() + "\t\tClearing: " + server1clearing);
        System.out.println("Server #2: \tSend: " + server2.isWorking() + "\t\tCollision: " + server2.isCollDetected() + "\t\tConfirmed: " + server2.isErrDetected() + "\t\tReceiving: " + server2.isReceiving() + "\t\tClearing: " + server2clearing);
    }



    private static void summary(){
        if(server1.receiving) {
            statsServ1DataGet++;
            if(statsServ1DataGet == cableLength-1) {
                statsServ1Received++;
                statsServ1DataGet = 0;
            }
        }else{
            if(statsServ1DataGet != 0) statsServ1NotReceived++;
            statsServ1DataGet = 0;
        }

        if(server2.receiving) {
            statsServ2DataGet++;
            if(statsServ2DataGet == cableLength-1) {
                statsServ2Received++;
                statsServ2DataGet = 0;
            }
        }else{
            if(statsServ2DataGet != 0) statsServ2NotReceived++;
            statsServ2DataGet = 0;
        }
        System.out.println("\n[S1 Received:] " + statsServ1Received + "\t[S1 Broken] " + statsServ1NotReceived);
        System.out.println("[S2 Received:] " + statsServ2Received + "\t[S2 Broken] " + statsServ2NotReceived);
    }
}