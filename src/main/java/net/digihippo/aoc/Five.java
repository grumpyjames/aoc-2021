package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Five {
    public static int countAllOverlaps(InputStream stream) throws IOException {
        return countTwoOrMore(stream, v -> true);
    }

    public static int countOverlaps(InputStream stream) throws IOException {
        return countTwoOrMore(stream, Vector::noDiagonalComponent);
    }

    private static int countTwoOrMore(
            InputStream stream,
            Predicate<Vector> ventChoice) throws IOException {
        final List<Vector> ventLines = readVents(stream);
        final TwoD bounds = computeMaxBounds(ventLines);

        int[][] grid =
                TwoDArrays.generate(bounds.x + 1, bounds.y + 1);

        ventLines
                .stream()
                .filter(ventChoice)
                .forEach(v -> v.visit(grid));

        return countTwoOrMoreVents(grid);
    }

    private static int countTwoOrMoreVents(int[][] grid) {
        int twoOrMore = 0;
        for (int[] ints : grid) {
            for (int anInt : ints) {
                if (anInt >= 2) {
                    twoOrMore++;
                }
            }
        }
        return twoOrMore;
    }

    private static TwoD computeMaxBounds(List<Vector> ventLines) {
        return ventLines
                .stream()
                .reduce(new TwoD(0, 0), (twoD, vector) -> new TwoD(
                        Math.max(twoD.x, vector.maxX()),
                        Math.max(twoD.y, vector.maxY())
                ), (twoD, twoD2) -> new TwoD(
                        Math.max(twoD.x, twoD2.x),
                        Math.max(twoD.y, twoD2.y)
                ));
    }

    private static List<Vector> readVents(InputStream stream) throws IOException {
        final List<Vector> ventLines = new ArrayList<>();
        final Pattern p = Pattern.compile("(\\d+),(\\d+) -> (\\d+),(\\d+)");
        Lines.processLines(stream, line -> {
            Matcher matcher = p.matcher(line);
            if (matcher.find())
            {
                ventLines.add(new Vector(
                        new TwoD(
                                Integer.parseInt(matcher.group(1)),
                                Integer.parseInt(matcher.group(2))),
                        new TwoD(
                                Integer.parseInt(matcher.group(3)),
                                Integer.parseInt(matcher.group(4)))
                ));
            }
        });
        return ventLines;
    }

    record TwoD(int x, int y) {
        public TwoD from(TwoD origin) {
            if (origin.x > x && origin.y > y)
            {
                return new TwoD(-1, -1);
            }
            else if (origin.x > x && origin.y < y)
            {
                return new TwoD(-1, 1);
            }
            else if (origin.x < x && origin.y < y)
            {
                return new TwoD(1, 1);
            }
            else
            {
                return new TwoD(1, -1);
            }
        }

        public TwoD add(TwoD direction) {
            return new TwoD(x + direction.x, y + direction.y);
        }
    }
    record Vector(TwoD start, TwoD end) {
        public int maxX() {
            return Math.max(start.x, end.x);
        }

        public int maxY() {
            return Math.max(start.y, end.y);
        }

        public boolean noDiagonalComponent() {
            return start.y == end.y || start.x == end.x;
        }

        public void visit(int[][] grid) {
            if (!noDiagonalComponent())
            {
                TwoD point = this.start;
                TwoD direction = this.end.from(this.start());
                while (!point.equals(end))
                {
                    grid[point.y][point.x]++;
                    point = point.add(direction);
                }
                grid[end.y][end.x]++;
            }
            else if (start.y == end.y)
            {
                int start = Math.min(this.start.x, end.x);
                int end = Math.max(this.start.x, this.end.x);
                for (int i = start; i <= end; i++) {
                    grid[this.start.y][i]++;
                }
            }
            else
            {
                int start = Math.min(this.start.y, end.y);
                int end = Math.max(this.start.y, this.end.y);
                for (int i = start; i <= end; i++) {
                    grid[i][this.start.x]++;
                }
            }
        }
    }
}
