package net.digihippo.aoc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Eighteen {

    public static final Literal ZERO = new Literal(0);

    public static Number sum(InputStream stream) throws IOException {
        final List<Number> inputs = Lines.parseLines(stream, Eighteen::parse);

        Number sum = inputs.get(0);
        for (int i = 1; i < inputs.size(); i++) {
            Pair pair = new Pair(sum, inputs.get(i));
            sum = reduce(pair);
        }

        return sum;
    }

    static Number explode(Number number) {
        final AtomicReference<Message> exploded = new AtomicReference<>(null);
        return number.reduce(0, exploded);
    }

    static Number split(Number number) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        return number.split(atomicBoolean);
    }

    static String print(Number number) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        number.printTo(new PrintStream(stream));
        return stream.toString(StandardCharsets.UTF_8);
    }

    public static Number parse(String line) {
        final Stack<Number> stack = new Stack<>();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            switch (c) {
                case '[':
                case ',':
                    break;
                case ']':
                    Number right = stack.pop();
                    Number left = stack.pop();
                    stack.push(new Pair(left, right));
                    break;
                default:
                    // must be a literal
                    stack.push(new Literal(c - '0'));
                    break;
            }
        }
        return stack.pop();
    }

    public static Number reduce(Number number) {
        boolean anyReduction = true;
        Number result = number;
        while (anyReduction)
        {
            final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            final AtomicReference<Message> exploded = new AtomicReference<>(null);
            result = result.reduce(0, exploded);
            if (exploded.get() == null) {
                result = result.split(atomicBoolean);
            }

            anyReduction = atomicBoolean.get() || exploded.get() != null;
        }

        return result;
    }

    public static int magnitude(Number number) {
        return number.magnitude();
    }

    public static int largestMagnitude(InputStream stream) throws IOException {
        List<Number> numbers = Lines.parseLines(stream, Eighteen::parse);
        int maximum = 0;
        for (int i = 0; i < numbers.size(); i++) {
            for (int j = 0; j < numbers.size(); j++) {
                 if (i != j)
                 {
                     Number result = reduce(new Pair(numbers.get(i), numbers.get(j)));
                     maximum = Math.max(maximum, magnitude(result));
                 }
            }
        }

        return maximum;
    }

    enum Side {
        Left,
        Right
    }

    private static final class MessagePart
    {
        boolean processed = false;
        final Side visitFirst;
        final Number toAdd;

        private MessagePart(Side side, Number toAdd) {
            this.visitFirst = side;
            this.toAdd = toAdd;
        }
    }


    private static final class Message {
        Number from;
        final MessagePart addLeft;
        final MessagePart addRight;

        private Message(Number from, Number addLeft, Number addRight) {
            this.from = from;
            this.addLeft = new MessagePart(Side.Right, addLeft);
            this.addRight = new MessagePart(Side.Left, addRight);
        }
    }

    sealed interface Number permits Pair, Literal {
        int depth();

        Number reduce(int depth, AtomicReference<Message> exploded);

        Number add(Number another);

        void printTo(PrintStream p);

        Number accept(MessagePart message);

        Number split(AtomicBoolean atomicBoolean);

        int magnitude();
    }

    record Pair(Number left, Number right) implements Number {
        @Override
        public int depth() {
            return 1 + Math.max(left.depth(), right.depth());
        }

        @Override
        public Number reduce(int depth, AtomicReference<Message> exploded) {
            if (exploded.get() != null)
            {
                return this;
            }

            if (depth == 4)
            {
                exploded.set(new Message(this, left, right));
                return ZERO;
            }


            Number newLeft = left.reduce(depth + 1, exploded);
            Message newMessage = exploded.get();
            if (newMessage != null)
            {
                Number newRight = right.accept(newMessage.addRight);
                return new Pair(newLeft, newRight);
            }

            Number newRight = right.reduce(depth + 1, exploded);
            newMessage = exploded.get();
            if (newMessage != null)
            {
                newLeft = newLeft.accept(newMessage.addLeft);
                return new Pair(newLeft, newRight);
            }

            return new Pair(newLeft, newRight);
        }

        @Override
        public Number add(Number another) {
            return new Pair(this, another);
        }

        @Override
        public void printTo(PrintStream p) {
            p.print('[');
            left.printTo(p);
            p.print(',');
            right.printTo(p);
            p.print(']');
        }

        @Override
        public Number accept(MessagePart message) {
            if (message.visitFirst == Side.Left)
            {
                Number newLeft = left.accept(message);
                return new Pair(newLeft, right.accept(message));
            }
            else
            {
                Number newRight = right.accept(message);
                return new Pair(left.accept(message), newRight);
            }
        }

        @Override
        public Number split(AtomicBoolean atomicBoolean) {
            if (atomicBoolean.get())
            {
                return this;
            }

            Number newLeft = left.split(atomicBoolean);
            Number newRight = right.split(atomicBoolean);
            return new Pair(newLeft, newRight);
        }

        @Override
        public int magnitude() {
            return 3 * left.magnitude() + 2 * right.magnitude();
        }
    }

    record Literal(int value) implements Number {
        @Override
        public int depth() {
            return 1;
        }

        @Override
        public Number reduce(int depth, AtomicReference<Message> exploded) {
            return this;
        }

        @Override
        public Number add(Number another) {
            if (another instanceof Literal l) {
                return new Literal(value + l.value);
            }
            return new Pair(this, another);
        }

        @Override
        public void printTo(PrintStream p) {
            p.print(value);
        }

        @Override
        public Number accept(MessagePart message) {
            if (!message.processed) {
                message.processed = true;
                return add(message.toAdd);
            }

            return this;
        }

        @Override
        public Number split(AtomicBoolean atomicBoolean) {
            if (atomicBoolean.get())
            {
                return this;
            }

            if (value >= 10)
            {
                double d = value / 2D;
                int left = (int) Math.floor(d);
                int right = (int) Math.ceil(d);
                atomicBoolean.set(true);
                return new Pair(new Literal(left), new Literal(right));
            }
            return this;
        }

        @Override
        public int magnitude() {
            return value;
        }
    }


}
