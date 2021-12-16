package net.digihippo.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Sixteen {
    public static long parse(InputStream stream) throws IOException {
        return Lines.parseLine(stream, Sixteen::parse);
    }

    public static long compute(InputStream stream) throws IOException {
        return Lines.parseLine(stream, Sixteen::compute);
    }

    private sealed interface Packet permits Literal, Operator {
        long versionSum();
        long eval();
    }

    record Literal(int packetType, int packetVersion, long value) implements Packet {
        @Override
        public long versionSum() {
            return packetVersion;
        }

        @Override
        public long eval() {
            return value;
        }
    }

    record Operator(int packetType, int version, List<Packet> subpackets) implements Packet {
        @Override
        public long versionSum() {
            return version + subpackets.stream().mapToLong(Packet::versionSum).sum();
        }

        @Override
        public long eval() {
            switch (packetType) // polymorphism? What's that?
            {
                case 0:
                    return subpackets.stream().mapToLong(Packet::eval).sum();
                case 1:
                    long prod = 1;
                    for (Packet subpacket : subpackets) {
                        prod *= subpacket.eval();
                    }
                    return prod;
                case 2:
                    //noinspection OptionalGetWithoutIsPresent
                    return subpackets.stream().mapToLong(Packet::eval).min().getAsLong();
                case 3:
                    //noinspection OptionalGetWithoutIsPresent
                    return subpackets.stream().mapToLong(Packet::eval).max().getAsLong();
                case 5: {
                    final long zeroVal = subpackets.get(0).eval();
                    final long oneVal = subpackets.get(1).eval();
                    if (zeroVal > oneVal) {
                        return 1;
                    }
                    return 0;
                }
                case 6: {
                    final long zeroVal = subpackets.get(0).eval();
                    final long oneVal = subpackets.get(1).eval();
                    if (zeroVal < oneVal) {
                        return 1;
                    }
                    return 0;
                }
                case 7: {
                    final long zeroVal = subpackets.get(0).eval();
                    final long oneVal = subpackets.get(1).eval();
                    if (oneVal == zeroVal) {
                        return 1;
                    }
                    return 0;
                }
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    private static final class PacketParser {
        final String packet;
        int offset = 0;

        private PacketParser(String input) {
            this.packet = input;
        }

        public Packet parseVersioned() {
            final int packetVersion = readInt(3);
            return parse(packetVersion);
        }

        public Packet parse(int packetVersion) {
            final int packetType = readInt(3);
            if (packetType == 4) {
                boolean lastGroup = false;
                StringBuilder parts = new StringBuilder();
                while (!lastGroup) {
                    final String headBit =  packet.substring(offset, offset + 1);
                    offset++;
                    final String part = packet.substring(offset, offset + 4);
                    parts.append(part);
                    if (headBit.startsWith("0")) {
                        lastGroup = true;
                    }
                    offset += 4;
                }
                return new Literal(4, packetVersion, Long.parseLong(parts.toString(), 2));
            }

            // otherwise, operator packet (read in heavy Russian accent)
            final int lengthType = readInt(1);
            switch (lengthType) {
                case 0 -> {
                    final List<Packet> subpackets = new ArrayList<>();
                    final int subPacketLength = readInt(15);
                    int stop = offset + subPacketLength;
                    while (offset < stop) {
                        subpackets.add(parseVersioned());
                    }

                    return new Operator(packetType, packetVersion, subpackets);
                }
                case 1 -> {
                    final List<Packet> subpackets = new ArrayList<>();
                    final int packetCount = readInt(11);
                    for (int i = 0; i < packetCount; i++) {
                        subpackets.add(parseVersioned());
                    }

                    return new Operator(packetType, packetVersion, subpackets);
                }
                default -> throw new UnsupportedOperationException();
            }
        }

        private int readInt(int length) {
            final int packetType = Integer.parseInt(packet.substring(offset, offset + length), 2);
            offset += length;
            return packetType;
        }

    }

    public static long parse(String packetAsString) {
        final String packet = binaryString(packetAsString);
        final PacketParser packetParser = new PacketParser(packet);
        final Packet parsed = packetParser.parseVersioned();

        return parsed.versionSum();
    }

    public static long compute(String packetAsString) {
        final String packet = binaryString(packetAsString);
        final PacketParser packetParser = new PacketParser(packet);
        final Packet parsed = packetParser.parseVersioned();

        return parsed.eval();
    }

    private static String binaryString(String packetAsString) {
        final StringBuilder binaryString = new StringBuilder();
        for (int i = 0; i < packetAsString.length(); i++) {
            char c = packetAsString.charAt(i);
            switch (c) {
                case '0' -> binaryString.append("0000");
                case '1' -> binaryString.append("0001");
                case '2' -> binaryString.append("0010");
                case '3' -> binaryString.append("0011");
                case '4' -> binaryString.append("0100");
                case '5' -> binaryString.append("0101");
                case '6' -> binaryString.append("0110");
                case '7' -> binaryString.append("0111");
                case '8' -> binaryString.append("1000");
                case '9' -> binaryString.append("1001");
                case 'A' -> binaryString.append("1010");
                case 'B' -> binaryString.append("1011");
                case 'C' -> binaryString.append("1100");
                case 'D' -> binaryString.append("1101");
                case 'E' -> binaryString.append("1110");
                case 'F' -> binaryString.append("1111");
            }
        }

        return binaryString.toString();
    }
}
