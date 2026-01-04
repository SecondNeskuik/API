package io.neskdev.api.utils.math;

import java.util.concurrent.ThreadLocalRandom;

public class RandomStringUtils {
    /*
    FROM https://stackoverflow.com/questions/10771160/performance-improvement-generating-random-string-of-any-length
     */

    private static final int range = 36 * 36 * 36 * 36 * 36; // 36^5 is less than 2^31
    private static final String zeroes = "00000";

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length+5);
        while (sb.length() < length) {
            String x = Integer.toString(ThreadLocalRandom.current().nextInt(range), 36);
            if(x.length()<5)
                sb.append(zeroes, 0, 5-x.length());
            sb.append(x);
        }
        return sb.substring(0, length);
    }

    public static int getRandomBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }



}
