package brass_compass;

import brass_compass.destinations.Destinations;
import brass_compass.item.BrassCompassItem;
import brass_compass.item.DestinationsCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import brass_compass.ui.SwitchListing;
import brass_compass.ui.SwitchMenu;
import com.zurrtum.create.AllCreativeModeTabs;
import com.zurrtum.create.api.registry.CreateRegistries;
import com.zurrtum.create.foundation.gui.menu.MenuType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
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

    public static final ResourceKey<Item> BRASS_COMPASS_KEY = ResourceKey.create(Registries.ITEM, id("brass_compass"));
    /** The item (COMPASS-REQ-001): one per stack, born with no entries. */
    public static final Item BRASS_COMPASS = Registry.register(BuiltInRegistries.ITEM, BRASS_COMPASS_KEY,
        new BrassCompassItem(new Item.Properties().setId(BRASS_COMPASS_KEY).stacksTo(1).component(DESTINATIONS, Destinations.EMPTY)));

    /** The switch screen's menu, in Create Fly's menu registry so its screen framework draws it (ARCH-DEC-002). */
    public static final MenuType<SwitchListing> SWITCH_MENU = Registry.register(CreateRegistries.MENU_TYPE, id("switch"),
        (MenuType<SwitchListing>) (syncId, inventory, listing) -> new SwitchMenu(syncId, inventory, listing));

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        // COMPASS-DEC-005: listed in Create Fly's base tab, beside the brass things.
        CreativeModeTabEvents.modifyOutputEvent(AllCreativeModeTabs.BASE_GROUP).register(output ->
            output.accept(new ItemStack(BRASS_COMPASS), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
        LOGGER.info("Brass Compass ready beside Create Fly");
    }
}
