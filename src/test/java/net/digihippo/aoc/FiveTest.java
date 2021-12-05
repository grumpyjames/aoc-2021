package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FiveTest
{
    private final String input = """
            0,9 -> 5,9
            8,0 -> 0,8
            9,4 -> 3,4
            2,2 -> 2,1
            7,0 -> 7,4
            6,4 -> 2,0
            0,9 -> 2,9
            3,4 -> 1,4
            0,0 -> 8,8
            5,5 -> 8,2
            """;

    @Test
    void examplePartOne() throws IOException {
        assertEquals(5, Five.countOverlaps(Inputs.asInputStream(input)));
    }

    @Test
    void partOne() throws IOException {
        System.out.println(Five.countOverlaps(Inputs.puzzleInput("five.txt")));
    }

    @Test
    void examplePartTwo() throws IOException {
        assertEquals(12, Five.countAllOverlaps(Inputs.asInputStream(input)));
    }

    @Test
    void partTwo() throws IOException {
        System.out.println(
                Five.countAllOverlaps(Inputs.puzzleInput("five.txt")));
    }
}