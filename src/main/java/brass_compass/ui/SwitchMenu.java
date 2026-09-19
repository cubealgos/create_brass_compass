package brass_compass.ui;

import brass_compass.BrassCompass;
import brass_compass.destinations.Destinations;
import brass_compass.item.BrassCompassItem;
import com.zurrtum.create.foundation.gui.menu.MenuBase;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * The switch screen's menu: no slots, one row per entry of the holder's dimension. Choosing is a
 * vanilla menu button click with the row index (UI-REQ-004); the server re-reads the compass in
 * the player's hand before applying anything (COMPASS-REQ-007, UI-REQ-009).
 */
public final class SwitchMenu extends MenuBase<SwitchListing> {
    private final InteractionHand hand;

    public SwitchMenu(int syncId, Inventory inventory, SwitchListing listing, InteractionHand hand) {
        super(BrassCompass.SWITCH_MENU, syncId, inventory, listing);
        this.hand = hand;
    }

    /** The client's constructor: the listing came over the wire, the hand is not needed there. */
    public SwitchMenu(int syncId, Inventory inventory, SwitchListing listing) {
        this(syncId, inventory, listing, InteractionHand.MAIN_HAND);
    }

    @Override
    protected void initAndReadInventory(SwitchListing listing) {
    }

    @Override
    protected void addSlots() {
    }

    @Override
    protected void saveData(SwitchListing listing) {
    }

    public SwitchListing listing() {
        return contentHolder;
    }

    /** The compass this menu is about, if the player still holds one in that hand. */
    private ItemStack held(Player player) {
        ItemStack stack = player.getItemInHand(hand);
        return stack.is(BrassCompass.BRASS_COMPASS) ? stack : ItemStack.EMPTY;
    }

    /** Server side: the player chose a row. Returns whether the choice was applied. */
    @Override
    public boolean clickMenuButton(Player player, int row) {
        if (player.level().isClientSide() || row < 0 || row >= contentHolder.rows().size()) {
            return false;
        }
        ItemStack stack = held(player);
        if (stack.isEmpty()) {
            return false;
        }
        Destinations before = BrassCompassItem.destinationsOf(stack);
        Destinations after = before.choose(contentHolder.dimension(), contentHolder.rows().get(row).entryIndex());
        if (after == before) {
            return false;
        }
        stack.set(BrassCompass.DESTINATIONS, after);
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
