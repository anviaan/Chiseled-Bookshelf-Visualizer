package net.anvian.chiseledbookshelfvisualizer.common.network.server;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerMod;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryRequestPacket;
import net.anvian.chiseledbookshelfvisualizer.common.util.BookshelfBlockUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BookInventoryRequestHandler implements ServerPlayNetworking.PlayPayloadHandler<BookInventoryRequestPacket> {
    @Override
    public void receive(BookInventoryRequestPacket bookInventoryRequestPacket, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if (ChiseledBookshelfVisualizerMod.getServerInstance() == null) return;

            ItemStack stack = BookshelfBlockUtil.getItemById(bookInventoryRequestPacket.pos(), bookInventoryRequestPacket.slotNum(), context.player());
            if (stack == null) {
                ServerPlayNetworking.send(context.player(), new BookInventoryPacket(Items.AIR.getDefaultInstance(), bookInventoryRequestPacket.pos(), bookInventoryRequestPacket.slotNum()));
                return;
            }
            ServerPlayNetworking.send(context.player(), new BookInventoryPacket(stack, bookInventoryRequestPacket.pos(), bookInventoryRequestPacket.slotNum()));
        });
    }
}
