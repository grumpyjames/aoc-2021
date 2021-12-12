package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Twelve {
    public static int pathCountUgh(InputStream stream) throws IOException {
        final Map<String, List<Path>> directedEdges = direct(readPaths(stream));

        final List<HandyPath> interestingPaths = new ArrayList<>();

        final String end = "end";
        List<Path> backPaths = directedEdges.get("end");
        for (Path p : backPaths) {
            HandyPath hp = new Node(end, new Empty());
            backTrack(p, hp, new HashSet<>(), directedEdges,
                    interestingPaths, true);
        }

        for (HandyPath interestingPath : interestingPaths) {
            StringBuilder sb = new StringBuilder();
            interestingPath.describe(sb);
            System.out.println(sb);
        }

        return interestingPaths.size();
    }

    public static int pathCount(InputStream stream) throws IOException {
        final Map<String, List<Path>> directedEdges = direct(readPaths(stream));

        final List<HandyPath> interestingPaths = new ArrayList<>();

        final String end = "end";
        List<Path> backPaths = directedEdges.get("end");
        for (Path p : backPaths) {
            HandyPath hp = new Node(end, new Empty());
            backTrack(
                    p,
                    hp,
                    new HashSet<>(),
                    directedEdges,
                    interestingPaths,
                    false);
        }

        for (HandyPath interestingPath : interestingPaths) {
            StringBuilder sb = new StringBuilder();
            interestingPath.describe(sb);
            System.out.println(sb);
        }

        return interestingPaths.size();
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

    private static void backTrack(
            Path p,
            HandyPath bread,
            Set<String> smallCavesVisited,
            Map<String, List<Path>> directedEdges,
            List<HandyPath> interestingPaths,
            boolean secondVisitAllowed) {
        String travellingTo = p.end;
        HashSet<String> newVisited = new HashSet<>(smallCavesVisited);
        if (travellingTo.equals("start")) {
            HandyPath newPath = bread.add(travellingTo);
            interestingPaths.add(newPath);
        }
        else if (travellingTo.equals("end")) {
            //noinspection UnnecessaryReturnStatement
            return;
        }
        else if (smallCave(travellingTo))
        {
            if (newVisited.add(travellingTo))
            {
                List<Path> away = directedEdges.get(travellingTo);
                for (Path path : away) {
                    HandyPath newPath = bread.add(travellingTo);
                    backTrack(
                            path,
                            newPath,
                            newVisited,
                            directedEdges,
                            interestingPaths,
                            secondVisitAllowed);
                }
            }
            else if (secondVisitAllowed)
            {
                List<Path> away = directedEdges.get(travellingTo);
                for (Path path : away) {
                    HandyPath newPath = bread.add(travellingTo);
                    backTrack(
                            path,
                            newPath,
                            newVisited,
                            directedEdges,
                            interestingPaths,
                            false);
                }
            }
        }
        else {
            List<Path> away = directedEdges.get(travellingTo);
            for (Path path : away) {
                HandyPath newPath = bread.add(travellingTo);
                backTrack(
                        path,
                        newPath,
                        newVisited,
                        directedEdges,
                        interestingPaths,
                        secondVisitAllowed);
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

    private static boolean smallCave(String location) {
        boolean notStartOrEnd = !location.equals("start") && !location.equals("end");
        return notStartOrEnd && location.toLowerCase(Locale.ROOT).equals(location);
    }
}
