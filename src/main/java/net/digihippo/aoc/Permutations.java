package net.digihippo.aoc;

import java.util.function.Consumer;

public final class Permutations
{
    static int[][] permutationsPlease(int[] input) {
        final int[][] actual = new int[factorial(input.length)][];
        genAllRecursive(input.length, input, new Consumer<>() {
            int index = 0;

            @Override
            public void accept(int[] ints) {
                final int[] copy = new int[ints.length];
                System.arraycopy(ints, 0, copy, 0, ints.length);
                actual[index] = copy;
                index++;
            }
        });
        return actual;
    }

    private static int factorial(int i)
    {
        if (i == 0)
        {
            return 1;
        }
        return i * factorial(i - 1);
    }

    private static void genAllRecursive(
            int n, int[] elements, Consumer<int[]> c) {

        if (n == 1) {
            c.accept(elements);
        } else {
            for (int i = 0; i < n - 1; i++) {
                genAllRecursive(n - 1, elements, c);
                if (n % 2 == 0) {
                    swap(elements, i, n - 1);
                } else {
                    swap(elements, 0, n - 1);
                }
            }
            genAllRecursive(n - 1, elements, c);
        }
    }

    private static void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }

}
