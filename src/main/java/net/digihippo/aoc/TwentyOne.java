package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwentyOne {
    static class GameCounter
    {
        long games = 0;
        long playerOneWins = 0;
        long playerTwoWins = 0;
        long frequency;

        void playerOneWins() {
            playerOneWins += frequency;
        }

        void playerTwoWins() {
            playerTwoWins += frequency;
        }

        public void setFrequency(long frequency) {
            this.frequency = frequency;
        }

        public long higher() {
            return Math.max(playerOneWins, playerTwoWins);
        }

        public void runningGame() {
            ++games;
        }
    }

    public static long partTwo(InputStream stream, int winningScore) throws IOException {
        List<Player> players = parsePlayers(stream, winningScore);

        int[] rollFrequencies = new int[] {1, 3, 6, 7, 6, 3, 1};
        int[] rollNumbers     = new int[] {3, 4, 5, 6, 7, 8, 9};

        final GameCounter gameCounter = new GameCounter();
        final Die die = new Die() {
            private int[] ints;
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < ints.length;
            }

            @Override
            public int next() {
                return ints[index++];
            }

            @Override
            public void reset(int[] ints) {
                this.ints = ints;
                index = 0;
            }
        };

        runGames(players, 1, new int[] {}, rollNumbers, rollFrequencies, gameCounter, die);

        return gameCounter.higher();
    }

    private static void runGames(
            List<Player> players,
            long frequency,
            int[] rolls,
            int[] rollNumbers,
            int[] rollFrequencies,
            GameCounter gameCounter,
            Die die) {
        int[] newRolls = new int[rolls.length + 1];
        if (rolls.length > 0)
        {
            System.arraycopy(rolls, 0, newRolls, 0, rolls.length);
        }

        for (int i = 0; i < rollNumbers.length; i++) {
            int rollNumber = rollNumbers[i];
            int rollFrequency = rollFrequencies[i];

            newRolls[rolls.length] = rollNumber;
            final long newFrequency = rollFrequency * frequency;
            gameCounter.setFrequency(newFrequency);

            die.reset(newRolls);
            boolean complete = runGame(players, die, gameCounter);

            if (!complete) {
                runGames(
                        players,
                        newFrequency,
                        newRolls,
                        rollNumbers,
                        rollFrequencies,
                        gameCounter,
                        die);
            }
//            else
//            {
//                System.out.println("These rolls yield a result: " + Arrays.toString(newRolls));
//            }

        }
    }

    private static boolean runGame(List<Player> players, Die die, GameCounter gameCounter) {
        players.forEach(Player::reset);
        int moveIndex = 0;
        gameCounter.runningGame();
        while (die.hasNext())
        {
            final int playerIndex = moveIndex % 2;
            final boolean won = players
                    .get(playerIndex)
                    .moveOnce(die);
            if (won)
            {
                if (playerIndex == 0) {
                    gameCounter.playerOneWins();
                } else {
                    gameCounter.playerTwoWins();
                }
                return true;
            }

            ++moveIndex;
        }

        return false;
    }

    public static int partOne(InputStream stream) throws IOException {
        List<Player> players = parsePlayers(stream, 1000);

        int move = 1;
        final Die die = new Die() {
            int off = 1;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public int next() {
                int result = ((3 * off) + 3) % 100;

                off = (off + 3) % 100;

                return result;
            }

            @Override
            public void reset(int[] ints) {

            }
        };
        while (true)
        {
            final int playerIndex = (move - 1) % 2;
            Player player = players.get(playerIndex);
            final boolean winner = player.moveOnce(die);
            if (winner) {
                if (playerIndex == 0)
                {
                    return players.get(1).score * move * 3;
                }
                else
                {
                    return players.get(0).score * move * 3;
                }
            }
            ++move;
        }
    }

    private static List<Player> parsePlayers(InputStream input, int winningScore) throws IOException {
        final Pattern p = Pattern.compile("Player ([0-9]) starting position: ([0-9]+)");
        return Lines.parseLines(input, l -> {
            final Matcher matcher = p.matcher(l);
            if (matcher.find()) {
                return new Player(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), winningScore);
            }
            throw new UnsupportedOperationException();
        });
    }

    interface Die
    {
        boolean hasNext();

        int next();

        void reset(int[] ints);
    }

    private static final class Player
    {
        private final int index;

        private final int originalPosition;
        private final int winningScore;

        private int position;
        private int score;

        private Player(
                int index,
                int position,
                int winningScore) {
            this.index = index;
            this.position = position - 1;
            this.originalPosition = position - 1;
            this.winningScore = winningScore;
        }

        public void reset() {
            this.position = originalPosition;
            this.score = 0;
        }

        public boolean moveOnce(Die die) {
            final int next = die.next();
            int newPosition = (position + next) % 10;
            position = newPosition;
            score += newPosition + 1;
            return score >= winningScore;
        }

        @Override
        public String toString() {
            return "Player " + index + " position " + (position + 1) + " score " + score;
        }
    }

}
