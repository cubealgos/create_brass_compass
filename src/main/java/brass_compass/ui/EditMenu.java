package brass_compass.ui;

import brass_compass.BrassCompass;
import brass_compass.destinations.Destinations;
import brass_compass.item.BrassCompassItem;
import com.zurrtum.create.foundation.gui.menu.MenuBase;
import java.util.OptionalInt;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * The edit screen's menu: no slots. Saving goes through its own packet (UI-REQ-007); removing is
 * menu button {@link #REMOVE}. Both re-read the compass in the hand before touching it.
 */
public final class EditMenu extends MenuBase<EditListing> {
    public static final int REMOVE = 0;

    public EditMenu(int syncId, Inventory inventory, EditListing listing) {
        super(BrassCompass.EDIT_MENU, syncId, inventory, listing);
    }

    @Override
    protected void initAndReadInventory(EditListing listing) {
    }

    @Override
    protected void addSlots() {
    }

    @Override
    protected void saveData(EditListing listing) {
    }

    public EditListing listing() {
        return contentHolder;
    }

    private ItemStack held(Player player) {
        ItemStack stack = player.getItemInHand(contentHolder.hand());
        return stack.is(BrassCompass.BRASS_COMPASS) ? stack : ItemStack.EMPTY;
    }

    /** Server side: remove the entry at this place. Returns whether something was removed. */
    @Override
    public boolean clickMenuButton(Player player, int button) {
        if (player.level().isClientSide() || button != REMOVE) {
            return false;
        }
        ItemStack stack = held(player);
        if (stack.isEmpty()) {
            return false;
        }
        Destinations before = BrassCompassItem.destinationsOf(stack);
        OptionalInt index = before.indexOf(contentHolder.dimension(), contentHolder.pos().getX(), contentHolder.pos().getY(), contentHolder.pos().getZ());
        if (index.isEmpty()) {
            return false;
        }
        stack.set(BrassCompass.DESTINATIONS, before.remove(index.getAsInt()));
        BrassCompassItem.refresh(stack, (ServerLevel) player.level());
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.level().isClientSide() || !held(player).isEmpty();
    }
}
