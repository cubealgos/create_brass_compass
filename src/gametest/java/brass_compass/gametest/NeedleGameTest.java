package brass_compass.gametest;

import brass_compass.BrassCompass;
import brass_compass.destinations.Destinations;
import brass_compass.item.BrassCompassItem;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.block.Blocks;

/** BC-3: the tracker follows the chosen entry of the holder's dimension; a lodestone gone marks the entry lost, back marks it present. */
public final class NeedleGameTest {
    @GameTest
    public void theNeedleFollowsTheChoiceAndNoticesALostLodestone(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos at = helper.absolutePos(new BlockPos(2, 1, 2));
        helper.setBlock(new BlockPos(2, 1, 2), Blocks.LODESTONE);
        ItemStack stack = new ItemStack(BrassCompass.BRASS_COMPASS);
        BrassCompassItem.refresh(stack, level);
        LodestoneTracker none = stack.get(DataComponents.LODESTONE_TRACKER);
        helper.assertTrue(none != null && none.target().isEmpty() && !none.tracked(), "nothing chosen: untracked, the needle spins");

        String dim = BrassCompassItem.dimensionId(level);
        stack.set(BrassCompass.DESTINATIONS, Destinations.EMPTY.save(dim, at.getX(), at.getY(), at.getZ(), "Home").destinations());
        BrassCompassItem.refresh(stack, level);
        LodestoneTracker tracker = stack.get(DataComponents.LODESTONE_TRACKER);
        helper.assertTrue(tracker != null && tracker.tracked() && tracker.target().isPresent() && tracker.target().get().pos().equals(at),
            "the tracker points at the chosen lodestone: " + tracker);

        helper.setBlock(new BlockPos(2, 1, 2), Blocks.AIR);
        BrassCompassItem.refresh(stack, level);
        helper.assertTrue(!BrassCompassItem.destinationsOf(stack).entries().get(0).present(), "the entry is marked lost");
        LodestoneTracker lost = stack.get(DataComponents.LODESTONE_TRACKER);
        helper.assertTrue(lost.tracked() && lost.target().isEmpty(), "a lost lodestone leaves the needle spinning");
        helper.assertTrue(BrassCompassItem.destinationsOf(stack).chosenIndex(dim).isPresent(), "and still chosen");

        helper.setBlock(new BlockPos(2, 1, 2), Blocks.LODESTONE);
        BrassCompassItem.refresh(stack, level);
        helper.assertTrue(BrassCompassItem.destinationsOf(stack).entries().get(0).present(), "placed back, the entry is present again");
        helper.assertTrue(stack.get(DataComponents.LODESTONE_TRACKER).target().isPresent(), "and pointed at");
        helper.succeed();
    }
}
