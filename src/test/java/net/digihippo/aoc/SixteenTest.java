package net.digihippo.aoc;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SixteenTest {
    @Test
    void examplePartOneA() {
        assertEquals(6, Sixteen.parse("D2FE28"));
    }

    @Test
    void examplePartOneB() {
        assertEquals(9, Sixteen.parse("38006F45291200"));
    }

    @Test
    void examplePartOneC() {
        assertEquals(14, Sixteen.parse("EE00D40C823060"));
    }

    @Test
    void examplePartOneD() {
        assertEquals(16, Sixteen.parse("8A004A801A8002F478"));
        assertEquals(12, Sixteen.parse("620080001611562C8802118E34"));
        assertEquals(23, Sixteen.parse("C0015000016115A2E0802F182340"));
        assertEquals(31, Sixteen.parse("A0016C880162017C3686B18A3D4780"));
    }

    @Test
    void partOne() throws IOException {
        System.out.println(Sixteen.parse(Inputs.puzzleInput("sixteen.txt")));
    }

    @Test
    void examplePartTwo() {
        assertEquals(3, Sixteen.compute("C200B40A82"));
        assertEquals(54, Sixteen.compute("04005AC33890"));
        assertEquals(7, Sixteen.compute("880086C3E88112"));
        assertEquals(9, Sixteen.compute("CE00C43D881120"));
        assertEquals(1, Sixteen.compute("D8005AC2A8F0"));
        assertEquals(0, Sixteen.compute("F600BC2D8F"));
        assertEquals(0, Sixteen.compute("9C005AC2F8F0"));
        assertEquals(1, Sixteen.compute("9C0141080250320F1802104A08"));
    }

    @Test
    void partTwo() throws IOException {
        System.out.println(Sixteen.compute(Inputs.puzzleInput("sixteen.txt")));
    }
}