package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static net.digihippo.aoc.Permutations.permutationsPlease;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EightTest
{
    private final String exampleInput = """
            be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
            edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
            fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
            fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
            aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
            fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
            dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
            bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
            egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
            gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce""";

    @Test
    void partOneExample() throws IOException {
        assertEquals(26, Eight.what(Inputs.asInputStream(exampleInput)));
    }

    @Test
    void permutations()
    {
        final int[][] expected = {new int[]{0, 1}, new int[]{1, 0}};
        final int[][] actual = permutationsPlease(new int[]{0, 1});

        assertArrayEquals(
                expected,
                actual);
    }

    @Test
    void partOne() throws IOException {
        System.out.println(Eight.what(Inputs.puzzleInput("eight.txt")));
    }

    @Test
    void partTwoOneExample() throws IOException {
        assertEquals(
                5353,
                Eight.solve(Inputs.asInputStream("acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab |cdfeb fcadb cdfeb cdbaf")));
    }

    @Test
    void partTwoExample() throws IOException {
        assertEquals(61229, Eight.solve(Inputs.asInputStream(exampleInput)));
    }

    @Test
    void partTwo() throws IOException {
        System.out.println(Eight.solve(Inputs.puzzleInput("eight.txt")));
    }
}