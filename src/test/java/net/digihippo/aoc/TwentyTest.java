package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TwentyTest {
    @Test
    void examplePartOne() throws IOException {
        final String example = """
                ..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..##
                #..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###
                .######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#.
                .#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#.....
                .#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#..
                ...####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.....
                ..##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#
                                
                #..#.
                #....
                ##..#
                ..#..
                ..###""";
        assertEquals(35, Twenty.litPixels(2, Inputs.asInputStream(example)));
    }

    @Test
    void examplePartOneTweakedAlgo() throws IOException {
        final String example = """
                #.#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..##
                #..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###
                .######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#.
                .#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#.....
                .#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#..
                ...####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.....
                ..##..####..#...#.#.#...##..#.#..###..#####........#..####......#...
                                
                #..#.
                #....
                ##..#
                ..#..
                ..###""";
        assertEquals(35, Twenty.litPixels(2, Inputs.asInputStream(example)));
    }

    @Test
    void partOne() throws IOException {
        // 5037 : too high.
        // 4928 tick
        System.out.println(Twenty.litPixels(2, Inputs.puzzleInput("twenty.txt")));
    }

    @Test
    void partTwo() throws IOException {
        System.out.println(Twenty.litPixels(50, Inputs.puzzleInput("twenty.txt")));
    }
}