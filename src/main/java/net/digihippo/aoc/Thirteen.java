package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Thirteen {
    record Point(int x, int y) {}
    record Fold(String xOrY, int coord) {}
    record Instructions(int[][] grid, List<Fold> folds) {
        public Instructions applyFold(int foldIndex) {
            final Fold fold = folds.get(foldIndex);
            if (fold.xOrY.equals("x")) {
                assert ((grid[0].length / 2) == fold.coord);

                int[][] newGrid = TwoDArrays.generate(grid[0].length / 2, grid.length);
                for (int y = 0; y < newGrid.length; y++) {
                    for (int x = 0; x < fold.coord; x++) {
                        newGrid[y][x] += grid[y][x];
                        final int readX = grid[0].length - x - 1;
                        final int foldX = flip(readX, fold.coord);
                        newGrid[y][foldX] += grid[y][readX];
                    }
                }
                return new Instructions(newGrid, folds);
            }
            else
            {
                assert ((grid.length / 2) == fold.coord);

                int[][] newGrid = TwoDArrays.generate(grid[0].length, grid.length / 2);
                for (int y = 0; y < fold.coord; y++) {
                    for (int x = 0; x < grid[0].length; x++) {
                        newGrid[y][x] += grid[y][x];
                        final int readY = grid.length - y - 1;
                        final int foldY = flip(readY, fold.coord);
                        newGrid[foldY][x] += grid[readY][x];
                    }
                }
                return new Instructions(newGrid, folds);
            }
        }

        private int flip(int index, int about) {
            return about - (index - about);
        }

        public int dotCount() {
            int result = 0;
            for (int[] ints : grid) {
                for (int anInt : ints) {
                    if (anInt > 0) {
                        result++;
                    }
                }
            }
            return result;
        }

        public void printGrid(PrintStream out) {
            for (int[] ints : grid) {
                for (int anInt : ints) {
                    if (anInt > 0) {
                        out.print("#");
                    } else {
                        out.print(".");
                    }
                }
                out.println();
            }
        }
    }

    public static int visibleDots(int folds, InputStream stream) throws IOException {
        Instructions instructions = Lines.parseLines(stream, new InstructionsParser());

        for (int i = 0; i < folds; i++) {
            instructions = instructions.applyFold(i);
        }

        return instructions.dotCount();
    }

    public static void applyAllFolds(InputStream stream, PrintStream out) throws IOException {
        Instructions instructions = Lines.parseLines(stream, new InstructionsParser());

        for (int i = 0; i < instructions.folds.size(); i++) {
            instructions = instructions.applyFold(i);
        }

        instructions.printGrid(out);
    }

    private static class InstructionsParser implements Lines.Parser<Instructions> {
        final Pattern p = Pattern.compile("fold along ([xy])=([0-9]*)");

        final List<Point> points = new ArrayList<>();
        final List<Fold> folds = new ArrayList<>();
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        boolean foldMode = false;

        @Override
        public void onLine(String line) {
            if (line.strip().isEmpty()) {
                foldMode = true;
                return;
            }

            if (foldMode) {
                final Matcher matcher = p.matcher(line);
                if (matcher.find()) {
                    folds.add(new Fold(
                            matcher.group(1),
                            Integer.parseInt(matcher.group(2))));
                }
            } else {
                final String[] point = line.split(",");
                final int x = Integer.parseInt(point[0]);
                final int y = Integer.parseInt(point[1]);
                points.add(new Point(x, y));
                maxX = Math.max(x, maxX);
                maxY = Math.max(y, maxY);
            }
        }

        @Override
        public Instructions build() {
            final int[][] rows = TwoDArrays.generate(maxX + 1, maxY + 1);

            for (Point point : points) {
                rows[point.y][point.x] = 1;
            }

            return new Instructions(rows, folds);
        }
    }
}
