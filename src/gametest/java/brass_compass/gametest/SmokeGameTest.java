package brass_compass.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

/** M0: the mod loads beside Create Fly and a lodestone can be placed; everything else follows. */
public final class SmokeGameTest {
    @GameTest
    public void theModLoadsBesideCreateFly(GameTestHelper helper) {
        helper.assertTrue(net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("create"), "Create Fly is loaded");
        helper.assertTrue(net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("brass_compass"), "the mod is loaded");
        helper.setBlock(new net.minecraft.core.BlockPos(1, 1, 1), Blocks.LODESTONE);
        helper.succeed();
    }
}
