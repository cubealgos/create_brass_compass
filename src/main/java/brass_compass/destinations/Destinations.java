package brass_compass.destinations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.TreeMap;

/**
 * Everything a brass compass remembers (docs/spec/contracts/data-contract.md): entries in the
 * order they were saved, the chosen entry per dimension, and the schema version. Immutable: every
 * change returns a new value, which is what a data component wants. A value read from a newer
 * version is read-only and every change returns itself unchanged (DATA-REQ-001).
 */
public record Destinations(int version, List<Entry> entries, Map<String, Integer> current) {
    /** The schema this build writes. */
    public static final int VERSION = 1;
    public static final Destinations EMPTY = new Destinations(VERSION, List.of(), Map.of());

    public Destinations {
        List<Entry> fixed = List.copyOf(entries);
        Map<String, Integer> checked = new TreeMap<>();
        current.forEach((dimension, index) -> {
            if (index >= 0 && index < fixed.size() && fixed.get(index).dimension().equals(dimension)) {
                checked.put(dimension, index);
            }
        });
        entries = fixed;
        current = Collections.unmodifiableMap(checked);
    }

    /** True when written by a newer build than this one: shown, never edited. */
    public boolean readOnly() {
        return version > VERSION;
    }

    /** The result of saving: the destinations and the index of the entry, new or already there. */
    public record Saved(Destinations destinations, int index, boolean created) {
    }

    /**
     * Saves a lodestone: an entry already at that place is returned as it is (COMPASS-REQ-010),
     * otherwise a new one is appended. Either way it becomes its dimension's choice (COMPASS-REQ-004).
     */
    public Saved save(String dimension, int x, int y, int z, String name) {
        if (readOnly()) {
            return new Saved(this, -1, false);
        }
        OptionalInt existing = indexOf(dimension, x, y, z);
        if (existing.isPresent()) {
            return new Saved(choose(dimension, existing.getAsInt()), existing.getAsInt(), false);
        }
        List<Entry> next = new ArrayList<>(entries);
        next.add(new Entry(dimension, x, y, z, name, true));
        int index = next.size() - 1;
        Map<String, Integer> chosen = new TreeMap<>(current);
        chosen.put(dimension, index);
        return new Saved(new Destinations(version, next, chosen), index, true);
    }

    public Destinations rename(int index, String name) {
        if (readOnly() || !valid(index)) {
            return this;
        }
        List<Entry> next = new ArrayList<>(entries);
        next.set(index, next.get(index).withName(name));
        return new Destinations(version, next, current);
    }

    /** Removes an entry; indices above it shift down and the per-dimension choices follow (or vanish). */
    public Destinations remove(int index) {
        if (readOnly() || !valid(index)) {
            return this;
        }
        List<Entry> next = new ArrayList<>(entries);
        next.remove(index);
        Map<String, Integer> chosen = new TreeMap<>();
        current.forEach((dimension, i) -> {
            if (i < index) {
                chosen.put(dimension, i);
            } else if (i > index) {
                chosen.put(dimension, i - 1);
            }
        });
        return new Destinations(version, next, chosen);
    }

    /** Chooses an entry for its own dimension (COMPASS-REQ-015); an index in another dimension is refused. */
    public Destinations choose(String dimension, int index) {
        if (readOnly() || !valid(index) || !entries.get(index).dimension().equals(dimension)) {
            return this;
        }
        Map<String, Integer> chosen = new TreeMap<>(current);
        chosen.put(dimension, index);
        return new Destinations(version, entries, chosen);
    }

    public Destinations markPresent(int index, boolean present) {
        if (!valid(index) || entries.get(index).present() == present) {
            return this;
        }
        List<Entry> next = new ArrayList<>(entries);
        next.set(index, next.get(index).withPresent(present));
        return new Destinations(version, next, current);
    }

    /** The chosen entry's index in a dimension, if any. */
    public OptionalInt chosenIndex(String dimension) {
        Integer i = current.get(dimension);
        return i == null ? OptionalInt.empty() : OptionalInt.of(i);
    }

    public Optional<Entry> chosen(String dimension) {
        OptionalInt i = chosenIndex(dimension);
        return i.isPresent() ? Optional.of(entries.get(i.getAsInt())) : Optional.empty();
    }

    public OptionalInt indexOf(String dimension, int x, int y, int z) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).samePlace(dimension, x, y, z)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    /** The indices of the entries in one dimension, in saved order (UI-REQ-002). */
    public List<Integer> indicesIn(String dimension) {
        List<Integer> out = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).dimension().equals(dimension)) {
                out.add(i);
            }
        }
        return out;
    }

    private boolean valid(int index) {
        return index >= 0 && index < entries.size();
    }
}
