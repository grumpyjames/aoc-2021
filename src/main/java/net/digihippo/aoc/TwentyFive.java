package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class TwentyFive {
    record Point(int x, int y) {}
    record Move(Point from, Point to, char thing) {
        public void enact(char[][] grid) {
            grid[from.y][from.x] = '.';
            grid[to.y][to.x] = thing;
        }
    }


    record Seabed(char[][] grid)
    {

        public boolean move() {
            final List<Move> eastMoves = new ArrayList<>();
            for (int y = 0; y < grid.length; y++) {
                char[] row = grid[y];
                for (int x = 0; x < row.length; x++) {
                    char c = row[x];
                    if (c == '>')
                    {
                        int targetX = (x + 1) % row.length;
                        if (row[targetX] == '.')
                        {
                            eastMoves.add(new Move(new Point(x, y), new Point(targetX, y), '>'));
                        }
                    }
                }
            }

            eastMoves.forEach(m -> m.enact(grid));

            List<Move> southMoves = new ArrayList<>();

            for (int x = 0; x < grid[0].length; x++) {
                for (int y = 0; y < grid.length; y++) {
                    if (grid[y][x] == 'v')
                    {
                        int rowTarget = (y + 1) % grid.length;
                        if (grid[rowTarget][x] == '.')
                        {
                            southMoves.add(new Move(new Point(x, y), new Point(x, rowTarget), 'v'));
                        }
                    }
                }
            }

            southMoves.forEach(m -> m.enact(grid));

            return (eastMoves.size() + southMoves.size()) > 0;
        }

        public void printTo(PrintStream out) {
            for (char[] chars : grid) {
                out.println(new String(chars));
            }
            out.println();
        }

        public String asString() {
            StringBuilder sb = new StringBuilder();
            for (char[] chars : grid) {
                sb.append(chars);
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    public static int stepCount(InputStream stream) throws IOException {
        Seabed seabed = parse(stream);

        boolean moved = true;
        int count = 0;
        seabed.printTo(System.out);
        while (moved) {
            moved = seabed.move();
            System.out.println("After " + (count + 1) + " moves:");
            seabed.printTo(System.out);
            count++;
        }

        return count;
    }

    static Seabed parse(InputStream stream) throws IOException {
        return Lines.parseLines(stream, new Lines.Parser<>() {
            private final List<String> rows = new ArrayList<>();

            @Override
            public void onLine(String string) {
                rows.add(string);
            }

            @Override
            public Seabed build() {
                return new Seabed(rows.stream().map(String::toCharArray).toList().toArray(new char[][]{}));
            }
        });
    }
}
