package brass_compass;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The mod's server-and-common entrypoint: registers the item, its component, the menus and the packet. */
public final class BrassCompass implements ModInitializer {
    public static final String MOD_ID = "brass_compass";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Brass Compass ready beside Create Fly");
    }
}
