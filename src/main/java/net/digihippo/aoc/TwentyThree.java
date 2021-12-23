package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TwentyThree {
    public static Burrow emptyHalls(String[] corridors) {
        final char[] hallway = new char[11];
        Arrays.fill(hallway, '.');
        return new Burrow(corridors, hallway);
    }

    public static Burrow parse(InputStream stream) throws IOException {
        return Lines.parseLines(stream, new BurrowParser());
    }

    sealed interface Move permits Exit, Entry {}

    record Entry(int fromHallwayIndex, char pod, int toRoomIndex, int roomSlot, long cost) implements Move {}
    record Exit(int roomIndex, int roomSlot, char pod, int whereTo, long cost) implements Move {}

    private static final long[] COSTS = new long[]{1, 10, 100, 1000};
    private static final int[] VALID_HALLWAY_POSITIONS = new int[] {0, 1, 3, 5, 7, 9, 10};
    private static final int[] HALLWAY_ADJACENTS = new int[] {2, 4, 6, 8};

    record Burrow(String[] corridors, char[] hallway) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Burrow burrow = (Burrow) o;
            return Arrays.equals(corridors, burrow.corridors) && Arrays.equals(hallway, burrow.hallway);
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(corridors);
            result = 31 * result + Arrays.hashCode(hallway);
            return result;
        }

        @Override
        public String toString() {
            return "Burrow{" +
                    "corridors=" + Arrays.toString(corridors) +
                    ", hallway=" + new String(hallway) +
                    '}';
        }
    }

    static boolean reachable(char[] hallway, int toIndex, int fromIndex)
    {
        final int low = Math.min(fromIndex, toIndex);
        final int high = Math.max(fromIndex, toIndex);

        for (int i = low; i <= high; i++) {
            char c = hallway[i];
            if (i != fromIndex && c != '.')
            {
                return false;
            }
        }

        return true;
    }

    public static List<Move> validMoves(Burrow burrow) {
        final String[] corridors = burrow.corridors;

        final List<Move> moves = new ArrayList<>();

        for (int i = 0; i < burrow.hallway.length; i++) {
            char c = burrow.hallway[i];

            if (c != '.')
            {
                addEntryMoves(burrow, i, c, c - 'A', moves);
            }
        }

        for (char i = 0; i < corridors.length; i++) {
            String corridor = corridors[i];
            char bottom = corridor.charAt(1);
            char top = corridor.charAt(0);
            //noinspection StatementWithEmptyBody
            if (bottom == top && bottom == ('A' + i)) {
                // already in the right state, don't move it!
            }
            else if (top != '.') {
                long moveCost = COSTS[top - 'A'];
                for (int validHallwayPosition : VALID_HALLWAY_POSITIONS) {
                    final int exitPoint = HALLWAY_ADJACENTS[i];
                    if (reachable(burrow.hallway, validHallwayPosition, exitPoint)) {
                        moves.add(new Exit(
                                i,
                                0,
                                top,
                                validHallwayPosition,
                                moveCost + (Math.abs(exitPoint - validHallwayPosition) * moveCost)
                        ));
                    }
                }
            }
            else if (bottom != '.') {
                long moveCost = COSTS[bottom - 'A'];
                for (int validHallwayPosition : VALID_HALLWAY_POSITIONS) {
                    final int exitPoint = HALLWAY_ADJACENTS[i];
                    if (reachable(burrow.hallway, validHallwayPosition, exitPoint)) {
                        moves.add(new Exit(
                                i,
                                1,
                                bottom,
                                validHallwayPosition,
                                (2 * moveCost) + (Math.abs(exitPoint - validHallwayPosition) * moveCost)
                        ));
                    }
                }
            }
        }
        return moves;
    }

    private static void addEntryMoves(Burrow burrow, int i, char c, int index, List<Move> moves) {

        if (reachable(burrow.hallway, 3, i)) {
            long moveCost = COSTS[index];
            int roomOffset = HALLWAY_ADJACENTS[index];
            final String corridor = burrow.corridors[index];

            int hMove = Math.abs(roomOffset - i);

            if (corridor.charAt(1) == '.') {
                moves.add(new Entry(i, c, index, 1, (2 + hMove) * moveCost));
            } else if (corridor.charAt(0) == '.') {
                moves.add(new Entry(i, c, index, 0, (1 + hMove) * moveCost));
            }
        }
    }

    private static class BurrowParser implements Lines.Parser<Burrow> {
        int count = 0;
        String[] corridors = new String[4];

        @Override
        public void onLine(String string) {
            if (count == 2)
            {
                for (int i = 0; i < corridors.length; i++) {
                    corridors[i] = string.substring(3 + (2 * i), 3 + (2 * i) + 1);
                }
            }

            if (count == 3)
            {
                for (int i = 0; i < corridors.length; i++) {
                    corridors[i] += string.substring(3 + (2 * i), 3 + (2 * i) + 1);
                }
            }

            ++count;
        }

        @Override
        public Burrow build() {
            return emptyHalls(corridors);
        }
    }
}
