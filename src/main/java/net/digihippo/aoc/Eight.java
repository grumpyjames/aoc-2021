package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Eight
{
    record IO(List<Note> input, List<Note> output)
    {

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
    }

    private static List<Note> toNotes(String s)
    {
        return Arrays.stream(s.split(" ")).map(String::trim).map(Note::new).collect(Collectors.toList());
    }

    public static long what(InputStream stream) throws IOException {
        final List<IO> inputs = Lines.parseLines(stream, line -> {
            final String[] io = line.split("\\|");

            return new IO(toNotes(io[0]), toNotes(io[1]));
        });

        return inputs
                .stream()
                .map(io -> io.output)
                .flatMap(Collection::stream)
                .filter(Note::isOneFourSevenOrEight)
                .count();
    }
}
