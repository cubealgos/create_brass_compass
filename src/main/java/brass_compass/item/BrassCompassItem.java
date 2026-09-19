package brass_compass.item;

import brass_compass.BrassCompass;
import brass_compass.destinations.Destinations;
import brass_compass.destinations.Entry;
import brass_compass.ui.EditProvider;
import brass_compass.ui.SwitchProvider;
import net.minecraft.world.item.context.UseOnContext;
import com.zurrtum.create.foundation.gui.menu.MenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * The brass compass (docs/spec/domains/compass.md). Its needle is vanilla's: every server
 * inventory tick the {@code lodestone_tracker} component is derived from the chosen entry of the
 * holder's dimension (COMPASS-REQ-005, -006, -015), and that entry is marked lost or present by
 * looking at the block (COMPASS-REQ-008, -009). Nothing chosen means an untracked tracker, and the
 * vanilla model spins the needle (COMPASS-DEC-006).
 */
public final class BrassCompassItem extends Item {
    public BrassCompassItem(Properties properties) {
        super(properties);
    }

    public static Destinations destinationsOf(ItemStack stack) {
        Destinations d = stack.get(BrassCompass.DESTINATIONS);
        return d == null ? Destinations.EMPTY : d;
    }

    public static String dimensionId(Level level) {
        return level.dimension().identifier().toString();
    }

    /**
     * Not sneaking, a use on a lodestone opens the add/edit screen for it (COMPASS-REQ-002);
     * anything else passes on to {@link #use}. Sneaking is a normal right-click (COMPASS-REQ-014).
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || player.isShiftKeyDown() || !context.getLevel().getBlockState(context.getClickedPos()).is(Blocks.LODESTONE)) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer server) {
            MenuProvider.openHandledScreen(server, new EditProvider(context.getHand(), context.getClickedPos()));
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * Not sneaking, a use anywhere but on a lodestone opens the switch screen (COMPASS-REQ-003);
     * sneaking is a normal right-click (COMPASS-REQ-014). A lodestone is handled in useOn.
     */
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer server) {
            MenuProvider.openHandledScreen(server, new SwitchProvider(hand));
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.SUCCESS;
    }

    /** Glints like a vanilla compass bound to a lodestone: whenever the needle has a target (COMPASS-REQ-016). */
    @Override
    public boolean isFoil(ItemStack stack) {
        LodestoneTracker tracker = stack.get(DataComponents.LODESTONE_TRACKER);
        return tracker != null && tracker.target().isPresent();
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity holder, EquipmentSlot slot) {
        refresh(stack, level);
    }

    /** Derives the tracker and the present marks for the level the holder is in; public for the game tests. */
    public static void refresh(ItemStack stack, ServerLevel level) {
        Destinations d = destinationsOf(stack);
        String dimension = dimensionId(level);
        OptionalInt chosen = d.chosenIndex(dimension);
        if (chosen.isEmpty()) {
            stack.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.empty(), false));
            return;
        }
        Entry entry = d.entries().get(chosen.getAsInt());
        BlockPos pos = new BlockPos(entry.x(), entry.y(), entry.z());
        boolean present = level.isLoaded(pos) ? level.getBlockState(pos).is(Blocks.LODESTONE) : entry.present();
        if (present != entry.present()) {
            d = d.markPresent(chosen.getAsInt(), present);
            stack.set(BrassCompass.DESTINATIONS, d);
        }
        ResourceKey<Level> key = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, Identifier.parse(entry.dimension()));
        LodestoneTracker tracker = present
            ? new LodestoneTracker(Optional.of(GlobalPos.of(key, pos)), true)
            : new LodestoneTracker(Optional.empty(), true);
        stack.set(DataComponents.LODESTONE_TRACKER, tracker);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        Destinations d = destinationsOf(stack);
        int total = d.entries().size();
        if (d.readOnly()) {
            lines.accept(Component.translatable("tooltip.brass_compass.newer_version"));
        }
        lines.accept(Component.translatable("tooltip.brass_compass.saved", total));
        // The tooltip has no level: the chosen entry of any dimension is named, the holder's first.
        d.current().values().stream().findFirst()
            .map(i -> d.entries().get(i))
            .ifPresentOrElse(
                e -> lines.accept(Component.translatable(e.present() ? "tooltip.brass_compass.destination" : "tooltip.brass_compass.destination_lost", e.name())),
                () -> lines.accept(Component.translatable("tooltip.brass_compass.no_destination")));
    }
}
