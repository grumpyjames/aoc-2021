package net.digihippo.aoc;

import net.digihippo.aoc.Nineteen.Observation;
import net.digihippo.aoc.Nineteen.Point;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static net.digihippo.aoc.Nineteen.ROTATORS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NineteenTest {
    @Test
    void matrices()
    {
        Nineteen.Matrix matrix = Nineteen.parse("""
                1 0 0
                0 1 0
                0 0 1""");

        Point p = new Point(0, 0, 0);

        assertEquals(p, matrix.apply(p));
    }

    @Test
    void inversions() {
        for (Nineteen.Matrix rotator : ROTATORS) {
            Point input = new Point(1, 3, 2);
            Point p = rotator.apply(rotator.invert().apply(input));
            assertEquals(input, p);
        }
    }

    @Test
    void inversionsAgain() {
        Nineteen.Matrix initial = ROTATORS.get(8);
        Nineteen.Matrix rot = initial.invert();

        System.out.println(Arrays.toString(initial.parts()[0]));
        System.out.println(Arrays.toString(initial.parts()[1]));
        System.out.println(Arrays.toString(initial.parts()[2]));

        System.out.println(Arrays.toString(rot.parts()[0]));
        System.out.println(Arrays.toString(rot.parts()[1]));
        System.out.println(Arrays.toString(rot.parts()[2]));
    }

    @Test
    void permutationsAgain() {
        Point p = new Point(1, 2, 3);
        Set<Point> ps = new HashSet<>();
        for (Nineteen.Matrix value : ROTATORS) {
            ps.add(value.apply(p));
        }

        assertEquals(24, ps.size());
    }

    @Test
    void findBeaconsExampleOne() throws IOException {
        final String example = """
                --- scanner 0 ---
                0,2
                4,1
                3,3
                                
                --- scanner 1 ---
                -1,-1
                -5,0
                -2,1""";
        assertEquals(3, Nineteen.beacons(3, Inputs.asInputStream(example)));
    }

    @Test
    void findBeaconsExampleTwo() throws IOException {
        final String anotherExample = """
                --- scanner 0 ---
                404,-588,-901
                528,-643,409
                -838,591,734
                390,-675,-793
                -537,-823,-458
                -485,-357,347
                -345,-311,381
                -661,-816,-575
                -876,649,763
                -618,-824,-621
                553,345,-567
                474,580,667
                -447,-329,318
                -584,868,-557
                544,-627,-890
                564,392,-477
                455,729,728
                -892,524,684
                -689,845,-530
                423,-701,434
                7,-33,-71
                630,319,-379
                443,580,662
                -789,900,-551
                459,-707,401
                                
                --- scanner 1 ---
                686,422,578
                605,423,415
                515,917,-361
                -336,658,858
                95,138,22
                -476,619,847
                -340,-569,-846
                567,-361,727
                -460,603,-452
                669,-402,600
                729,430,532
                -500,-761,534
                -322,571,750
                -466,-666,-811
                -429,-592,574
                -355,545,-477
                703,-491,-529
                -328,-685,520
                413,935,-424
                -391,539,-444
                586,-435,557
                -364,-763,-893
                807,-499,-711
                755,-354,-619
                553,889,-390
                                
                --- scanner 2 ---
                649,640,665
                682,-795,504
                -784,533,-524
                -644,584,-595
                -588,-843,648
                -30,6,44
                -674,560,763
                500,723,-460
                609,671,-379
                -555,-800,653
                -675,-892,-343
                697,-426,-610
                578,704,681
                493,664,-388
                -671,-858,530
                -667,343,800
                571,-461,-707
                -138,-166,112
                -889,563,-600
                646,-828,498
                640,759,510
                -630,509,768
                -681,-892,-333
                673,-379,-804
                -742,-814,-386
                577,-820,562
                                
                --- scanner 3 ---
                -589,542,597
                605,-692,669
                -500,565,-823
                -660,373,557
                -458,-679,-417
                -488,449,543
                -626,468,-788
                338,-750,-386
                528,-832,-391
                562,-778,733
                -938,-730,414
                543,643,-506
                -524,371,-870
                407,773,750
                -104,29,83
                378,-903,-323
                -778,-728,485
                426,699,580
                -438,-605,-362
                -469,-447,-387
                509,732,623
                647,635,-688
                -868,-804,481
                614,-800,639
                595,780,-596
                                
                --- scanner 4 ---
                727,592,562
                -293,-554,779
                441,611,-461
                -714,465,-776
                -743,427,-804
                -660,-479,-426
                832,-632,460
                927,-485,-438
                408,393,-506
                466,436,-512
                110,16,151
                -258,-428,682
                -393,719,612
                -211,-452,876
                808,-476,-593
                -575,615,604
                -485,667,467
                -680,325,-822
                -627,-443,-432
                872,-547,-609
                833,512,582
                807,604,487
                839,-516,451
                891,-625,532
                -652,-548,-490
                30,-46,-14""";
        assertEquals(79, Nineteen.beacons(12, Inputs.asInputStream(anotherExample)));
    }

    @Test
    void findOrientor() {
        /*
0 = {Nineteen$Observation@1746} "Observation[scannerName=--- scanner 0 ---, one=Point[x=423, y=-701, z=434], two=Point[x=459, y=-707, z=401]]"
1 = {Nineteen$Observation@1747} "Observation[scannerName=--- scanner 1 ---, one=Point[x=-355, y=545, z=-477], two=Point[x=-391, y=539, z=-444]]"
2 = {Nineteen$Observation@1748} "Observation[scannerName=--- scanner 2 ---, one=Point[x=682, y=-795, z=504], two=Point[x=646, y=-828, z=498]]"
3 = {Nineteen$Observation@1749} "Observation[scannerName=--- scanner 4 ---, one=Point[x=-660, y=-479, z=-426], two=Point[x=-627, y=-443, z=-432]]"
         */
        final Observation scanner_one = new Observation(
                "scanner one",
                new Point(423, -701, 434),
                new Point(459, -707, 401)
        );
        final Observation scanner_two = new Observation(
                "scanner two",
                new Point(-355, 545, -477),
                new Point(-391, 539, -444)
        );
        var cr = Nineteen.findOrientationChange(
                scanner_one.directionVector(), scanner_two.directionVector()
        );
        System.out.println(cr);
    }

    @Test
    void exampleOne() throws IOException {
        System.out.println(Nineteen.beacons(12, Inputs.puzzleInput("nineteen.txt")));
    }
}