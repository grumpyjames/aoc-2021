package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ElevenTest {

    private final String exampleInput = """
            5483143223
            2745854711
            5264556173
            6141336146
            6357385478
            4167524645
            2176841721
            6882881134
            4846848554
            5283751526""";

    @Test
    void toyExample() throws IOException {
        final String input = """
                11111
                19991
                19191
                19991
                11111
                """;
        assertEquals(
                9,
                Eleven.flashCount(1, Inputs.asInputStream(input)));
    }

    @Test
    void examplePartOne() throws IOException {
        assertEquals(204, Eleven.flashCount(10, Inputs.asInputStream(exampleInput)));
        assertEquals(1656, Eleven.flashCount(100, Inputs.asInputStream(exampleInput)));
    }

    @Test
    void partOne() throws IOException {
        System.out.println(Eleven.flashCount(100, Inputs.puzzleInput("eleven.txt")));
    }

    @Test
    void examplePartTwo() throws IOException {
        assertEquals(
                195,
                Eleven.allFlashStep(Inputs.asInputStream(exampleInput)));
    }

    @Test
    void partTwo() throws IOException {
        System.out.println(Eleven.allFlashStep(Inputs.puzzleInput("eleven.txt")));
    }
}