package brass_compass.gametest;

import brass_compass.BrassCompass;
import brass_compass.destinations.Destinations;
import brass_compass.item.BrassCompassItem;
import brass_compass.network.SavePayload;
import brass_compass.ui.EditListing;
import brass_compass.ui.EditMenu;
import brass_compass.ui.EditProvider;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;

/** BC-6: the save packet, its refusals, renaming a lost entry, removing, and use-on-lodestone opening the edit menu. */
public final class EditGameTest {
    private static SavePayload save(String dimension, BlockPos pos, String name) {
        return new SavePayload(InteractionHand.MAIN_HAND, dimension, pos, name);
    }

    @GameTest
    public void theSavePacketWritesOnlyToTheHeldCompassAtALodestone(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        String dim = BrassCompassItem.dimensionId(level);
        BlockPos home = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos bare = helper.absolutePos(new BlockPos(5, 1, 5));
        helper.setBlock(new BlockPos(1, 1, 1), Blocks.LODESTONE);
        ItemStack compass = new ItemStack(BrassCompass.BRASS_COMPASS);
        player.setItemInHand(InteractionHand.MAIN_HAND, compass);

        helper.assertTrue(SavePayload.apply(player, save(dim, home, "§cHo§lme")), "a lodestone stands there: applied");
        Destinations d = BrassCompassItem.destinationsOf(player.getItemInHand(InteractionHand.MAIN_HAND));
        helper.assertTrue(d.entries().size() == 1 && d.entries().get(0).name().equals("Home"), "stored plain and once: " + d.entries());
        helper.assertTrue(d.chosen(dim).isPresent(), "and chosen");
        helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).get(DataComponents.LODESTONE_TRACKER).target().orElseThrow().pos().equals(home), "the needle swings to it");

        helper.assertTrue(!SavePayload.apply(player, save(dim, bare, "Nowhere")), "no lodestone and no entry there: refused");
        helper.assertTrue(!SavePayload.apply(player, save("minecraft:the_nether", home, "Elsewhere")), "another dimension than the player's: refused");
        helper.assertTrue(BrassCompassItem.destinationsOf(player.getItemInHand(InteractionHand.MAIN_HAND)).entries().size() == 1, "nothing was added by the refusals");

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.COMPASS));
        helper.assertTrue(!SavePayload.apply(player, save(dim, home, "Other")), "another item in hand: refused");
        player.setItemInHand(InteractionHand.MAIN_HAND, compass);

        helper.setBlock(new BlockPos(1, 1, 1), Blocks.AIR);
        BrassCompassItem.refresh(compass, level);
        helper.assertTrue(!BrassCompassItem.destinationsOf(compass).entries().get(0).present(), "the lodestone is gone: the entry is lost");
        helper.assertTrue(SavePayload.apply(player, save(dim, home, "Old home")), "a rename of a lost entry is accepted");
        helper.assertTrue(BrassCompassItem.destinationsOf(compass).entries().get(0).name().equals("Old home"), "renamed");
        helper.assertTrue(BrassCompassItem.destinationsOf(compass).entries().size() == 1, "not duplicated");
        helper.succeed();
    }

    @GameTest
    public void removingThroughTheMenuClearsTheChoice(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        String dim = BrassCompassItem.dimensionId(level);
        BlockPos home = helper.absolutePos(new BlockPos(2, 1, 2));
        helper.setBlock(new BlockPos(2, 1, 2), Blocks.LODESTONE);
        ItemStack compass = new ItemStack(BrassCompass.BRASS_COMPASS);
        player.setItemInHand(InteractionHand.MAIN_HAND, compass);
        SavePayload.apply(player, save(dim, home, "Home"));

        EditListing listing = EditProvider.listing(compass, player, InteractionHand.MAIN_HAND, home);
        helper.assertTrue(listing.existing() && listing.name().equals("Home"), "the listing finds the entry: " + listing);
        EditMenu menu = new EditMenu(0, player.getInventory(), listing);
        helper.assertTrue(menu.clickMenuButton(player, EditMenu.REMOVE), "remove is applied");
        Destinations after = BrassCompassItem.destinationsOf(compass);
        helper.assertTrue(after.entries().isEmpty() && after.chosen(dim).isEmpty(), "entry and choice are gone: " + after);
        helper.assertTrue(!compass.get(DataComponents.LODESTONE_TRACKER).target().isPresent(), "the needle is free");
        helper.assertTrue(!menu.clickMenuButton(player, EditMenu.REMOVE), "removing again finds nothing");

        EditListing fresh = EditProvider.listing(compass, player, InteractionHand.MAIN_HAND, home);
        helper.assertTrue(!fresh.existing() && fresh.name().startsWith("Lodestone at"), "a fresh listing offers the default name: " + fresh);
        helper.succeed();
    }

    @GameTest
    public void useOnALodestoneOpensTheEditMenuUnlessSneaking(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        BlockPos lodestone = helper.absolutePos(new BlockPos(3, 1, 3));
        BlockPos stone = helper.absolutePos(new BlockPos(4, 1, 4));
        helper.setBlock(new BlockPos(3, 1, 3), Blocks.LODESTONE);
        helper.setBlock(new BlockPos(4, 1, 4), Blocks.STONE);
        ItemStack compass = new ItemStack(BrassCompass.BRASS_COMPASS);
        player.setItemInHand(InteractionHand.MAIN_HAND, compass);

        player.setShiftKeyDown(true);
        helper.assertTrue(BrassCompass.BRASS_COMPASS.useOn(context(player, lodestone)) == InteractionResult.PASS, "sneaking: nothing of ours");
        helper.assertTrue(!(player.containerMenu instanceof EditMenu), "no menu opened while sneaking");
        player.setShiftKeyDown(false);

        helper.assertTrue(BrassCompass.BRASS_COMPASS.useOn(context(player, stone)) == InteractionResult.PASS, "not a lodestone: falls through to use");
        InteractionResult opened = BrassCompass.BRASS_COMPASS.useOn(context(player, lodestone));
        helper.assertTrue(opened == InteractionResult.SUCCESS_SERVER, "a lodestone opens the edit menu: " + opened);
        helper.assertTrue(player.containerMenu instanceof EditMenu, "the edit menu is open: " + player.containerMenu);
        helper.assertTrue(((EditMenu) player.containerMenu).listing().pos().equals(lodestone), "for that lodestone");
        helper.succeed();
    }

    private static UseOnContext context(ServerPlayer player, BlockPos pos) {
        return new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
    }
}
