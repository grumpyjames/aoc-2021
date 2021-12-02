package net.digihippo.aoc;

import java.io.*;
import java.util.Arrays;
import java.util.function.Consumer;

public class One
{
    public static int countIncreases(InputStream input) throws IOException {

        final IncreaseCounter counter = new IncreaseCounter();
        Lines.processLines(input, counter);

        return counter.increases;
    }

    public static int countSumIncreases(InputStream inputStream) throws IOException {

        final SumIncreaseCounter counter = new SumIncreaseCounter();
        Lines.processLines(inputStream, counter);

        return counter.increases();
    }

    private static final class IncreaseCounter implements Consumer<String>
    {
        private int increases = 0;
        private int lastDepth = Integer.MAX_VALUE;

        @Override
        public void accept(String line) {
            final int depth = Integer.parseInt(line);
            if (depth > lastDepth) {
                increases++;
            }
            lastDepth = depth;
        }
    }

    private static final class Window
    {
        private int increases = 0;

        private final int[] window = new int[3];
        private int index = 0;
        private boolean full = false;

        public void accept(int depth) {
            if (!full)
            {
                window[index++] = depth;
                if (index == 3)
                {
                    full = true;
                }
            }
            else
            {
                int previousSum = Arrays.stream(window).sum();
                window[index++] = depth;
                int currentSum = Arrays.stream(window).sum();
                if (currentSum > previousSum)
                {
                    increases++;
                }
            }

            index = index % 3;
        }
    }

    private static final class SumIncreaseCounter implements Consumer<String> {
        private final Window window = new Window();

        @Override
        public void accept(String line) {
            window.accept(Integer.parseInt(line));
        }

        public int increases() {
            return window.increases;
        }
    }
}
