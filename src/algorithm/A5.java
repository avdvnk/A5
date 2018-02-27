package algorithm;

import tests.Tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;

class A5 {

    void processData(String fileInput, String fileOutput, byte[] inputKey, byte[] inputFrame) throws FileNotFoundException {
        File input = new File(fileInput);
        byte[] key = inputKey;
        byte[] frame = inputFrame;
        System.out.println("Key: " + Arrays.toString(key) + "\nFrame Number: " + Arrays.toString(frame) + "\n");
        KeyGenerator keyGenerator = new KeyGenerator(key, frame);
        long fileSize = input.length();
        byte part;
        final int BUFFERSIZE = 64;
        byte[] encryptedBytes = new byte[BUFFERSIZE];
        byte keyStream;
        keyGenerator.initPeriod();
        FileInputStream fis = new FileInputStream(input);
        for(long i = 0; i < fileSize; i++) {
            try {
                part = (byte)fis.read();
                keyStream = keyGenerator.getStreamKey();
                encryptedBytes[(int) (i % BUFFERSIZE)] = (byte) (keyStream ^ part);
                if((i + 1) % BUFFERSIZE == 0) {
                    FileOutputStream fos = new FileOutputStream(fileOutput, true);
                    fos.write(encryptedBytes, 0, BUFFERSIZE);
                }
                else if(i == fileSize - 1) {
                    FileOutputStream fos = new FileOutputStream(fileOutput, true);
                    fos.write(encryptedBytes, 0, (int)(i % BUFFERSIZE) + 1);
                }
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        Tests tests = new Tests(key, frame);
        tests.tests();
    }
}
