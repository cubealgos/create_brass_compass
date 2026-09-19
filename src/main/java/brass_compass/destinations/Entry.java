package brass_compass.destinations;

import java.util.Objects;

/**
 * One saved lodestone: where it is, what the holder called it, and whether it was there the last
 * time the compass looked (docs/spec/domains/compass.md, entry lifecycle).
 *
 * @param dimension the dimension id as text, such as {@code minecraft:overworld}
 */
public record Entry(String dimension, int x, int y, int z, String name, boolean present) {
    public Entry {
        Objects.requireNonNull(dimension, "dimension");
        if (dimension.isBlank()) {
            throw new IllegalArgumentException("an entry needs a dimension");
        }
        name = Names.sanitize(name, Names.defaultFor(x, y, z));
    }

    /** The same lodestone? Position and dimension, never the name. */
    public boolean samePlace(String otherDimension, int ox, int oy, int oz) {
        return dimension.equals(otherDimension) && x == ox && y == oy && z == oz;
    }

    public Entry withName(String newName) {
        return new Entry(dimension, x, y, z, newName, present);
    }

    public Entry withPresent(boolean isPresent) {
        return present == isPresent ? this : new Entry(dimension, x, y, z, name, isPresent);
    }
}
