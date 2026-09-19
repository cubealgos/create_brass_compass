package brass_compass.ui;

import brass_compass.destinations.Destinations;
import brass_compass.destinations.Names;
import brass_compass.item.BrassCompassItem;
import com.zurrtum.create.foundation.gui.menu.MenuBase;
import com.zurrtum.create.foundation.gui.menu.MenuProvider;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Opens the edit screen for one lodestone place and the compass in a hand. */
public record EditProvider(InteractionHand hand, BlockPos pos) implements MenuProvider {
    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.brass_compass.edit");
    }

    /** The entry at the place if there is one, else a fresh listing with the default name. */
    public static EditListing listing(ItemStack stack, Player holder, InteractionHand hand, BlockPos pos) {
        Destinations d = BrassCompassItem.destinationsOf(stack);
        String dimension = BrassCompassItem.dimensionId(holder.level());
        OptionalInt index = d.indexOf(dimension, pos.getX(), pos.getY(), pos.getZ());
        String name = index.isPresent() ? d.entries().get(index.getAsInt()).name() : Names.defaultFor(pos.getX(), pos.getY(), pos.getZ());
        return new EditListing(hand, dimension, pos, name, index.isPresent());
    }

    @Override
    public MenuBase<?> createMenu(int syncId, Inventory inventory, Player player, RegistryFriendlyByteBuf buf) {
        EditListing listing = listing(player.getItemInHand(hand), player, hand, pos);
        EditListing.STREAM_CODEC.encode(buf, listing);
        return new EditMenu(syncId, inventory, listing);
    }
}
