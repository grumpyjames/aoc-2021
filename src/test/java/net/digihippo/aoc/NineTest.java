package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class NineTest {

    private final String exampleInput = """
            2199943210
            3987894921
            9856789892
            8767896789
            9899965678""";

    @Test
    void exampleOne() throws IOException {
        assertEquals(15, Nine.riskLevel(Inputs.asInputStream(exampleInput)));
    }

    @Test
    void partOne() throws IOException {
        System.out.println(Nine.riskLevel(Inputs.puzzleInput("nine.txt")));
    }

    @Test
    void exampleTwo() throws IOException {
        assertEquals(1134, Nine.basinSize(Inputs.asInputStream(exampleInput)));
    }

    @Test
    void partTwo() throws IOException {
        System.out.println(Nine.basinSize(Inputs.puzzleInput("nine.txt")));
    }
}