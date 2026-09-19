package brass_compass.item;

import brass_compass.destinations.Destinations;
import brass_compass.destinations.Entry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** The destinations component's codecs: the on-disk shape of docs/spec/contracts/data-contract.md, and the wire shape for the client. */
public final class DestinationsCodec {
    public static final Codec<Entry> ENTRY = RecordCodecBuilder.create(b -> b.group(
        Codec.STRING.fieldOf("dimension").forGetter(Entry::dimension),
        Codec.INT.fieldOf("x").forGetter(Entry::x),
        Codec.INT.fieldOf("y").forGetter(Entry::y),
        Codec.INT.fieldOf("z").forGetter(Entry::z),
        Codec.STRING.fieldOf("name").forGetter(Entry::name),
        Codec.BOOL.optionalFieldOf("present", true).forGetter(Entry::present)
    ).apply(b, Entry::new));

    public static final Codec<Destinations> CODEC = RecordCodecBuilder.create(b -> b.group(
        Codec.INT.optionalFieldOf("version", Destinations.VERSION).forGetter(Destinations::version),
        ENTRY.listOf().optionalFieldOf("entries", List.of()).forGetter(Destinations::entries),
        Codec.unboundedMap(Codec.STRING, Codec.INT).optionalFieldOf("current", Map.of()).forGetter(Destinations::current)
    ).apply(b, Destinations::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Destinations> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    private DestinationsCodec() {
    }
}
