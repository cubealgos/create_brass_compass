package brass_compass.gametest;

import brass_compass.BrassCompass;
import brass_compass.item.BrassCompassItem;
import com.zurrtum.create.AllItems;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

/** BC-4: both recipes of DEC-005 yield one brass compass with no entries, through the server's recipe manager, so a datapack override is what the manager would serve instead. */
public final class RecipeGameTest {
    private static Optional<RecipeHolder<CraftingRecipe>> find(ServerLevel level, int width, int height, List<ItemStack> grid) {
        return level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, CraftingInput.of(width, height, grid), level);
    }

    @GameTest
    public void aCompassRingedByBrassMakesABrassCompass(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ItemStack brass = new ItemStack(AllItems.BRASS_INGOT);
        List<ItemStack> grid = List.of(brass.copy(), brass.copy(), brass.copy(), brass.copy(), new ItemStack(Items.COMPASS), brass.copy(), brass.copy(), brass.copy(), brass.copy());
        RecipeHolder<CraftingRecipe> recipe = find(level, 3, 3, grid).orElseThrow(() -> new AssertionError("no recipe matched the brass ring"));
        helper.assertTrue(recipe.id().identifier().getNamespace().equals(BrassCompass.MOD_ID), "the mod's recipe answered: " + recipe.id());
        ItemStack result = recipe.value().assemble(CraftingInput.of(3, 3, grid));
        helper.assertTrue(result.is(BrassCompass.BRASS_COMPASS) && result.getCount() == 1, "one brass compass: " + result);
        helper.assertTrue(BrassCompassItem.destinationsOf(result).entries().isEmpty(), "born with no entries");
        helper.succeed();
    }

    @GameTest
    public void aCompassAndAPrecisionMechanismMakeABrassCompass(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        List<ItemStack> grid = List.of(new ItemStack(Items.COMPASS), new ItemStack(AllItems.PRECISION_MECHANISM));
        RecipeHolder<CraftingRecipe> recipe = find(level, 2, 1, grid).orElseThrow(() -> new AssertionError("no recipe matched compass plus mechanism"));
        helper.assertTrue(recipe.id().identifier().getPath().equals("brass_compass_from_mechanism"), "the shapeless recipe answered: " + recipe.id());
        ItemStack result = recipe.value().assemble(CraftingInput.of(2, 1, grid));
        helper.assertTrue(result.is(BrassCompass.BRASS_COMPASS) && result.getCount() == 1, "one brass compass: " + result);
        helper.succeed();
    }
}
