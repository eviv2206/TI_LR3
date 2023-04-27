package com.example.lr3;

import java.math.BigInteger;
import java.security.SecureRandom;

public class MillerRabinTest {
    public static boolean isProbablePrime(BigInteger n, int k) {
        if (n.compareTo(BigInteger.ONE) <= 0) {
            return false; // отрицательные числа, 0 и 1 не являются простыми
        }
        if (n.compareTo(BigInteger.TWO) == 0) {
            return true; // 2 - простое число
        }
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return false; // четные числа не являются простыми
        }
        int s = 0;
        BigInteger d = n.subtract(BigInteger.ONE);
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            s++;
            d = d.divide(BigInteger.TWO);
        }
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < k; i++) {
            BigInteger a = new BigInteger(n.bitLength(), random);
            a = a.mod(n.subtract(BigInteger.TWO)).add(BigInteger.TWO);
            BigInteger x = a.modPow(d, n);
            if (x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE))) {
                continue;
            }
            boolean isPrime = false;
            for (int r = 1; r < s; r++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(n.subtract(BigInteger.ONE))) {
                    isPrime = true;
                    break;
                }
            }
            if (!isPrime) {
                return false; // число n не является простым
            }
        }
        return true; // число n, вероятно, является простым
    }
}
