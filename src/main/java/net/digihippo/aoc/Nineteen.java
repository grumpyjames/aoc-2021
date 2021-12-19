package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Nineteen {
    public static List<Matrix> findOrientationChange(Point alternative, Point original) {

        return ROTATORS.stream().filter(r -> {
            return r.apply(alternative).equals(original); // || r.apply(original).equals(alternative.reversed());
        }).toList();
    }

    static List<Matrix> ROTATORS = List.of(
            parse("""
                    1 0 0
                    0 1 0
                    0 0 1
                    """),
            parse("""
                    0	0	1
                    0	1	0
                    -1	0	0"""),
            parse("""
                    -1	0	0
                    0	1	0
                    0	0	-1"""),
            parse("""
                    0	0	-1
                    0	1	0
                    1	0	0"""),
            parse("""
                    0	-1	0
                    1	0	0
                    0	0	1
                    """),
            parse("""
                    0	0	1
                    1	0	0
                    0	1	0"""),
            parse("""
                    0	1	0
                    1	0	0
                    0	0	-1
                    """),
            parse("""
                    0	0	-1
                    1	0	0
                    0	-1	0"""),
            parse("""
                    0	1	0
                    -1	0	0
                    0	0	1"""),
            parse("""
                    0	0	1
                    -1	0	0
                    0	-1	0"""),
            parse("""
                    0	-1	0
                    -1	0	0
                    0	0	-1"""),
            parse("""
                    0	0	-1
                    -1	0	0
                    0	1	0"""),
            parse("""
                    1	0	0
                    0	0	-1
                    0	1	0"""),
            parse("""
                    0	1	0
                    0	0	-1
                    -1	0	0"""),
            parse("""
                    -1	0	0
                    0	0	-1
                    0	-1	0"""),
            parse("""        
                    0	-1	0
                    0	0	-1
                    1	0	0"""),
            parse("""
                    1	0	0
                    0	-1	0
                    0	0	-1"""),
            parse("""
                    0	0	-1
                    0	-1	0
                    -1	0	0"""),
            parse("""
                    -1	0	0
                    0	-1	0
                    0	0	1"""),
            parse("""
                    0	0	1
                    0	-1	0
                    1	0	0"""),
            parse("""
                    1	0	0
                    0	0	1
                    0	-1	0"""),
            parse("""
                    0	-1	0
                    0	0	1
                    -1	0	0"""),
            parse("""
                    -1	0	0
                    0	0	1
                    0	1	0"""),
            parse("""
                    0	1	0
                    0	0	1
                    1	0	0""")
    );

    record Matrix(int[][] parts)
    {
        public Point apply(Point p) {

            return new Point(
                    multiply(parts[0], p),
                    multiply(parts[1], p),
                    multiply(parts[2], p)
            );
        }

        private int multiply(int[] row, Point p) {
            return row[0] * p.x + row[1] * p.y + row[2] * p.z;
        }

        Matrix invert()
        {
            return new Matrix(inverse(parts));
        }

        private static int determinant(int[][] matrix) {
            if (matrix.length != matrix[0].length)
                throw new IllegalStateException("invalid dimensions");

            if (matrix.length == 2)
                return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

            int det = 0;
            for (int i = 0; i < matrix[0].length; i++)
                det += Math.pow(-1, i) * matrix[0][i]
                        * determinant(submatrix(matrix, 0, i));
            return det;
        }

        private static int[][] inverse(int[][] matrix) {
            int[][] inverse = new int[matrix.length][matrix.length];

            // minors and cofactors
            for (int i = 0; i < matrix.length; i++)
                for (int j = 0; j < matrix[i].length; j++)
                    inverse[i][j] = ((int) Math.pow(-1, i + j))
                            * determinant(submatrix(matrix, i, j));

            // adjugate and determinant
            double det = 1.0 / determinant(matrix);
            for (int i = 0; i < inverse.length; i++) {
                for (int j = 0; j <= i; j++) {
                    int temp = inverse[i][j];
                    inverse[i][j] = (int) (inverse[j][i] * det);
                    inverse[j][i] = (int) (temp * det);
                }
            }

            return inverse;
        }

        private static int[][] submatrix(int[][] matrix, int row, int column) {
            int[][] submatrix = new int[matrix.length - 1][matrix.length - 1];

            for (int i = 0; i < matrix.length; i++)
                for (int j = 0; i != row && j < matrix[i].length; j++)
                    if (j != column)
                        submatrix[i < row ? i : i - 1][j < column ? j : j - 1] = matrix[i][j];
            return submatrix;
        }
    }

    static Matrix parse(String matrixInput) {
        String[] rows = matrixInput.split("\n");
        int[][] matrix = new int[rows.length][];
        int rowIndex = 0;
        for (String row : rows)
        {
            String[] entries = row.split("\\s+");
            matrix[rowIndex] = new int[entries.length];
            for (int i = 0; i < entries.length; i++) {
                matrix[rowIndex][i] = Integer.parseInt(entries[i]);
            }
            rowIndex++;
        }
        return new Matrix(matrix);
    }

    record Point(int x, int y, int z) {
        public long squaredDistanceTo(Point two) {
            return
                    ((long) (x - two.x) * (x - two.x)) +
                            ((long) (y - two.y) * (y - two.y)) +
                            ((long) (z - two.z) * (z - two.z));
        }

        public Point directionFrom(Point one) {
            return new Point(x - one.x, y - one.y, z - one.z);
        }

        public Point reversed() {
            return new Point(-x, -y, -z);
        }

        public Point add(Point offset) {
            return new Point(x + offset.x, y + offset.y, z + offset.z);
        }

        public void print(PrintStream p)
        {
            p.print(x);
            p.print(',');
            p.print(y);
            p.print(',');
            p.print(z);
            p.println();
        }
    }

    record Scanner(String name, List<Point> observations) {
        public void visit(Map<Long, List<Observation>> distanceToObservations) {
            for (int i = 0; i < observations.size(); i++) {
                for (int j = i + 1; j < observations.size(); j++) {
                    Point one = observations.get(i);
                    Point two = observations.get(j);
                    long squaredDistance = one.squaredDistanceTo(two);
                    distanceToObservations
                            .computeIfAbsent(squaredDistance, k -> new ArrayList<>())
                            .add(new Observation(name, one, two));
                }
            }
        }

        public Scanner applyTransform(Transform t) {
            final List<Point> newPoints =
                    observations
                            .stream()
                            .map(t::transform)
                            .toList();

            return new Scanner(name, newPoints);
        }
    }

    record Observation(String scannerName, Point one, Point two) {
        public Point directionVector() {
            return two.directionFrom(one);
        }
    }

    record ScannerOffsetGuess(Matrix matrix, String scannerFrom, String scannerTo, Point offset) {}
    record Source(String scannerName, Transform t) {}

    record Transform(Matrix m, Point offset, boolean offsetFirst)
    {
        Point transform(Point p)
        {
            if (offsetFirst)
            {
                return m.apply(p.add(offset));
            }
            else
            {
                return offset.add(m.apply(p));
            }
        }
    }


    public static int beacons(int commonBeacons, InputStream stream) throws IOException {
        List<Scanner> scanners = Lines.parseLines(stream, new ScannerParser());

        Map<String, List<Source>> source = new HashMap<>();
        for (int i = 0; i < scanners.size(); i++) {
            final Scanner alternateBasisScanner = scanners.get(i);
            for (int j = i + 1; j < scanners.size(); j++) {
                final Scanner knownBasisScanner = scanners.get(j);

                final Map<Long, List<Observation>> distanceToObservations = new HashMap<>();
                alternateBasisScanner.visit(distanceToObservations);
                knownBasisScanner.visit(distanceToObservations);

                List<Map.Entry<Long, List<Observation>>> pairs =
                        distanceToObservations.entrySet().stream().filter(e -> e.getValue().size() == 2).toList();

                final Optional<Map.Entry<ScannerOffsetGuess, List<ObservationPair>>> e =
                        computeOffsetGuess(pairs);
                if (e.isPresent()) {
                    Map.Entry<ScannerOffsetGuess, List<ObservationPair>> scannerOffsetGuessLongEntry = e.get();

                    Set<Point> inAlternativeBasis = beaconSet(scannerOffsetGuessLongEntry, o -> o.a);
                    Set<Point> inGoodBasis = beaconSet(scannerOffsetGuessLongEntry, o -> o.b);
                    assertMaps(scannerOffsetGuessLongEntry.getKey(), inAlternativeBasis, inGoodBasis);
                    if (inAlternativeBasis.size() >= commonBeacons) {

                        ScannerOffsetGuess guess = scannerOffsetGuessLongEntry.getKey();

                        source.computeIfAbsent(alternateBasisScanner.name, k -> new ArrayList<>())
                                .add(new Source(knownBasisScanner.name, new Transform(guess.matrix, guess.offset, false)));
                        source.computeIfAbsent(knownBasisScanner.name, k -> new ArrayList<>())
                                .add(new Source(alternateBasisScanner.name, new Transform(guess.matrix.invert(), guess.offset.reversed(), true)));
                    }
                }
            }
        }

        final List<Scanner> shifted = new ArrayList<>();
        shifted.add(scanners.get(0));
        for (int i = 1; i < scanners.size(); i++) {
            Scanner initial = scanners.get(i);
            // try to get back to root scanner;
            List<Source> route = findRoute(initial.name(), scanners.get(0).name, source, new HashSet<>());
            assert route != null;
            for (Source src : route) {
                initial = initial.applyTransform(src.t);
            }

            shifted.add(initial);
        }

        Set<Point> beacons = shifted.stream().flatMap(s -> s.observations.stream()).collect(Collectors.toSet());

        beacons.stream()
                .sorted(Comparator.comparingInt((Point p) -> p.x).thenComparingInt((Point p) -> p.y).thenComparingInt((Point p) -> p.z))
                .forEach(p -> p.print(System.out));

        return beacons.size();
    }

    private static void assertMaps(ScannerOffsetGuess guess, Set<Point> fromPoints, Set<Point> toPoints) {
        for (Point aPoint : fromPoints) {
            Point rotated = guess.matrix.apply(aPoint);
            Point offset = rotated.add(guess.offset);
            boolean contains = toPoints.contains(offset);
            assert contains;
        }

        for (Point bPoint : toPoints) {
            Point offset = bPoint.add(guess.offset.reversed());
            Point rotated = guess.matrix.invert().apply(offset);
            boolean contains = fromPoints.contains(rotated);
            assert contains;
        }
    }

    private static Set<Point> beaconSet(
            Map.Entry<ScannerOffsetGuess, List<ObservationPair>> scannerOffsetGuessLongEntry,
            Function<ObservationPair, Observation> extr) {
        return scannerOffsetGuessLongEntry.getValue()
                .stream()
                .map(extr)
                .flatMap(a -> Stream.of(a.one, a.two))
                .collect(Collectors.toSet());
    }

    private static List<Source> findRoute(String from, String to, Map<String, List<Source>> source, Set<Source> visited) {
        for (Source a : source.get(from)) {
            if (visited.contains(a))
            {
                return null;
            }

            if (a.scannerName.equals(to))
            {
                ArrayList<Source> rs = new ArrayList<>();
                rs.add(a);
                return rs;
            }
            else
            {
                visited.add(a);
                List<Source> route = findRoute(a.scannerName, to, source, visited);
                if (route != null)
                {
                    List<Source> justA = new ArrayList<>();
                    justA.add(a);
                    justA.addAll(route);
                    return justA;
                }
            }
        }
        return null;
    }

    record ObservationPair(Observation a, Observation b) {}

    private static Optional<Map.Entry<ScannerOffsetGuess, List<ObservationPair>>> computeOffsetGuess(List<Map.Entry<Long, List<Observation>>> pairs) {
        final Map<ScannerOffsetGuess, List<ObservationPair>> guesses = new HashMap<>();
        for (Map.Entry<Long, List<Observation>> eere : pairs) {
            final List<Observation> entry = eere.getValue();
            if (entry.size() != 2)
            {
                continue;
            }

            Observation alternative = entry.get(0);
            Observation original = entry.get(1);
            List<Matrix> orientationChanges = findOrientationChange(alternative.directionVector(), original.directionVector());
            for (Matrix orientationChange: orientationChanges) {
                Point reorientedAOne = orientationChange.apply(alternative.one);
                Point reorientedATwo = orientationChange.apply(alternative.two);

                Point offsetOne = original.two.directionFrom(reorientedATwo);
                Point offsetTwo = original.one.directionFrom(reorientedAOne);

                Point swappedOffsetOne = original.two.directionFrom(reorientedAOne);
                Point swappedOffsetTwo = original.one.directionFrom(reorientedATwo);

                if (offsetOne.equals(offsetTwo))
                {
                    ScannerOffsetGuess guess = new ScannerOffsetGuess(orientationChange, original.scannerName, alternative.scannerName, offsetOne);
                    guesses.computeIfAbsent(guess, k -> new ArrayList<>()).add(new ObservationPair(alternative, original));
                }
                else if (swappedOffsetOne.equals(swappedOffsetTwo))
                {
                    // what does this mean? does it mean anything?
                    ScannerOffsetGuess guess = new ScannerOffsetGuess(orientationChange, original.scannerName, alternative.scannerName, swappedOffsetOne);
                    guesses.computeIfAbsent(guess, k -> new ArrayList<>()).add(new ObservationPair(alternative, original));
                }
                else
                {
                    throw new UnsupportedOperationException();
                }
            }


        }

        return guesses
                .entrySet()
                .stream()
                .max(Comparator.comparingLong(e -> e.getValue().size()));
    }

    private static class ScannerParser implements Lines.Parser<List<Scanner>> {
        boolean readingName = true;
        String name;
        List<Point> observations;
        List<Scanner> scanners = new ArrayList<>();

        @Override
        public void onLine(String string) {
            if (readingName) {
                name = string.strip();
                observations = new ArrayList<>();
                readingName = false;
            } else {
                if (string.isBlank()) {
                    readingName = true;
                    scanners.add(new Scanner(name, observations));
                } else {
                    String[] split = string.split(",");
                    if (split.length == 2) {
                        observations.add(new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1]), 0));
                    } else {
                        observations.add(new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])));
                    }
                }
            }
        }

        @Override
        public List<Scanner> build() {
            scanners.add(new Scanner(name, observations));

            return scanners;
        }
    }
}
