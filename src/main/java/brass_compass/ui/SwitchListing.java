package brass_compass.ui;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * What the switch screen shows (UI-UC-002, UI-DEC-002): the holder's dimension, its entries in
 * saved order with their distance, and which is chosen. Sent once when the screen opens.
 *
 * @param dimension the dimension id the holder is in, shown as the list's indicator
 * @param chosenRow the row index of the chosen entry, or -1
 */
public record SwitchListing(String dimension, List<Row> rows, int chosenRow) {
    /** One entry as a row: its index on the item, its name, its distance in blocks, whether its lodestone was last seen. */
    public record Row(int entryIndex, String name, int distance, boolean present) {
        public static final StreamCodec<ByteBuf, Row> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, Row::entryIndex,
            ByteBufCodecs.STRING_UTF8, Row::name,
            ByteBufCodecs.VAR_INT, Row::distance,
            ByteBufCodecs.BOOL, Row::present,
            Row::new);
    }

    public static final StreamCodec<ByteBuf, SwitchListing> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, SwitchListing::dimension,
        Row.STREAM_CODEC.apply(ByteBufCodecs.list()), SwitchListing::rows,
        ByteBufCodecs.VAR_INT, SwitchListing::chosenRow,
        SwitchListing::new);
}
