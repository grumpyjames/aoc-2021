package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static net.digihippo.aoc.TwentyFour.Operator.*;
import static org.junit.jupiter.api.Assertions.*;

class TwentyFourTest {

    @Test
    void multiply() throws IOException {
        final long[] registers = TwentyFour.run(
                Inputs.asInputStream("""
                        inp x
                        mul x -1
                        """),
                "4"
        );
        
        assertEquals(-4L, registers[1]);
    }

    @Test
    void expressions() throws IOException {
        TwentyFour.RegisterValue[] exprs = TwentyFour.expressions(
                Inputs.asInputStream("""
                        inp x
                        mul x -1
                        """)
        );

        assertEquals("{0}*-1", exprs[1].expr());
    }

    @Test
    void equality() throws IOException {
        final String program = """
                inp z
                inp x
                mul z 3
                eql z x
                """;
        long[] registers = TwentyFour.run(
                Inputs.asInputStream(program),
                "26"
        );
        assertEquals(registers[3], 1L);

        registers = TwentyFour.run(
                Inputs.asInputStream(program),
                "39"
        );
        assertEquals(registers[3], 1L);

        registers = TwentyFour.run(
                Inputs.asInputStream(program),
                "23"
        );
        assertEquals(registers[3], 0L);
    }

    @Test
    public void addition() throws IOException {
        final String program = """
                inp z
                add z 4
                """;
        long[] registers = TwentyFour.run(
                Inputs.asInputStream(program),
                "2"
        );
        assertEquals(registers[3], 6);
    }

    @Test
    public void division() throws IOException {
        final String program = """
                inp z
                inp x
                div z x
                """;
        long[] registers = TwentyFour.run(
                Inputs.asInputStream(program),
                "83"
        );
        assertEquals(registers[3], 2);
    }

    @Test
    public void modulo() throws IOException {
        final String program = """
                inp z
                inp x
                mod z x
                """;
        long[] registers = TwentyFour.run(
                Inputs.asInputStream(program),
                "43"
        );
        assertEquals(registers[3], 1);
    }

    @Test
    void bitTwiddling() throws IOException {
        final String input = """
                inp w
                add z w
                mod z 2
                div w 2
                add y w
                mod y 2
                div w 2
                add x w
                mod x 2
                div w 2
                mod w 2""";
        final TwentyFour.LazyAlu lazyAlu = TwentyFour.prepareAlu(Inputs.asInputStream(input));
        final TwentyFour.RegisterValue[] expressions = lazyAlu.expressions;
        for (TwentyFour.RegisterValue expression : expressions) {
            System.out.println(expression.evaluate(new int[] {4}));
        }
    }

    @Test
    void simplifyEquality() {
        final TwentyFour.Compound compound =
                new TwentyFour.Compound(new TwentyFour.ReadInput(1), new TwentyFour.Exactly(14), TwentyFour.Operator.Eq);

        assertEquals(
                new TwentyFour.Exactly(0),
                compound.simplify()
        );

        final TwentyFour.Compound compoundTwo =
                new TwentyFour.Compound(new TwentyFour.Exactly(14), new TwentyFour.ReadInput(1), TwentyFour.Operator.Eq);

        assertEquals(
                new TwentyFour.Exactly(0),
                compoundTwo.simplify()
        );
    }

    @Test
    void simplifyAddition() {
        final TwentyFour.Compound compound =
                new TwentyFour.Compound(new TwentyFour.ReadInput(1), new TwentyFour.Exactly(0), Add);

        assertEquals(
                new TwentyFour.ReadInput(1),
                compound.simplify()
        );

        final TwentyFour.Compound compoundTwo =
                new TwentyFour.Compound(new TwentyFour.Exactly(0), new TwentyFour.ReadInput(1), Add);

        assertEquals(
                new TwentyFour.ReadInput(1),
                compoundTwo.simplify()
        );
    }

    @Test
    void simplifyMultiplicationIdentity() {
        final TwentyFour.Compound compound =
                new TwentyFour.Compound(new TwentyFour.ReadInput(1), new TwentyFour.Exactly(1), TwentyFour.Operator.Mul);

        assertEquals(
                new TwentyFour.ReadInput(1),
                compound.simplify()
        );

        final TwentyFour.Compound compoundTwo =
                new TwentyFour.Compound(new TwentyFour.Exactly(1), new TwentyFour.ReadInput(1), TwentyFour.Operator.Mul);

        assertEquals(
                new TwentyFour.ReadInput(1),
                compoundTwo.simplify()
        );
    }

    @Test
    void simplifyMultiplicationZero() {
        final TwentyFour.Compound compound =
                new TwentyFour.Compound(new TwentyFour.ReadInput(1), new TwentyFour.Exactly(0), TwentyFour.Operator.Mul);

        assertEquals(
                new TwentyFour.Exactly(0),
                compound.simplify()
        );

        final TwentyFour.Compound compoundTwo =
                new TwentyFour.Compound(new TwentyFour.Exactly(0), new TwentyFour.ReadInput(1), TwentyFour.Operator.Mul);

        assertEquals(
                new TwentyFour.Exactly(0),
                compoundTwo.simplify()
        );
    }

    @Test
    void simplifyDiv() {
        final TwentyFour.Compound compound =
                new TwentyFour.Compound(new TwentyFour.ReadInput(1), new TwentyFour.Exactly(1), TwentyFour.Operator.Div);

        assertEquals(
                new TwentyFour.ReadInput(1),
                compound.simplify()
        );
    }

    @Test
    void simplifyCompoundEquals() {
        final TwentyFour.Compound compound =
                new TwentyFour.Compound(
                        new TwentyFour.ReadInput(1),
                        new TwentyFour.ReadInput(2),
                        TwentyFour.Operator.Eq);
        final TwentyFour.Compound eq = new TwentyFour.Compound(
                compound,
                new TwentyFour.Exactly(2),
                TwentyFour.Operator.Eq
        );
        final TwentyFour.Compound eq2 = new TwentyFour.Compound(
                new TwentyFour.Exactly(2),
                compound,
                TwentyFour.Operator.Eq
        );

        assertEquals(
                new TwentyFour.Exactly(0),
                eq.simplify()
        );

        assertEquals(
                new TwentyFour.Exactly(0),
                eq2.simplify()
        );
    }

    @Test
    void simplifyMod() {
        final TwentyFour.Compound compound =
                new TwentyFour.Compound(
                        new TwentyFour.ReadInput(1),
                        new TwentyFour.Exactly(14),
                        Add);
        final TwentyFour.Compound mod =
                new TwentyFour.Compound(
                        compound,
                        new TwentyFour.Exactly(40),
                        Mod
                );
        assertEquals(compound, mod.simplify());
    }

    @Test
    void simplifyCompoundAdds() {
        final TwentyFour.Compound oneAdd =
                new TwentyFour.Compound(
                        new TwentyFour.ReadInput(1),
                        new TwentyFour.Exactly(14),
                        Add);
        final TwentyFour.Compound another =
                new TwentyFour.Compound(
                        new TwentyFour.Exactly(16),
                        oneAdd,
                        Add);
        final TwentyFour.Compound anotherFlipped =
                new TwentyFour.Compound(
                        oneAdd,
                        new TwentyFour.Exactly(16),
                        Add);

        assertEquals(
                new TwentyFour.Compound(
                        new TwentyFour.ReadInput(1),
                        new TwentyFour.Exactly(30),
                        Add),
                another.simplify()
        );

        assertEquals(
                new TwentyFour.Compound(
                        new TwentyFour.ReadInput(1),
                        new TwentyFour.Exactly(30),
                        Add),
                anotherFlipped.simplify()
        );

        final TwentyFour.Compound irreducable =
                new TwentyFour.Compound(
                        oneAdd,
                        new TwentyFour.ReadInput(6),
                        Add);
        assertEquals(irreducable, irreducable.simplify());
    }

    @Test
    void simplifyEqualityWithRanges() {
        final TwentyFour.Compound add =
                new TwentyFour.Compound(
                        new TwentyFour.ReadInput(1),
                        new TwentyFour.Exactly(26),
                        Add);

        final TwentyFour.Compound cmp =
                new TwentyFour.Compound(
                        new TwentyFour.ReadInput(2),
                        add,
                        TwentyFour.Operator.Eq);

        assertEquals(new TwentyFour.Exactly(0), cmp.simplify());
    }

    @Test
    void simplifyDivOfMultiply() {
        final TwentyFour.Compound mul =
                new TwentyFour.Compound(
                        new TwentyFour.Exactly(25),
                        new TwentyFour.Exactly(4),
                        TwentyFour.Operator.Mul);
        final TwentyFour.Compound mul2 =
                new TwentyFour.Compound(
                        new TwentyFour.Exactly(4),
                        new TwentyFour.Exactly(25),
                        TwentyFour.Operator.Mul);

        final TwentyFour.Compound div =
                new TwentyFour.Compound(
                        mul,
                        new TwentyFour.Exactly(4),
                        TwentyFour.Operator.Div);
        final TwentyFour.Compound div2 =
                new TwentyFour.Compound(
                        mul2,
                        new TwentyFour.Exactly(4),
                        TwentyFour.Operator.Div);

        assertEquals(new TwentyFour.Exactly(25), div.simplify());
        assertEquals(new TwentyFour.Exactly(25), div2.simplify());
    }

    @Test
    void simplifyMultiplyWithOneOnLeft()
    {
        // (* (+ {0} 16) 1)
        final TwentyFour.Compound compound = new TwentyFour.Compound(
                new TwentyFour.Exactly(24),
                new TwentyFour.Compound(
                        new TwentyFour.ReadInput(3),
                        new TwentyFour.Exactly(4),
                        Add
                ),
                Mul
        );

        final TwentyFour.Compound compoundTwo = new TwentyFour.Compound(
                new TwentyFour.Compound(
                        new TwentyFour.ReadInput(3),
                        new TwentyFour.Exactly(4),
                        Add
                ),
                new TwentyFour.Exactly(24),
                Mul
        );

        final TwentyFour.Compound compoundThree = new TwentyFour.Compound(
                new TwentyFour.Compound(
                        new TwentyFour.Exactly(4),
                        new TwentyFour.ReadInput(3),
                        Add
                ),
                new TwentyFour.Exactly(24),
                Mul
        );

        final TwentyFour.Compound compoundFour = new TwentyFour.Compound(
                new TwentyFour.Compound(
                        new TwentyFour.ReadInput(3),
                        new TwentyFour.Exactly(4),
                        Add
                ),
                new TwentyFour.Exactly(24),
                Mul
        );


        final TwentyFour.Compound simpler = new TwentyFour.Compound(
                new TwentyFour.Compound(
                        new TwentyFour.Exactly(24),
                        new TwentyFour.ReadInput(3),
                        Mul),
                new TwentyFour.Exactly(24 * 4),
                Add
        );

        assertEquals(simpler, compound.simplify());
        assertEquals(simpler, compoundTwo.simplify());
        assertEquals(simpler, compoundThree.simplify());
        assertEquals(simpler, compoundFour.simplify());
    }

//    @Test
//    void simplifyMultiplyOfDiv() {
//        final TwentyFour.Compound div =
//                new TwentyFour.Compound(
//                        new TwentyFour.ReadInput(4),
//                        new TwentyFour.Exactly(4),
//                        TwentyFour.Operator.Div);
//
//        final TwentyFour.Compound mul =
//                new TwentyFour.Compound(
//                        div,
//                        new TwentyFour.Exactly(4),
//                        TwentyFour.Operator.Mul);
//
//        assertEquals(
//                new TwentyFour.Compound(
//                        new TwentyFour.ReadInput(4),
//                        new TwentyFour.Exactly(4),
//                        TwentyFour.Operator.Rem
//                ),
//                mul.simplify()
//        );
//    }

    // mod plus mul
    @Test
    void deep()
    {
        final TwentyFour.Exactly five = new TwentyFour.Exactly(5);
        final TwentyFour.Compound modOfAddOfMul = new TwentyFour.Compound(
                new TwentyFour.Compound(
                        new TwentyFour.Compound(
                                new TwentyFour.ReadInput(3),
                                five,
                                Mul),
                        new TwentyFour.Exactly(11),
                        Add
                ),
                five,
                Mod
        );
        final TwentyFour.Compound modOfAddOfMul2 = new TwentyFour.Compound(
                new TwentyFour.Compound(
                        new TwentyFour.Compound(
                                five,
                                new TwentyFour.ReadInput(3),
                                Mul),
                        new TwentyFour.Exactly(11),
                        Add
                ),
                five,
                Mod
        );
        final TwentyFour.Compound modOfAddOfMul3 = new TwentyFour.Compound(
                new TwentyFour.Compound(
                        new TwentyFour.Exactly(11),
                        new TwentyFour.Compound(
                                five,
                                new TwentyFour.ReadInput(3),
                                Mul),
                        Add
                ),
                five,
                Mod
        );

        final TwentyFour.Exactly one = new TwentyFour.Exactly(1);
        assertEquals(one, modOfAddOfMul3.simplify());
        assertEquals(one, modOfAddOfMul2.simplify());
        assertEquals(one, modOfAddOfMul.simplify());
    }

    @Test
    void deeper()
    {
        final TwentyFour.Exactly five = new TwentyFour.Exactly(28);
        final TwentyFour.Compound modOfAddOfMul = new TwentyFour.Compound(
                new TwentyFour.Compound(
                        new TwentyFour.Compound(
                                new TwentyFour.ReadInput(3),
                                five,
                                Mul),
                        new TwentyFour.Compound(
                                new TwentyFour.ReadInput(3),
                                new TwentyFour.Exactly(11),
                                Add),
                        Add
                ),
                five,
                Mod
        );

        assertEquals(
                new TwentyFour.Compound(
                        new TwentyFour.ReadInput(3),
                        new TwentyFour.Exactly(11),
                        Add),
                modOfAddOfMul.simplify());
    }

    @Test
    void exploration() throws IOException {
        final TwentyFour.RegisterValue[] expressions = TwentyFour.expressions(
                Inputs.puzzleInput("twentyfour.txt")
        );
        System.out.println(expressions[3].min());
        System.out.println(expressions[3].max());
        expressions[3].printTo(System.out, 1);
    }

    @Test
    void partOne() throws IOException {
        System.out.println(TwentyFour.findLargestModelNumber(Inputs.puzzleInput("twentyfour.txt")));
    }

    @Test
    void partOneEager() throws IOException {
        System.out.println(TwentyFour.findLargestModelNumberEager(Inputs.puzzleInput("twentyfour.txt")));
    }

    @Test
    void handRolled() {
        System.out.println(TwentyFour.handRolled(
                new long[] {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                new long[] {14, 11, 12, 11, -10, 15, -14, 10, -4, -3, 13, -3, -9, -12},
                new long[] {16, 3, 2, 7, 13, 6, 10, 11, 6, 5, 11, 4, 4, 6},
                new long[] {1, 1, 1, 1, 26, 1, 26, 1, 26, 26, 1, 26, 26, 26}
        ));
    }
}