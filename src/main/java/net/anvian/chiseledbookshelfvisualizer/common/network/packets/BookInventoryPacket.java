package net.anvian.chiseledbookshelfvisualizer.common.network.packets;

import net.anvian.chiseledbookshelfvisualizer.common.network.NetworkConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

public record BookInventoryPacket(ItemStack itemStack, BlockPos pos, int slotNum) implements CustomPacketPayload {
    public static final Type<BookInventoryPacket> TYPE = new Type<>(NetworkConstants.BOOK_SHELF_INVENTORY_PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, BookInventoryPacket> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, BookInventoryPacket::itemStack,
            BlockPos.STREAM_CODEC, BookInventoryPacket::pos,
            ByteBufCodecs.INT, BookInventoryPacket::slotNum,
            BookInventoryPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
