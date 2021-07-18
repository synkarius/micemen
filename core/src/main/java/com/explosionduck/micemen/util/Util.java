package com.explosionduck.micemen.util;

public class Util {
    public static int intPow(int num, int pow) {
        // doesn't handle pow < 1
        int result = num;
        for (int i = 0; i < pow - 1; i++)
            result *= num;
        return result;
    }
}
