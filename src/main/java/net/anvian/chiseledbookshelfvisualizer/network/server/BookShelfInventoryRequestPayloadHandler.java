package net.anvian.chiseledbookshelfvisualizer.network.server;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizer;
import net.anvian.chiseledbookshelfvisualizer.network.packets.BookShelfInventoryPayload;
import net.anvian.chiseledbookshelfvisualizer.network.packets.BookShelfInventoryRequestPayload;
import net.anvian.chiseledbookshelfvisualizer.util.BookshelfTools;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BookShelfInventoryRequestPayloadHandler implements ServerPlayNetworking.PlayPayloadHandler<BookShelfInventoryRequestPayload> {
    @Override
    public void receive(BookShelfInventoryRequestPayload bookShelfInventoryRequestPayload, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if (ChiseledBookshelfVisualizer.serverInstance == null) return;

            ItemStack stack = BookshelfTools.getItemById(bookShelfInventoryRequestPayload.pos(), bookShelfInventoryRequestPayload.slotNum(), context.player());
            if (stack == null) {
                ServerPlayNetworking.send(context.player(), new BookShelfInventoryPayload(Items.AIR.getDefaultStack(), bookShelfInventoryRequestPayload.pos(), bookShelfInventoryRequestPayload.slotNum()));
                return;
            }
            ServerPlayNetworking.send(context.player(), new BookShelfInventoryPayload(stack, bookShelfInventoryRequestPayload.pos(), bookShelfInventoryRequestPayload.slotNum()));
        });
    }
}
