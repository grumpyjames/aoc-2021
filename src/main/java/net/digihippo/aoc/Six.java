package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Six {
    public static long compute(int dayCount, InputStream stream) throws IOException {
        final long[] fishCount = new long[9];

        Lines.processLines(stream, input -> {
            final String[] split = input.split(",");
            for (String part : split) {
                final int timer = Integer.parseInt(part);
                fishCount[timer]++;
            }
        });

        for (int i = 0; i < dayCount; i++) {
             runDay(fishCount);
        }

        return Arrays.stream(fishCount).sum();
    }

    private static void runDay(long[] timerCounts) {
        long newEightFish = timerCounts[0];

        System.arraycopy(timerCounts, 1, timerCounts, 0, timerCounts.length - 1);
        timerCounts[6] += newEightFish;
        timerCounts[8] = newEightFish;
    }
}
