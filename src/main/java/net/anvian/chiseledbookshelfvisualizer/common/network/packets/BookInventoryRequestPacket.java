package net.anvian.chiseledbookshelfvisualizer.common.network.packets;

import net.anvian.chiseledbookshelfvisualizer.common.network.NetworkConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record BookInventoryRequestPacket(BlockPos pos, int slotNum) implements CustomPacketPayload {

    public static final Type<BookInventoryRequestPacket> TYPE = new Type<>(NetworkConstants.BOOK_SHELF_INVENTORY_REQUEST_PACKET_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, BookInventoryRequestPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BookInventoryRequestPacket::pos,
            ByteBufCodecs.INT, BookInventoryRequestPacket::slotNum,
            BookInventoryRequestPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}