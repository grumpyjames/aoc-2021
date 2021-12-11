package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.ToIntFunction;

public class Nine {
    public static int riskLevel(InputStream stream) throws IOException {
        return riskLevel(TwoDArrays.parse(stream));
    }

    public static int basinSize(InputStream stream) throws IOException {
        return basinSize(TwoDArrays.parse(stream));
    }

    private static int riskLevel(int[][] heatmap) {
        RiskLevelComputer riskLevelComputer = new RiskLevelComputer(heatmap);
        findLows(heatmap, riskLevelComputer);
        return riskLevelComputer.riskLevel;
    }

    public static int basinSize(int[][] heatmap) {
        BasinSizeComputer basinSizeComputer = new BasinSizeComputer(heatmap);
        findLows(heatmap, basinSizeComputer);
        return basinSizeComputer.computeResult();
    }

    interface LowConsumer
    {

        void onLow(int y, int x);
    }

    private static void findLows(int[][] heatmap, LowConsumer lowConsumer)
    {
        int rowLength = heatmap[0].length;
        for (int y = 0; y < heatmap.length; y++) {
            for (int x = 0; x < rowLength; x++) {
                int elem = heatmap[y][x];
                int up = access(heatmap, y - 1, x, Integer.MAX_VALUE);
                int down = access(heatmap, y + 1, x, Integer.MAX_VALUE);
                int left = access(heatmap, y, x - 1, Integer.MAX_VALUE);
                int right = access(heatmap, y, x + 1, Integer.MAX_VALUE);
                if (elem < up && elem < down && elem < left && elem < right)
                {
                    lowConsumer.onLow(y, x);
                }
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static int access(int[][] heatmap, int y, int x, int def) {
        if (0 <= y && y < heatmap.length)
        {
            if (0 <= x && x < heatmap[0].length)
            {
                return heatmap[y][x];
            }
        }
        return def;
    }

    private static class RiskLevelComputer implements LowConsumer {
        private final int[][] heatmap;
        int riskLevel;

        public RiskLevelComputer(int[][] heatmap) {
            this.heatmap = heatmap;
        }

        @Override
        public void onLow(int y, int x) {
            riskLevel += heatmap[y][x];
            riskLevel += 1;
        }
    }

    record Point(int y, int x) {}



    private static class BasinSizeComputer implements LowConsumer {
        private final int[][] heatmap;
        private final List<List<Point>> basins = new ArrayList<>();

        private BasinSizeComputer(int[][] heatmap) {
            this.heatmap = heatmap;
        }

        @Override
        public void onLow(int y, int x) {
            final Queue<Point> searchSpace = new ArrayDeque<>();
            HashSet<Point> queued = new HashSet<>();
            searchSpace.add(new Point(y, x));
            queued.add(new Point(y, x));

            seekBasin(searchSpace, queued);
        }

        private void seekBasin(Queue<Point> queue, Set<Point> queued) {
            Point p;
            List<Point> basin = new ArrayList<>();
            while ((p = queue.poll()) != null)
            {
                int heat = access(heatmap, p.y, p.x, Integer.MAX_VALUE);
                if (heat != 9 && heat != Integer.MAX_VALUE)
                {
                    basin.add(p);
                    maybeQueue(queue, queued, new Point(p.y + 1, p.x));
                    maybeQueue(queue, queued, new Point(p.y - 1, p.x));
                    maybeQueue(queue, queued, new Point(p.y, p.x + 1));
                    maybeQueue(queue, queued, new Point(p.y, p.x - 1));
                }
            }
            basins.add(basin);
        }

        private void maybeQueue(Queue<Point> queue, Set<Point> queued, Point p)
        {
            if (queued.add(p)) {
                queue.add(p);
            }
        }

        public int computeResult() {
            Comparator<List<Point>> comp =
                    Comparator.comparingInt((ToIntFunction<List<Point>>) List::size).reversed();
            basins.sort(comp);

            return basins.get(0).size() * basins.get(1).size() * basins.get(2).size();
        }
    }
}
