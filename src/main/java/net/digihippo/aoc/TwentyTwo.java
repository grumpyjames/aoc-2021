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

    sealed interface Space permits Universe, Empty, Union, Bounds {
        Space intersect(Space another);
        Space union(Space another);
        long volume();
    }

    record Universe() implements Space {
        @Override
        public Space intersect(Space another) {
            return another;
        }

        @Override
        public Space union(Space another) {
            return this;
        }

        @Override
        public long volume() {
            throw new UnsupportedOperationException();
        }
    }

    record Empty() implements Space {

        @Override
        public Space intersect(Space another) {
            return this;
        }

        @Override
        public Space union(Space another) {
            return another;
        }

        @Override
        public long volume() {
            return 0;
        }
    }

    record Union(List<Bounds> spaces) implements Space
    {
        public Space intersect(Space other) {
            switch (other)
            {
                case Union u: {
                    // union of intersections
                    ArrayList<Bounds> newSpaces = new ArrayList<>(spaces.size() * u.spaces.size());
                    for (Space space : this.spaces) {
                        for (Space another : u.spaces) {
                            Space intersect = space.intersect(another);
                            addSpace(newSpaces, intersect);
                        }
                    }
                    return new Union(newSpaces);
                }
                case Bounds b: {
                    ArrayList<Bounds> newSpaces = new ArrayList<>(spaces.size());
                    for (Bounds bounds : this.spaces) {
                        addSpace(newSpaces, bounds.intersect(b));
                    }
                    return new Union(newSpaces);
                }
                default:
                    return other.intersect(this);
            }
        }

        private void addSpace(ArrayList<Bounds> newSpaces, Space intersect) {
            switch (intersect)
            {
                case Bounds b:
                    newSpaces.add(b);
                    break;
                case Empty e:
                    break;
                case Universe u:
                    throw new UnsupportedOperationException();
                case Union union:
                    throw new UnsupportedOperationException();
            }
        }

        public Space union(Space space) {
            switch (space)
            {
                case Bounds b:
                {
                    ArrayList<Bounds> newSpaces = new ArrayList<>(this.spaces);
                    newSpaces.add(b);
                    return new Union(newSpaces);
                }
                case Union u:
                {
                    ArrayList<Bounds> newSpaces = new ArrayList<>(this.spaces);
                    newSpaces.addAll(u.spaces);
                    return new Union(newSpaces);
                }
                default:
                    return space.union(this);
            }
        }

        @Override
        public long volume() {
            Space countedAlready = new Empty();
            long volume = 0;
            for (Space space : spaces) {
                long diff = space.volume() - countedAlready.intersect(space).volume();
                volume += diff;
                countedAlready = countedAlready.union(space);
            }

            return volume;
        }
    }

    record Bounds(boolean on, Bound lower, Bound upper, List<Space> off) implements Space {
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
            // overlap in one dimension:
            // that:   min      max
            // this:          min          max

            final boolean xOverLap;
            if (that.lower.p.x < lower.p.x)
            {
                xOverLap = lower.p.x <= that.upper.p.x;
            }
            else
            {
                xOverLap = that.lower.p.x <= upper.p.x;
            }

            final boolean yOverLap;
            if (that.lower.p.y < lower.p.y)
            {
                yOverLap = lower.p.y <= that.upper.p.y;
            }
            else
            {
                yOverLap = that.lower.p.y <= upper.p.y;
            }

            final boolean zOverLap;
            if (that.lower.p.z < lower.p.z)
            {
                zOverLap = lower.p.z <= that.upper.p.z;
            }
            else
            {
                zOverLap = that.lower.p.z <= upper.p.z;
            }

            return (xOverLap && yOverLap && zOverLap);
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

        Space intersect(Bounds another) {
            if (overlapsWith(another)) {

                return newBounds(
                        true,
                        lower.max(another.lower),
                        upper.min(another.upper)
                );
            }

            return new Empty();
        }

        public long onCount() {
            if (!off.isEmpty()) {
                // Bounds initOff = null;
                Space allOff = new Empty();
                long offCount = 0;
                for (Space space : off) {
                    Space intersect = space.intersect(this);
                    Space alreadyOff = allOff.intersect(space);
                    long wouldTurnOffCount = intersect.volume();
                    long alreadyOffCount = alreadyOff.volume();
                    long oneOffDiff = Math.max(0, wouldTurnOffCount - alreadyOffCount);
                    if (oneOffDiff > volume())
                    {
                        // wtf
                        System.out.println("wtf");
                    }
                    offCount += oneOffDiff;
                    allOff = allOff.union(intersect);
                }

                return volume() - offCount;
            }

            return volume();
        }

        @Override
        public long volume() {
            final long xDim = (1 + (upper.p.x - lower.p.x));
            final long yDim = (1 + (upper.p.y - lower.p.y));
            final long zDim = (1 + (upper.p.z - lower.p.z));

            return xDim * yDim * zDim;
        }

        public void mask(Bounds another) {
            if (!another.on)
            {
                off.add(intersect(another));
            }
            else
            {
                off.add(intersect(another));
            }
        }

        @Override
        public Space intersect(Space space) {
            switch (space)
            {
                case Bounds b:
                    if (this.overlapsWith(b))
                    {
                        return intersect(b);
                    }
                    else
                    {
                        return new Empty();
                    }
                case Universe ignored:
                    return this;
                case Union union:
                {
                    Space result = this;
                    for (Space s : union.spaces) {
                        result = this.intersect(s);
                    }
                    return result;
                }
                case Empty empty:
                    return empty;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        @Override
        public Space union(Space another) {
            if (another instanceof Bounds b) {
                return new Union(List.of(this, b));
            }
            return another.union(this);
        }
    }

    record Input(List<Bounds> range)
    {

    }

    public static long onCubes(InputStream stream) throws IOException {
        final Input input = parseInput(stream);

        return partOne(input.range);
    }

    public static int partOne(List<Bounds> range) {
        final Set<Point> on = new HashSet<>();
        for (Bounds bounds : range) {
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
        for (Bounds bounds : input.range) {
            if (!bounds.outOfRange()) {
                if (bounds.on) {
                    bounds.forEach(on::add);
                } else {
                    bounds.forEach(on::remove);
                }
            }
        }

        List<Bounds> toProcess = input.range.stream().filter(Bounds::outOfRange).toList();
        final long outsideSum = sumOutsiders(toProcess);

        return on.size() + outsideSum;
    }

    static long sumOutsiders(List<Bounds> toProcess) {
        List<Bounds> outOfRange = new ArrayList<>();
        for (Bounds bounds : toProcess) {
            for (Bounds oor : outOfRange) {
                if (bounds.overlapsWith(oor)) {
                    oor.mask(bounds);
                }
            }

            if (bounds.on)
            {
                outOfRange.add(bounds);
            }
        }
        return outOfRange.stream().mapToLong(Bounds::onCount).sum();
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
