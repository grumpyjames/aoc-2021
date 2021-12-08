package net.digihippo.aoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

final class Lines
{
    static void processLines(InputStream inputStream, Consumer<String> callback) throws IOException {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                callback.accept(line);
            }
        }
    }

    static <T> List<T> parseLines(InputStream inputStream, Function<String, T> callback) throws IOException {
        final List<T> result = new ArrayList<>();
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.add(callback.apply(line));
            }
        }

        return result;
    }

    static <T> T parseLine(InputStream inputStream, Function<String, T> callback) throws IOException {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = bufferedReader.readLine();
            return callback.apply(line);
        }
    }
}
