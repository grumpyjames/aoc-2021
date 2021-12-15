package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FourteenTest {

    private final String exampleInput = """
            NNCB
                            
            CH -> B
            HH -> N
            CB -> H
            NH -> C
            HB -> C
            HC -> B
            HN -> C
            NN -> C
            BH -> H
            NC -> B
            NB -> B
            BN -> B
            BB -> N
            BC -> B
            CC -> N
            CN -> C
            """;

    @Test
    void examplePartOne() throws IOException {
        assertEquals(1588,
                Fourteen.applyPolymer(10, Inputs.asInputStream(exampleInput)));
    }

    @Test
    void partOne() throws IOException {
        System.out.println(Fourteen.applyPolymer(10,
                Inputs.puzzleInput("fourteen.txt")));
    }

    @Test
    void examplePartTwo() throws IOException {
        assertEquals(2188189693529L,
                Fourteen.applyPolymer(40, Inputs.asInputStream(exampleInput)));
    }

    @Test
    void partTwo() throws IOException {
        System.out.println(Fourteen.applyPolymer(40,
                Inputs.puzzleInput("fourteen.txt")));
    }
}