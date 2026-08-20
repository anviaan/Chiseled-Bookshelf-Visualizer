package net.anvian.chiseledbookshelfvisualizer.common.network.client;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerClient;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.ModStatusPacket;

public final class ClientPacketHandlers {
    private ClientPacketHandlers() {}

    public static void handleBookInventory(BookInventoryPacket packet) {
        ChiseledBookshelfVisualizerClient.handleBookInventory(packet);
    }

    public static void handleModStatus(ModStatusPacket packet) {
        ChiseledBookshelfVisualizerClient.handleModStatus(packet);
    }
}
