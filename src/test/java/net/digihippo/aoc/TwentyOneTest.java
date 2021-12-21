package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TwentyOneTest {
    @Test
    void partOneExample() throws IOException {
        final String input = """
                Player 1 starting position: 4
                Player 2 starting position: 8""";
        assertEquals(739785, TwentyOne.partOne(Inputs.asInputStream(input)));
    }

    @Test
    void partOne() throws IOException {
        System.out.println(TwentyOne.partOne(Inputs.puzzleInput("twentyone.txt")));
    }

    @Test
    void partTwoExample() throws IOException {
        final String input = """
                Player 1 starting position: 4
                Player 2 starting position: 8""";
        assertEquals(444356092776315L, TwentyOne.partTwo(Inputs.asInputStream(input)));
    }
}