package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SeventeenTest {
    @Test
    void examplePartOne() throws IOException {
        assertEquals(45, Seventeen.maxHeight(Inputs.asInputStream("""
                target area: x=20..30, y=-10..-5""")));
    }

    @Test
    void partOne() throws IOException {
        System.out.println(Seventeen.maxHeight(Inputs.puzzleInput("seventeen.txt")));
    }

    @Test
    void bounds() {
        assertEquals(
                new Seventeen.Bounds(new Seventeen.Bound(6, 30), new Seventeen.Bound(-10, 9)),
                Seventeen.bounds(20, 30, -10)
                );
    }

    @Test
    void examplePartTwo() throws IOException {
        assertEquals(
                112,
                Seventeen.partTwo(Inputs.asInputStream("target area: x=20..30, y=-10..-5"))
        );
    }

    @Test
    void partTwo() throws IOException {
        System.out.println(Seventeen.partTwo(Inputs.puzzleInput("seventeen.txt")));
    }
}