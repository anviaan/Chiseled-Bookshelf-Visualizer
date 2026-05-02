package net.anvian.chiseledbookshelfvisualizer;

import net.anvian.chiseledbookshelfvisualizer.client.config.ClientConfigWrapper;
import net.anvian.chiseledbookshelfvisualizer.client.data.BookInfo;
import net.anvian.chiseledbookshelfvisualizer.client.data.BookshelfState;
import net.anvian.chiseledbookshelfvisualizer.client.input.KeyBindings;
import net.anvian.chiseledbookshelfvisualizer.client.raycast.BlockInspector;
import net.anvian.chiseledbookshelfvisualizer.client.render.BookInfoRenderer;
import net.anvian.chiseledbookshelfvisualizer.common.network.client.BookInventoryHandler;
import net.anvian.chiseledbookshelfvisualizer.common.network.client.ModStatusHandler;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.ModStatusPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class ChiseledBookshelfVisualizerClient implements ClientModInitializer {
    public static final ClientConfigWrapper CONFIG = ClientConfigWrapper.createAndLoad();

    private static BookInfo currentBookInfo = BookInfo.empty();
    private static BookshelfState bookshelfState = new BookshelfState();
    private static boolean modAvailable = false;
    private final BlockInspector blockInspector = new BlockInspector();

    @Override
    public void onInitializeClient() {
        KeyBindings.register();
        registerClientPacketHandlers();
        registerClientEvents();
        registerHudElement();

        ChiseledBookshelfVisualizerMod.LOGGER.info("Bookshelf Visualizer client initialized");
    }

    private void registerClientPacketHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(
                BookInventoryPacket.TYPE,
                new BookInventoryHandler()
        );
        ClientPlayNetworking.registerGlobalReceiver(
                ModStatusPacket.TYPE,
                new ModStatusHandler()
        );
    }

    private void registerClientEvents() {
        ClientPlayConnectionEvents.DISCONNECT.register((_, _) -> {
            modAvailable = false;
            bookshelfState = new BookshelfState();
        });

        ClientTickEvents.END_CLIENT_TICK.register(blockInspector::inspect);
    }

    private void registerHudElement() {
        Identifier id = Identifier.fromNamespaceAndPath(ChiseledBookshelfVisualizerMod.MOD_ID, "book_info");
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CROSSHAIR,
                id,
                (context, _) -> BookInfoRenderer.hudRender(context, Minecraft.getInstance())
        );
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
