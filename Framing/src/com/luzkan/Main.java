package com.luzkan;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.CRC32;

public class Main {

    public static void main(String[] args) {

        System.out.println("- Encoding -");

        System.out.print("To Encode MSG: \t");
        String msg = Read("input.txt");
        String msgCRC = ToEncryptWithCRC(msg);
        System.out.println("Msg w/ CRC: \t" + (msg+msgCRC));
        String msgFormatted = Distend((msg+msgCRC));
        System.out.println("Distended: \t\t" + msgFormatted);
        Write(msgFormatted);

        System.out.println("- Decoding -");

        System.out.print("To Decode MSG: \t");
        String toDecrypt = Read("output.txt");
        Print(toDecrypt);

    }

    private static String Distend(String toDistend) {

        // Regex to create int array
        String[] arr = toDistend.split("(?!^)");
        int [] arrInt = new int [arr.length];
        for(int i=0; i<arr.length; i++) {
            arrInt[i] = Integer.parseInt(arr[i]);
        }

        // Detects "11111" sequence
        int rememberThat = 0;
        ArrayList<Integer> arrList = new ArrayList<>();
        for (int value : arrInt) {

            if (value == 1) {
                rememberThat++;
                if (rememberThat == 6) {
                    arrList.add(0);
                    rememberThat = 0;
                }
            }else{
                rememberThat = 0;
            }

            arrList.add(value);
        }

        // We want arraylist as a string list
        ArrayList<String> arrListString = new ArrayList<>();
        for (int value : arrList) {
            arrListString.add(String.valueOf(value));
        }

        // And convert it to simple string
        String res = String.join("", arrListString);

        return "01111110" + res + "01111110";
    }

    private static void Print(String toDecrypt) {

        String msgNoForm = toDecrypt.substring(8, (toDecrypt.length()-8));
        System.out.println("Msg w/o form: \t" + msgNoForm);


        // Regex to create int array
        String[] arr = msgNoForm.split("(?!^)");
        int [] arrInt = new int [arr.length];
        for(int i=0; i<arr.length; i++) {
            arrInt[i] = Integer.parseInt(arr[i]);
        }

        // Detects "11111" sequence
        int rememberThat = 0;
        boolean skipNext = false;
        ArrayList<Integer> arrList = new ArrayList<>();
        for (int value : arrInt) {

            if(skipNext) {
                skipNext = false;
                continue;
            }

            if (value == 1) {
                rememberThat++;
                if (rememberThat == 5) {
                    rememberThat = 0;
                    skipNext = true;
                }
            }else{
                rememberThat = 0;
            }

            arrList.add(value);
        }

        // We want arraylist as a string list
        ArrayList<String> arrListString = new ArrayList<>();
        for (int value : arrList) {
            arrListString.add(String.valueOf(value));
        }

        // And convert it to simple string
        String msgNoDist = String.join("", arrListString);

        System.out.println("Msg w/o dist: \t" + msgNoDist);

        System.out.println("Msg decoded: \t" + msgNoDist.substring(0, msgNoDist.length()-32));

    }

    private static String ToEncryptWithCRC(String toFormat) {

        // Java lets us use CRC32 which is superior to regular CRC
        // It takes longer to compute but unlike regular CRC it is
        // cryptographically secure and it's already implemented in Java.

        CRC32 crc = new CRC32();
        crc.update(toFormat.getBytes());
        //System.out.println("CRC32: " + crc.getValue());
        long toWriteLong = crc.getValue();
        return String.format("%32s", Long.toBinaryString(toWriteLong)).replace(' ', '0');
    }

    private static String Read(String fileName){

        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            if((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }

            // Always close files.
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        return line;
    }

    private static void Write(String toWrite){
        // The name of the file to open.
        String fileName = "output.txt";

        try {
            // Assume default encoding.
            FileWriter fileWriter =
                    new FileWriter(fileName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            // Note that write() does not automatically
            // append a newline character.
            bufferedWriter.write(toWrite);
            // bufferedWriter.newLine();

            // Always close files.
            bufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println("Error writing to file '" + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
    }
}

