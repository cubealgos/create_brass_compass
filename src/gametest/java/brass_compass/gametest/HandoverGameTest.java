package brass_compass.gametest;

import brass_compass.BrassCompass;
import brass_compass.destinations.Destinations;
import brass_compass.item.BrassCompassItem;
import brass_compass.network.SavePayload;
import brass_compass.ui.SwitchListing;
import brass_compass.ui.SwitchMenu;
import brass_compass.ui.SwitchProvider;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Blocks;

/** BC-7: UC-005 hand-over between two players, the empty listing (UI-REQ-005) and the tooltip (COMPASS-REQ-012). */
public final class HandoverGameTest {
    @GameTest
    public void aHandedOverCompassKeepsEveryEntryAndOnlyTheNewHolderCanChangeIt(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerPlayer first = helper.makeMockServerPlayerInLevel();
        ServerPlayer second = helper.makeMockServerPlayerInLevel();
        String dim = BrassCompassItem.dimensionId(level);
        BlockPos home = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos mine = helper.absolutePos(new BlockPos(5, 1, 5));
        helper.setBlock(new BlockPos(1, 1, 1), Blocks.LODESTONE);
        helper.setBlock(new BlockPos(5, 1, 5), Blocks.LODESTONE);
        ItemStack compass = new ItemStack(BrassCompass.BRASS_COMPASS);
        first.setItemInHand(InteractionHand.MAIN_HAND, compass);
        helper.assertTrue(SwitchProvider.listing(compass, first).rows().isEmpty(), "a fresh compass lists nothing (UI-REQ-005)");
        helper.assertTrue(tooltip(compass).contains("tooltip.brass_compass.no_destination"), "and says no destination: " + tooltip(compass));

        SavePayload.apply(first, new SavePayload(InteractionHand.MAIN_HAND, dim, home, "Home"));
        SavePayload.apply(first, new SavePayload(InteractionHand.MAIN_HAND, dim, mine, "Mine"));
        helper.assertTrue(BrassCompassItem.destinationsOf(compass).entries().size() == 2, "the first player saved two");
        helper.assertTrue(tooltip(compass).contains("tooltip.brass_compass.destination"), "the tooltip names the destination: " + tooltip(compass));

        // Hand-over: the stack leaves the first hand and lands in the second (UC-005).
        first.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        second.setItemInHand(InteractionHand.MAIN_HAND, compass);
        SwitchListing seen = SwitchProvider.listing(compass, second);
        helper.assertTrue(seen.rows().size() == 2 && seen.rows().get(0).name().equals("Home") && seen.rows().get(1).name().equals("Mine"), "the second player sees every entry: " + seen);

        helper.assertTrue(!SavePayload.apply(first, new SavePayload(InteractionHand.MAIN_HAND, dim, home, "Stolen")), "the first player's packet finds no compass in hand: refused");
        SwitchMenu firstsMenu = new SwitchMenu(0, first.getInventory(), seen, InteractionHand.MAIN_HAND);
        helper.assertTrue(!firstsMenu.clickMenuButton(first, 0), "and the first player's menu click is refused too");
        helper.assertTrue(!firstsMenu.stillValid(first), "their menu is no longer valid (UI-REQ-009)");

        SwitchMenu secondsMenu = new SwitchMenu(1, second.getInventory(), seen, InteractionHand.MAIN_HAND);
        helper.assertTrue(secondsMenu.clickMenuButton(second, 0), "the second player chooses home");
        Destinations after = BrassCompassItem.destinationsOf(compass);
        helper.assertTrue(after.chosen(dim).orElseThrow().name().equals("Home"), "home is chosen");
        helper.assertTrue(compass.get(DataComponents.LODESTONE_TRACKER).target().orElseThrow().pos().equals(home), "the needle points home for the new holder");
        helper.assertTrue(BrassCompass.BRASS_COMPASS.isFoil(compass), "and the item glints (COMPASS-REQ-016)");
        helper.assertTrue(compass.is(net.minecraft.tags.ItemTags.COMPASSES), "and is a compass to the client's glint rule (COMPASS-REQ-016)");
        helper.succeed();
    }

    /** The tooltip's translation keys, in order. */
    private static List<String> tooltip(ItemStack stack) {
        List<String> keys = new ArrayList<>();
        stack.getItem().appendHoverText(stack, Item.TooltipContext.EMPTY, TooltipDisplay.DEFAULT, line -> keys.add(keyOf(line)), TooltipFlag.NORMAL);
        return keys;
    }

    private static String keyOf(Component line) {
        return line.getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents t ? t.getKey() : line.getString();
    }
}
