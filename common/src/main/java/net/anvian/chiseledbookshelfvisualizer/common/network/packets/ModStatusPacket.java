package net.anvian.chiseledbookshelfvisualizer.common.network.packets;

import net.anvian.chiseledbookshelfvisualizer.common.network.NetworkConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record ModStatusPacket(boolean modActivated) implements CustomPacketPayload {
    public static final Type<ModStatusPacket> TYPE = new Type<>(NetworkConstants.MOD_CHECK_PACKET_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ModStatusPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ModStatusPacket::modActivated,
            ModStatusPacket::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}