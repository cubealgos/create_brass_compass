package brass_compass.ui;

import brass_compass.destinations.Destinations;
import brass_compass.destinations.Entry;
import brass_compass.item.BrassCompassItem;
import com.zurrtum.create.foundation.gui.menu.MenuBase;
import com.zurrtum.create.foundation.gui.menu.MenuProvider;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Opens the switch screen for the compass in a hand: builds the listing for the holder's dimension and position. */
public record SwitchProvider(InteractionHand hand) implements MenuProvider {
    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.brass_compass.switch");
    }

    /** The rows of the holder's dimension, with distances from where the holder stands (UI-REQ-002, -010). */
    public static SwitchListing listing(ItemStack stack, Player holder) {
        Destinations d = BrassCompassItem.destinationsOf(stack);
        String dimension = BrassCompassItem.dimensionId(holder.level());
        List<SwitchListing.Row> rows = new ArrayList<>();
        int chosenRow = -1;
        for (int index : d.indicesIn(dimension)) {
            Entry e = d.entries().get(index);
            int distance = (int) Math.round(Math.sqrt(holder.distanceToSqr(e.x() + 0.5, e.y() + 0.5, e.z() + 0.5)));
            if (d.chosenIndex(dimension).isPresent() && d.chosenIndex(dimension).getAsInt() == index) {
                chosenRow = rows.size();
            }
            rows.add(new SwitchListing.Row(index, e.name(), distance, e.present()));
        }
        return new SwitchListing(dimension, rows, chosenRow);
    }

    @Override
    public MenuBase<?> createMenu(int syncId, Inventory inventory, Player player, RegistryFriendlyByteBuf buf) {
        SwitchListing listing = listing(player.getItemInHand(hand), player);
        SwitchListing.STREAM_CODEC.encode(buf, listing);
        return new SwitchMenu(syncId, inventory, listing, hand);
    }
}
