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

        return Integer.parseInt(output.epsilon.toString(), 2) * Integer.parseInt(output.gamma.toString(), 2);
    }

    public static int computePartTwo(InputStream stream) throws IOException {
        final ReducingComputer callback = new ReducingComputer();
        Lines.processLines(stream, callback);

        return callback.power();
    }

    private static final class Output
    {
        private final String gamma;
        private final String epsilon;

        private Output(String gamma, String epsilon) {
            this.gamma = gamma;
            this.epsilon = epsilon;
        }
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
            final StringBuilder epsilon = new StringBuilder();

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
                    epsilon.append('0');
                } else {
                    epsilon.append('1');
                    gamma.append('0');
                }
            }

            return new Output(gamma.toString(), epsilon.toString());
        }
    }

    private static class ReducingComputer implements Consumer<String> {
        final List<String> inputs = new ArrayList<>();
        final Computer computer = new Computer();

        @Override
        public void accept(String s) {
            inputs.add(s);
            computer.accept(s);
        }

        public int power() {
            final List<String> oxygen = new ArrayList<>(inputs);
            final List<String> co2 = new ArrayList<>(inputs);

            final Output power = computer.power();

            int bitIndex = 0;
            char acceptable = power.gamma.charAt(bitIndex);
            while (oxygen.size() != 1)
            {
                final int bii = bitIndex;
                final char pred = acceptable;
                oxygen.removeIf(s -> s.charAt(bii) != pred);
                bitIndex++;

                // recompute :-/
                if (oxygen.size() != 1) {
                    final Computer computer = new Computer();
                    oxygen.forEach(computer);
                    acceptable = computer.power().gamma.charAt(bitIndex);
                }
            }

            bitIndex = 0;
            acceptable = power.epsilon.charAt(bitIndex);
            while (co2.size() != 1)
            {
                final int bii = bitIndex;
                final char pred = acceptable;
                co2.removeIf(s -> s.charAt(bii) != pred);
                bitIndex++;

                // recompute :-/
                if (co2.size() != 1) {
                    final Computer computer = new Computer();
                    co2.forEach(computer);
                    acceptable = computer.power().epsilon.charAt(bitIndex);
                }
            }

            return Integer.parseInt(oxygen.get(0), 2) * Integer.parseInt(co2.get(0), 2);
        }
    }
}
