package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Fifteen {
    public static int shortestPathOfFullGrid(InputStream stream) throws IOException {
        int[][] risk = Lines.parseLines(stream, new GridParser());
        final int initialHeight = risk.length;
        final int initialWidth = risk[0].length;
        int[][] bigRisk = TwoDArrays.generate(initialWidth * 5, initialHeight * 5);
        for (int dy = 0; dy < 5; dy++) {
            for (int dx = 0; dx < 5; dx++) {
                for (int y = 0; y < initialHeight; y++) {
                    for (int x = 0; x < initialWidth; x++) {
                        int newY = dy * initialHeight + y;
                        int newX = dx * initialWidth + x;
                        bigRisk[newY][newX] = mutate(risk[y][x], dx, dy);
                    }
                }
            }
        }

        return dijkstra(bigRisk);
    }

    private static int mutate(int risk, int dx, int dy) {
        final int result = risk + dx + dy;
        if (result > 9)
        {
            return result - 9;
        }
        return result;
    }

    public static int shortestPath(InputStream stream) throws IOException {
        int[][] risk = Lines.parseLines(stream, new GridParser());

        return dijkstra(risk);
    }

    private static int dijkstra(int[][] risk) {
        final Set<Point> unvisited = new HashSet<>();
        for (int y = 0; y < risk.length; y++) {
            int[] ints = risk[y];
            for (int x = 0; x < ints.length; x++) {
                unvisited.add(new Point(x, y));
            }
        }

        final int[][] distances =
                TwoDArrays.generate(risk[0].length, risk.length, Integer.MAX_VALUE);
        distances[0][0] = 0;
        Point initialNode = new Point(0, 0);

        while (initialNode != null && !initialNode.equals(new Point(risk[0].length - 1, risk.length - 1)))
        {
            unvisited.remove(initialNode);
            int distanceSoFar = distances[initialNode.y][initialNode.x];
            List<Point> neigh = initialNode.neighbours(risk);
            for (Point point : neigh) {
                if (unvisited.contains(point)) {
                    distances[point.y][point.x] =
                            Math.min(distances[point.y][point.x], distanceSoFar + risk[point.y][point.x]);
                }
            }
            // this is just silly.
            initialNode = findCheapest(unvisited, distances);
        }

        return distances[risk[0].length - 1][risk.length - 1];
    }

    record Point(int x, int y) {
        public List<Point> neighbours(int[][] risk) {
            final List<Point> result = new ArrayList<>();

            if (x + 1 < risk[0].length)
            {
                result.add(new Point(x + 1, y));
            }

            if (y + 1 < risk.length)
            {
                result.add(new Point(x, y + 1));
            }

            if (x - 1 < risk[0].length)
            {
                result.add(new Point(x - 1, y));
            }

            if (y - 1 < risk.length)
            {
                result.add(new Point(x, y - 1));
            }

            return result;
        }
    }

    private static Point findCheapest(Set<Point> unvisited, int[][] distances) {
        Point result = null;
        int minDistance = Integer.MAX_VALUE;
        for (Point point : unvisited) {
            if (distances[point.y][point.x] < minDistance)
            {
                result = point;
                minDistance = distances[point.y][point.x];
            }
        }

        return result;
    }

    private static class GridParser implements Lines.Parser<int[][]> {
        final List<int[]> rows = new ArrayList<>();

        @Override
        public void onLine(String string) {
            final int[] row = new int[string.length()];
            for (int i = 0; i < row.length; i++) {
                row[i] = string.charAt(i) - '0';
            }
            rows.add(row);
        }

        @Override
        public int[][] build() {
            return rows.toArray(new int[][]{});
        }
    }
}
