package net.digihippo.aoc;

import java.util.function.Consumer;

public final class Permutations
{
    static void permutations(int[] input, Consumer<int[]> consumer) {
        genAllRecursive(input.length, input, consumer);
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
