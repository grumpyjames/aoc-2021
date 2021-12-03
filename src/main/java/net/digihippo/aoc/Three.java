package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Three
{
    public static int compute(InputStream stream) throws IOException
    {
        final Computer callback = new Computer();
        Lines.processLines(stream, callback);

        final BinaryString binaryString = callback.power();

        return binaryString.toInt() * binaryString.not().toInt();
    }

    public static int computePartTwo(InputStream stream) throws IOException
    {
        final ReducingComputer callback = new ReducingComputer();
        Lines.processLines(stream, callback);

        return callback.power();
    }

    private record BinaryString(String bits)
    {
        public BinaryString not()
        {
            return new BinaryString(notBits(bits));
        }

        public int toInt()
        {
            return Integer.parseInt(bits, 2);
        }

        private String notBits(final String binaryChars)
        {
            final StringBuilder result = new StringBuilder();
            for (int i = 0; i < binaryChars.length(); i++) {
                if (binaryChars.charAt(i) == '1') {
                    result.append('0');
                } else {
                    result.append('1');
                }
            }
            return result.toString();
        }
    }

    private static class Computer implements Consumer<String>
    {
        private int[] counters = null;
        private int count = 0;

        @Override
        public void accept(String s)
        {
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

        public BinaryString power()
        {
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

            return new BinaryString(gamma.toString());
        }
    }

    private static class ReducingComputer implements Consumer<String>
    {
        final List<String> inputs = new ArrayList<>();

        @Override
        public void accept(String s) {
            inputs.add(s);
        }

        public int power() {
            final BinaryString oxygen = reduce(inputs, true);
            final BinaryString co2 = reduce(inputs, false);

            return oxygen.toInt() * co2.toInt();
        }

        private BinaryString reduce(List<String> inputs, boolean dominant) {
            final List<String> copy = new ArrayList<>(inputs);

            for (int i = 0; i < copy.get(0).length(); i++) {
                final Computer computer = new Computer();
                copy.forEach(computer);

                final int bii = i;
                final String result = dominant ? computer.power().bits : computer.power().not().bits;
                final char pred = result.charAt(i);
                copy.removeIf(s -> s.charAt(bii) != pred);

                if (copy.size() == 1) {
                    return new BinaryString(copy.get(0));
                }
            }

            throw new IllegalStateException();
        }
    }
}
