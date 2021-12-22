package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TwentyTwo {
    record Point(int x, int y, int z) {
        public Point max(Point p) {
            return new Point(
                    Math.max(x, p.x),
                    Math.max(y, p.y),
                    Math.max(z, p.z)
            );
        }

        public Point min(Point p) {
            return new Point(
                    Math.min(x, p.x),
                    Math.min(y, p.y),
                    Math.min(z, p.z)
            );
        }
    }

    record Bound(Point p) {
        public Bound replaceX(int newX) {
            return new Bound(new Point(newX, p.y, p.z));
        }

        public Bound replaceY(int newY) {
            return new Bound(new Point(p.x, newY, p.z));
        }

        public Bound replaceZ(int newZ) {
            return new Bound(new Point(p.x, p.y, newZ));
        }

        public Bound max(Bound lower) {
            return new Bound(
                    p.max(lower.p)
            );
        }

        public Bound min(Bound lower) {
            return new Bound(
                    p.min(lower.p)
            );
        }
    }

    static Bounds newBounds(boolean on, Bound lower, Bound upper)
    {
        return new Bounds(on, lower, upper, new ArrayList<>());
    }

    record Bounds(boolean on, Bound lower, Bound upper, List<Bounds> off) {
        void forEach(final Consumer<Point> consumer)
        {
            for (int x = lower.p.x; x <= upper.p.x; x++) {
                for (int y = lower.p.y; y <= upper.p.y; y++) {
                    for (int z = lower.p.z; z <= upper.p.z; z++) {
                        final Point p = new Point(x, y, z);

                        consumer.accept(p);
                    }
                }
            }
        }

        private boolean containsPoint(Point p) {
            return
                    lower.p.x <= p.x && p.x <= upper.p.x &&
                    lower.p.y <= p.y && p.y <= upper.p.y &&
                    lower.p.z <= p.z && p.z <= upper.p.z;
        }

        boolean outOfRange() {
            final boolean outOfRangeOne = upper.p.x < -50 || upper.p.y < -50 || upper.p.z < -50;
            final boolean outOfRangeTwo = lower.p.x > 50 || lower.p.y > 50 || lower.p.z > 50;
            return outOfRangeOne || outOfRangeTwo;
        }

        public List<Bounds> overlappedWith(Bounds that) {
            if (this.on && that.on)
            {
                return List.of(this, that);
            }

            return null;
        }

        public boolean overlapsWith(Bounds that) {
            return corners().anyMatch(that::containsPoint) || this.contains(that);
        }

        private Stream<Point> corners() {
            return Stream.of(
                    new Point(lower.p.x, lower.p.y, lower.p.z),
                    new Point(upper.p.x, lower.p.y, lower.p.z),
                    new Point(lower.p.x, upper.p.y, lower.p.z),
                    new Point(lower.p.x, lower.p.y, upper.p.z),
                    new Point(upper.p.x, upper.p.y, lower.p.z),
                    new Point(lower.p.x, upper.p.y, upper.p.z),
                    new Point(upper.p.x, lower.p.y, upper.p.z),
                    new Point(upper.p.x, upper.p.y, upper.p.z)
            );
        }

        public boolean contains(Bounds that) {
            return lowerWithinBounds(that) && upperWithinBounds(that);
        }

        private boolean upperWithinBounds(Bounds that) {
            return lower.p.x <= that.upper.p.x && that.upper.p.x < upper.p.x &&
                    lower.p.y <= that.upper.p.y && that.upper.p.y < upper.p.y &&
                    lower.p.z <= that.upper.p.z && that.upper.p.z < upper.p.z;
        }

        private boolean lowerWithinBounds(Bounds that) {
            return lower.p.x <= that.lower.p.x && that.lower.p.x < upper.p.x &&
            lower.p.y <= that.lower.p.y && that.lower.p.y < upper.p.y &&
            lower.p.z <= that.lower.p.z && that.lower.p.z < upper.p.z;
        }

        public List<Bounds> maskedBy(Bounds another) {
            final List<Bounds> newBounds = new ArrayList<>();

            if (!another.on) {
                // try a different approach: find the corner of another that's inside us.
                if (!another.contains(this)) {
                    if (containsPoint(another.lower.p)) {
                        int minX = lower.p.x;
                        if ((lower.p.x <= another.lower.p.x) && (another.lower.p.x <= upper.p.x)) {
                            int newX = Math.min(another.lower.p.x - 1, upper.p.x);
                            minX = newX + 1;
                            newBounds.add(
                                    newBounds(
                                            true,
                                            lower,
                                            upper.replaceX(newX)));
                        }

                        int minY = lower.p.y;
                        if ((lower.p.y <= another.lower.p.y) && (another.lower.p.y <= upper.p.y)) {
                            // gonna lose x up to min(another.upper.p.y, upper.p.y)
                            int newY = Math.min(another.lower.p.y - 1, upper.p.y);
                            minY = newY + 1;
                            newBounds.add(
                                    newBounds(
                                            true,
                                            lower.replaceX(minX),
                                            upper.replaceY(newY)));
                        }

                        if ((lower.p.z <= another.lower.p.z) && (another.lower.p.z <= upper.p.z)) {
                            // gonna lose x up to min(another.upper.p.z, upper.p.z)
                            int newZ = Math.min(another.lower.p.z - 1, upper.p.z);
                            newBounds.add(
                                    newBounds(
                                            true,
                                            lower.replaceX(minX).replaceY(minY),
                                            upper.replaceZ(newZ)));
                        }
                    } else if (containsPoint(another.upper.p)) {
                        int maxX = upper.p.x;
                        if ((lower.p.x <= another.upper.p.x) && (another.upper.p.x <= upper.p.x)) {
                            int newX = Math.max(another.upper.p.x + 1, lower.p.x);
                            maxX = newX - 1;
                            newBounds.add(
                                    newBounds(
                                            true,
                                            lower.replaceX(newX),
                                            upper));
                        }

                        int maxY = upper.p.y;
                        if ((lower.p.y <= another.upper.p.y) && (another.upper.p.y <= upper.p.y)) {
                            int newY = Math.max(another.upper.p.y + 1, lower.p.y);
                            maxY = newY - 1;
                            newBounds.add(
                                    newBounds(
                                            true,
                                            lower.replaceY(newY),
                                            upper.replaceX(maxX)));
                        }

                        if ((lower.p.z <= another.upper.p.z) && (another.upper.p.z <= upper.p.z)) {
                            int newZ = Math.max(another.upper.p.z + 1, lower.p.z);
                            newBounds.add(
                                    newBounds(
                                            true,
                                            lower.replaceZ(newZ),
                                            upper.replaceX(maxX).replaceY(maxY)));
                        }
                    } else {
                        newBounds.add(this);
                    }
                }
            }
            else
            {
                newBounds.addAll(this.maskedBy(another.butOff()));
                newBounds.addAll(another.maskedBy(this.butOff()));
                newBounds.add(this.intersect(another));
            }
            return newBounds;
        }

        Bounds intersect(Bounds another) {
            return newBounds(
                    true,
                    lower.max(another.lower),
                    upper.min(another.upper)
            );
        }

        private Bounds butOff() {
            return newBounds(false, lower, upper);
        }

        public long onCount() {
            if (!off.isEmpty()) {
                //Bounds initOff = null;
                int offCount = 0;
                for (Bounds bounds : off) {
                    final Bounds intersection = this.intersect(bounds);
                    offCount += intersection.volume();
                }

                return volume() - offCount;
            }

            return volume();
        }

        private long volume() {
            final long xDim = (1 + (upper.p.x - lower.p.x));
            final long yDim = (1 + (upper.p.y - lower.p.y));
            final long zDim = (1 + (upper.p.z - lower.p.z));

            return xDim * yDim * zDim;
        }

        public void mask(Bounds another) {
            if (!another.on)
            {
                off.add(another);
            }
            else
            {
                off.add(intersect(another));
            }
        }
    }

    record Input(List<Bounds> inRange)
    {

    }

    public static long onCubes(InputStream stream) throws IOException {
        final Input input = parseInput(stream);

        final Set<Point> on = new HashSet<>();
        for (Bounds bounds : input.inRange) {
            if (!bounds.outOfRange()) {
                if (bounds.on) {
                    bounds.forEach(on::add);
                } else {
                    bounds.forEach(on::remove);
                }
            }
        }

        return on.size();
    }

    public static long onCubesPartTwo(InputStream stream) throws IOException {
        final Input input = parseInput(stream);

        final Set<Point> on = new HashSet<>();
        List<Bounds> outOfRange = new ArrayList<>();
        for (Bounds bounds : input.inRange) {
            if (!bounds.outOfRange()) {
                if (bounds.on) {
                    bounds.forEach(on::add);
                } else {
                    bounds.forEach(on::remove);
                }
            }
            else
            {
                final List<Bounds> newOutOfRange = new ArrayList<>();
                for (Bounds oor : outOfRange) {
                    if (oor.overlapsWith(bounds) || bounds.overlapsWith(oor)) {
                        newOutOfRange.addAll(oor.maskedBy(bounds));
                    } else {
                        newOutOfRange.add(oor);
                    }
                }

                if (bounds.on)
                {
                    newOutOfRange.add(bounds);
                }


                outOfRange = newOutOfRange;

            }
        }

        final long outsideSum = outOfRange.stream().mapToLong(Bounds::onCount).sum();

        return on.size() + outsideSum;
    }


    static Input parseInput(InputStream stream) throws IOException {
        return Lines.parseLines(stream, new Lines.Parser<>() {
            final Pattern p =
                    Pattern.compile("(on|off) x=(-?[0-9]+)\\.\\.(-?[0-9]+),y=(-?[0-9]+)\\.\\.(-?[0-9]+),z=(-?[0-9]+)\\.\\.(-?[0-9]+)");
            final List<Bounds> bounds = new ArrayList<>();

            @Override
            public void onLine(String line) {
                final Matcher matcher = p.matcher(line);
                if (matcher.find()) {
                    final String onOrOff = matcher.group(1);


                    final int x1 = Integer.parseInt(matcher.group(2));
                    final int x2 = Integer.parseInt(matcher.group(3));
                    final int y1 = Integer.parseInt(matcher.group(4));
                    final int y2 = Integer.parseInt(matcher.group(5));
                    final int z1 = Integer.parseInt(matcher.group(6));
                    final int z2 = Integer.parseInt(matcher.group(7));
                    bounds.add(
                            newBounds(
                                    onOrOff.equals("on"),
                                    new Bound(new Point(x1, y1, z1)),
                                    new Bound(new Point(x2, y2, z2))
                            )
                    );
                }

            }

            @Override
            public Input build() {
                return new Input(bounds);
            }
        });
    }
}
