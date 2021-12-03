package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Three
{
    public static int compute(InputStream stream) throws IOException {
        final Computer callback = new Computer();
        Lines.processLines(stream, callback);

        final Output output = callback.power();

        return Integer.parseInt(not(output.dominantBits), 2) * Integer.parseInt(output.dominantBits, 2);
    }

    public static int computePartTwo(InputStream stream) throws IOException {
        final ReducingComputer callback = new ReducingComputer();
        Lines.processLines(stream, callback);

        return callback.power();
    }

    private record Output(String dominantBits) {}

    private static String not(final String binaryChars)
    {
        final StringBuilder notted = new StringBuilder();
        for (int i = 0; i < binaryChars.length(); i++) {
            if (binaryChars.charAt(i) == '1')
            {
                notted.append('0');
            }
            else
            {
                notted.append('1');
            }
        }
        return notted.toString();
    }

    private static class Computer implements Consumer<String> {
        private int[] counters = null;
        private int count = 0;

        @Override
        public void accept(String s) {
            if (counters == null)
            {
                counters = new int[s.length()];
            }

            for (int i = 0; i < s.length(); i++) {
                 if (s.charAt(i) == '1')
                 {
                     counters[i]++;
                 }
            }
            count++;
        }

        public Output power() {
            final StringBuilder gamma = new StringBuilder();

            final int discriminator;
            if (count % 2 == 1)
            {
                discriminator = count / 2;
            }
            else
            {
                discriminator = count / 2 - 1;
            }

            for (int counter : counters) {
                if (counter > discriminator) {
                    gamma.append('1');
                } else {
                    gamma.append('0');
                }
            }

            return new Output(gamma.toString());
        }
    }

    private static class ReducingComputer implements Consumer<String> {
        final List<String> inputs = new ArrayList<>();

        @Override
        public void accept(String s) {
            inputs.add(s);
        }

        public int power() {
            final String oxygen = reduce(inputs, true);
            final String co2 = reduce(inputs, false);

            return Integer.parseInt(oxygen, 2) * Integer.parseInt(co2, 2);
        }

        private String reduce(List<String> inputs, boolean dominant) {
            final List<String> copy = new ArrayList<>(inputs);

            for (int i = 0; i < copy.get(0).length(); i++) {
                final Computer computer = new Computer();
                copy.forEach(computer);

                final int bii = i;
                final String dominantBits = computer.power().dominantBits;
                final char pred =
                        dominant ? dominantBits.charAt(i) : not(dominantBits).charAt(i);
                copy.removeIf(s -> s.charAt(bii) != pred);

                if (copy.size() == 1) {
                    return copy.get(0);
                }
            }

            throw new IllegalStateException();
        }
    }
}
