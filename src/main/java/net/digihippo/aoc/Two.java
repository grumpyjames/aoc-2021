package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Two
{
    public static int execute(InputStream asInputStream) throws IOException {
        final TwoDInterpreter interpreter = new TwoDInterpreter();
        final Submarine sub = new Submarine(interpreter);
        Lines.processLines(asInputStream, sub);
        return interpreter.answer();
    }

    public static int executeTwo(InputStream asInputStream) throws IOException {
        final AimInterpreter interpreter = new AimInterpreter();
        final Submarine sub = new Submarine(interpreter);
        Lines.processLines(asInputStream, sub);
        return interpreter.answer();
    }

    private static final class TwoDInterpreter implements BiConsumer<String, Integer>
    {
        private int x,y;

        public int answer() {
            return x * y;
        }

        @Override
        public void accept(String direction, Integer scalar) {
            switch (direction) {
                case "forward" -> x += scalar;
                case "backward" -> x -= scalar;
                case "up" -> y -= scalar;
                case "down" -> y += scalar;
                default -> throw new UnsupportedOperationException();
            }
        }
    }


    private static final class Submarine implements Consumer<String> {
        private final BiConsumer<String, Integer> computer;

        private Submarine(BiConsumer<String, Integer> computer) {
            this.computer = computer;
        }

        private final Pattern pattern = Pattern.compile("([a-z]*) ([\\d]*)");

        @Override
        public void accept(String s) {
            final Matcher matcher = pattern.matcher(s);
            if (matcher.find())
            {
                final String direction = matcher.group(1);
                final int scalar = Integer.parseInt(matcher.group(2));
                computer.accept(direction, scalar);
            }
        }
    }

    private static class AimInterpreter implements BiConsumer<String, Integer>
    {
        private int x,y,aim;

        @Override
        public void accept(String direction, Integer scalar) {
            switch (direction) {
                case "forward" -> {
                    x += scalar;
                    y += (aim * scalar);
                }
                case "backward" -> {
                    x -= scalar;
                    y -= (aim * scalar);
                }
                case "up" -> aim -= scalar;
                case "down" -> aim += scalar;
                default -> throw new UnsupportedOperationException();
            }
        }

        public int answer() {
            return x * y;
        }
    }
}
