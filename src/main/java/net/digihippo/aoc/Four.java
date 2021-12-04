package net.digihippo.aoc;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Four {
    public static int bingo(InputStream stream) throws IOException {
        Game g = readGame(stream);

        return g.playUntilRowComplete();
    }

    public static int bingoTwo(InputStream stream) throws IOException {
        Game g = readGame(stream);

        return g.lastWinner();
    }

    private static Game readGame(InputStream stream) throws IOException {
        try (final BufferedReader reader =
                     new BufferedReader(new InputStreamReader(stream)))
        {
            List<Board> boards = new ArrayList<>();
            int[] draw = readInts(reader.readLine(), ",");

            while (reader.ready())
            {
                reader.readLine(); // empty
                int[][] rows = new int[5][];
                for (int i = 0; i < 5; i++) {
                    rows[i] = readInts(reader.readLine(), "\\s+");
                }

                boards.add(new Board(rows));
            }
            return new Game(draw, boards);
        }
    }

    private static int[] readInts(String draw, String regex) {
        try {
            final String[] split = draw.stripLeading().split(regex);
            final int[] drawn = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                drawn[i] = Integer.parseInt(split[i]);
            }

            return drawn;
        } catch (NumberFormatException nfe)
        {
            throw new RuntimeException("Unable to parse: " + draw);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static boolean[][] alloc(int width, int depth, boolean value)
    {
        boolean[][] result = new boolean[depth][];
        for (int i = 0; i < depth; i++) {
            boolean[] row = new boolean[width];
            Arrays.fill(row, value);
            result[i] = row;
        }

        return result;
    }

    private static final class Board
    {
        private final int[][] rows;
        private final boolean[][] marked;

        private Board(int[][] rows) {
            this.rows = rows;
            this.marked = alloc(rows.length, rows[0].length, false);
        }

        public boolean enter(int drawn) {
            for (int i = 0; i < rows.length; i++) {
                int[] row = rows[i];
                for (int j = 0; j < row.length; j++) {
                    int number = row[j];
                    if (number == drawn)
                    {
                        marked[i][j] = true;
                        return rowAllMarked(i) || columnAllMarked(j);
                    }
                }
            }
            return false;
        }

        private boolean columnAllMarked(int columnIndex) {
            for (boolean[] marks : marked) {
                if (!marks[columnIndex]) {
                    return false;
                }
            }
            return true;
        }

        private boolean rowAllMarked(int rowIndex) {
            boolean[] marks = marked[rowIndex];
            for (boolean mark : marks) {
                if (!mark) {
                    return false;
                }
            }

            return true;
        }

        public int unmarked() {
            int result = 0;
            for (int i = 0; i < marked.length; i++) {
                boolean[] row = marked[i];
                for (int j = 0; j < row.length; j++) {
                    boolean marked = row[j];
                    if (!marked)
                    {
                        result += rows[i][j];
                    }
                }
            }

            return result;
        }
    }

    private record Game(int[] drawOrder, List<Board> boards) {
        public int playUntilRowComplete() {
            for (int drawn : drawOrder) {
                for (Board board : boards) {
                    if (board.enter(drawn)) {
                        return drawn * board.unmarked();
                    }
                }
            }
            throw new IllegalStateException();
        }

        public int lastWinner() {
            int boardCount = boards.size();
            for (int drawn : drawOrder) {
                Iterator<Board> iter = boards.iterator();
                while (iter.hasNext()) {
                    Board board = iter.next();
                    if (board.enter(drawn)) {
                        boardCount--;
                        iter.remove();

                        if (boardCount == 0) {
                            return drawn * board.unmarked();
                        }
                    }
                }
            }
            throw new IllegalStateException();
        }
    }
}
