package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Seventeen {
    public static Bounds bounds(int xLow, int xHigh, int yLow) {
        return new Bounds(
                new Bound(minX(xLow, xHigh), xHigh),
                new Bound(yLow, (-yLow) - 1)
        );
    }

    public static int partTwo(InputStream stream) throws IOException {
        final Bounds bounds = Lines.parseLine(stream, Seventeen::parseBounds);

        Bounds speedBounds = bounds(
                bounds.x.low,
                bounds.x.high,
                bounds.y.low
        );
        int count = 0;
        for (int x = speedBounds.x.low; x <= speedBounds.x.high; x++) {
            for (int y = speedBounds.y.low; y <= speedBounds.y.high; y++) {
                if (hitsTarget(bounds, x, y)) {
                    count++;
                }
            }
        }
        return count;
    }

    private static boolean hitsTarget(Bounds target, int x, int y) {
        int posX = 0;
        int posY = 0;
        int dx = x;
        int dy = y;
        while (posY >= target.y.low)
        {
            posX += dx;
            posY += dy;

            dx = Math.max(0, dx - 1);
            --dy;

            if (target.inBounds(posX, posY))
            {
                return true;
            }
        }

        return false;
    }

    record Bound(int low, int high) {
        public boolean contains(int q) {
            return low <= q && q <= high;
        }
    }
    record Bounds(Bound x, Bound y) {
        public boolean inBounds(int x, int y) {
            return this.x.contains(x) && this.y.contains(y);
        }
    }

    public static int maxHeight(InputStream stream) throws IOException {
        final Bounds bounds = Lines.parseLine(stream, Seventeen::parseBounds);

        return oneToNSum(-bounds.y.low - 1);
    }

    private static Bounds parseBounds(String line) {
        Pattern p = Pattern.compile("target area: x=([0-9]+)\\.\\.([0-9]+), y=-([0-9]+)\\.\\.-([0-9]+)");
        final Matcher matcher = p.matcher(line);
        if (matcher.find()) {
            return new Bounds(
                    new Bound(
                            Integer.parseInt(matcher.group(1)),
                            Integer.parseInt(matcher.group(2))
                    ),
                    new Bound(
                            -Integer.parseInt(matcher.group(3)),
                            -Integer.parseInt(matcher.group(4))
                    ));
        }
        throw new UnsupportedOperationException("Unparseable: " + line);
    }

    public static int minX(int targetLow, int targetHigh) {
        for (int x = 0; x < targetHigh; x++) {
            int distance = oneToNSum(x);
            if (targetLow <= distance && distance <= targetHigh)
            {
                return x;
            }
        }
        throw new UnsupportedOperationException();
    }

    public static int oneToNSum(int n) {
        final double m = (n + 1) / 2D;
        return (int) (n * m);
    }
}
