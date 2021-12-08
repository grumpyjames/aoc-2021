package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Eight
{
    public static long what(InputStream stream) throws IOException {
        final List<IO> inputs = parse(stream);

        return inputs
                .stream()
                .map(io -> io.output)
                .flatMap(Collection::stream)
                .filter(Note::isOneFourSevenOrEight)
                .count();
    }

    public static int solve(InputStream stream) throws IOException {
        final List<Map<Character, Rail>> poss = allPossibleArrangements();
        return parse(stream)
                .stream()
                .mapToInt(io -> io.interpret(poss))
                .sum();
    }

    private enum Rail
    {
        Top,
        Left_Top,
        Left_Bottom,
        Right_Top,
        Right_Bottom,
        Middle,
        Bottom
    }

    private static final Map<EnumSet<Rail>, Integer> MAPPING = new HashMap<>();
    static
    {
        MAPPING.put(
                EnumSet.of(Rail.Top, Rail.Left_Top, Rail.Right_Top, Rail.Left_Bottom, Rail.Right_Bottom, Rail.Bottom),
                0);
        MAPPING.put(
                EnumSet.of(Rail.Right_Top, Rail.Right_Bottom),
                1);
        MAPPING.put(
                EnumSet.of(Rail.Top, Rail.Right_Top, Rail.Middle, Rail.Left_Bottom, Rail.Bottom),
                2);
        MAPPING.put(
                EnumSet.of(Rail.Top, Rail.Right_Top, Rail.Middle, Rail.Right_Bottom, Rail.Bottom),
                3);
        MAPPING.put(
                EnumSet.of(Rail.Left_Top, Rail.Right_Top, Rail.Middle, Rail.Right_Bottom),
                4);
        MAPPING.put(
                EnumSet.of(Rail.Top, Rail.Left_Top, Rail.Middle, Rail.Right_Bottom, Rail.Bottom),
                5);
        MAPPING.put(
                EnumSet.of(Rail.Top, Rail.Left_Top, Rail.Middle, Rail.Left_Bottom, Rail.Right_Bottom, Rail.Bottom),
                6);
        MAPPING.put(
                EnumSet.of(Rail.Top, Rail.Right_Top, Rail.Right_Bottom),
                7);
        MAPPING.put(
                EnumSet.allOf(Rail.class),
                8);
        MAPPING.put(
                EnumSet.of(Rail.Top, Rail.Left_Top, Rail.Right_Top, Rail.Middle, Rail.Right_Bottom, Rail.Bottom),
                9);
    }

    private sealed interface Constraint permits None, Must, OneOf, And {
        boolean satisfiedBy(Map<Character, Rail> assignment);
    }

    private record None() implements Constraint {
        @Override
        public boolean satisfiedBy(Map<Character, Rail> assignment) {
            return true;
        }
    }

    private record Must(char c, Rail rail) implements Constraint {
        @Override
        public boolean satisfiedBy(Map<Character, Rail> assignment) {
            return assignment.get(c) == rail;
        }
    }

    private record OneOf(List<Constraint> constraints) implements Constraint {
        @Override
        public boolean satisfiedBy(Map<Character, Rail> assignment) {
            int count = 0;
            for (Constraint constraint : constraints) {
                if (constraint.satisfiedBy(assignment))
                {
                    count++;
                }
            }

            return count == 1;
        }
    }

    private record And(List<Constraint> constraints) implements Constraint {
        @Override
        public boolean satisfiedBy(Map<Character, Rail> assignment) {
            return constraints.stream().allMatch(c -> c.satisfiedBy(assignment));
        }
    }

    record IO(List<Note> input, List<Note> output)
    {
        Constraint constraint()
        {
            return new And(input
                    .stream()
                    .map(Note::constraint)
                    .collect(Collectors.toList()));
        }

        public Map<Character, Rail> chooseFirst(List<Map<Character, Rail>> possibilities) {
            return possibilities
                    .stream()
                    .filter(m -> satisfied(m, this.constraint()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No arrangement satisfied for " + this));
        }

        public int interpret(List<Map<Character, Rail>> possibilities)
        {
            return interpret(chooseFirst(possibilities));
        }

        private boolean satisfied(Map<Character, Rail> m, Constraint constraint) {
            return constraint.satisfiedBy(m) && interpretable(m);
        }

        private boolean interpretable(Map<Character, Rail> m) {
            return output.stream().allMatch(n -> n.interpretableBy(m));
        }

        private int interpret(Map<Character, Rail> map) {
            int result = 0;
            for (int i = 0; i < 4; i++) {
                int powerOfTen = 3 - i;
                Note note = output.get(i);
                result += (Math.pow(10, powerOfTen) * note.interpret(map));
            }
            return result;
        }
    }

    record Note(String active)
    {
        boolean isOneFourSevenOrEight()
        {
            return active.length() == 2 ||
                    active.length() == 3 ||
                    active.length() == 4 ||
                    active.length() == 7;
        }

        public Constraint constraint() {

            if (active.length() == 2)
            {
                return permutations(active, Rail.Right_Top, Rail.Right_Bottom);
            }
            else if (active.length() == 3)
            {
                return permutations(active, Rail.Top, Rail.Right_Top, Rail.Right_Bottom);
            }
            else if (active.length() == 4)
            {
                return permutations(active, Rail.Left_Top, Rail.Right_Top, Rail.Middle, Rail.Right_Bottom);
            }

            return new None();
        }

        public int interpret(Map<Character, Rail> map) {
            final EnumSet<Rail> on = computeRails(map);
            final Integer integer = MAPPING.get(on);
            if (integer == null)
            {
                throw new IllegalStateException("wtf: " + on);
            }
            return integer;
        }

        private EnumSet<Rail> computeRails(Map<Character, Rail> map) {
            final EnumSet<Rail> on = EnumSet.noneOf(Rail.class);
            for (int i = 0; i < active.length(); i++) {
                char c = active.charAt(i);
                on.add(map.get(c));
            }
            return on;
        }

        public boolean interpretableBy(Map<Character, Rail> m) {
            final EnumSet<Rail> on = computeRails(m);
            return MAPPING.containsKey(on);
        }
    }

    static Constraint permutations(String expr, Rail... rails) {
        assert (expr.length() == rails.length);
        final List<Constraint> result = new ArrayList<>();
        for (int[] perm : perms(expr)) {
            final List<Constraint> constraints = new ArrayList<>(perm.length);
            for (int i = 0; i < perm.length; i++) {
                int index = perm[i];
                constraints.add(new Must(expr.charAt(index), rails[i]));
            }
            result.add(new And(constraints));
        }

        return new OneOf(result);
    }

    static List<Map<Character, Rail>> allPossibleArrangements() {
        final String chars = "abcdefg";
        final Rail[] rails = Rail.values();
        final List<Map<Character, Rail>> result = new ArrayList<>();
        for (int[] perm : perms(chars)) {
            final Map<Character, Rail> assignments = new HashMap<>();
            for (int i = 0; i < perm.length; i++) {
                int index = perm[i];
                assignments.put(chars.charAt(index), rails[i]);
            }
            result.add(assignments);
        }

        return result;
    }

    private static int[][] perms(String expr) {
        int[] ints = new int[expr.length()];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = i;
        }
        return Permutations.permutationsPlease(ints);
    }

    private static List<IO> parse(InputStream stream) throws IOException {
        return Lines.parseLines(stream, line -> {
            final String[] io = line.split("\\|");

            return new IO(toNotes(io[0]), toNotes(io[1]));
        });
    }

    private static List<Note> toNotes(String s)
    {
        return Arrays
                .stream(s.trim().split(" "))
                .map(String::trim)
                .map(Note::new)
                .collect(Collectors.toList());
    }
}
