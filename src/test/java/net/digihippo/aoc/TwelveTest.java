package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TwelveTest {

    private final String inputOne = """
            start-A
            start-b
            A-c
            A-b
            b-d
            A-end
            b-end""";
    private final String inputOneA = """
            dc-end
            HN-start
            start-kj
            dc-start
            dc-HN
            LN-dc
            HN-end
            kj-sa
            kj-HN
            kj-dc""";
    private final String inputOneB = """
            fs-end
            he-DX
            fs-he
            start-DX
            pj-DX
            end-zg
            zg-sl
            zg-pj
            pj-he
            RW-he
            fs-DX
            pj-RW
            zg-RW
            start-pj
            he-WI
            zg-he
            pj-fs
            start-RW""";

    @Test
    void examplePartOne() throws IOException {
        assertEquals(10, Twelve.pathCount(Inputs.asInputStream(inputOne)));
    }

    @Test
    void examplePartOneA() throws IOException {
        assertEquals(19, Twelve.pathCount(Inputs.asInputStream(inputOneA)));
    }

    @Test
    void examplePartOneB() throws IOException {
        assertEquals(226, Twelve.pathCount(Inputs.asInputStream(inputOneB)));
    }

    @Test
    void partOne() throws IOException {
        System.out.println(Twelve.pathCount(Inputs.puzzleInput("twelve.txt")));
    }

    @Test
    void examplePartTwo() throws IOException {
        assertEquals(36, Twelve.pathCountUgh(Inputs.asInputStream(inputOne)));
    }

    @Test
    void examplePartTwoA() throws IOException {
        assertEquals(103, Twelve.pathCountUgh(Inputs.asInputStream(inputOneA)));
    }

    @Test
    void examplePartTwoB() throws IOException {
        assertEquals(3509, Twelve.pathCountUgh(Inputs.asInputStream(inputOneB)));
    }

    @Test
    void partTwo() throws IOException {
        System.out.println(
                Twelve.pathCountUgh(Inputs.puzzleInput("twelve.txt")));
    }
}