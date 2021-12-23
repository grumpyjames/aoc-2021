package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TwentyThreeTest {
    private final String exampleOne = """
            #############
            #...........#
            ###B#C#B#D###
              #A#D#C#A#
              #########""";

    @Test
    void burrowParse() throws IOException {
        assertEquals(
                new TwentyThree.Burrow(
                        new String[]{"BA", "CD", "BC", "DA"},
                        new char[]{'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'}
                ),
                TwentyThree.parse(Inputs.asInputStream(exampleOne))
        );
    }

    @Test
    void validMovesFromStartPosition() {
        final String[] corridors = new String[] { "BA", "CD", "BC", "DA" };

        final List<TwentyThree.Move> moves = List.of(
                exit(0, 0, 'B', 0, 30),
                exit(0, 0, 'B', 1, 20),
                exit(0, 0, 'B', 3, 20),
                exit(0, 0, 'B', 5, 40),
                exit(0, 0, 'B', 7, 60),
                exit(0, 0, 'B', 9, 80),
                exit(0, 0, 'B', 10, 90),

                exit(1, 0, 'C', 0, 500),
                exit(1, 0, 'C', 1, 400),
                exit(1, 0, 'C', 3, 200),
                exit(1, 0, 'C', 5, 200),
                exit(1, 0, 'C', 7, 400),
                exit(1, 0, 'C', 9, 600),
                exit(1, 0, 'C', 10, 700),

                exit(2, 0, 'B', 0, 70),
                exit(2, 0, 'B', 1, 60),
                exit(2, 0, 'B', 3, 40),
                exit(2, 0, 'B', 5, 20),
                exit(2, 0, 'B', 7, 20),
                exit(2, 0, 'B', 9, 40),
                exit(2, 0, 'B', 10, 50),

                exit(3, 0, 'D', 0, 9000),
                exit(3, 0, 'D', 1, 8000),
                exit(3, 0, 'D', 3, 6000),
                exit(3, 0, 'D', 5, 4000),
                exit(3, 0, 'D', 7, 2000),
                exit(3, 0, 'D', 9, 2000),
                exit(3, 0, 'D', 10, 3000));
        final List<TwentyThree.Move> validMoves = TwentyThree.validMoves(burrow(corridors));

        assertMovesMatch(moves, validMoves);
    }

    @Test
    void validMovesWithNonEmptyCorridor() {
        final String[] corridors = new String[] { "BA", ".D", "BC", "DA" };
        final char[] hallway = ".C.........".toCharArray();

        final List<TwentyThree.Move> moves = List.of(
                exit(0, 0, 'B', 3, 20),
                exit(0, 0, 'B', 5, 40),
                exit(0, 0, 'B', 7, 60),
                exit(0, 0, 'B', 9, 80),
                exit(0, 0, 'B', 10, 90),

                exit(1, 1, 'D', 3, 3000),
                exit(1, 1, 'D', 5, 3000),
                exit(1, 1, 'D', 7, 5000),
                exit(1, 1, 'D', 9, 7000),
                exit(1, 1, 'D', 10, 8000),

                exit(2, 0, 'B', 3, 40),
                exit(2, 0, 'B', 5, 20),
                exit(2, 0, 'B', 7, 20),
                exit(2, 0, 'B', 9, 40),
                exit(2, 0, 'B', 10, 50),

                exit(3, 0, 'D', 3, 6000),
                exit(3, 0, 'D', 5, 4000),
                exit(3, 0, 'D', 7, 2000),
                exit(3, 0, 'D', 9, 2000),
                exit(3, 0, 'D', 10, 3000));


        final List<TwentyThree.Move> validMoves = TwentyThree.validMoves(new TwentyThree.Burrow(corridors, hallway));
        assertMovesMatch(moves, validMoves);
    }

    @Test
    void validMovesIncludeEnteringANonEmptyRoomOfTheCorrectSort() {
        final String[] corridors = new String[] { ".A", "BD", "BC", "DA" };
        final char[] hallway = ".A.........".toCharArray();
        final TwentyThree.Burrow burrow = new TwentyThree.Burrow(corridors, hallway);

        final List<TwentyThree.Move> moves = List.of(entry(1, 'A', 0, 0, 2));

        final List<TwentyThree.Move> validMoves = TwentyThree.validMoves(burrow);

        assertMovesMatch(moves, validMoves);
    }

    @Test
    void validMovesIncludeEnteringAnEmptyRoomOfTheCorrectSortA() {
        final String[] corridors = new String[] { "..", "BB", "CC", "DD" };
        final char[] hallway = "A........A.".toCharArray();
        final TwentyThree.Burrow burrow = new TwentyThree.Burrow(corridors, hallway);

        final List<TwentyThree.Move> moves = List.of(
                entry(0, 'A', 0, 1, 4),
                entry(9, 'A', 0, 1, 9)
        );

        final List<TwentyThree.Move> validMoves = TwentyThree.validMoves(burrow);

        assertMovesMatch(moves, validMoves);
    }

    @Test
    void validMovesIncludeEnteringAnEmptyRoomOfTheCorrectSort() {
        final String[] corridors = new String[] { "AA", "..", "CC", "DD" };
        final char[] hallway = "B........B.".toCharArray();
        final TwentyThree.Burrow burrow = new TwentyThree.Burrow(corridors, hallway);

        final List<TwentyThree.Move> moves = List.of(
                entry(0, 'B', 1, 1, 60),
                entry(9, 'B', 1, 1, 70)
                );

        final List<TwentyThree.Move> validMoves = TwentyThree.validMoves(burrow);

        assertMovesMatch(moves, validMoves);
    }

    @Test
    void enactAnEntry() {
        final String[] corridors = new String[] { "AA", "..", "CC", "DD" };
        final char[] hallway = "B........B.".toCharArray();

        TwentyThree.Burrow b = new TwentyThree.Burrow(corridors, hallway);

        TwentyThree.Burrow newBurrow = TwentyThree.enactMove(b, entry(0, 'B', 1, 1, 60));

        final String[] afterCorridors = new String[] { "AA", ".B", "CC", "DD" };
        final char[] afterHallway = ".........B.".toCharArray();
        TwentyThree.Burrow afterBurrow = new TwentyThree.Burrow(afterCorridors, afterHallway);

        assertEquals(afterBurrow, newBurrow);
    }

    @Test
    void enactAnExit() {
        final String[] corridors = new String[] { "BA", "AB", "CC", "DD" };
        final char[] hallway = "...........".toCharArray();

        TwentyThree.Burrow b = new TwentyThree.Burrow(corridors, hallway);

        TwentyThree.Burrow newBurrow = TwentyThree.enactMove(b, exit(1, 0, 'A', 3, 2));

        final String[] afterCorridors = new String[] { "BA", ".B", "CC", "DD" };
        final char[] afterHallway = "...A.......".toCharArray();
        TwentyThree.Burrow afterBurrow = new TwentyThree.Burrow(afterCorridors, afterHallway);

        assertEquals(afterBurrow, newBurrow);
    }

    @Test
    void noMoreExitsIfThereIsAnEntryAvailable() {
        final String[] corridors = new String[] { ".A", "CB", "BC", "DD" };
        final char[] hallway = ".A..........".toCharArray();

        TwentyThree.Burrow b = new TwentyThree.Burrow(corridors, hallway);
        final List<TwentyThree.Move> moves = List.of(
                entry(1, 'A', 0, 0, 2)
        );

        final List<TwentyThree.Move> validMoves = TwentyThree.validMoves(b);

        assertMovesMatch(moves, validMoves);
    }

    @Test
    void canGoDirectlyFromCorridorToCorridor() {
        final String[] corridors = new String[] { "BA", "CD", ".C", "DA" };
        final char[] hallway = "...B.......".toCharArray();

        TwentyThree.Burrow b = new TwentyThree.Burrow(corridors, hallway);
        final TwentyThree.Switch s = new TwentyThree.Switch(
                exit(1, 0, 'C', 6, 300),
                entry(6, 'C', 2, 0, 100));
        final List<TwentyThree.Move> moves = List.of(
                s
        );

        final List<TwentyThree.Move> validMoves = TwentyThree.validMoves(b);

        assertMovesMatch(moves, validMoves);

        final TwentyThree.Burrow moved = TwentyThree.enactMove(b, s);
        assertEquals(
                new TwentyThree.Burrow(new String[] { "BA", ".D", "CC", "DA" }, "...B.......".toCharArray()),
                moved
        );
    }

    @Test
    void canGoDirectlyFromCorridorToCorridorMarkTwo() {
        final String[] corridors = new String[] { "BA", "CD", "..", "DA" };
        final char[] hallway = "...B......C".toCharArray();

        TwentyThree.Burrow b = new TwentyThree.Burrow(corridors, hallway);
        final List<TwentyThree.Move> moves = List.of(
                new TwentyThree.Entry(
                        10, 'C', 2, 1, 600
                ),
                new TwentyThree.Switch(
                        exit(1, 0, 'C', 6, 300),
                        entry(6, 'C', 2, 1, 200))
        );

        final List<TwentyThree.Move> validMoves = TwentyThree.validMoves(b);

        assertMovesMatch(moves, validMoves);
    }

    @Test
    void anotherMoveVariant() throws IOException {
        final TwentyThree.Burrow burrow = TwentyThree.parse(Inputs.asInputStream("""
                #############
                #...B.......#
                ###B#.#C#D###
                  #A#D#C#A#
                  #########"""));
        final List<TwentyThree.Move> moves = TwentyThree.validMoves(burrow);

        assertTrue(
                moves.contains(new TwentyThree.Exit(1, 1, 'D', 5, 3000))
        );
    }

    @Test
    void yetAnotherMoveVariant() throws IOException {
        final TwentyThree.Burrow burrow = TwentyThree.parse(Inputs.asInputStream("""
                #############
                #...B.D.....#
                ###B#.#C#D###
                  #A#.#C#A#
                  #########"""));
        final List<TwentyThree.Move> moves = TwentyThree.validMoves(burrow);

        assertTrue(
                moves.contains(new TwentyThree.Entry(3, 'B', 1, 1, 30))
        );
    }

    @Test
    void oneMoreMoveVariant() throws IOException {
        final String input = """
                #############
                #.....D.D...#
                ###.#B#C#.###
                  #A#B#C#A#
                  #########""";

        final TwentyThree.Burrow burrow = TwentyThree.parse(Inputs.asInputStream(input));
        final List<TwentyThree.Move> moves = TwentyThree.validMoves(burrow);

        assertTrue(
                moves.contains(new TwentyThree.Exit(3, 1, 'A', 9, 3))
        );
    }

    @Test
    void partOneExample() throws IOException {
        assertEquals(
                12521,
                TwentyThree.minimumCost(Inputs.asInputStream(exampleOne)));
    }

    @Test
    void partOne() throws IOException {
        System.out.println(TwentyThree.minimumCost(Inputs.puzzleInput("twentythree.txt")));
    }

    @Test
    void unfold() throws IOException {
        final String input = """
                #############
                #.....D.D...#
                ###.#B#C#.###
                  #A#B#C#A#
                  #########""";
        TwentyThree.Burrow b = TwentyThree.parse(Inputs.asInputStream(input))
                .unfold(new String[]{ "DD", "CB", "BA", "AC" });

        System.out.println(b);
    }

    @Test
    void examplePartTwo() throws IOException {
        assertEquals(
                12521,
                TwentyThree.minimumCostUnfolded(Inputs.asInputStream(exampleOne)));
    }

    @Test
    void moreMoveChecks() throws IOException {
        final TwentyThree.Burrow burrow = TwentyThree.extendedParse(Inputs.asInputStream("""
                #############
                #AA.....B.BD#
                ###B#.#.#.###
                  #D#C#.#.#
                  #D#B#C#C#
                  #A#D#C#A#
                  #########"""));

        final List<TwentyThree.Move> moves = TwentyThree.validMoves(burrow);

        assertTrue(moves.contains(
                new TwentyThree.Switch(
                        new TwentyThree.Exit(1, 0, 'C', 6, 400),
                        new TwentyThree.Entry(6, 'C', 2, 1, 200)
                )
        ));
    }

    @Test
    void yetMoreMoveChecks() throws IOException {
        final TwentyThree.Burrow burrow = TwentyThree.extendedParse(Inputs.asInputStream("""
                #############
                #AA.....B.BD#
                ###B#.#.#.###
                  #D#.#C#.#
                  #D#B#C#C#
                  #A#D#C#A#
                  #########"""));

        final List<TwentyThree.Move> moves = TwentyThree.validMoves(burrow);

        final List<TwentyThree.Move> expected = List.of(
                new TwentyThree.Exit(0, 0, 'B', 3, 20),
                new TwentyThree.Exit(0, 0, 'B', 5, 40),
                new TwentyThree.Exit(1, 2, 'B', 3, 40),
                new TwentyThree.Exit(1, 2, 'B', 5, 40)
        );

        assertMovesMatch(expected, moves);
    }

    @Test
    void yetMoreYetMoreChecks() throws IOException {
        final TwentyThree.Burrow burrow = TwentyThree.extendedParse(Inputs.asInputStream("""
                #############
                #AA...B.B.BD#
                ###B#.#.#.###
                  #D#.#C#.#
                  #D#.#C#C#
                  #A#D#C#A#
                  #########"""));

        final List<TwentyThree.Move> moves = TwentyThree.validMoves(burrow);

        final List<TwentyThree.Move> expected = List.of(
                new TwentyThree.Exit(0, 0, 'B', 3, 20),
                new TwentyThree.Exit(1, 3, 'D', 3, 5000)
        );

        assertMovesMatch(expected, moves);
    }

    @Test
    void andMore() throws IOException {
        final TwentyThree.Burrow burrow = TwentyThree.extendedParse(Inputs.asInputStream("""
                #############
                #AA.D.B.B.BD#
                ###B#.#.#.###
                  #D#.#C#.#
                  #D#.#C#C#
                  #A#.#C#A#
                  #########"""));

        final List<TwentyThree.Move> moves = TwentyThree.validMoves(burrow);

        final List<TwentyThree.Move> expected = List.of(
                new TwentyThree.Entry(5, 'B', 1, 3, 50)
        );

        assertMovesMatch(expected, moves);
    }

    @Test
    void andStillMore() throws IOException {
        final TwentyThree.Burrow burrow = TwentyThree.extendedParse(Inputs.asInputStream("""
                #############
                #...........#
                ###B#C#B#D###
                  #D#C#B#A#
                  #D#B#A#C#
                  #A#D#C#A#
                  #########"""));

        final List<TwentyThree.Move> moves = TwentyThree.validMoves(burrow);

        moves.forEach(System.out::println);
//        final List<TwentyThree.Move> expected = List.of(
//                new TwentyThree.Entry(7, 'B', 1, 2, 60)
//        );
//
//        assertMovesMatch(expected, moves);
    }

    private TwentyThree.Entry entry(int fromHallwayIndex, char pod, int toRoomIndex, int roomSlot, long cost) {
        return new TwentyThree.Entry(fromHallwayIndex, pod, toRoomIndex, roomSlot, cost);
    }

    private TwentyThree.Burrow burrow(String[] corridors) {
        return TwentyThree.emptyHalls(corridors);
    }

    private TwentyThree.Exit exit(int roomIndex, int roomSlot, char pod, int whereTo, long cost)
    {
        return new TwentyThree.Exit(roomIndex, roomSlot, pod, whereTo, cost);
    }

    private void assertMovesMatch(List<TwentyThree.Move> moves, List<TwentyThree.Move> validMoves) {
        assertEquals(moves.size(), validMoves.size());
        for (int i = 0; i < moves.size(); i++) {
            TwentyThree.Move move = moves.get(i);
            assertEquals(move, validMoves.get(i), "Mismatch at index " + i);
        }
    }
}