package net.anvian.chiseledbookshelfvisualizer;

import net.anvian.chiseledbookshelfvisualizer.config.ChiseledBookshelfVisualizerConfig;
import net.anvian.chiseledbookshelfvisualizer.data.BookData;
import net.anvian.chiseledbookshelfvisualizer.data.BookShelfData;
import net.anvian.chiseledbookshelfvisualizer.network.client.BookShelfInventoryHandler;
import net.anvian.chiseledbookshelfvisualizer.network.client.ModPayloadHandler;
import net.anvian.chiseledbookshelfvisualizer.network.packets.BookShelfInventoryPayload;
import net.anvian.chiseledbookshelfvisualizer.network.packets.ModCheckPayload;
import net.anvian.chiseledbookshelfvisualizer.util.KeyInput;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class ChiseledBookshelfVisualizerClient implements ClientModInitializer {
    public static BookData currentBookData = BookData.empty();
    public static BookShelfData bookShelfData = new BookShelfData();
    public static boolean modAvailable = false;
    public static final ChiseledBookshelfVisualizerConfig CONFIG = ChiseledBookshelfVisualizerConfig.createAndLoad();

    @Override
    public void onInitializeClient() {
        KeyInput.register();
        ClientPlayNetworking.registerGlobalReceiver(BookShelfInventoryPayload.ID, new BookShelfInventoryHandler());
        ClientPlayNetworking.registerGlobalReceiver(ModCheckPayload.ID, new ModPayloadHandler());

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            modAvailable = false;
            bookShelfData = new BookShelfData();
        });
    }
}
