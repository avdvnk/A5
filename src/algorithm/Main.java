package algorithm;

import java.io.*;
import java.util.Random;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws IOException {
        System.out.print("Write input filename: ");
        String inputFile = new Scanner(System.in).nextLine();
        System.out.print("Write output filename: ");
        String outputFile = new Scanner(System.in).nextLine();
        //String inputFile = "tank.bmp";
        //String outputFile = "out.bmp";
        System.out.print("Encrypt(0) / decrypt(1): ");
        boolean encrypt = false;
        if (new Scanner(System.in).nextInt() == 0){
            encrypt = true;
        }
        byte[] key = keyGenerate(encrypt);
        byte [] frame = frameGenerate(encrypt);
        A5 a5 = new A5();
        a5.processData(inputFile, outputFile, key, frame);
    }
    private static byte[] keyGenerate (boolean encrypt) throws IOException {
        if (encrypt){
            byte [] tmp = ByteGenerate(8);
            FileOutputStream fos = new FileOutputStream(new File("key.txt"));
            fos.write(tmp);
            return tmp;
        }
        else{
            FileInputStream fis = new FileInputStream(new File("key.txt"));
            byte[] tmp = new byte[8];
            fis.read(tmp);
            return tmp;
        }
    }
    private static byte[] ByteGenerate (int length){
        Random rand = new Random();
        byte [] key = new byte[length];
        rand.nextBytes(key);
        return key;
    }
    public static byte[] frameGenerate (boolean encrypt) throws IOException {
        if (encrypt){
            byte [] tmp = ByteGenerate(4);
            FileOutputStream fos = new FileOutputStream(new File("frame.txt"));
            fos.write(tmp);
            return tmp;
        }
        else{
            FileInputStream fis = new FileInputStream(new File("frame.txt"));
            byte[] tmp = new byte[4];
            fis.read(tmp);
            return tmp;
        }
    }
}
