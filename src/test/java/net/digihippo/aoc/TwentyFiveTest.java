package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TwentyFiveTest {
    @Test
    void horizontal() throws IOException {
        TwentyFive.Seabed bed = TwentyFive.parse(Inputs.asInputStream("...>>>>>..."));

        bed.move();

        assertEquals(
                "...>>>>.>..\n",
                bed.asString()
        );
    }

    @Test
    void smallerExample() throws IOException {
        final String input = """
                ..........
                .>v....v..
                .......>..
                ..........""";
        TwentyFive.Seabed bed = TwentyFive.parse(Inputs.asInputStream(input));

        bed.move();

        assertEquals(
                """
                        ..........
                        .>........
                        ..v....v>.
                        ..........
                        """,
                bed.asString());
    }

    @Test
    void yetMoreExamples() throws IOException {
        TwentyFive.Seabed bed = TwentyFive.parse(Inputs.asInputStream("""
                ...>...
                .......
                ......>
                v.....>
                ......>
                .......
                ..vvv.."""));

        bed.move();

        assertEquals(
                """
                        ..vv>..
                        .......
                        >......
                        v.....>
                        >......
                        .......
                        ....v..
                        """,
                bed.asString());

        bed.move();

        assertEquals(
                """
                        ....v>.
                        ..vv...
                        .>.....
                        ......>
                        v>.....
                        .......
                        .......
                        """,
                bed.asString());

        bed.move();

        assertEquals(
                """
                        ......>
                        ..v.v..
                        ..>v...
                        >......
                        ..>....
                        v......
                        .......
                        """,
                bed.asString());

        bed.move();

        assertEquals(
                """
                        >......
                        ..v....
                        ..>.v..
                        .>.v...
                        ...>...
                        .......
                        v......
                        """,
                bed.asString()
        );
    }

    @Test
    void partOneExample() throws IOException {
        final String input = """
                v...>>.vv>
                .vv>>.vv..
                >>.>v>...v
                >>v>>.>.v.
                v>v.vv.v..
                >.>>..v...
                .vv..>.>v.
                v.v..>>v.v
                ....v..v.>""";
        assertEquals(58, TwentyFive.stepCount(Inputs.asInputStream(input)));
    }

    @Test
    void partOne() throws IOException {
        System.out.println(TwentyFive.stepCount(Inputs.puzzleInput("twentyfive.txt")));
    }
}