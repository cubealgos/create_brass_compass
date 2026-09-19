package brass_compass.gametest;

import brass_compass.BrassCompass;
import brass_compass.destinations.Destinations;
import com.mojang.serialization.DynamicOps;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** BC-2: the destinations component survives an item stack's save and parse round trip. */
public final class ComponentGameTest {
    @GameTest
    public void theComponentRoundTripsThroughAnItemStack(GameTestHelper helper) {
        Destinations d = Destinations.EMPTY.save("minecraft:overworld", 10, 64, -3, "Home").destinations()
            .save("minecraft:the_nether", 5, 40, 5, "Fortress").destinations()
            .markPresent(1, false);
        ItemStack stack = new ItemStack(Items.COMPASS);
        stack.set(BrassCompass.DESTINATIONS, d);
        DynamicOps<Tag> ops = helper.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE);
        Tag saved = ItemStack.CODEC.encodeStart(ops, stack).getOrThrow(msg -> new AssertionError("encode: " + msg));
        ItemStack back = ItemStack.CODEC.parse(ops, saved).getOrThrow(msg -> new AssertionError("parse: " + msg));
        Destinations read = back.get(BrassCompass.DESTINATIONS);
        helper.assertTrue(read != null && read.equals(d), "the component round-trips: " + read);
        helper.assertTrue(!read.entries().get(1).present(), "the lost mark survived");
        helper.succeed();
    }
}
