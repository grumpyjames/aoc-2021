package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fourteen {
    static final class Input {
        final String firstBase;
        final String lastBase;
        Map<String, Long> pairCount;
        final Map<String, List<String>> rules;

        Input(
            String firstBase,
            String lastBase,
            Map<String, Long> pairCount,
            Map<String, List<String>> rules) {
            this.firstBase = firstBase;
            this.lastBase = lastBase;
            this.pairCount = pairCount;
            this.rules = rules;
        }

        public void step() {
            final Map<String, Long> newEntries = new HashMap<>();
            for (Map.Entry<String, Long> entry: pairCount.entrySet()) {
                List<String> pairs = rules.get(entry.getKey());
                for (String pair : pairs) {
                    newEntries.put(
                            pair,
                            newEntries.getOrDefault(pair, 0L) + entry.getValue());
                }
            }
            this.pairCount = newEntries;
        }

        public long score() {
            Map<String, Long> counts = new HashMap<>();
            for (Map.Entry<String, Long> stringLongEntry : pairCount.entrySet()) {
                String key = stringLongEntry.getKey();
                String firstHalf = key.substring(0, 1);
                String secondHalf = key.substring(1, 2);
                counts.put(firstHalf,
                        counts.getOrDefault(firstHalf, 0L) + stringLongEntry.getValue());
                counts.put(secondHalf,
                        counts.getOrDefault(secondHalf, 0L) + stringLongEntry.getValue());
            }
            counts.replaceAll((k, v) -> counts.get(k) / 2);
            counts.put(firstBase, counts.getOrDefault(firstBase, 0L) + 1);
            counts.put(lastBase, counts.getOrDefault(lastBase, 0L) + 1);

            List<Map.Entry<String, Long>> entries =
                    counts.entrySet().stream().sorted(Map.Entry.comparingByValue()).toList();

            return entries.get(entries.size() - 1).getValue() - entries.get(0).getValue();
        }
    }

    public static long applyPolymer(int steps, InputStream stream) throws IOException {
        Input input = Lines.parseLines(stream, new InputParser());

        for (int i = 0; i < steps; i++) {
            input.step();
        }

        return input.score();
    }

    private static final class InputParser implements Lines.Parser<Input> {
        boolean ruleMode = false;
        private String whole = null;
        private final Map<String, Long> template = new HashMap<>();
        private final Map<String, List<String>> rules = new HashMap<>();

        @Override
        public void onLine(String line) {
            if (line.isBlank()) {
                ruleMode = true;
                return;
            }

            if (ruleMode) {
                ArrayList<String> generated = new ArrayList<>();
                String pair = line.substring(0, 2);
                String base = line.substring(6, 7);
                generated.add(pair.charAt(0) + base);
                generated.add(base + pair.charAt(1));
                rules.put(
                        pair,
                        generated);
            } else {
                for (int i = 0; i < line.length() - 1; i++) {
                    String pair = line.substring(i, i + 2);
                    template.putIfAbsent(pair, 0L);
                    template.put(pair, template.get(pair) + 1L);
                }
                whole = line.strip();
            }
        }

        @Override
        public Input build() {
            return new Input(
                    whole.substring(0, 1),
                    whole.substring(whole.length() - 1),
                    template,
                    rules);
        }
    }
}
