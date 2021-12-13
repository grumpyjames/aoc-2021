package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ThirteenTest {

    private final String exampleInput = """
            6,10
            0,14
            9,10
            0,3
            10,4
            4,11
            6,0
            6,12
            4,1
            0,13
            10,12
            3,4
            3,0
            8,4
            1,10
            2,14
            8,10
            9,0
                            
            fold along y=7
            fold along x=5""";

    @Test
    void examplePartOne() throws IOException {
        assertEquals(17, Thirteen.visibleDots(1, Inputs.asInputStream(exampleInput)));
    }

    @Test
    void partOne() throws IOException {
        System.out.println(Thirteen.visibleDots(1, Inputs.puzzleInput("thirteen.txt")));
    }

    @Test
    void examplePartTwo() throws IOException {
        Thirteen.applyAllFolds(Inputs.asInputStream(exampleInput), System.out);
    }

    @Test
    void partTwo() throws IOException {
        Thirteen.applyAllFolds(Inputs.puzzleInput("thirteen.txt"), System.out);
    }
}