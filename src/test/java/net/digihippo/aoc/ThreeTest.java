package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class ThreeTest {

    private final String testInput = """
            00100
            11110
            10110
            10111
            10101
            01111
            00111
            11100
            10000
            11001
            00010
            01010
            """;

    @Test
    void name() throws IOException {
        assertEquals(198, Three.compute(Inputs.asInputStream(testInput)));
    }

    @Test
    void realPartOne() throws IOException {
        System.out.println(Three.compute(puzzleInput()));
    }

    private InputStream puzzleInput() {
        return getClass().getResourceAsStream("/three.txt");
    }

    @Test
    void partTwo() throws IOException {
        assertEquals(230, Three.computePartTwo(Inputs.asInputStream(testInput)));
    }

    @Test
    void partTwoReal() throws IOException {
        System.out.println(Three.computePartTwo(puzzleInput()));
    }
}