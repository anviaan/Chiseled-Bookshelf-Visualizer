package net.anvian.chiseledbookshelfvisualizer.common.network.packets;

import net.anvian.chiseledbookshelfvisualizer.common.network.NetworkConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ModStatusPacket(boolean modActivated) implements CustomPayload {
    public static final CustomPayload.Id<ModStatusPacket> ID = new CustomPayload.Id<>(NetworkConstants.MOD_CHECK_PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, ModStatusPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, ModStatusPacket::modActivated,
            ModStatusPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}