package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Six {
    public static long compute(int dayCount, InputStream stream) throws IOException {
        long[] fishCount = new long[9];

        long[] finalFishCount = fishCount;
        Lines.processLines(stream, input -> {
            final String[] split = input.split(",");
            for (String part : split) {
                final int timer = Integer.parseInt(part);
                finalFishCount[timer]++;
            }
        });

        for (int i = 0; i < dayCount; i++) {
             fishCount = runDay(fishCount);
        }

        return Arrays.stream(fishCount).sum();
    }

    private static long[] runDay(long[] timerCounts) {
        long[] newTimerCounts = new long[9];

        long newEightFish = timerCounts[0];

        System.arraycopy(timerCounts, 1, newTimerCounts, 0, newTimerCounts.length - 1);
        newTimerCounts[6] += newEightFish;
        newTimerCounts[8] += newEightFish;

        return newTimerCounts;
    }
}
