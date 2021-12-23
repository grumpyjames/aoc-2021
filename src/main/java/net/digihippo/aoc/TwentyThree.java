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
        return Lines.parseLines(stream, new BurrowParser(2));
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
        final Burrow burrow = Lines.parseLines(stream, new BurrowParser(2));
        final Burrow finito = emptyHalls(new String[]{"AA", "BB", "CC", "DD"});

        return minimumCost(burrow, finito);
    }

    public static long minimumCostUnfolded(InputStream stream) throws IOException {
        Burrow burrow = Lines.parseLines(stream, new BurrowParser(2));

        burrow = burrow.unfold(new String[]{ "DD", "CB", "BA", "AC" });

        final Burrow finito = emptyHalls(new String[]{"AAAA", "BBBB", "CCCC", "DDDD"});

        return minimumCost(burrow, finito);
    }

    public static Burrow extendedParse(InputStream stream) throws IOException {
        return Lines.parseLines(stream, new BurrowParser(4));
    }

    record Location(long cost, Burrow burrow) {}

    private static Long minimumCost(Burrow burrow, Burrow finito) {
        final TreeMap<Long, Set<Burrow>> frontier = new TreeMap<>();
        final Set<Burrow> visited = new HashSet<>(2 ^ 17);
        final Map<Burrow, Long> burrowToMinCost = new HashMap<>(2 ^ 14);


        final List<Move> moves = validMoves(burrow);
        for (Move move : moves) {
            final Burrow value = enactMove(burrow, move);
            frontier.computeIfAbsent(move.cost(), k -> new HashSet<>()).add(value);
            burrowToMinCost.put(value, move.cost());
            visited.add(burrow);
        }

        Optional<Location> maybeEntry =
                Optional.of(new Location(frontier.firstEntry().getKey(), frontier.firstEntry().getValue().iterator().next()));
        int routesExamined = 0;
        while (maybeEntry.isPresent()) {
            final Location entry = maybeEntry.get();
            final long costSoFar = entry.cost;
            final Burrow nextBurrow = entry.burrow;
            if (visited.add(nextBurrow)) {
                final Set<Burrow> burrows = frontier.get(costSoFar);
                burrows.remove(nextBurrow);
                if (burrows.isEmpty())
                {
                    frontier.remove(costSoFar);
                }

                final List<Move> newMoves = validMoves(nextBurrow);
                for (Move newMove : newMoves) {
                    final Burrow moveDestination = enactMove(nextBurrow, newMove);
                    if (moveDestination.equals(finito))
                    {
                        // could trim the frontier at this point ?
                        System.out.println("Found an ending");
                    }
                    final long newCost = costSoFar + newMove.cost();
                    if (burrowToMinCost.containsKey(moveDestination)) {
                        final long oldCost = burrowToMinCost.get(moveDestination);
                        final boolean cheaper = newCost < oldCost;
                        if (cheaper) {
                            final Set<Burrow> frontierSet = frontier.get(oldCost);
                            if (frontierSet != null) {
                                frontierSet.remove(moveDestination);
                                frontier.computeIfAbsent(newCost, k -> new HashSet<>()).add(moveDestination);
                                burrowToMinCost.put(moveDestination, newCost);
                            } else {
                                System.out.println("Interesting");
                            }
                        }
                    } else {
                        burrowToMinCost.put(moveDestination, newCost);
                        frontier.computeIfAbsent(newCost, k -> new HashSet<>()).add(moveDestination);
                    }
                }
            }
            else
            {
                // huh ?
                System.out.println("wtf");
            }

            final long minCostSoFar = burrowToMinCost.getOrDefault(finito, Long.MAX_VALUE);
            maybeEntry = nextEntry(frontier, visited, minCostSoFar);
            if (maybeEntry.isPresent() && visited.contains(maybeEntry.get().burrow))
            {
                // what?
                System.out.println("woaref");
            }

            routesExamined++;
            if (routesExamined % 10000 == 0)
            {
                System.out.println("Examined " + routesExamined + " routes, min cost so far is " + minCostSoFar);
                System.out.println("Burrow was " + nextBurrow);
            }
        }

        if (burrowToMinCost.containsKey(finito)) {
            return burrowToMinCost.get(finito);
        } else {
            System.out.println("No route found!");
            throw new IllegalStateException();
        }
    }

    private static Optional<Location> nextEntry(
            TreeMap<Long, Set<Burrow>> frontier,
            Set<Burrow> visited,
            long minCostSoFar) {
        for (Map.Entry<Long, Set<Burrow>> ee : frontier.entrySet()) {
            if (ee.getKey() > minCostSoFar)
            {
                return Optional.empty();
            }
            else
            {
                for (Burrow b : ee.getValue()) {
                    if (!visited.contains(b))
                    {
                        return Optional.of(new Location(ee.getKey(), b));
                    }
                }
            }
        }

        return Optional.empty();
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

    public static final class Burrow {
        public final String[] corridors;
        public final char[] hallway;
        private final int hashCode;

        Burrow(String[] corridors, char[] hallway)
        {
            this.corridors = corridors;
            this.hallway = hallway;
            this.hashCode = hash();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Burrow burrow = (Burrow) o;
            return Arrays.equals(corridors, burrow.corridors) && Arrays.equals(hallway, burrow.hallway);
        }

        public int hash()
        {
            int result = 1;
            result = 31 * result + Arrays.hashCode(corridors);
            result = 31 * result + Arrays.hashCode(hallway);
            return result;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public String toString() {
            return "Burrow{" +
                    "corridors=" + Arrays.toString(corridors) +
                    ", hallway=" + new String(hallway) +
                    '}';
        }

        public Burrow unfold(String[] extraBits) {
            final String[] newCorridors = new String[4];

            for (int i = 0; i < newCorridors.length; i++) {
                newCorridors[i] = corridors[i].charAt(0) + extraBits[i] + corridors[i].charAt(1);
            }

            return new Burrow(newCorridors, hallway);
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
        seekEntries(burrow, moves);

        // look for switches
        seekSwitches(burrow, corridors, moves);

        if (!moves.isEmpty())
        {
            return moves;
        }

        seekExits(burrow, corridors, moves);

        return moves;
    }

    private static void seekExits(Burrow burrow, String[] corridors, List<Move> moves) {
        for (char i = 0; i < corridors.length; i++) {
            String corridor = corridors[i];
            for (int slot = 0; slot < corridor.length(); slot++) {
                final char pod = corridor.charAt(slot);
                char correctPod = (char) ('A' + i);
                if (pod != '.')
                {
                    // don't suggest moving stacks of the right thing.
                    boolean allGood = true;
                    for (int j = slot; j < corridor.length(); j++) {
                         allGood &= corridor.charAt(j) == correctPod;
                    }

                    if (!allGood)
                    {
                        // suggest entries for the topmost pod.
                        long moveCost = COSTS[pod - 'A'];
                        for (int validHallwayPosition : VALID_HALLWAY_POSITIONS) {
                            final int exitPoint = HALLWAY_ADJACENTS[i];
                            if (reachable(burrow.hallway, validHallwayPosition, exitPoint)) {
                                moves.add(new Exit(
                                        i,
                                        slot,
                                        pod,
                                        validHallwayPosition,
                                        ((1 + slot + Math.abs(exitPoint - validHallwayPosition)) * moveCost)
                                ));
                            }
                        }
                    }

                    break;
                }
            }
        }
    }

    private static void seekEntries(Burrow burrow, List<Move> moves) {
        for (int i = 0; i < burrow.hallway.length; i++) {
            char c = burrow.hallway[i];

            if (c != '.')
            {
                addEntryMoves(burrow, i, c, c - 'A', moves);
            }
        }
    }

    private static void seekSwitches(Burrow burrow, String[] corridors, List<Move> moves) {
        for (char i = 0; i < corridors.length; i++) {
            String corridor = corridors[i];
            char correctPod = (char) ('A' + i);
            for (int slot = 0; slot < corridor.length(); slot++) {
                final char pod = corridor.charAt(slot);
                if (pod != '.') {
                    if (pod != correctPod) {
                        int targetCorridorIndex = pod - 'A';
                        long moveCost = COSTS[targetCorridorIndex];
                        final int newX = HALLWAY_ADJACENTS[targetCorridorIndex];
                        final int oldX = HALLWAY_ADJACENTS[i];
                        if (reachable(burrow.hallway, oldX, newX)) {
                            final Exit exit = new Exit(i, 0, pod, newX, moveCost * (1 + slot + Math.abs(oldX - newX)));
                            final String targetCorridor = corridors[targetCorridorIndex];
                            for (int targetSlot = 0; targetSlot < targetCorridor.length(); targetSlot++) {
                                boolean matching = true;
                                for (int j = 0; j <= targetSlot; j++) {
                                    matching &= targetCorridor.charAt(j) == '.';
                                }
                                for (int j = targetSlot + 1; j < targetCorridor.length(); j++) {
                                    matching &= targetCorridor.charAt(j) == pod;
                                }
                                if (matching) {
                                    moves.add(new Switch(
                                            exit,
                                            new Entry(newX, pod, targetCorridorIndex, targetSlot, (1 + targetSlot) * moveCost)
                                    ));
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private static void addEntryMoves(Burrow burrow, int i, char c, int index, List<Move> moves) {

        final int targetX = HALLWAY_ADJACENTS[index];
        final char targetPod = (char) ('A' + ((char) index));
        if (targetPod == c && reachable(burrow.hallway, targetX, i)) {
            long moveCost = COSTS[index];
            final String corridor = burrow.corridors[index];

            int hMove = Math.abs(targetX - i);

            for (int j = 0; j < corridor.length(); j++) {
                boolean matching = true;
                for (int k = 0; k <= j; k++) {
                    matching &= corridor.charAt(k) == '.';
                }
                for (int k = j + 1; k < corridor.length(); k++) {
                    matching &= corridor.charAt(k) == targetPod;
                }

                if (matching)
                {
                    moves.add(new Entry(i, c, index, j, (j + 1 + hMove) * moveCost));
                    return;
                }
            }
        }
    }

    private static class BurrowParser implements Lines.Parser<Burrow> {
        int count = 0;
        char[] hallway;
        private final int corridorDepth;

        BurrowParser(int corridorDepth)
        {
            this.corridorDepth = corridorDepth;
        }

        String[] corridors = new String[] {"", "", "", ""};

        @Override
        public void onLine(String string) {
            if (count == 1)
            {
                hallway = string.substring(1, string.length() - 1).toCharArray();
            }

            if (2 <= count && count < 2 + corridorDepth)
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
