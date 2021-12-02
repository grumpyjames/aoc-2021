package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class OneTest
{
    @Test
    void exampleInput() throws IOException
    {
        final String input =
"""
199
200
208
210
200
207
240
269
260
263
""";
        assertEquals(7, One.countIncreases(Inputs.asInputStream(input)));
    }

    @Test
    void realInput() throws IOException
    {
        System.out.println(One.countIncreases(this.getClass().getResourceAsStream("/one.txt")));
    }

    @Test
    void partTwo() throws IOException
    {
        final String input =
"""
199
200
208
210
200
207
240
269
260
263
""";
        assertEquals(5, One.countSumIncreases(Inputs.asInputStream(input)));
    }

    @Test
    void partTwoForRealz() throws IOException {
         System.out.println(One.countSumIncreases(this.getClass().getResourceAsStream("/one.txt")));
    }

}