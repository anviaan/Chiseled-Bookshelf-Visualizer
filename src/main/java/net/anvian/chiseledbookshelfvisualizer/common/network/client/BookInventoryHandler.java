package net.anvian.chiseledbookshelfvisualizer.common.network.client;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerClient;
import net.anvian.chiseledbookshelfvisualizer.client.data.BookInfo;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class BookInventoryHandler implements ClientPlayNetworking.PlayPayloadHandler<BookInventoryPacket> {
    @Override
    public void receive(BookInventoryPacket bookInventoryPacket, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            ChiseledBookshelfVisualizerClient.getBookshelfState().requestSent = false;
            if (bookInventoryPacket.itemStack().isEmpty()) {
                ChiseledBookshelfVisualizerClient.getBookshelfState().isCurrentBookDataToggled = false;
                ChiseledBookshelfVisualizerClient.setCurrentBookInfo(BookInfo.empty());
                ChiseledBookshelfVisualizerClient.getCurrentBookInfo().slotId = -2;
            } else {
                ChiseledBookshelfVisualizerClient.getBookshelfState().isCurrentBookDataToggled = true;
                ChiseledBookshelfVisualizerClient.setCurrentBookInfo(new BookInfo(bookInventoryPacket.itemStack(), bookInventoryPacket.pos(), bookInventoryPacket.slotNum()));
            }
        });
    }
}