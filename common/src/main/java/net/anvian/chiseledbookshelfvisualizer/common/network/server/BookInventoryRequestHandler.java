package net.anvian.chiseledbookshelfvisualizer.common.network.server;

import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryRequestPacket;
import net.anvian.chiseledbookshelfvisualizer.common.util.BookshelfBlockUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class BookInventoryRequestHandler {
    private BookInventoryRequestHandler() {}

    public static BookInventoryPacket createResponse(BookInventoryRequestPacket request, ServerPlayer player) {
        ItemStack stack = BookshelfBlockUtil.getItemById(request.pos(), request.slotNum(), player);
        if (stack == null) stack = Items.AIR.getDefaultInstance();

        return new BookInventoryPacket(stack, request.pos(), request.slotNum());
    }
}
