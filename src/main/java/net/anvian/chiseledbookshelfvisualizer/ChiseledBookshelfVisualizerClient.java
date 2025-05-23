package net.anvian.chiseledbookshelfvisualizer;

import net.anvian.chiseledbookshelfvisualizer.client.config.ClientConfigWrapper;
import net.anvian.chiseledbookshelfvisualizer.client.data.BookInfo;
import net.anvian.chiseledbookshelfvisualizer.client.data.BookshelfState;
import net.anvian.chiseledbookshelfvisualizer.client.input.KeyBindings;
import net.anvian.chiseledbookshelfvisualizer.common.network.client.BookInventoryHandler;
import net.anvian.chiseledbookshelfvisualizer.common.network.client.ModStatusHandler;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.ModStatusPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class ChiseledBookshelfVisualizerClient implements ClientModInitializer {
    public static final ClientConfigWrapper CONFIG = ClientConfigWrapper.createAndLoad();

    private static BookInfo currentBookInfo = BookInfo.empty();
    private static BookshelfState bookshelfState = new BookshelfState();
    private static boolean modAvailable = false;

    @Override
    public void onInitializeClient() {
        KeyBindings.register();
        registerClientPacketHandlers();
        registerClientEvents();

        ChiseledBookshelfVisualizerMod.LOGGER.info("Bookshelf Visualizer client initialized");
    }

    private void registerClientPacketHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(
                BookInventoryPacket.ID,
                new BookInventoryHandler()
        );
        ClientPlayNetworking.registerGlobalReceiver(
                ModStatusPacket.ID,
                new ModStatusHandler()
        );
    }

    private void registerClientEvents() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            modAvailable = false;
            bookshelfState = new BookshelfState();
        });
    }

    public static BookInfo getCurrentBookInfo() {
        return currentBookInfo;
    }

    public static void setCurrentBookInfo(BookInfo bookInfo) {
        currentBookInfo = bookInfo;
    }

    public static BookshelfState getBookshelfState() {
        return bookshelfState;
    }

    public static boolean isModAvailable() {
        return modAvailable;
    }

    public static void setModAvailable(boolean available) {
        modAvailable = available;
    }
}