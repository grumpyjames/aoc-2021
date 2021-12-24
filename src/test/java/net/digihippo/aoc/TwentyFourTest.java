package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

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
        String[] exprs = TwentyFour.expressions(
                Inputs.asInputStream("""
                        inp x
                        mul x -1
                        """)
        );

        assertEquals("({0}) * (-1)", exprs[1]);
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
    void exploration() throws IOException {
        TwentyFour.expressions(
                Inputs.puzzleInput("twentyfour.txt")
        );
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
                new TwentyFour.Compound(new TwentyFour.ReadInput(1), new TwentyFour.Exactly(0), TwentyFour.Operator.Add);

        assertEquals(
                new TwentyFour.ReadInput(1),
                compound.simplify()
        );

        final TwentyFour.Compound compoundTwo =
                new TwentyFour.Compound(new TwentyFour.Exactly(0), new TwentyFour.ReadInput(1), TwentyFour.Operator.Add);

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
                        TwentyFour.Operator.Add);
        final TwentyFour.Compound mod =
                new TwentyFour.Compound(
                        compound,
                        new TwentyFour.Exactly(40),
                        TwentyFour.Operator.Mod
                );
        assertEquals(compound, mod.simplify());
    }

    @Test
    void simplifyCompoundAdds() {
        final TwentyFour.Compound oneAdd =
                new TwentyFour.Compound(
                        new TwentyFour.ReadInput(1),
                        new TwentyFour.Exactly(14),
                        TwentyFour.Operator.Add);
        final TwentyFour.Compound another =
                new TwentyFour.Compound(
                        new TwentyFour.ReadInput(16),
                        oneAdd,
                        TwentyFour.Operator.Add);
        assertEquals(
                new TwentyFour.Compound(
                        new TwentyFour.ReadInput(1),
                        new TwentyFour.Exactly(14),
                        TwentyFour.Operator.Add),
                another.simplify()
        );
    }

    @Test
    void partOne() throws IOException {
//        System.out.println(TwentyFour.findLargestModelNumber(Inputs.puzzleInput("twentyfour.txt")));
    }
}