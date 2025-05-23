package net.anvian.chiseledbookshelfvisualizer.common.network.packets;

import net.anvian.chiseledbookshelfvisualizer.common.network.NetworkConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record LecternInventoryRequestPacket(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<LecternInventoryRequestPacket> ID = new CustomPayload.Id<>(NetworkConstants.LECTERN_INVENTORY_REQUEST_PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, LecternInventoryRequestPacket> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, LecternInventoryRequestPacket::pos,
            LecternInventoryRequestPacket::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}