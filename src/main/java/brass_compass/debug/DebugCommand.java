package brass_compass.debug;

import brass_compass.BrassCompass;
import brass_compass.destinations.Destinations;
import brass_compass.destinations.Entry;
import brass_compass.item.BrassCompassItem;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Development-only: {@code /brass_compass debug [count]} fills the brass compass in the main hand
 * (or gives a new one) with {@code count} mock entries at realistic bearings and distances from the
 * player, named from a fixed list, the second one chosen, the last one lost, plus one entry in the
 * other dimension. For UI checks and screenshots; never registered in a released jar (BC-15).
 */
public final class DebugCommand {
    public static final int DEFAULT_COUNT = 5;
    public static final int MAX_COUNT = 32;
    static final String[] NAMES = {
        "Home", "Iron mine", "Windmill farm", "Coastal outpost", "Old quarry", "Deep shaft", "Harbour",
        "Watchtower", "Cherry grove", "Trading post", "Wither arena", "Ancient city",
    };
    static final int[] DISTANCES = {228, 1469, 2901, 6622, 353, 940, 4120, 12750, 610, 2210, 7800, 1130};
    /** The golden angle in degrees: successive bearings never line up. */
    private static final double BEARING_STEP = 137.5;

    private DebugCommand() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> dispatcher.register(
            Commands.literal("brass_compass").then(Commands.literal("debug")
                .executes(c -> run(c.getSource(), DEFAULT_COUNT))
                .then(Commands.argument("count", IntegerArgumentType.integer(1, MAX_COUNT))
                    .executes(c -> run(c.getSource(), IntegerArgumentType.getInteger(c, "count")))))));
    }

    static int run(CommandSourceStack source, int count) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        Destinations d = mock(player, count);
        ItemStack held = player.getMainHandItem();
        ItemStack target = held.is(BrassCompass.BRASS_COMPASS) ? held : new ItemStack(BrassCompass.BRASS_COMPASS);
        target.set(BrassCompass.DESTINATIONS, d);
        BrassCompassItem.refresh(target, (ServerLevel) player.level());
        if (target != held && !player.getInventory().add(target)) {
            player.drop(target, false);
        }
        int n = d.entries().size();
        source.sendSuccess(() -> Component.translatable("command.brass_compass.debug.done", n), false);
        return n;
    }

    /**
     * The mock destinations for a player: {@code count} entries in the player's dimension, one more
     * in the other one. Pure of the command so the game test can call it.
     */
    public static Destinations mock(Player player, int count) {
        String here = BrassCompassItem.dimensionId(player.level());
        String other = here.equals("minecraft:the_nether") ? "minecraft:overworld" : "minecraft:the_nether";
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double bearing = Math.toRadians(i * BEARING_STEP);
            int distance = DISTANCES[i % DISTANCES.length];
            int x = (int) Math.round(player.getX() + Math.sin(bearing) * distance);
            int z = (int) Math.round(player.getZ() - Math.cos(bearing) * distance);
            int y = 30 + (i * 17) % 80;
            String name = NAMES[i % NAMES.length] + (i >= NAMES.length ? " " + (i / NAMES.length + 1) : "");
            boolean lost = count >= 2 && i == count - 1;
            entries.add(new Entry(here, x, y, z, name, !lost));
        }
        entries.add(new Entry(other, (int) player.getX() / 8 + 120, 40, (int) player.getZ() / 8 - 64,
            other.equals("minecraft:the_nether") ? "Fortress" : "Surface camp", true));
        Map<String, Integer> current = new TreeMap<>();
        current.put(here, Math.min(1, count - 1));
        current.put(other, count);
        return new Destinations(Destinations.VERSION, entries, current);
    }
}
