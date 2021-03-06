package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Supplier;

public class Twelve {
    public static int pathCountCaveObsession(InputStream stream) throws IOException {
        final Map<String, List<Path>> directedEdges = direct(readPaths(stream));

        final List<HandyPath> interestingPaths =
                findPaths(
                        directedEdges,
                        () -> new ChangeableVisitConditions(new HashSet<>(), true));

        return interestingPaths.size();
    }

    public static int pathCount(InputStream stream) throws IOException {
        final Map<String, List<Path>> directedEdges = direct(readPaths(stream));
        List<HandyPath> paths = findPaths(
                directedEdges,
                () -> new ChangeableVisitConditions(new HashSet<>(), false));

        return paths.size();
    }

    private static List<HandyPath> findPaths(
            Map<String, List<Path>> directedEdges,
            Supplier<ChangeableVisitConditions> s) {
        final List<HandyPath> interestingPaths = new ArrayList<>();
        final String end = "end";
        List<Path> backPaths = directedEdges.get(end);

        for (Path p : backPaths) {
            HandyPath hp = new Node(end, new Empty());
            backTrack(
                    p.end,
                    hp,
                    directedEdges,
                    s.get(),
                    interestingPaths
            );
        }
        return interestingPaths;
    }

    private static Map<String, List<Path>> direct(List<Path> input) {
        final Map<String, List<Path>> directedEdges = new HashMap<>();
        for (Path path : input) {
            directedEdges
                    .computeIfAbsent(path.start, s -> new ArrayList<>())
                    .add(path);
            directedEdges
                    .computeIfAbsent(path.end, s -> new ArrayList<>())
                    .add(path.reverse());
        }
        return directedEdges;
    }

    private static List<Path> readPaths(InputStream stream) throws IOException {
        return Lines.parseLines(stream, l -> {
            String[] split = l.split("-");
            return new Path(split[0], split[1]);
        });
    }

    // FIXME:
    //  stop 'end' being special
    //  we're accruing state in pathSoFar and conditions
    private static void backTrack(
            String next,
            HandyPath pathSoFar,
            Map<String, List<Path>> directedEdges,
            ChangeableVisitConditions conditions,
            List<HandyPath> interestingPaths) {
        if (next.equals("start")) {
            HandyPath newPath = pathSoFar.add(next);
            interestingPaths.add(newPath);
        }
        else
        {
            Allowed allowed = conditions.performVisitationCheck(next);
            if (allowed.allowed)
            {
                List<Path> away = directedEdges.get(next);
                for (Path path : away) {
                    HandyPath newPath = pathSoFar.add(next);
                    backTrack(
                            path.end,
                            newPath,
                            directedEdges,
                            allowed.newConditions,
                            interestingPaths
                    );
                }
            }
        }
    }

    record Path(String start, String end) {
        public Path reverse() {
            return new Path(end, start);
        }
    }

    sealed interface HandyPath permits Empty, Node {
        default HandyPath add(String s) {
            return new Node(s, this);
        }

        void describe(StringBuilder sb);
    }

    record Node(String point, HandyPath p) implements HandyPath {
        @Override
        public void describe(StringBuilder sb) {
            sb.append("->").append(point);
            p.describe(sb);
        }
    }
    record Empty() implements HandyPath {
        @Override
        public void describe(StringBuilder sb) {

        }
    }

    record Allowed(ChangeableVisitConditions newConditions, boolean allowed) {}

    private record ChangeableVisitConditions(
            HashSet<String> visited,
            boolean secondVisitAllowed) {

        Allowed performVisitationCheck(String s) {
            if (s.equals("end")) {
                return new Allowed(this, false);
            }

            if (smallCave(s)) {
                HashSet<String> newThing = new HashSet<>(visited);
                boolean newCave = newThing.add(s);
                if (newCave) {
                    return new Allowed(
                            new ChangeableVisitConditions(
                                    newThing,
                                    secondVisitAllowed),
                            true);

                } else if (secondVisitAllowed) {
                    return new Allowed(
                            new ChangeableVisitConditions(
                                    visited,
                                    false),
                            true);
                } else {
                    return new Allowed(this, false);
                }
            }

            return new Allowed(this, true);
        }
    }

    private static boolean smallCave(String location) {
        return location.toLowerCase(Locale.ROOT).equals(location);
    }
}
