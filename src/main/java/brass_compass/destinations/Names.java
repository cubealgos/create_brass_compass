package brass_compass.destinations;

/** Entry names as the spec bounds them: plain text, at most 32 characters, a default from the position (COMPASS-FAIL-002, UI-FAIL-002). */
public final class Names {
    public static final int MAX_LENGTH = 32;
    /** Minecraft's formatting prefix, the section sign. */
    static final char FORMATTING = (char) 0xA7;

    private Names() {
    }

    /** Strips formatting codes and control characters, trims, truncates; an empty result becomes the fallback. */
    public static String sanitize(String raw, String fallback) {
        if (raw == null) {
            return fallback;
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == FORMATTING) {
                i++; // the code letter after the section sign goes with it
                continue;
            }
            if (c >= ' ' && c != (char) 0x7F) {
                out.append(c);
            }
        }
        String cleaned = out.toString().trim();
        if (cleaned.isEmpty()) {
            return fallback;
        }
        return cleaned.length() > MAX_LENGTH ? cleaned.substring(0, MAX_LENGTH).trim() : cleaned;
    }

    public static String defaultFor(int x, int y, int z) {
        return "Lodestone at " + x + ", " + y + ", " + z;
    }
}
