package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Eleven {
    public static int allFlashStep(InputStream stream) throws IOException {
        int[][] grid = TwoDArrays.parse(stream);
        int stepCount = 0;
        while (true)
        {
            stepCount++;
            Set<Point> flashed = step(grid);
            if (flashed.size() == grid.length * grid[0].length)
            {
                return stepCount;
            }
        }
    }

    public static int flashCount(int steps, InputStream stream) throws IOException {
        int[][] grid = TwoDArrays.parse(stream);
        int flashCount = 0;
        for (int i = 0; i < steps; i++) {
            Set<Point> flashed = step(grid);
            flashCount += flashed.size();
        }
        return flashCount;
    }

    record Point(int x, int y) {}

    private static Set<Point> step(int[][] grid) {
        Queue<Point> flashers = new ArrayDeque<>();
        for (int y = 0; y < grid.length; y++) {
            int[] row = grid[y];
            for (int x = 0; x < row.length; x++) {
                ++row[x];
                if (row[x] > 9)
                {
                    flashers.add(new Point(x, y));
                }
            }
        }

        Set<Point> flashed = new HashSet<>();
        while (!flashers.isEmpty())
        {
            Point point = flashers.poll();
            if (flashed.add(point))
            {
                List<Point> neighbours = neighbours(grid, point);
                for (Point neighbour : neighbours) {
                    ++grid[neighbour.y][neighbour.x];
                    if (grid[neighbour.y][neighbour.x] > 9)
                    {
                        flashers.add(neighbour);
                    }
                }
            }
        }
        flashed.forEach(p -> grid[p.y][p.x] = 0);
        return flashed;
    }

    private static List<Point> neighbours(int[][] grid, Point p) {
        final List<Point> result = new ArrayList<>();
        for (int dy = -1; dy < 2; dy++) {
            for (int dx = -1; dx < 2; dx++) {
                if (!(dx == 0 && dy == 0)) {
                    int x2 = p.x + dx;
                    int y2 = p.y + dy;

                    if (0 <= x2 && x2 < grid[0].length) {
                        if (0 <= y2 && y2 < grid.length) {
                            result.add(new Point(x2, y2));
                        }
                    }
                }
            }
        }

        return result;
    }
}
