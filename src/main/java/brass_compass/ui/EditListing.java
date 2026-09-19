package brass_compass.ui;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;

/**
 * What the edit screen shows (UI-UC-001): the lodestone's place, the current or default name, and
 * whether an entry exists there already (which is when remove is offered).
 *
 * @param hand which hand holds the compass, so the client can address the save packet
 */
public record EditListing(InteractionHand hand, String dimension, BlockPos pos, String name, boolean existing) {
    public static final StreamCodec<ByteBuf, EditListing> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, listing -> listing.hand() == InteractionHand.OFF_HAND,
        ByteBufCodecs.STRING_UTF8, EditListing::dimension,
        BlockPos.STREAM_CODEC, EditListing::pos,
        ByteBufCodecs.STRING_UTF8, EditListing::name,
        ByteBufCodecs.BOOL, EditListing::existing,
        (off, dimension, pos, name, existing) -> new EditListing(off ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, dimension, pos, name, existing));
}
