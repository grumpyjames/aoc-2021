package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Seven
{
    public static int fuelCostTwo(InputStream stream) throws IOException {
        return computeFuelCost(stream, Seven::moveCostTwo);
    }

    public static int fuelCost(InputStream stream) throws IOException {
        return computeFuelCost(stream, Seven::moveCost);
    }

    interface CostFunction
    {
        int cost(int positionOne, int positionTwo);
    }

    private static int computeFuelCost(InputStream stream, CostFunction cf)
            throws IOException {
        int[] crabPositions = Lines.parseLine(stream, line -> {
            String[] parts = line.split(",");
            int[] positions = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                positions[i] = Integer.parseInt(part);
            }
            return positions;
        });

        int fuelCost = Integer.MAX_VALUE;
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        int max = Arrays.stream(crabPositions).max().getAsInt();
        for (int i = 0; i < max; i++) {
            int thisCost = 0;
            for (int position : crabPositions) {
                thisCost += cf.cost(i, position);
            }
            fuelCost = Math.min(thisCost, fuelCost);
        }
        return fuelCost;
    }

    private static int moveCost(int i, int position) {
        return Math.abs(position - i);
    }

    private static int moveCostTwo(int positionOne, int positionTwo) {
        int distance = Math.abs(positionOne - positionTwo);
        int cost = 0;
        for (int i = 0; i <= distance; i++) {
             cost += i;
        }
        return cost;
    }
}
