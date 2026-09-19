package brass_compass;

import brass_compass.destinations.Destinations;
import brass_compass.item.DestinationsCodec;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The mod's server-and-common entrypoint: registers the item, its component, the menus and the packet. */
public final class BrassCompass implements ModInitializer {
    public static final String MOD_ID = "brass_compass";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /** What a brass compass remembers (docs/spec/contracts/data-contract.md). */
    public static final DataComponentType<Destinations> DESTINATIONS = Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        id("destinations"),
        DataComponentType.<Destinations>builder()
            .persistent(DestinationsCodec.CODEC)
            .networkSynchronized(DestinationsCodec.STREAM_CODEC)
            .build());

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Brass Compass ready beside Create Fly");
    }
}
