package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class TwoTest
{

    private final String testInput = """
    forward 5
    down 5
    forward 8
    up 3
    down 8
    forward 2
    """;

    @Test
    void exampleInput() throws IOException {
        assertEquals(150, Two.execute(Inputs.asInputStream(testInput)));
    }

    @Test
    void realInput() throws IOException {
        System.out.println(Two.execute(puzzleInput()));
    }

    @Test
    void partTwo() throws IOException {
        assertEquals(900, Two.executeTwo(Inputs.asInputStream(testInput)));
    }

    @Test
    void partTwoForReal() throws IOException {
        System.out.println(Two.executeTwo(puzzleInput()));
    }

    private InputStream puzzleInput() {
        return getClass().getResourceAsStream("/two.txt");
    }
}