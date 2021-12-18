package net.digihippo.aoc;

import net.digihippo.aoc.Eighteen.Literal;
import net.digihippo.aoc.Eighteen.Pair;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EighteenTest {
    @Test
    void parsing() {
        assertEquals(
                new Pair(new Literal(5), new Literal(6)),
                Eighteen.parse("[5,6]"));
    }

    @Test
    void moreParsing() {
        assertEquals(
                new Pair(new Pair(new Literal(5), new Literal(6)), new Literal(8)),
                Eighteen.parse("[[5,6],8]"));
    }

    @Test
    void explode() {
        assertExplodes(
                "[[[[0,9],2],3],4]",
                "[[[[[9,8],1],2],3],4]"
        );
    }

    @Test
    void explodeRight() {
        assertEquals(
                Eighteen.parse("[7,[6,[5,[7,0]]]]"),
                Eighteen.explode(Eighteen.parse("[7,[6,[5,[4,[3,2]]]]]"))
        );
    }

    @Test
    void explodeMiddle() {
        assertEquals(
                "[[6,[5,[7,0]]],3]",
                Eighteen.print(Eighteen.explode(Eighteen.parse("[[6,[5,[4,[3,2]]]],1]")))
        );
    }

    @Test
    void explodeFirstOfDepthThenStop() {
        assertExplodes("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]", "[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]");
    }

    private void assertExplodes(String output, String input) {
        assertEquals(
                output,
                Eighteen.print(Eighteen.explode(Eighteen.parse(input))
                ));
    }

    @Test
    void otherExplodeCases() {
        /* [[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]] becomes [[3,[2,[8,0]]],[9,[5,[7,0]]]]. */
        assertEquals(
                "[[3,[2,[8,0]]],[9,[5,[7,0]]]]",
                Eighteen.print(Eighteen.explode(Eighteen.parse("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")))
        );
    }

    @Test
    void explodeFail() {
        assertExplodes("[[[[0,7],4],[15,[0,13]]],[1,1]]", "[[[[0,7],4],[7,[[8,4],9]]],[1,1]]");
    }

    @Test
    void simplestSplit() {
        assertEquals(
                "[[5,5],1]",
                Eighteen.print(Eighteen.split(new Eighteen.Pair(new Literal(10), new Literal(1))))
        );
    }

    @Test
    void anotherSplit() {
        assertEquals(
                "[[5,6],1]",
                Eighteen.print(Eighteen.split(new Eighteen.Pair(new Literal(11), new Literal(1))))
        );
    }

    @Test
    void bananaSplit() {
        assertEquals(
                "[[6,6],1]",
                Eighteen.print(Eighteen.split(new Eighteen.Pair(new Literal(12), new Literal(1))))
        );
    }

    @Test
    void reduceCases() {
        assertReduces("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]", "[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]");
    }

    @Test
    void examplePartOne() throws IOException {
        String expected = "[[[[1,1],[2,2]],[3,3]],[4,4]]";
        String input = """
                [1,1]
                [2,2]
                [3,3]
                [4,4]""";
        assertSum(expected, input);
    }

    @Test
    void examplePartOneA() throws IOException {

        assertSum("[[[[3,0],[5,3]],[4,4]],[5,5]]",
                """
                [1,1]
                [2,2]
                [3,3]
                [4,4]
                [5,5]""");
    }

    @Test
    void examplePartOneB() throws IOException {

        assertSum("[[[[5,0],[7,4]],[5,5]],[6,6]]",
                """
                [1,1]
                [2,2]
                [3,3]
                [4,4]
                [5,5]
                [6,6]""");
    }

    @Test
    void examplePartOneC() throws IOException {
        assertSum(
                "[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]",
                """
                   [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
                   [7,[[[3,7],[4,3]],[[6,3],[8,8]]]]""");
    }

    @Test
    void examplePartOneD() throws IOException {
        assertSum(
                "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]",
                """
                   [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
                   [7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
                   [[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
                   [[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
                   [7,[5,[[3,8],[1,4]]]]
                   [[2,[2,2]],[8,[8,1]]]
                   [2,9]
                   [1,[[[9,3],9],[[9,0],[0,7]]]]
                   [[[5,[7,4]],7],1]
                   [[[[4,2],2],6],[8,7]]"""
        );
    }

    @Test
    void magnitude() {
        assertMagnitude("[1,9]", 21);
        assertMagnitude("[9,1]", 29);
        assertMagnitude("[[9,1],[1,9]]", 129);
        assertMagnitude("[[1,2],[[3,4],5]]", 143);
        assertMagnitude("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]", 3488);
    }

    @Test
    void magnitudePartOne() throws IOException {

        assertEquals(4140, Eighteen.magnitude(Eighteen.sum(Inputs.asInputStream("""
                [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
                [[[5,[2,8]],4],[5,[[9,9],0]]]
                [6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
                [[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
                [[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
                [[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
                [[[[5,4],[7,7]],8],[[8,3],8]]
                [[9,3],[[9,9],[6,[4,9]]]]
                [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
                [[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]"""))));
    }

    @Test
    void partOne() throws IOException {
        System.out.println(Eighteen.magnitude(Eighteen.sum(Inputs.puzzleInput("eighteen.txt"))));
    }

    @Test
    void examplePartTwo() throws IOException {
        assertEquals(
                3993,
                Eighteen.largestMagnitude(Inputs.asInputStream("""
                        [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
                        [[[5,[2,8]],4],[5,[[9,9],0]]]
                        [6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
                        [[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
                        [[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
                        [[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
                        [[[[5,4],[7,7]],8],[[8,3],8]]
                        [[9,3],[[9,9],[6,[4,9]]]]
                        [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
                        [[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
                        """))
        );
    }

    @Test
    void partTwo() throws IOException {
        System.out.println(Eighteen.largestMagnitude(Inputs.puzzleInput("eighteen.txt")));
    }

    private void assertMagnitude(String input, int magnitude) {
        assertEquals(magnitude, Eighteen.magnitude(Eighteen.parse(input)));
    }

    private void assertSum(String expected, String input) throws IOException {
        assertEquals(expected, Eighteen.print(Eighteen.sum(Inputs.asInputStream(input))));
    }

    private void assertReduces(String output, String input) {
        assertEquals(
                output,
                Eighteen.print(Eighteen.reduce(Eighteen.parse(input)))
        );
    }
}