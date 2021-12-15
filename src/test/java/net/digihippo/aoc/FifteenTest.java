package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FifteenTest {
    @Test
    void examplePartOne() throws IOException {
        assertEquals(40, Fifteen.shortestPath(Inputs.asInputStream("""
                1163751742
                1381373672
                2136511328
                3694931569
                7463417111
                1319128137
                1359912421
                3125421639
                1293138521
                2311944581""")));
    }

    @Test
    void partOne() throws IOException {
        System.out.println(Fifteen.shortestPath(Inputs.puzzleInput("fifteen.txt")));
    }

    @Test
    void examplePartTwo() throws IOException {
        assertEquals(315, Fifteen.shortestPathOfFullGrid(Inputs.asInputStream("""
                1163751742
                1381373672
                2136511328
                3694931569
                7463417111
                1319128137
                1359912421
                3125421639
                1293138521
                2311944581""")));
    }

    @Test
    void partTwo() throws IOException {
        System.out.println(Fifteen.shortestPathOfFullGrid(Inputs.puzzleInput("fifteen.txt")));
    }
}