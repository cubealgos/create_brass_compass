package brass_compass.destinations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** The pure rules of docs/spec/domains/compass.md: unique places, a choice per dimension, names bounded, newer versions read-only. */
class DestinationsTest {
    static final String OVERWORLD = "minecraft:overworld";
    static final String NETHER = "minecraft:the_nether";
    static final String F = String.valueOf(Names.FORMATTING);

    @Test
    void savingAppendsAndChoosesAndSavingThePlaceAgainReturnsTheExistingEntry() {
        Destinations.Saved first = Destinations.EMPTY.save(OVERWORLD, 10, 64, -3, "Home");
        assertTrue(first.created());
        assertEquals(0, first.index());
        assertEquals("Home", first.destinations().chosen(OVERWORLD).orElseThrow().name());
        Destinations.Saved again = first.destinations().save(OVERWORLD, 10, 64, -3, "Somewhere else");
        assertFalse(again.created(), "the same place is not saved twice");
        assertEquals(0, again.index());
        assertEquals(1, again.destinations().entries().size());
        assertEquals("Home", again.destinations().entries().get(0).name(), "the existing name stays");
    }

    @Test
    void theChoiceIsPerDimensionAndAChoiceAcrossDimensionsIsRefused() {
        Destinations d = Destinations.EMPTY.save(OVERWORLD, 0, 64, 0, "Home").destinations()
            .save(NETHER, 5, 40, 5, "Fortress").destinations()
            .save(OVERWORLD, 100, 70, 100, "Mine").destinations();
        assertEquals("Mine", d.chosen(OVERWORLD).orElseThrow().name(), "the last saved in a dimension is chosen there");
        assertEquals("Fortress", d.chosen(NETHER).orElseThrow().name());
        assertEquals(List.of(0, 2), d.indicesIn(OVERWORLD));
        Destinations chosen = d.choose(OVERWORLD, 0);
        assertEquals("Home", chosen.chosen(OVERWORLD).orElseThrow().name());
        assertSame(chosen, chosen.choose(OVERWORLD, 1), "a nether entry cannot be the overworld's choice");
        assertSame(chosen, chosen.choose(OVERWORLD, 9), "out of range is refused");
    }

    @Test
    void removingShiftsIndicesAndDropsAChoiceThatPointedAtIt() {
        Destinations d = Destinations.EMPTY.save(OVERWORLD, 0, 64, 0, "Home").destinations()
            .save(OVERWORLD, 1, 64, 1, "Mine").destinations()
            .save(OVERWORLD, 2, 64, 2, "Farm").destinations()
            .choose(OVERWORLD, 2);
        Destinations withoutMine = d.remove(1);
        assertEquals(List.of("Home", "Farm"), withoutMine.entries().stream().map(Entry::name).toList());
        assertEquals(1, withoutMine.chosenIndex(OVERWORLD).orElseThrow(), "the choice followed the farm down");
        Destinations withoutFarm = withoutMine.remove(1);
        assertTrue(withoutFarm.chosen(OVERWORLD).isEmpty(), "removing the chosen entry leaves none chosen");
    }

    @Test
    void namesAreSanitisedBoundedAndDefaulted() {
        assertEquals("Lodestone at 1, 2, 3", Names.sanitize("   ", Names.defaultFor(1, 2, 3)));
        assertEquals("Home", Names.sanitize(F + "cHo" + F + "lme", "x"), "formatting codes are stripped");
        String tooLong = "a".repeat(50);
        assertEquals(32, Names.sanitize(tooLong, "x").length());
        Entry e = new Entry(OVERWORLD, 1, 2, 3, "", true);
        assertEquals("Lodestone at 1, 2, 3", e.name());
        Destinations d = Destinations.EMPTY.save(OVERWORLD, 0, 0, 0, "Home").destinations().rename(0, F + "kBase");
        assertEquals("Base", d.entries().get(0).name());
    }

    @Test
    void lostAndRevivedKeepTheChoice() {
        Destinations d = Destinations.EMPTY.save(OVERWORLD, 0, 64, 0, "Home").destinations();
        Destinations lost = d.markPresent(0, false);
        assertFalse(lost.entries().get(0).present());
        assertEquals(0, lost.chosenIndex(OVERWORLD).orElseThrow(), "a lost entry stays chosen");
        assertSame(lost, lost.markPresent(0, false), "no change, same value");
        assertTrue(lost.markPresent(0, true).entries().get(0).present());
    }

    @Test
    void aNewerVersionIsReadOnlyAndAnInvalidChoiceIsDropped() {
        Destinations newer = new Destinations(Destinations.VERSION + 1, List.of(new Entry(OVERWORLD, 0, 0, 0, "Future", true)), Map.of(OVERWORLD, 0));
        assertTrue(newer.readOnly());
        assertSame(newer, newer.rename(0, "Nope"));
        assertSame(newer, newer.remove(0));
        assertEquals(-1, newer.save(OVERWORLD, 5, 5, 5, "Nope").index());
        Destinations broken = new Destinations(Destinations.VERSION, List.of(new Entry(OVERWORLD, 0, 0, 0, "Home", true)), Map.of(OVERWORLD, 7, NETHER, 0));
        assertTrue(broken.current().isEmpty(), "an index out of range and a choice in the wrong dimension are dropped on construction");
    }
}
