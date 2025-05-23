package net.anvian.chiseledbookshelfvisualizer.common.network.packets;

import net.anvian.chiseledbookshelfvisualizer.common.network.NetworkConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record BookInventoryRequestPacket(BlockPos pos, int slotNum) implements CustomPayload {

    public static final CustomPayload.Id<BookInventoryRequestPacket> ID = new CustomPayload.Id<>(NetworkConstants.BOOK_SHELF_INVENTORY_REQUEST_PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, BookInventoryRequestPacket> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, BookInventoryRequestPacket::pos,
            PacketCodecs.INTEGER, BookInventoryRequestPacket::slotNum,
            BookInventoryRequestPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}