package tests;


import algorithm.KeyGenerator;
import java.util.ArrayList;
import java.util.Random;

public class Tests {
    private ArrayList<Byte> data;
    private KeyGenerator kg;
    public Tests (byte[] key, byte[] frame){
        this.kg = new KeyGenerator(key, frame);
        data = new ArrayList<>();
        getData();
    }
    public void tests (){
        frequencyTest();
        autoCorrelationTest();
        sequenceTest();
        seriesTest();
        universalTest();
    }
    public void getData(){
        kg.initPeriod();
        for (int i = 0; i < 250000; i++) {
            //data.add(kg.getStreamKey());
            Random r = new Random();
            data.add((byte)r.nextInt());
        }
    }

    private String byteToString(byte b) {
        return Integer.toBinaryString(b & 255 | 256).substring(1);
    }

    int getCountOnes(ArrayList<Byte> data) {
        int result = 0;
        String byteString;
        for (byte b : data) {
            byteString = byteToString(b);
            for (int i = 0; i < byteString.length(); i++) {
                if (Integer.parseInt(String.valueOf(byteString.charAt(i))) == 1){
                    result++;
                }
            }
        }
        return result;
    }

    private double frequencyTest(int countOnes, int N) {
        return (Math.pow((N * 8 - countOnes) - countOnes, 2)) / (N * 8);
    }

    void frequencyTest() {
        int length = data.size();
        int countOnes = getCountOnes(data);
        double result = frequencyTest(countOnes, length);
        System.out.println("Ones: " + countOnes);
        System.out.println("Zeros: " + (length * 8 - countOnes) + "\n");
        if(result >= -2.7055 && result <= 2.7055)
            System.out.println("Frequency test passed: " + result + "\n");
        else
            System.out.println("Frequency test not passed: " + result + "\n");
    }

    void autoCorrelationTest() {
        System.out.println("Autocorrelation test:");
        for(int tau = 10; tau < 30; tau += 5) {
            int countOnes = 0;
            for(int i = 0; i < data.size() - tau; i++) {
                String tmp = byteToString(data.get(i));
                for(int k = 0; k < tmp.length(); k++) {
                    if (Integer.parseInt(String.valueOf(tmp.charAt(k))) == 0) {
                        countOnes++;
                    }
                }
            }
            double res = frequencyTest(countOnes, data.size() - tau);
            if (Math.abs(res) <= 1.2816)
                System.out.println("Tau = " + tau + " freqTest passed = " + res);
            else
                System.out.println("Tau = " + tau + " freqTest not passed = " + res);
        }
        System.out.println("\n");
    }

    void sequenceTest() {
        int[] freq = new int[256];
        int L = 8;
        long N = data.size() * 8;
        for (byte b : data) {
            freq[b & 0xFF] += 1;
        }
        double result = 0;
        double k = N / L;
        for (int i : freq) {
            result += Math.pow(i, 2);
        }
        result *= (256. / k);
        result -= k;
        if(result <= 284.3359)
            System.out.println("Sequence test passed: " + result + "\n");
        else
            System.out.println("Sequence test not passed: " + result + "\n");
    }

    void seriesTest() {
        int L = 15;
        long N = data.size() * 8;
        int[] B = new int[L + 1];
        int[] G = new int[L + 1];
        int lenOne = 0;
        int lenZero = 0;

        for (byte b : data) {
            String tmp = byteToString(b);
            for (int k = 0; k < tmp.length(); k++) {
                if (Integer.parseInt(String.valueOf(tmp.charAt(k))) == 1) {
                    lenOne++;
                    if(lenZero <= L)
                        B[lenZero]++;
                    lenZero = 0;
                } else {
                    lenZero++;
                    if(lenOne <= L)
                        G[lenOne]++;
                    lenOne = 0;
                }
            }
        }
        double ones = 0;
        double zeroes = 0;

        for (int i = 1; i < L + 1; i++) {
            double e = (N - i + 3) / (Math.pow(2, i + 2));
            ones += Math.pow(B[i] - e, 2) / e;
            zeroes += Math.pow(G[i] - e, 2) / e;
        }

        double result = ones + zeroes;
        if(result <= 40.2560)
            System.out.println("Series test passed: " + result + "\n");
        else
            System.out.println("Series test not passed: " + result + "\n");
    }

    void universalTest() {
        int L = 8;
        long N = data.size();
        double V = Math.pow(2,L);
        int Q = (int) (V * 10);
        double K;
        K = data.size() - Q;
        int[] TAB = new int[(int) V];
        ArrayList<Integer> parts = new ArrayList<>();
        int bit;
        int tmp = 0;
        int z = 0;
        for (int i = 0; i < N; i++) {
            String byteString = byteToString(data.get(i));
            for (int k = 0; k < byteString.length(); k++) {
                bit = Integer.parseInt(String.valueOf(byteString.charAt(k)));
                tmp += bit * Math.pow(2, z);
                z++;
                if(z == L) {
                    parts.add(tmp);
                    z = 0;
                    tmp = 0;
                }
            }
        }
        int i;
        for (i = 0; i < Q; i++) {
            int b = parts.get(i);
            TAB[b] = i;
        }
        double sum = 0;
        for (int k = i; k < Q + K; k++) {
            int b = parts.get(k);
            sum += (Math.log10(k - TAB[b]) / Math.log10(2));
            TAB[b] = k;
        }
        sum = sum / K;
        double e = 7.1836656;
        double d = 3.238;
        double C = 0.7 - 0.8 / L + ((4 + 32 / L) * Math.pow(K, - (double) 3 / L)) / 15;
        double result = (sum - e) / (C * Math.sqrt(d));
        if(Math.abs(result) <= 1.2816)
            System.out.println("Universal test passed: " + result);
        else
            System.out.println("Universal test not passed: " + result);
    }
}
