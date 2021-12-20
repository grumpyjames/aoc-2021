package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Twenty {
    record Input(String algorithm, char[][] image) {
        public Input enhance(int stepIndex) {
            final char zerothChar = algorithm.charAt(0);
            final char defaultChar;

            if (zerothChar == '.')
            {
                defaultChar = '0';
            }
            else
            {
                defaultChar = stepIndex % 2 == 0 ? '0' : '1';
            }


            final char[][] next = new char[image.length + 2][];

            for (int y = 0; y < next.length; y++) {
                next[y] = new char[image[0].length + 2];
                for (int x = 0; x < next[y].length; x++) {
                    String binaryInput = readSquare(image, x, y, defaultChar);
                    final int readIndex = Integer.parseInt(binaryInput, 2);
                    final char enhanced = algorithm.charAt(readIndex);

                    next[y][x] = enhanced;
                }
            }

            return new Input(algorithm, next);
        }

        private String readSquare(char[][] image, int x, int y, char defaultChar) {
            // remember: 0,0 is -1,-1
            StringBuilder b = new StringBuilder();

            for (int dy = -1; dy < 2; dy++) {
                for (int dx = -1; dx < 2; dx++) {
                    int xInImage = (x + dx) - 1;
                    int yInImage = (y + dy) - 1;
                    if (0 <= xInImage && xInImage < image[0].length && 0 <= yInImage && yInImage < image.length)
                    {
                        b.append(image[yInImage][xInImage] == '#' ? '1' : '0');
                    }
                    else
                    {
                        b.append(defaultChar);
                    }
                }
            }

            return b.toString();
        }

        public void print(PrintStream out) {
            for (char[] chars : image) {
                for (char aChar : chars) {
                    out.print(aChar);
                }
                out.println();
            }
        }

        public int pixelCount() {
            int result = 0;
            for (char[] chars : image) {
                for (char aChar : chars) {
                    if (aChar == '#')
                    {
                        ++result;
                    }
                }
            }

            return result;
        }
    }

    public static int litPixels(int enhancements, InputStream stream) throws IOException {
        Input i = Lines.parseLines(stream, new Lines.Parser<>() {
            boolean algo = true;

            String algorithm = "";

            final List<char[]> image = new ArrayList<>();

            @Override
            public void onLine(String string) {

                if (string.isBlank())
                {
                    algo = false;
                }

                if (algo)
                {
                    algorithm += string.trim();
                    return;
                }

                if (!string.isBlank())
                {
                    image.add(string.toCharArray());
                }
            }

            @Override
            public Input build() {
                return new Input(algorithm, image.toArray(new char[][]{}));
            }
        });

        i.print(System.out);
        System.out.println();
        for (int j = 0; j < enhancements; j++) {
            i = i.enhance(j);
            i.print(System.out);
            System.out.println();
        }


        return i.pixelCount();
    }
}
