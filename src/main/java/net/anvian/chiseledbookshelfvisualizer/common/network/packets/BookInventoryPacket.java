package net.anvian.chiseledbookshelfvisualizer.common.network.packets;

import net.anvian.chiseledbookshelfvisualizer.common.network.NetworkConstants;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record BookInventoryPacket(ItemStack itemStack, BlockPos pos, int slotNum) implements CustomPayload {
    public static final CustomPayload.Id<BookInventoryPacket> ID = new CustomPayload.Id<>(NetworkConstants.BOOK_SHELF_INVENTORY_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, BookInventoryPacket> CODEC = PacketCodec.tuple(
            ItemStack.OPTIONAL_PACKET_CODEC, BookInventoryPacket::itemStack,
            BlockPos.PACKET_CODEC, BookInventoryPacket::pos,
            PacketCodecs.INTEGER, BookInventoryPacket::slotNum,
            BookInventoryPacket::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
