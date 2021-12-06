package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SixTest {
    @Test
    void examplePartOne() throws IOException {
        final String exampleInput = "3,4,3,1,2";
        assertEquals(26, Six.compute(18, Inputs.asInputStream(exampleInput)));
    }

    @Test
    void examplePartOneEightyDays() throws IOException {
        final String exampleInput = "3,4,3,1,2";
        assertEquals(5934, Six.compute(80, Inputs.asInputStream(exampleInput)));
    }

    @Test
    void partOne() throws IOException {
        System.out.println(Six.compute(80, Inputs.puzzleInput("six.txt")));
    }

    @Test
    void partTwo() throws IOException {
        System.out.println(Six.compute(256, Inputs.puzzleInput("six.txt")));
    }
}