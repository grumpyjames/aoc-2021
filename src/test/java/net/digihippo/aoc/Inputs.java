package net.digihippo.aoc;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

final class Inputs
{
    static ByteArrayInputStream asInputStream(String input) {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }
}
