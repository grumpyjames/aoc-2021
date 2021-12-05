package net.digihippo.aoc;

public final class TwoDArrays
{
    static int[][] generate(final int width, final int height)
    {
        int[][] result = new int[height][];
        for (int i = 0; i < result.length; i++) {
            int[] row = new int[width];
            result[i] = row;
        }
        return result;
    }
}
