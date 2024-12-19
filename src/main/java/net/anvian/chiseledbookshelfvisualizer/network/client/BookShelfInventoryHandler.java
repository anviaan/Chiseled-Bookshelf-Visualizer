package net.anvian.chiseledbookshelfvisualizer.network.client;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerClient;
import net.anvian.chiseledbookshelfvisualizer.data.BookData;
import net.anvian.chiseledbookshelfvisualizer.network.packets.BookShelfInventoryPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.Items;

@Environment(EnvType.CLIENT)
public class BookShelfInventoryHandler implements ClientPlayNetworking.PlayPayloadHandler<BookShelfInventoryPayload> {
    @Override
    public void receive(BookShelfInventoryPayload bookShelfInventoryPayload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            ChiseledBookshelfVisualizerClient.bookShelfData.requestSent = false;
            if (bookShelfInventoryPayload.itemStack().isOf(Items.AIR)) {
                ChiseledBookshelfVisualizerClient.bookShelfData.isCurrentBookDataToggled = false;
                ChiseledBookshelfVisualizerClient.currentBookData = BookData.empty();
                ChiseledBookshelfVisualizerClient.currentBookData.slotId = -2;
            } else {
                ChiseledBookshelfVisualizerClient.bookShelfData.isCurrentBookDataToggled = true;
                ChiseledBookshelfVisualizerClient.currentBookData = new BookData(bookShelfInventoryPayload.itemStack(), bookShelfInventoryPayload.pos(), bookShelfInventoryPayload.slotNum());
            }
        });
    }
}