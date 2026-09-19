package brass_compass.gametest;

import brass_compass.BrassCompass;
import brass_compass.debug.DebugCommand;
import brass_compass.destinations.Destinations;
import brass_compass.destinations.Entry;
import brass_compass.item.BrassCompassItem;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/** BC-15: the mock destinations are realistic, and the development-only command is registered and runs on a development server. */
public final class DebugCommandGameTest {
    @GameTest
    public void mockDestinationsAreFarApartWithOneLostOneChosenAndOneElsewhere(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        String here = BrassCompassItem.dimensionId(helper.getLevel());
        Destinations d = DebugCommand.mock(player, 5);
        long hereCount = d.entries().stream().filter(e -> e.dimension().equals(here)).count();
        helper.assertTrue(d.entries().size() == 6 && hereCount == 5, "five here, one elsewhere: " + d.entries());
        for (Entry e : d.entries()) {
            if (!e.dimension().equals(here)) {
                continue;
            }
            double distance = Math.hypot(e.x() - player.getX(), e.z() - player.getZ());
            helper.assertTrue(distance >= 150, e.name() + " is only " + distance + " blocks away");
        }
        helper.assertTrue(!d.entries().get(4).present() && d.entries().get(3).present(), "the last one here is lost, the others present");
        helper.assertTrue(d.chosen(here).orElseThrow().name().equals("Iron mine"), "the second one is chosen: " + d.chosen(here));
        helper.assertTrue(d.chosen("minecraft:the_nether").isPresent(), "the nether entry is chosen there");
        helper.assertTrue(DebugCommand.mock(player, 1).entries().get(0).present(), "a single entry is not marked lost");
        helper.succeed();
    }

    @GameTest
    public void theCommandFillsTheHeldCompassOnADevelopmentServer(GameTestHelper helper) {
        helper.assertTrue(FabricLoader.getInstance().isDevelopmentEnvironment(), "game tests run in the development environment, where the command exists");
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        ItemStack compass = new ItemStack(BrassCompass.BRASS_COMPASS);
        player.setItemInHand(InteractionHand.MAIN_HAND, compass);
        var dispatcher = helper.getLevel().getServer().getCommands().getDispatcher();
        try {
            int result = dispatcher.execute(dispatcher.parse("brass_compass debug 3", player.createCommandSourceStack().withSuppressedOutput()));
            helper.assertTrue(result == 4, "three here plus one elsewhere: " + result);
        } catch (Exception e) {
            helper.fail("the command did not run: " + e.getMessage());
        }
        Destinations d = BrassCompassItem.destinationsOf(player.getItemInHand(InteractionHand.MAIN_HAND));
        helper.assertTrue(d.entries().size() == 4, "the held compass was filled: " + d.entries());
        helper.assertTrue(compass.get(DataComponents.LODESTONE_TRACKER).target().isPresent(), "and the needle has a target");
        helper.succeed();
    }
}
