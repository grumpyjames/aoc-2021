package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Ten {
    public static int syntaxErrorScore(InputStream stream) throws IOException {
        final StringConsumer consumer = new StringConsumer();
        Lines.processLines(stream, consumer::checkSyntax);

        return consumer.score;
    }

    public static long middleScore(InputStream stream) throws IOException {
        final StringConsumer consumer = new StringConsumer();

        Lines.processLines(stream, consumer::checkUnclosed);

        return consumer.middleScore();
    }

    private static class StringConsumer {
        int score = 0;
        final List<Long> closeScores = new ArrayList<>();

        public void checkSyntax(String s) {
            final Stack<Character> stack = new Stack<>();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (isOpener(c))
                {
                    stack.push(c);
                }
                else
                {
                    final Character popped = stack.pop();
                    if (!closes(popped, c))
                    {
                        System.out.println("Character " + c + " at index " + i + " doesn't close " + popped);
                        this.score += score(c);
                        return;
                    }
                }
            }
        }

        public void checkUnclosed(String s) {
            final Stack<Character> stack = new Stack<>();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (isOpener(c))
                {
                    stack.push(c);
                }
                else
                {
                    final Character popped = stack.pop();
                    if (!closes(popped, c))
                    {
                        System.out.println("Character " + c + " at index " + i + " doesn't close " + popped);
                        return;
                    }
                }
            }

            if (!stack.isEmpty()) {
                long closeScore = 0;
                while (!stack.empty()) {
                    final char c = stack.pop();
                    closeScore *= 5;
                    closeScore += closePlus(c);
                }

                closeScores.add(closeScore);
            }
        }

        private int closePlus(char c) {
            return switch (c) {
                case '{' -> 3;
                case '(' -> 1;
                case '<' -> 4;
                case '[' -> 2;
                default -> throw new IllegalStateException("Unmapped: " + c);
            };
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        private boolean closes(char opener, char maybeCloser)
        {
            return
                    (opener == '(' && maybeCloser == ')') ||
                    (opener == '[' && maybeCloser == ']') ||
                    (opener == '{' && maybeCloser == '}') ||
                    (opener == '<' && maybeCloser == '>');
        }

        private boolean isOpener(char c) {
            return c == '{' || c == '(' || c == '<' || c == '[';
        }

        private int score(char closer) {
            return switch (closer) {
                case '}' -> 1197;
                case ')' -> 3;
                case '>' -> 25137;
                case ']' -> 57;
                default -> throw new IllegalStateException("Unmapped: " + closer);
            };
        }

        public long middleScore() {
            closeScores.sort(Long::compare);

            return closeScores.get(closeScores.size() / 2);
        }
    }
}
