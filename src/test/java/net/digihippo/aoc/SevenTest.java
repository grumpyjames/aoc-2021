package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SevenTest {
    private final String example = "16,1,2,0,4,2,7,1,2,14";

    @Test
    void examplePartOne() throws IOException {
        assertEquals(37, Seven.fuelCost(Inputs.asInputStream(example)));
    }

    @Test
    void partOne() throws IOException {
        System.out.println(Seven.fuelCost(Inputs.puzzleInput("seven.txt")));
    }

    @Test
    void examplePartTwo() throws IOException {
        assertEquals(168, Seven.fuelCostTwo(Inputs.asInputStream(example)));
    }

    @Test
    void partTwo() throws IOException {
        System.out.println(Seven.fuelCostTwo(Inputs.puzzleInput("seven.txt")));
    }
}