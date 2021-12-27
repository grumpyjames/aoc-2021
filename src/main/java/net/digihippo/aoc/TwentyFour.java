package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

public class TwentyFour {
    public static long findLargestModelNumber(InputStream stream) throws IOException {
        final LazyAlu alu = prepareAlu(stream);

        final int[] digits = new int[14];
        int count = 0;

        for (int i = 1; i < 10; i++) {
            Arrays.fill(digits, i);
            final long injected = alu.expressions[3].evaluate(digits);
            System.out.println("eval: " + injected + ", input: " + Arrays.toString(digits));
        }
//
//        while (digits[12] > 0) {
//            final long evaluate = alu.expressions[3].evaluate(digits);
//            System.out.println(count + ", " + evaluate + ", " + Arrays.toString(digits));
//            decrement(digits);
//            count++;
//
//            if (count % 1_000_000 == 0)
//            {
//                System.out.println("Done " + count + " " + Arrays.toString(digits));
//            }
//        }
//
        return 0;
    }

    /*
    inp w
mul x 0
add x z
mod x 26
div z 1
add x 14
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y 16
mul y x
add z y
     */
    int read(int i)
    {
        return 1;
    }

    public static long handRolled(long[] input, long[] xOff, long[] yOff, long[] zDiv)
    {
        /*
        inp w
mul x 0
add x z
mod x 26
div z 1
add x 14
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y 16
mul y x
add z y

Given z_0 = 0,
  z_01 = (16 + input[0])


         */
        long z = 0;
        for (int i = 0; i < 14; i++) {
            if ((z % 26) != input[i] - xOff[i]) // eql x w, eql x 0
            {
                long interim = 26 * (z / zDiv[i]); // This is either 26z, or z to the nearest multiple of 26.
                z = interim + (yOff[i] + input[i]); // mul y 0, add y 25, mul y x, add y 1, div z 1 (zDiv[i]), mul z y mul y 0, add y w, add y 16 (yOff[i]), mul y x
            }
            else
            {
                z /= zDiv[i]; // This is either z or z to the nearest multiple of 26.
            }
        }

        return z;
    }

    public static long handRolledRec(int inputIndex, long[] input, long[] xOff, long[] yOff, long[] zDiv)
    {
        /*
        inp w
mul x 0
add x z
mod x 26
div z 1
add x 14
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y 16
mul y x
add z y
         */
        long z = 0;
        if (inputIndex == 0)
        {
            return computeZ(0, input[inputIndex], inputIndex, xOff, yOff, zDiv);
        }
        else
        {
            long prevZ = handRolledRec(inputIndex - 1, input, xOff, yOff, zDiv);
            return computeZ(
                    prevZ,
                    input[inputIndex],
                    inputIndex,
                    xOff,
                    yOff,
                    zDiv
            );
        }
    }

    static long computeZ(long z, long input, int i, long[] xOff, long[] yOff, long[] zDiv)
    {
        // Either z was 0 already OR
        // z % 26 == (input[i] - xOff[i]) =>
        //

        if ((z % 26) != input - xOff[i]) // eql x w, eql x 0
        {
            return 26 * (z / zDiv[i]) + (yOff[i] + input); // mul y 0, add y 25, mul y x, add y 1, div z 1 (zDiv[i]), mul z y mul y 0, add y w, add y 16 (yOff[i]), mul y x
        }
        else
        {
            return z / zDiv[i];
        }
    }

    /*
      13, 12, 11, 9, 8, 4

      z_14 = (z_13 % 26 == input[13] + 12) ? z_13 / 26 : input[13] + 6 + (26 * z_13/26)
      z_13 = (z_12 % 26 == input[12] + 9)  ? z_12 / 26 : input[12] + 4 + (26 * z_12/26)
      z_12 = (z_11 % 26 == input[11] + 3)  ? z_11 / 26 : input[11] + 4 + (26 * z_11/26)
      z_11 = (z_10 % 26 == input[10] - 13) ? z_10 / 1  : input[10] + 11 + (26 * z_10/1)
      z_10 = (z_09 % 26 == input[9] + 3)   ? z_09 / 26 : input[9]  + 5 + (26 * z_09/26)
      z_09 = (z_08 % 26 == input[8] + 4)   ? z_08 / 26 : input[8]  + 6 + (26 * z_08/26)
      z_08 = (z_07 % 26 == input[7] - 10)  ? z_07 / 1 : input[7]   + 11 + (26 * z_07/1)
      z_07 = (z_06 % 26 == input[6] + 14)  ? z_06 / 26 : input[6]  + 10 + (26 * z_06/26)
      z_06 = (z_05 % 26 == input[5] - 15)  ? z_05 / 1  : input[5]  + 6 + (26 * z_05/1)
      z_05 = (z_04 % 26 == input[4] + 10)  ? z_04 / 26 : input[4]  + 13 + (26 * z_04/26)
      z_04 = (z_03 % 26 == input[3] - 11)  ? z_03 / 1  : input[3]  + 7 + (26 * z_03/1)
      z_03 = (z_02 % 26 == input[2] - 12)  ? z_02 / 1  : input[2]  + 2 + (26 * z_02/1)
      z_02 = (z_01 % 26 == input[1] - 11)  ? z_01 / 1  : input[1]  + 3 + (26 * z_01/1)
      z_01 = (z_00 % 26 == input[0] - 14)  ? z_00 / 1  : input[0]  + 16 + (26 * z_00/1)

      z_14 = (input[0] + 16 % 26 == input[13] + 12) ? z_13 / 26 : input[13] + 6 + (26 * z_13/26)
             let's try it, input[0] = 5, input[13] = 9
           = z_13 / 26
           = 0
           what if we just break _this_ condition ?
           = input[13] + 6 + (26 * (16 + input[0])/26) => still impossible.

           false tree
           = input[13] + 6 + (26 * z_13/26)
           = input[13] + 6 + 26(input[9] + 5 + 26(input[6] + 10 + 26(input[4] + 13 + 26(input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1)))))

      z_13 = (z_12 % 26 == input[12] + 9)  ? z_12 / 26 : input[12] + 4 + (26 * z_12/26)
           = input[1] + 3 % 26 == input[12] + 9 ? z_12 / 26 : input[12] + 4 + (26 * z_12/26)
             doable, pick input[1] = 9, input[12] = 3
           = z_12 / 26
           = 16 + input[0]
           false tree
             => input[11] + 4 % 26 != input[12] + 9
           = input[12] + 4 + (26 * z_12/26)
           = input[12] + 4 + 26(input[9] + 5 + 26(input[6] + 10 + 26(input[4] + 13 + 26(input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1)))))

      z_12 = (z_11 % 26 == input[11] + 3)  ? z_11 / 26 : input[11] + 4 + (26 * z_11/26)
           = (input[10] + 11 % 26) == input[11] + 3 ? z_11 / 26 : input[11] + 4 + (26 * z_11/26)
             Doable - let's try it. input[10] = 1 and input[11] = 9
           = z_11 / 26
           = input[1] + 3 + 26(16 + input[0])
           false tree
             => input[10] + 11 != input[11] + 3
           = input[11] + 4 + (26 * z_11/26)
           = input[11] + 4 + 26(input[9] + 5 + 26(input[6] + 10 + 26(input[4] + 13 + 26(input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1)))))

      z_11 = (z_10 % 26 == input[10] - 13) ? z_10 / 1  : input[10] + 11 + (26 * z_10/1)
           = input[1] + 3 % 26 == input[10] - 13 ? z_10 / 1  : input[10] + 11 + (26 * z_10/1)
           = false ? <w/e> : input[10] + 11 + (26 * z_10/1)
           = input[10] + 11 + 26(input[1] + 3 + 26(16 + input[0]))
           false tree
           = input[10] + 11 + (26 * z_10/1)
           = input[10] + 11 + 26(input[9] + 5 + 26(input[6] + 10 + 26(input[4] + 13 + 26(input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1)))))

      z_10 = (z_09 % 26 == input[9] + 3)   ? z_09 / 26 : input[9]  + 5 + (26 * z_09/26)
           = (input[2] + 2 % 26 == input[9] + 3) ? z_09 / 26 : input[9]  + 5 + (26 * z_09/26)
             Again, let's try the true case input[2] = 9, input[9] = 8
           = z_09 / 26
           = input[1] + 3 + 26(16 + input[0])
           false tree
             => input[8] + 6 !=  input[9] + 3
           = input[9] + 5 + 26(input[6] + 10 + 26(input[4] + 13 + 26(input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1))))

      z_09 = (z_08 % 26 == input[8] + 4)   ? z_08 / 26 : input[8]  + 6 + (26 * z_08/26)
           = (input[7] + 11 % 26 == input[8] + 4) ? z_08 / 26 : input[8]  + 6 + (26 * z_08/26)
             pick the true case: input[7] = 2, input[8] = 9
           = z_08 / 26
           = input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1)
           false tree
             => input[7] + 11 != input[8] + 4
           = input[8] + 6 + (26 * z_08/26)
           = input[8] + 6 + 26(input[6] + 10 + 26(input[4] + 13 + 26(input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1))))

      z_08 = (input[2] + 2 % 26 == input[7] - 10)  ? z_07 / 1 : input[7]   + 11 + (26 * z_07/1)
           = false ? <w/e> : input[7]   + 11 + (26 * z_07/1)
           = input[7] + 11 + 26(input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1))
           false tree
           = input[7] + 11 + (26 * z_07/1)
           = input[7] + 11 + 26(input[6] + 10 + 26(input[4] + 13 + 26(input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1))))


      z_07 = (z_06 % 26 == input[6] + 14)  ? z_06 / 26 : input[6]  + 10 + (26 * z_06/26)
           = (input[5] + 6 % 26) == input[6] + 14 ? z_06 / 26 : input[6]  + 10 + (26 * z_06/26)
           let's try the true case
           = z_06 / 26
           = (input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1))
           Lost input[6] and input[5], brilliant
           Set input[5] = 9  and input[6] = 1
           and the false tree
             => input[5] + 6 != input[6] + 14
           = input[6]  + 10 + (26 * z_06/26)
           = input[6] + 10 + 26(input[4] + 13 + 26(input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1)))


      z_06 = (z_05 % 26 == input[5] - 15)  ? z_05 / 1  : input[5]  + 6 + (26 * z_05/1)
           = (input[2] + 2) % 26 == input[5] - 15 ? z_05 / 1  : input[5]  + 6 + (26 * z_05/1)
           = false ? w/e : input[5] + 6 + 26((input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1)))
           = input[5] + 6 + 26((input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1)))
           false tree
           = (z_05 % 26 == input[5] - 15)  ? z_05 / 1  : input[5]  + 6 + (26 * z_05/1)
           = (input[4]  + 13 % 26) == input[5] - 15 ? z_05 / 1  : input[5]  + 6 + (26 * z_05/1)
           = false ? <w/e> : input[5]  + 6 + (26 * z_05/1)
           = input[5] + 6 + 26(input[4] + 13 + 26(input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1)))

      z_05 = (z_04 % 26 == input[4] + 10)  ? z_04 / 26 : input[4]  + 13 + (26 * z_04/26)
           = input[3] + 7 == input[4] + 10 ? z_04 / 26 : input[4]  + 13 + (26 * z_04/26)
           = don't know! Let's examine the true case:
             z_04 / 26 -> this is (input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1))
             We've lost input[4] _and_ input[3]. What a win!
             So set input[3] = 9 and input[4] = 6
             and the false case
             => input[3] + 7 != input[4] + 10
           = input[4] + 13 + (26 * z_04/26)
           = input[4] + 13 + 26(input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1))

      z_04 = (z_03 % 26 == input[3] - 11)  ? z_03 / 1  : input[3]  + 7 + (26 * z_03)
           = ((input[2] + 2) % 26 == input[3] - 11) ? (input[2] + 10896 + (26 * input[1]) + (676 * input[0])) / 1  : input[3]  + 7 + (26 * (input[2] + 10896 + (26 * input[1]) + (676 * input[0]))/1)
           = (false) : <w/e> : input[3] + 7 + (26 * z_03)
           = input[3] + 7 + 26((input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1)))
      z_03 = (z_02 % 26 == input[2] - 12)  ? z_02 / 1  : input[2]  + 2 + (26 * z_02/1)
           = (input[1] + 3 % 26 == input[2] - 12)  ? (input[1] + (419 + 26 * input[0])) / 1  : input[2]  + 2 + (26 * (input[1] + (419 + 26 * input[0]))/1)
           = false ? <w/e> : input[2] + 2 + (26 * (input[1] + 3 + 26(16 + input[0])/1))
           = (input[2] + 2 + 26(input[1] + 3 + 26(16 + input[0])/1))

      z_02 = (z_01 % 26 == input[1] - 11)  ? z_01 / 1  : input[1]  + 3 + (26 * z_01/1)
           = ((16 + input[0]) % 26 == input[1] - 11)  ? (16 + input[0]) / 1  : input[1]  + 3 + (26 * (16 + input[0])/1)
           = (false) ? <w/e> : input[1]  + 3 + (26 * (16 + input[0])/1)
           = input[1] + 3 + 26(16 + input[0])/1)
           = (input[1] + (419 + 26 * input[0]))

      z_01 = (0 % 26    == input[0] - 14)  ? z_00 / 1  : input[0]  + 16 + (26 * z_00/1)
        => z_01 = (16 + input[0])

      insert z_00 = 0

      input[0] = 5 and input[13] = 9
       => input[0] = 1 and input[13] = 5
      input[1] = 9 and input[12] = 3
       => input[1] = 7 and input[12] = 1
      input[2] = 9, input[9] = 8
       => input[2] = 2 and input[9] = 1
      input[3] = 9 and input[4] = 6
       => input[3] = 4 and input[4] = 1
      input[5] = 9 and input[6] = 1
       => input[5] = 9 and input[6] = 1
      input[7] = 2 and input[8] = 9
       => input[7] = 1 and input[8] = 8
      input[10] = 1 and input[11] = 9
       => input[10] = 1 and input[11] = 9

                0  1  2  3  4  5  6  7  8  9 10 11 12 13
      inputs = [5, 9, 9, 9, 6, 9, 1, 2, 9, 8, 1, 9, 3, 9]
      59996912981939

                0  1  2  3  4  5  6  7  8  9 10 11 12 13
      inputs = [1, 7, 2, 4, 1, 9, 1, 1, 8, 1, 1, 9, 1, 5]
      17241911811915

      17241191811915

      17941911881915


     */

    public static final long[] X_OFF = {14, 11, 12, 11, -10, 15, -14, 10, -4, -3, 13, -3, -9, -12};
    public static final long[] Y_OFF = {16, 3, 2, 7, 13, 6, 10, 11, 6, 5, 11, 4, 4, 6};
    public static final long[] Z_DIV = {1, 1, 1, 1, 26, 1, 26, 1, 26, 26, 1, 26, 26, 26};


    public static void main(String[] args)
    {
        // 554250000000 [9, 9, 4, 4, 5, 7, 4, 9, 9, 9, 9, 9, 9, 9]
        long[] digits = {9, 9, 4, 4, 5, 7, 4, 9, 9, 9, 9, 9, 9, 9};
        int[] skiplist = new int[] {4, 6};

        long count = 0;
        while (true)
        {
            boolean anyZero = false;
            for (long digit : digits) {
                if (digit == 0)
                {
                    anyZero = true;
                    break;
                }
            }
            if (!anyZero)
            {
                long result = TwentyFour.handRolled(
                        digits,
                        X_OFF,
                        Y_OFF,
                        Z_DIV
                );
                if (result == 0)
                {
                    System.out.println(Arrays.toString(digits));
                    break;
                }
            }

            decrement(digits, skiplist);
            count++;
            if (count % 10_000_000 == 0)
            {
                System.out.println(count + " " + Arrays.toString(digits));
            }
        }
    }


    public static long findLargestModelNumberEager(InputStream stream) throws IOException {
        final Alu alu = new Alu();
        final List<Instruction> instructions = parse(stream);

//      17941911881915
        final long biggest = 17241911811915L;
//        final long biggest = 99992129102366L;

        int count = 0;
        while (count < 1) {
            int j = 1;
            alu.reset(Long.toString(biggest - count));
            for (Instruction i : instructions) {
                i.execute(alu);
                System.out.println(j++ + ": " + Arrays.toString(alu.registers));
            }

            System.out.println(count + ", " + alu.registers[3] + ", " + (biggest - count));
            System.out.println();

            count++;
        }

        return 0;
    }

    public static long findSmallestModelNumberEager(InputStream stream) throws IOException {
        final Alu alu = new Alu();
        final List<Instruction> instructions = parse(stream);

//      17941911881915
        final long smallest = 11111111111111L;
//        final long biggest = 99992129102366L;

        int count = 0;
        while (true) {
            long candidate = smallest + count;
            String input = Long.toString(candidate);
            if (input.indexOf('0') != -1) {
                alu.reset(input);
                for (Instruction i : instructions) {
                    i.execute(alu);
                }

                if (alu.registers[3] == 0)
                {
                    System.out.println("Solution: " + smallest + count);
                }

                if (count % 1_000_000 == 0) {
                    System.out.println(count + ", " + alu.registers[3] + ", " + candidate);
                    System.out.println();
                }
            }
            count++;
        }
    }

    public static LazyAlu prepareAlu(InputStream stream) throws IOException {
        final List<Instruction> instructions = parse(stream);

        final LazyAlu alu = new LazyAlu();
        instructions.forEach(i -> i.executeLazy(alu));
        return alu;
    }

    static void decrement(long[] digits, int[] skiplist) {
        for (int i = digits.length; i > 0; i--) {
            // spot when we create a zero then stop
            boolean skipIt = false;
            for (int skip : skiplist) {
                if ((i - 1) == skip) {
                    skipIt = true;
                    break;
                }
            }

            if (!skipIt) {
                --digits[i - 1];
                if (digits[i - 1] >= 1)
                {
                    for (int j = i; j < digits.length; j++) {
                        digits[j] = 9;
                    }
                    return;
                }
            }
        }
        throw new UnsupportedOperationException(Arrays.toString(digits));
    }

    sealed interface RegisterValue permits Compound, Exactly, ReadInput {
        String expr();
        long evaluate(int[] input);
        RegisterValue replace(int inputIndex, int value);
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
        public RegisterValue replace(int inputIndex, int value) {
            if (index == inputIndex)
            {
                return new Exactly(value);
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
        public RegisterValue replace(int inputIndex, int value) {
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
        return new Compound(left, right, operator);
    }

    record Compound(
            RegisterValue left,
            RegisterValue right,
            Operator operator) implements RegisterValue {
        @Override
        public String expr() {
            return "(" + left.expr() + " " + operator.expr() + " " + right.expr() + ")";
        }

        @Override
        public long evaluate(int[] input) {
            return operator.lf.execute(left.evaluate(input), right.evaluate(input));
        }

        @Override
        public RegisterValue replace(int inputIndex, int value) {
            RegisterValue newLeft = left.replace(inputIndex, value);
            RegisterValue newRight = right.replace(inputIndex, value);
            if (newLeft instanceof Exactly e1 && newRight instanceof Exactly e2)
            {
                return new Exactly(operator.execute(e1.value, e2.value));
            }

            Compound compound = new Compound(newLeft, newRight, operator);
            return compound.simplify();
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
                    return left.min() * right.min();
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
//                System.out.println("Unable to simplify:\n\t" + this);
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
                    switch (left) {
                        case Compound c:
                            if (c.operator == Operator.Add) {
                                if (c.left.max() < e.value && c.right instanceof Compound d)
                                {
                                    if (d.left.equals(e))
                                    {
                                        return d.right;
                                    }
                                    if (d.right.equals(e))
                                    {
                                        return d.left;
                                    }
                                }
                                else if (c.right.max() < e.value && c.left instanceof Compound d)
                                {
                                    if (d.left.equals(e))
                                    {
                                        return d.right;
                                    }
                                    if (d.right.equals(e))
                                    {
                                        return d.left;
                                    }
                                }
                            }
                        default:
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
            i.executeLazy(lazyAlu);
        }

//        lazyAlu.expressions[3].printTo(System.out, 1);



        return lazyAlu.expressions;
    }

    sealed interface Instruction permits Add, Div, EqualityTest, Input, Mod, Multiply {
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

    static List<Instruction> parse(InputStream stream) throws IOException {
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
