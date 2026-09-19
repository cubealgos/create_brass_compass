package brass_compass.gametest;

import brass_compass.BrassCompass;
import brass_compass.destinations.Destinations;
import brass_compass.item.BrassCompassItem;
import brass_compass.ui.EditMenu;
import brass_compass.ui.SwitchListing;
import brass_compass.ui.SwitchMenu;
import brass_compass.ui.SwitchProvider;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/** BC-5: the switch listing shows the holder's dimension only, choosing a row moves the needle, an out-of-range row is ignored, sneaking passes through. */
public final class SwitchGameTest {
    @GameTest
    public void theListingIsPerDimensionAndChoosingARowMovesTheNeedle(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        BlockPos home = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos mine = helper.absolutePos(new BlockPos(6, 1, 6));
        helper.setBlock(new BlockPos(1, 1, 1), Blocks.LODESTONE);
        helper.setBlock(new BlockPos(6, 1, 6), Blocks.LODESTONE);
        String dim = BrassCompassItem.dimensionId(level);
        Destinations d = Destinations.EMPTY
            .save(dim, home.getX(), home.getY(), home.getZ(), "Home").destinations()
            .save("minecraft:the_nether", 5, 40, 5, "Fortress").destinations()
            .save(dim, mine.getX(), mine.getY(), mine.getZ(), "Mine").destinations();
        ItemStack compass = new ItemStack(BrassCompass.BRASS_COMPASS);
        compass.set(BrassCompass.DESTINATIONS, d);
        player.setItemInHand(InteractionHand.MAIN_HAND, compass);
        player.teleportTo(home.getX() + 0.5, home.getY(), home.getZ() + 0.5);

        SwitchListing listing = SwitchProvider.listing(compass, player);
        helper.assertTrue(listing.rows().size() == 2, "only the overworld's two entries are listed: " + listing);
        helper.assertTrue(listing.dimension().equals(dim), "the listing names the holder's dimension");
        helper.assertTrue(listing.chosenRow() == 1, "the last saved overworld entry is chosen: row " + listing.chosenRow());
        helper.assertTrue(listing.rows().get(0).name().equals("Home") && listing.rows().get(0).distance() <= 1, "home is where the holder stands");

        SwitchMenu menu = new SwitchMenu(0, player.getInventory(), listing, InteractionHand.MAIN_HAND);
        helper.assertTrue(menu.clickMenuButton(player, 0), "choosing the first row is applied");
        Destinations after = BrassCompassItem.destinationsOf(player.getItemInHand(InteractionHand.MAIN_HAND));
        helper.assertTrue(after.chosen(dim).orElseThrow().name().equals("Home"), "home is chosen now");
        helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).get(DataComponents.LODESTONE_TRACKER).target().orElseThrow().pos().equals(home), "the needle points home");
        helper.assertTrue(!menu.clickMenuButton(player, 9), "an out-of-range row is ignored");
        helper.assertTrue(!menu.clickMenuButton(player, 0), "choosing the chosen row again changes nothing");
        helper.assertTrue(!menu.clickMenuButton(player, SwitchMenu.EDIT + 1000), "an unknown action is ignored");

        helper.assertTrue(menu.clickMenuButton(player, SwitchMenu.EDIT + 1), "edit on a row opens its edit screen");
        helper.assertTrue(player.containerMenu instanceof EditMenu edit && edit.listing().pos().equals(mine), "for that entry's lodestone: " + player.containerMenu);
        helper.assertTrue(menu.clickMenuButton(player, SwitchMenu.REMOVE + 1), "remove on a row is applied");
        Destinations removed = BrassCompassItem.destinationsOf(player.getItemInHand(InteractionHand.MAIN_HAND));
        helper.assertTrue(removed.entries().size() == 2 && removed.indicesIn(dim).size() == 1, "the mine is gone, home and the nether entry stay: " + removed);
        helper.assertTrue(removed.chosen(dim).orElseThrow().name().equals("Home"), "the choice followed the shift");
        helper.assertTrue(player.containerMenu instanceof SwitchMenu reopened && reopened.listing().rows().size() == 1, "the list was reopened with one row: " + player.containerMenu);

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.assertTrue(!menu.clickMenuButton(player, 1), "with the compass gone from the hand, nothing is applied");
        helper.assertTrue(!menu.stillValid(player), "and the menu is no longer valid");
        helper.succeed();
    }

    @GameTest
    public void sneakingPassesTheUseThrough(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        ItemStack compass = new ItemStack(BrassCompass.BRASS_COMPASS);
        player.setItemInHand(InteractionHand.MAIN_HAND, compass);
        player.setShiftKeyDown(true);
        InteractionResult sneaking = BrassCompass.BRASS_COMPASS.use(level, player, InteractionHand.MAIN_HAND);
        helper.assertTrue(sneaking == InteractionResult.PASS, "sneaking: a normal right-click, nothing of ours: " + sneaking);
        player.setShiftKeyDown(false);
        helper.succeed();
    }
}
