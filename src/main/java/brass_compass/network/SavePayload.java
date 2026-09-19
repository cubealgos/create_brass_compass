package brass_compass.network;

import brass_compass.BrassCompass;
import brass_compass.destinations.Destinations;
import brass_compass.destinations.Names;
import brass_compass.item.BrassCompassItem;
import brass_compass.ui.EditMenu;
import io.netty.buffer.ByteBuf;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/**
 * Save an entry: the place, the dimension the client believes it is in, and the name. The server
 * applies it only to a brass compass in that hand of the sending player, in the player's own
 * dimension, and only if a lodestone stands there or an entry exists there already (COMPASS-REQ-007,
 * UI-FAIL-003). The name is stored plain (UI-FAIL-002).
 */
public record SavePayload(InteractionHand hand, String dimension, BlockPos pos, String name) implements CustomPacketPayload {
    public static final Type<SavePayload> TYPE = new Type<>(BrassCompass.id("save"));
    public static final StreamCodec<ByteBuf, SavePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, payload -> payload.hand() == InteractionHand.OFF_HAND,
        ByteBufCodecs.STRING_UTF8, SavePayload::dimension,
        BlockPos.STREAM_CODEC, SavePayload::pos,
        ByteBufCodecs.stringUtf8(256), SavePayload::name,
        (off, dimension, pos, name) -> new SavePayload(off ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, dimension, pos, name));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** The server's handling; returns whether the entry was written. Closes the edit screen either way. */
    public static boolean apply(ServerPlayer player, SavePayload payload) {
        boolean applied = write(player, payload);
        if (player.containerMenu instanceof EditMenu) {
            player.closeContainer();
        }
        return applied;
    }

    private static boolean write(ServerPlayer player, SavePayload payload) {
        ItemStack stack = player.getItemInHand(payload.hand());
        if (!stack.is(BrassCompass.BRASS_COMPASS)) {
            return false;
        }
        ServerLevel level = player.level();
        String dimension = BrassCompassItem.dimensionId(level);
        if (!dimension.equals(payload.dimension())) {
            return false;
        }
        BlockPos pos = payload.pos();
        Destinations before = BrassCompassItem.destinationsOf(stack);
        OptionalInt existing = before.indexOf(dimension, pos.getX(), pos.getY(), pos.getZ());
        boolean lodestone = level.isLoaded(pos) && level.getBlockState(pos).is(Blocks.LODESTONE);
        if (existing.isEmpty() && !lodestone) {
            return false;
        }
        String name = Names.sanitize(payload.name(), Names.defaultFor(pos.getX(), pos.getY(), pos.getZ()));
        Destinations after;
        if (existing.isPresent()) {
            after = before.rename(existing.getAsInt(), name).choose(dimension, existing.getAsInt());
        } else {
            after = before.save(dimension, pos.getX(), pos.getY(), pos.getZ(), name).destinations();
        }
        if (after.readOnly()) {
            return false;
        }
        stack.set(BrassCompass.DESTINATIONS, after);
        BrassCompassItem.refresh(stack, level);
        level.playSound(null, pos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
        return true;
    }
}
