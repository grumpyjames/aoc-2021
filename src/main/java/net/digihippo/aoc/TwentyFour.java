package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

public class TwentyFour {
    public static long findLargestModelNumber(InputStream stream) throws IOException {
        final LazyAlu alu = prepareAlu(stream);

        final int[] digits = new int[] {1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9};

        int count = 0;

        for (int i = 0; i < 9; i++) {
            int[] replace = new int[] {i, i, i, i, i, i, i, i};
            final RegisterValue injected = alu.expressions[3].replace(replace);
            System.out.println("min: " + injected.min() + ", max: " + injected.max());
        }

        while (digits[12] > 0) {
            final long evaluate = alu.expressions[3].evaluate(digits);
            System.out.println(count + ", " + evaluate + ", " + Arrays.toString(digits));
            decrement(digits);
            count++;

            if (count % 1_000_000 == 0)
            {
                System.out.println("Done " + count + " " + Arrays.toString(digits));
            }
        }
        
        return 0;
    }

    public static long findLargestModelNumberEager(InputStream stream) throws IOException {
        final Alu alu = new Alu();
        final List<Instruction> instructions = parse(stream);


        final long biggest = 99999999999999L;
//        final long biggest = 99992129102366L;

        int count = 0;
        while (count < 10000) {
            alu.reset(Long.toString(biggest - count));
            instructions.forEach(i -> i.execute(alu));

            System.out.println(count + ", " + alu.registers[3] + ", " + (biggest - count));

            count++;
        }

        return 0;
    }

    public static LazyAlu prepareAlu(InputStream stream) throws IOException {
        final List<Instruction> instructions = parse(stream);

        final LazyAlu alu = new LazyAlu();
        instructions.forEach(i -> i.executeLazy(alu));
        return alu;
    }

    private static void decrement(int[] digits) {
        for (int i = digits.length; i > 0; i--) {
            if (digits[i - 1] > 0L)
            {
                digits[i - 1] = digits[i - 1] - 1;
                for (int j = i; j < digits.length; j++)
                {
                    digits[j] = 9;
                }
                return;
            }
        }
    }

    sealed interface RegisterValue permits Compound, Exactly, ReadInput {
        String expr();
        long evaluate(int[] input);
        RegisterValue replace(int[] input);
        void printTo(PrintStream ps, int depth);

        long max();
        long min();
    }

    record ReadInput(int index) implements RegisterValue {
        @Override
        public String expr() {
            return "{" + index + "}";
        }

        @Override
        public long evaluate(int[] input) {
            return input[index];
        }

        @Override
        public RegisterValue replace(int[] input) {
            if (index < input.length)
            {
                return new Exactly(input[index]);
            }
            return this;
        }

        @Override
        public void printTo(PrintStream ps, int depth) {
            ps.print(expr());
        }

        @Override
        public long max() {
            return 9;
        }

        @Override
        public long min() {
            return 1;
        }
    }

    record Exactly(long value) implements RegisterValue {
        @Override
        public String expr() {
            return Long.toString(value);
        }

        @Override
        public long evaluate(int[] input) {
            return value;
        }

        @Override
        public RegisterValue replace(int[] input) {
            return this;
        }

        @Override
        public void printTo(PrintStream ps, int depth) {
            ps.print(value);
        }

        @Override
        public long max() {
            return value;
        }

        @Override
        public long min() {
            return value;
        }
    }

    static Compound newCompound(
            RegisterValue left,
            RegisterValue right,
            Operator operator)
    {
        if (operator == Operator.Add && left.equals(new ReadInput(13)) && right.equals(new Exactly(6)))
        {
            System.out.println("Hmm");
        }
        return new Compound(left, right, operator);
    }

    record Compound(
            RegisterValue left,
            RegisterValue right,
            Operator operator) implements RegisterValue {
        @Override
        public String expr() {
            return left.expr() + operator.expr() + right.expr();
        }

        @Override
        public long evaluate(int[] input) {
            return operator.lf.execute(left.evaluate(input), right.evaluate(input));
        }

        @Override
        public RegisterValue replace(int[] input) {
            return new Compound(left.replace(input), right.replace(input), operator);
        }

        @Override
        public void printTo(PrintStream ps, int depth) {
            ps.print("(" + operator.expr + " ");
            left.printTo(ps, depth + 1);
            ps.print(" ");
            right.printTo(ps, depth + 1);
            ps.print(")");
        }

        @Override
        public long max() {
            switch (operator)
            {
                case Add -> {
                    return left.max() + right.max();
                }
                case Mul -> {
                    // We don't know - signs could screw us.
                    return Math.abs(left.max() * right.max());
                }
                case Div -> {
                    return Math.abs(left.max() / right.min());
                }
                case Mod -> {
                    return Math.min(left.max(), right.max() - 1);
                }
                case Eq -> {
                    return 1L;
                }
//                case Rem -> {
//                    return left.max();
//                }
                default -> throw new UnsupportedOperationException();
            }
        }

        @Override
        public long min() {
            switch (operator)
            {
                case Add -> {
                    return left.min() + right.min();
                }
                case Mul -> {
                    return -(left.max() * right.max());
                }
                case Div -> {
                    return 0L;
                }
                case Mod -> {
                    return 0L;
                }
                case Eq -> {
                    return 0L;
                }
//                case Rem -> {
//                    // too hard to think about
//                    return Long.MIN_VALUE;
//                }
                default -> throw new UnsupportedOperationException();
            }
        }

        public RegisterValue simplify() {
            if (operator == Operator.Eq) {
                final RegisterValue simplified = simplifyEq();

                printSimplified(simplified);

                return simplified;
            }
            else if (operator == Operator.Add) {
                final RegisterValue simplified = simplifyAdd();

                printSimplified(simplified);

                return simplified;
            }
            else if (operator == Operator.Mul) {
                final RegisterValue simplified = simplifyMul();

                printSimplified(simplified);

                return simplified;
            }
            else if (operator == Operator.Div) {
                final RegisterValue simplified = simplifyDiv();

                printSimplified(simplified);

                return simplified;
            }
            else if (operator == Operator.Mod) {
                final RegisterValue simplified = simplifyMod();

                printSimplified(simplified);

                return simplified;
            }

            return this;
        }

        private void printSimplified(RegisterValue simplified) {
            if (simplified != this) {
//                System.out.println("Simplified:\n\t" + this + "\nto\n\t" + simplified + "\n");
            } else {
                System.out.println("Unable to simplify:\n\t" + this);
            }
        }

        private RegisterValue simplifyMod() {
            switch (right)
            {
                case Exactly e:
                    if (e.value == 0) {
                        return left;
                    } else if (e.value == 1) {
                        return new Exactly(1);
                    } else if (left.max() < e.value) {
                        return left;
                    } else {
                        switch (left) {
                            case Compound c:
                                if (c.operator == Operator.Add)
                                {
                                    if (
                                            c.left instanceof final Exactly e2 &&
                                            c.right instanceof final Compound depthTwo)
                                    {
                                        if (depthTwo.operator == Operator.Mul) {
                                            if (depthTwo.left.equals(e) || depthTwo.right.equals(e)) {
                                                return new Exactly(e2.value % e.value);
                                            }
                                        }
                                    }
                                    else if (
                                            c.right instanceof final Exactly e2 &&
                                            c.left instanceof final Compound depthTwo
                                    )
                                    {
                                        if (depthTwo.operator == Operator.Mul) {
                                            if (depthTwo.left.equals(e) || depthTwo.right.equals(e)) {
                                                return new Exactly(e2.value % e.value);
                                            }
                                        }
                                    }
                                    else if (
                                        c.left.max() < e.value &&
                                        c.right instanceof final Compound depthTwo
                                    )
                                    {
                                        if (depthTwo.operator == Operator.Mul) {
                                            if (depthTwo.left.equals(e) || depthTwo.right.equals(e)) {
                                                return c.left;
                                            }
                                        }
                                    }
                                    else if (
                                            c.right.max() < e.value &&
                                            c.left instanceof final Compound depthTwo
                                    )
                                    {
                                        if (depthTwo.operator == Operator.Mul) {
                                            if (depthTwo.left.equals(e) || depthTwo.right.equals(e)) {
                                                return c.right;
                                            }
                                        }
                                    }
                                }
                            default:
                                return this;
                        }
                    }
                default:
                    return this;
            }
        }

        private RegisterValue simplifyDiv() {
            switch (right)
            {
                case Exactly e:
                    if (e.value == 1) {
                        return left;
                    }
                default:
                    switch (left)
                    {
                        case Compound c:
                            if (c.operator == Operator.Mul)
                            {
                                if (c.left.equals(this.right))
                                {
                                    return c.right;
                                } else if (c.right.equals(this.right))
                                {
                                    return c.left;
                                }
                            }

                        default:
                            return this;
                    }
            }
        }

        private RegisterValue simplifyMul() {
            if (left.equals(new Exactly(0)) || right.equals(new Exactly(0)))
            {
                return new Exactly(0);
            }

            if (left.equals(new Exactly(1)))
            {
                return right;
            }

            if (right.equals(new Exactly(1)))
            {
                return left;
            }

            return this;
        }

        private RegisterValue simplifyAdd() {
            switch (left)
            {
                case Exactly e:
                    if (e.value == 0)
                    {
                        return right;
                    }
                    switch (right)
                    {
                        case Compound c:
                            return c.tryAdd(e.value, this);
                        case Exactly f:
                            if (f.value == 0) {
                                return left;
                            }
                        default:
                            return this;
                    }
                case Compound c:
                    return switch (right) {
                        case Exactly g -> c.tryAdd(g.value, this);
                        default -> this;
                    };
                case ReadInput readInput:
                    switch (right) {
                        case Exactly e2:
                            if (e2.value == 0) {
                                return left;
                            }
                        default:
                            return this;
                    }
                default:
                    return this;
            }
        }

        private RegisterValue tryAdd(long value, RegisterValue orElse) {
            if (operator == Operator.Add)
            {
                return switch (left) {
                    case Exactly e -> newCompound(new Exactly(e.value + value), right, operator);
                    default -> switch (right) {
                        case Exactly f -> newCompound(left, new Exactly(f.value + value), operator);
                        default -> orElse;
                    };
                };
            }
            return orElse;
        }

        private RegisterValue simplifyEq() {
            final long lMin = left.min();
            final long lMax = left.max();
            final long rMin = right.min();
            final long rMax = right.max();
            if (lMax < rMin || rMax < lMin)
            {
                return new Exactly(0);
            }

            switch (left)
            {
                case Compound c:
                    if (c.operator == Operator.Eq)
                    {
                        switch (right) {
                            case Exactly e:
                                if (e.value < 0 || e.value > 1)
                                {
                                    return new Exactly(0);
                                }
                            default:
                                return this;
                        }
                    }
                    return this;
                case ReadInput r:
                    switch (right)
                    {
                        case Exactly e:
                            if (e.value <= 0 || e.value > 9) {
                                return new Exactly(0);
                            }
                        default:
                            return this;
                    }
                case Exactly e:
                    if (e.value <= 0 || e.value > 9) {
                        return new Exactly(0);
                    }
                    switch (right)
                    {
                        case Compound c:
                            if (c.operator == Operator.Eq && (e.value < 0 || e.value > 1))
                            {
                                return new Exactly(0);
                            }
                        default:
                            return this;
                    }
            }

            return this;
        }
    }

    interface LongFunction {
        long execute(long one, long two);
    }

    static final class LazyAlu
    {
        final RegisterValue[] expressions = new RegisterValue[] {
                new Exactly(0), new Exactly(0), new Exactly(0), new Exactly(0)
        };
        private int inputIndex = 0;

        public void readOne(int register) {
            expressions[register] = new ReadInput(inputIndex++);
        }

        void express(int register, Operand operand, Operator o) {
            final RegisterValue rv = expressions[register];
            switch (rv)
            {
                case Exactly e:
                    switch (operand) {
                        case Literal lit -> expressions[register] = new Exactly(o.execute(e.value, lit.value));
                        case Register another -> {
                            final RegisterValue regVTwo = expressions[another.register];
                            switch (regVTwo) {
                                case Exactly e2 -> expressions[register] = new Exactly(o.execute(e.value, e2.value));
                                default -> expressions[register] = newCompound(rv, regVTwo, o).simplify();
                            }
                        }
                    }
                    break;
                case ReadInput ri:
                    expressions[register] = combine(rv, operand, o);
                    break;
                case Compound c:
                    expressions[register] = combine(rv, operand, o);
                    break;
            }
        }

        RegisterValue combine(RegisterValue left, Operand right, Operator operator)
        {
            final RegisterValue rvTwo = switch (right) {
                case Literal l -> new Exactly(l.value);
                case Register r -> expressions[r.register];
            };

            return newCompound(left, rvTwo, operator).simplify();
        }
    }

    enum Operator
    {
        Add(Long::sum, "+"),
        Mul((long one, long two) -> one * two, "*"),
        Div((long one, long two) -> one / two, "/"),
        Mod((long one, long two) -> one % two, "%"),
        Eq((long one, long two) -> one == two ? 1 : 0, "==");
//        Rem((long one, long two) -> one - (one % two), "rem");

        Operator(LongFunction lf, String expr) {
            this.lf = lf;
            this.expr = expr;
        }

        private final LongFunction lf;
        private final String expr;

        public String expr() {
            return expr;
        }

        public long execute(long left, long right) {
            return lf.execute(left, right);
        }
    }

    public static RegisterValue[] expressions(InputStream stream) throws IOException {
        final List<Instruction> instructions = parse(stream);

        final LazyAlu lazyAlu = new LazyAlu();
        for (int j = 0; j < instructions.size(); j++) {
            Instruction i = instructions.get(j);
            System.out.println("Executing instruction " + i);
            i.executeLazy(lazyAlu);
        }

//        lazyAlu.expressions[3].printTo(System.out, 1);



        return lazyAlu.expressions;
    }

    private sealed interface Instruction permits Add, Div, EqualityTest, Input, Mod, Multiply {
        void execute(Alu alu);
        void executeLazy(LazyAlu lazyAlu);
    }

    sealed interface Operand permits Literal, Register {

    }

    record Literal(int value) implements Operand {}
    record Register(int register) implements Operand {}

    record Input(Register register) implements Instruction {
        @Override
        public void execute(Alu alu) {
            alu.input(register.register);
        }

        @Override
        public void executeLazy(LazyAlu lazyAlu) {
            lazyAlu.readOne(register.register);
        }
    }
    record EqualityTest(Register register, Operand compareTwo) implements Instruction {
        @Override
        public void execute(Alu alu) {
            if (alu.registers[register.register] == alu.valueOf(compareTwo)) {
                alu.registers[register.register] = 1;
            }
            else
            {
                alu.registers[register.register] = 0;
            }
        }

        @Override
        public void executeLazy(LazyAlu lazyAlu) {
            lazyAlu.express(register.register, compareTwo, Operator.Eq);
        }
    }
    record Add(Register register, Operand operand) implements Instruction {
        @Override
        public void execute(Alu alu) {
            alu.registers[register.register] += alu.valueOf(operand);
        }

        @Override
        public void executeLazy(LazyAlu lazyAlu) {
            lazyAlu.express(register.register, operand, Operator.Add);
        }
    }
    record Multiply(Register register, Operand multiplicand) implements Instruction {
        @Override
        public void execute(Alu alu) {
            alu.registers[register.register] *= alu.valueOf(multiplicand);
        }

        @Override
        public void executeLazy(LazyAlu lazyAlu) {
            lazyAlu.express(register.register, multiplicand, Operator.Mul);
        }
    }
    record Div(Register register, Operand operand) implements Instruction {
        @Override
        public void execute(Alu alu) {
            alu.registers[register.register] /= alu.valueOf(operand);
        }

        @Override
        public void executeLazy(LazyAlu lazyAlu) {
            lazyAlu.express(register.register, operand, Operator.Div);
        }
    }
    record Mod(Register register, Operand operand) implements Instruction {
        @Override
        public void execute(Alu alu) {
            alu.registers[register.register] %= alu.valueOf(operand);
        }

        @Override
        public void executeLazy(LazyAlu lazyAlu) {
            lazyAlu.express(register.register, operand, Operator.Mod);
        }
    }

    private static final class Alu
    {
        private final long[] registers = new long[4];
        private String input;

        int inputOffset = 0;

        public Alu() {}

        void reset(final String input)
        {
            this.input = input;
            this.inputOffset = 0;
            Arrays.fill(registers, 0L);
        }
        
        public void input(int register) {
            registers[register] = input.charAt(inputOffset++) - '0';
        }

        public long valueOf(Operand operand) {
            return switch (operand) {
                case Literal l -> l.value;
                case Register r -> registers[r.register];
            };
        }
    }

    public static long[] run(InputStream stream, String input) throws IOException {
        final List<Instruction> instructions = parse(stream);

        final Alu alu = new Alu();
        alu.reset(input);
        instructions.forEach(i -> i.execute(alu));

        return alu.registers;
    }

    private static List<Instruction> parse(InputStream stream) throws IOException {
        return Lines.parseLines(stream, l -> {

            final String[] parts = l.split(" ");
            switch (parts[0])
            {
                case "mul":
                    return new Multiply(toRegister(parts[1]), toOperand(parts[2]));
                case "inp":
                    return new Input(toRegister(parts[1]));
                case "eql":
                    return new EqualityTest(toRegister(parts[1]), toOperand(parts[2]));
                case "add":
                    return new Add(toRegister(parts[1]), toOperand(parts[2]));
                case "div":
                    return new Div(toRegister(parts[1]), toOperand(parts[2]));
                case "mod":
                    return new Mod(toRegister(parts[1]), toOperand(parts[2]));
            }

            throw new UnsupportedOperationException(l);
        });
    }

    private static Operand toOperand(String part) {
        try
        {
            return new Literal(Integer.parseInt(part));
        }
        catch (NumberFormatException nfe)
        {
            return toRegister(part);
        }
    }

    private static Register toRegister(String part) {
        assert part.length() == 1;
        return new Register(part.charAt(0) - 'w');
    }
}
