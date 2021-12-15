package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class TwoDArrays
{
    static int[][] generate(final int width, final int height, int defaultValue)
    {
        int[][] result = new int[height][];
        for (int i = 0; i < result.length; i++) {
            int[] row = new int[width];
            result[i] = row;
            Arrays.fill(row, defaultValue);
        }
        return result;
    }

    static int[][] generate(final int width, final int height)
    {
        return generate(width, height, 0);
    }

    static int[][] parse(InputStream stream) throws IOException {
        return Lines.parseLines(stream, line -> {
            int[] result = new int[line.length()];
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                result[i] = c - '0';
            }
            return result;
        }).toArray(new int[][]{});
    }
}
