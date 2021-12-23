package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TwentyThreeTest {

    @Test
    void burrowParse() throws IOException {

        final String amphipodes = """
                #############
                #...........#
                ###B#C#B#D###
                  #A#D#C#A#
                  #########""";


        assertEquals(
                new TwentyThree.Burrow(
                        new String[]{"BA", "CD", "BC", "DA"},
                        new char[]{'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'}
                ),
                TwentyThree.parse(Inputs.asInputStream(amphipodes))
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

        final List<TwentyThree.Move> moves = List.of(
                entry(1, 'A', 0, 0, 2),

                exit(0, 1, 'A', 3, 3),
                exit(0, 1, 'A', 5, 5),
                exit(0, 1, 'A', 7, 7),
                exit(0, 1, 'A', 9, 9),
                exit(0, 1, 'A', 10, 10),

                exit(1, 0, 'B', 3, 20),
                exit(1, 0, 'B', 5, 20),
                exit(1, 0, 'B', 7, 40),
                exit(1, 0, 'B', 9, 60),
                exit(1, 0, 'B', 10, 70),

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

    private TwentyThree.Entry entry(int fromHallwayIndex, char pod, int toRoomIndex, int roomSlot, long cost) {
        return new TwentyThree.Entry(fromHallwayIndex, pod, toRoomIndex, roomSlot, cost);
    }

    private TwentyThree.Burrow burrow(String[] corridors) {
        return TwentyThree.emptyHalls(corridors);
    }

    private TwentyThree.Move exit(int roomIndex, int roomSlot, char pod, int whereTo, long cost)
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