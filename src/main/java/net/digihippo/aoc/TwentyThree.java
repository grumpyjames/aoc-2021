package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class TwentyThree {
    public static Burrow emptyHalls(String[] corridors) {
        final char[] hallway = new char[11];
        Arrays.fill(hallway, '.');
        return new Burrow(corridors, hallway);
    }

    public static Burrow parse(InputStream stream) throws IOException {
        return Lines.parseLines(stream, new BurrowParser());
    }

    public static Burrow enactMove(Burrow b, Move move) {
        String[] corridors = new String[4];

        char[] hallway = new char[11];
        System.arraycopy(b.hallway, 0, hallway, 0, b.hallway.length);

        switch (move) {
            case Switch s -> {
                enactExit(b, corridors, hallway, s.exit);
                hallway[s.entry.fromHallwayIndex] = '.';
                for (int i = 0; i < corridors.length; i++) {
                    if (i == s.entry.toRoomIndex) {
                        final char[] chars = b.corridors[i].toCharArray();
                        chars[s.entry.roomSlot] = s.entry.pod;
                        corridors[i] = new String(chars);
                    }
                }
            }
            case Entry entry -> enactEntry(b, corridors, hallway, entry);
            case Exit exit -> enactExit(b, corridors, hallway, exit);
        }

        return new Burrow(corridors, hallway);
    }

    private static void enactExit(Burrow b, String[] corridors, char[] hallway, Exit exit) {
        hallway[exit.whereTo] = exit.pod;
        for (int i = 0; i < corridors.length; i++) {
            if (i == exit.roomIndex) {
                final char[] chars = b.corridors[i].toCharArray();
                chars[exit.roomSlot] = '.';
                corridors[i] = new String(chars);
            } else {
                corridors[i] = b.corridors[i];
            }
        }
    }

    private static void enactEntry(Burrow b, String[] corridors, char[] hallway, Entry entry) {
        hallway[entry.fromHallwayIndex] = '.';
        for (int i = 0; i < corridors.length; i++) {
            if (i == entry.toRoomIndex) {
                final char[] chars = b.corridors[i].toCharArray();
                chars[entry.roomSlot] = entry.pod;
                corridors[i] = new String(chars);
            } else {
                corridors[i] = b.corridors[i];
            }
        }
    }

    public static long minimumCost(InputStream stream) throws IOException {
        final Burrow burrow = Lines.parseLines(stream, new BurrowParser());

        final TreeMap<Long, Set<Burrow>> frontier = new TreeMap<>();
        final Set<Burrow> visited = new HashSet<>();
        final Map<Burrow, Long> costs = new HashMap<>();

        final Burrow finito = emptyHalls(new String[]{"AA", "BB", "CC", "DD"});

        final List<Move> moves = validMoves(burrow);
        for (Move move : moves) {
            final Burrow value = enactMove(burrow, move);
            frontier.computeIfAbsent(move.cost(), k -> new HashSet<>()).add(value);
            costs.put(value, move.cost());
        }

        Optional<Map.Entry<Long, Set<Burrow>>> maybeEntry = Optional.of(frontier.firstEntry());
        int routesExamined = 0;
        while (maybeEntry.isPresent()) {
            final Map.Entry<Long, Set<Burrow>> entry = maybeEntry.get();
            final long costSoFar = entry.getKey();
            final Set<Burrow> nextBurrows = entry.getValue();
            @SuppressWarnings("OptionalGetWithoutIsPresent") final Burrow nextBurrow =
                    nextBurrows.stream().filter(b -> !visited.contains(b)).findFirst().get();
            if (visited.add(nextBurrow)) {
                final List<Move> newMoves = validMoves(nextBurrow);
                for (Move newMove : newMoves) {
                    final Burrow buzza = enactMove(nextBurrow, newMove);
                    if (buzza.equals(finito))
                    {
                        System.out.println("Found an ending");
                    }
                    final long newCost = costSoFar + newMove.cost();
                    if (costs.containsKey(buzza)) {
                        final long oldCost = costs.get(buzza);
                        if (newCost < oldCost) {
                            final Set<Burrow> frontierSet = frontier.get(oldCost);
                            if (frontierSet != null) {
                                frontierSet.remove(buzza);
                                frontier.computeIfAbsent(newCost, k -> new HashSet<>()).add(buzza);
                                costs.put(buzza, newCost);
                            } else {
                                System.out.println("Interesting");
                            }
                        }
                    } else {
                        costs.put(buzza, newCost);
                        frontier.computeIfAbsent(newCost, k -> new HashSet<>()).add(buzza);
                    }
                }
            }

            final long minCostSoFar = costs.getOrDefault(finito, Long.MAX_VALUE);
            maybeEntry = frontier
                    .entrySet()
                    .stream()
                    .filter(e -> e.getKey() < minCostSoFar && e.getValue().stream().anyMatch(b -> !visited.contains(b)))
                    .findFirst();

            routesExamined++;
            if (routesExamined % 10000 == 0)
            {
                System.out.println("Examined " + routesExamined + " routes, min cost so far is " + minCostSoFar);
            }
        }

        return costs.get(finito);
    }

    sealed interface Move permits Exit, Entry, Switch {
        long cost();
    }

    record Entry(int fromHallwayIndex, char pod, int toRoomIndex, int roomSlot, long cost) implements Move {}
    record Exit(int roomIndex, int roomSlot, char pod, int whereTo, long cost) implements Move {}
    record Switch(Exit exit, Entry entry) implements Move {
        @Override
        public long cost() {
            return exit.cost + entry.cost;
        }
    }

    private static final long[] COSTS = new long[]{1, 10, 100, 1000};
    private static final int[] VALID_HALLWAY_POSITIONS = new int[] {0, 1, 3, 5, 7, 9, 10};
    private static final int[] HALLWAY_ADJACENTS = new int[] {2, 4, 6, 8};

    static final int[] primes = new int[] {2, 3, 5, 7};

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
            int roomHash = 1;
            for (int i = 0; i < corridors.length; i++) {
                String corridor = corridors[i];
                int prime = primes[i];
                roomHash = roomHash * (prime * (corridor.charAt(0) - 'A'));
                roomHash = roomHash * (prime * (corridor.charAt(1) - 'A'));
            }

            return 11 * roomHash + Arrays.hashCode(hallway);
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

        // look for entries from the hallway to the corridor.
        for (int i = 0; i < burrow.hallway.length; i++) {
            char c = burrow.hallway[i];

            if (c != '.')
            {
                addEntryMoves(burrow, i, c, c - 'A', moves);
            }
        }

        // look for switches
        for (char i = 0; i < corridors.length; i++) {
            String corridor = corridors[i];
            char top = corridor.charAt(0);
            char bottom = corridor.charAt(1);

            final int correctPod = 'A' + i;
            if (top != '.' && top != correctPod)
            {
                int targetCorridorIndex = top - 'A';
                long moveCost = COSTS[targetCorridorIndex];
                final int newX = HALLWAY_ADJACENTS[targetCorridorIndex];
                final int oldX = HALLWAY_ADJACENTS[i];
                if (reachable(burrow.hallway, oldX, newX)) {
                    final Exit exit = new Exit(i, 0, top, newX, moveCost * (1 + Math.abs(oldX - newX)));
                    if (corridors[targetCorridorIndex].charAt(0) == '.' && corridors[targetCorridorIndex].charAt(1) == top) {
                        moves.add(new Switch(
                                exit,
                                new Entry(newX, top, targetCorridorIndex, 0, moveCost)
                        ));
                    }
                    else if (corridors[targetCorridorIndex].charAt(0) == '.' && corridors[targetCorridorIndex].charAt(1) == '.') {
                        moves.add(new Switch(
                                exit,
                                new Entry(newX, top, targetCorridorIndex, 1, 2 * moveCost)
                        ));
                    }
                }
            }

            if (top == '.' && bottom != '.' && bottom != correctPod)
            {
                final int targetCorridorIndex = bottom - 'A';
                long moveCost = COSTS[targetCorridorIndex];
                final int newX = HALLWAY_ADJACENTS[targetCorridorIndex];
                final int oldX = HALLWAY_ADJACENTS[i];
                if (reachable(burrow.hallway, oldX, newX)) {
                    if (corridors[targetCorridorIndex].charAt(0) == '.' && corridors[targetCorridorIndex].charAt(1) == bottom) {
                        moves.add(new Switch(
                                new Exit(i, 1, bottom, newX, moveCost * (2 + Math.abs(oldX - newX))),
                                new Entry(newX, bottom, targetCorridorIndex, 0, moveCost)
                        ));
                    }
                    else if (corridors[targetCorridorIndex].charAt(0) == '.' && corridors[targetCorridorIndex].charAt(1) == '.') {
                        moves.add(new Switch(
                                new Exit(i, 1, bottom, newX, moveCost * (2 + Math.abs(oldX - newX))),
                                new Entry(newX, bottom, targetCorridorIndex, 1, 2 * moveCost)
                        ));
                    }
                }
            }
        }

        if (!moves.isEmpty())
        {
            return moves;
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

        final int targetX = HALLWAY_ADJACENTS[index];
        final char targetPod = (char) ('A' + ((char) index));
        if (targetPod == c && reachable(burrow.hallway, targetX, i)) {
            long moveCost = COSTS[index];
            final String corridor = burrow.corridors[index];

            int hMove = Math.abs(targetX - i);

            if (corridor.charAt(1) == '.' && corridor.charAt(0) == '.') {
                moves.add(new Entry(i, c, index, 1, (2 + hMove) * moveCost));
            } else if (corridor.charAt(0) == '.' && corridor.charAt(1) == c) {
                moves.add(new Entry(i, c, index, 0, (1 + hMove) * moveCost));
            }
        }
    }

    private static class BurrowParser implements Lines.Parser<Burrow> {
        int count = 0;
        char[] hallway;
        String[] corridors = new String[4];

        @Override
        public void onLine(String string) {
            if (count == 1)
            {
                hallway = string.substring(1, string.length() - 1).toCharArray();
            }
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
            return new Burrow(corridors, hallway);
        }
    }
}
