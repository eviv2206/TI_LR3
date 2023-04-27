package com.example.lr3;

import javafx.util.Pair;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class RabinCrypto {

    private final String pathFile;

    private final BigInteger p;
    private final BigInteger q;
    private final BigInteger b;

    private final BigInteger n;

    public RabinCrypto(String pathFile, BigInteger p, BigInteger q, BigInteger b) {
        this.pathFile = pathFile;
        this.p = p;
        this.q = q;
        this.b = b;
        this.n = this.p.multiply(this.q);
    }

    public String encrypt() {
        byte[] bytes = getByteArrayFromFile();
        ArrayList<BigInteger> resultArray = new ArrayList<>();
        for (byte dataByte : bytes) {
            BigInteger data = new BigInteger(String.valueOf(dataByte));
            data = data.add(BigInteger.valueOf(128));
            data = data.multiply(data.add(this.b)).mod(this.n);
            resultArray.add(data);
        }
        try(FileWriter writer = new FileWriter(getOutEncodePath())) {
            for (int i = 0; i < resultArray.size(); i++) {
                writer.write(resultArray.get(i).toString());
                if (i != resultArray.size() - 1) {
                    writer.write(" ");
                }
            }
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
        return resultArray.toString();
    }


    public String decrypt(){
        ArrayList<BigInteger> bigIntegers = getArrayListFromFile();
        ArrayList<Byte> resultArray = new ArrayList<>();
        for (BigInteger num: bigIntegers){
            BigInteger d = (b.multiply(b)).add(BigInteger.valueOf(4).multiply(num)).mod(this.n);
            resultArray.add(findSymbol(d));
        }
        printToFile(resultArray);
        return resultArray.toString();

    }

    private byte findSymbol(BigInteger d){
        BigInteger mp = power(d, (this.p.add(BigInteger.ONE)).divide(BigInteger.valueOf(4))).mod(this.p);
        BigInteger mq = power(d, (this.q.add(BigInteger.ONE)).divide(BigInteger.valueOf(4))).mod(this.q);
        Pair<BigInteger, BigInteger> yPyQ = expandedEvklid(this.p, this.q);
        BigInteger d1 = ((yPyQ.getKey().multiply(this.p).multiply(mq)).add(yPyQ.getValue().multiply(this.q).multiply(mp))).mod(this.n);
        BigInteger d2 = this.n.subtract(d1);
        BigInteger d3 = ((yPyQ.getKey().multiply(this.p).multiply(mq)).subtract(yPyQ.getValue().multiply(this.q).multiply(mp))).mod(this.n);
        BigInteger d4 = this.n.subtract(d3);
        ArrayList<BigInteger> ds = new ArrayList<>();
        ds.add(d1);
        ds.add(d2);
        ds.add(d3);
        ds.add(d4);
        for (BigInteger di: ds){
            BigInteger value;
            if (((di.subtract(this.b)).mod(BigInteger.valueOf(2))).compareTo(BigInteger.ZERO) == 0){
                value = (di.subtract(this.b)).divide(BigInteger.valueOf(2));
            } else {
                value = ((di.add(this.n)).subtract(this.b)).divide(BigInteger.valueOf(2));
            }
            value = value.mod(this.n);
            if(value.compareTo(BigInteger.valueOf(256)) < 0){
                value = value.subtract(BigInteger.valueOf(128));
                return Byte.parseByte(value.toString());
            }
        }
        return Byte.parseByte(String.valueOf(null));
    }

    public ArrayList<BigInteger> getArrayListFromFile() {
        ArrayList<BigInteger> arrayList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(this.pathFile))) {
            String line = reader.readLine();
            String[] numbers = line.split("\\s+");
            for (String str : numbers) {
                BigInteger number = new BigInteger(str);
                arrayList.add(number);
            }
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
        return arrayList;
    }

    public byte[] getByteArrayFromFile() {
        try {
            return Files.readAllBytes(Path.of(this.pathFile));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    private BigInteger power(BigInteger a, BigInteger n) {
        if (n.equals(BigInteger.ZERO)) {
            return BigInteger.ONE;
        } else if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            BigInteger b = power(a, n.divide(BigInteger.TWO));
            return b.multiply(b);
        } else {
            BigInteger b = power(a, n.subtract(BigInteger.ONE).divide(BigInteger.TWO));
            return a.multiply(b).multiply(b);
        }
    }

    private Pair<BigInteger, BigInteger> expandedEvklid(BigInteger a, BigInteger b) {
        BigInteger x = BigInteger.ZERO, y = BigInteger.ONE, u = BigInteger.ONE, v = BigInteger.ZERO;
        while (!a.equals(BigInteger.ZERO)) {
            BigInteger q = b.divide(a);
            BigInteger r = b.mod(a);
            BigInteger m = x.subtract(u.multiply(q));
            BigInteger n = y.subtract(v.multiply(q));
            b = a;
            a = r;
            x = u;
            y = v;
            u = m;
            v = n;
        }
        return new Pair<>(x, y);
    }

    private String getOutEncodePath() {
        return this.pathFile + ".encoded";
    }

    private String getOutDecodePath() {
        return this.pathFile + ".decoded";
    }

    private void printToFile(ArrayList<Byte> data) {
        try (FileOutputStream fos = new FileOutputStream(getOutDecodePath())) {
            byte[] byteArray = new byte[data.size()];
            for (int i = 0; i < data.size(); i++) {
                byteArray[i] = data.get(i);
            }
            fos.write(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Integer> getBytesFromFile(){
        ArrayList<Integer> arrayList = new ArrayList<>();
        byte[] bytes = getByteArrayFromFile();
        for (byte data : bytes){
            arrayList.add(data + 128);
        }
        return arrayList;
    }

}
