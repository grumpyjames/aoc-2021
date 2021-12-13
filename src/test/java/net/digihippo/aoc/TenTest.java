package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TenTest {

    private final String input = """
            [({(<(())[]>[[{[]{<()<>>
            [(()[<>])]({[<{<<[]>>(
            {([(<{}[<>[]}>{[]{[(<()>
            (((({<>}<{<{<>}{[]{[]{}
            [[<[([]))<([[{}[[()]]]
            [{[{({}]{}}([{[{{{}}([]
            {<[[]]>}<{[{[{[]{()[[[]
            [<(<(<(<{}))><([]([]()
            <{([([[(<>()){}]>(<<{{
            <{([{{}}[<[[[<>{}]]]>[]]""";

    @Test
    public void exampleOne() throws IOException {
        assertEquals(26397, Ten.syntaxErrorScore(Inputs.asInputStream(input)));
    }

    @Test
    void partOne() throws IOException {
        System.out.println(Ten.syntaxErrorScore(Inputs.puzzleInput("ten.txt")));
    }

    @Test
    void examplePartTwo() throws IOException {
        assertEquals(288957, Ten.middleScore(Inputs.asInputStream(input)));
    }

    @Test
    void partTwo() throws IOException {
        System.out.println(Ten.middleScore(Inputs.puzzleInput("ten.txt")));
    }
}
