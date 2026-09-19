package brass_compass.ui;

import brass_compass.BrassCompass;
import brass_compass.destinations.Destinations;
import brass_compass.destinations.Entry;
import brass_compass.item.BrassCompassItem;
import com.zurrtum.create.foundation.gui.menu.MenuBase;
import com.zurrtum.create.foundation.gui.menu.MenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * The switch screen's menu: no slots, one row per entry of the holder's dimension. Every action is
 * a vanilla menu button click carrying a row index (UI-REQ-004, UI-REQ-011): plain ids choose,
 * {@link #REMOVE} plus the row removes and reopens the list, {@link #EDIT} plus the row opens the
 * edit screen for that entry. The server re-reads the compass in the player's hand before
 * applying anything (COMPASS-REQ-007, UI-REQ-009).
 */
public final class SwitchMenu extends MenuBase<SwitchListing> {
    /** Button id offset: remove the row. */
    public static final int REMOVE = 1000;
    /** Button id offset: open the edit screen for the row. */
    public static final int EDIT = 2000;

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

    /** Server side. Returns whether the action was applied. */
    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (player.level().isClientSide() || id < 0) {
            return false;
        }
        int row = id % 1000;
        int action = id - row;
        if (row >= contentHolder.rows().size() || action > EDIT) {
            return false;
        }
        ItemStack stack = held(player);
        if (stack.isEmpty()) {
            return false;
        }
        Destinations before = BrassCompassItem.destinationsOf(stack);
        int entryIndex = contentHolder.rows().get(row).entryIndex();
        if (entryIndex < 0 || entryIndex >= before.entries().size()) {
            return false;
        }
        ServerLevel level = (ServerLevel) player.level();
        switch (action) {
            case REMOVE -> {
                stack.set(BrassCompass.DESTINATIONS, before.remove(entryIndex));
                BrassCompassItem.refresh(stack, level);
                if (player instanceof ServerPlayer server) {
                    MenuProvider.openHandledScreen(server, new SwitchProvider(hand));
                }
                return true;
            }
            case EDIT -> {
                Entry e = before.entries().get(entryIndex);
                if (player instanceof ServerPlayer server) {
                    MenuProvider.openHandledScreen(server, new EditProvider(hand, new BlockPos(e.x(), e.y(), e.z())));
                }
                return true;
            }
            default -> {
                Destinations after = before.choose(contentHolder.dimension(), entryIndex);
                if (after == before) {
                    return false;
                }
                stack.set(BrassCompass.DESTINATIONS, after);
                BrassCompassItem.refresh(stack, level);
                return true;
            }
        }
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
