package net.anvian.chiseledbookshelfvisualizer;

import net.anvian.chiseledbookshelfvisualizer.client.raycast.BlockInspector;
import net.anvian.chiseledbookshelfvisualizer.client.render.BookInfoRenderer;
import net.anvian.chiseledbookshelfvisualizer.common.network.client.ClientPacketHandlers;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.ModStatusPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class FabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ChiseledBookshelfVisualizerClient.initConfig(FabricLoader.getInstance().getConfigDir());
        FabricKeyBindings.register();
        registerClientPacketHandlers();

        BlockInspector blockInspector = new BlockInspector(ClientPlayNetworking::send);
        ClientPlayConnectionEvents.DISCONNECT.register((_, _) -> ChiseledBookshelfVisualizerClient.reset());
        ClientTickEvents.END_CLIENT_TICK.register(blockInspector::inspect);

        Identifier id = Identifier.fromNamespaceAndPath(ChiseledBookshelfVisualizerMod.MOD_ID, "book_info");
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CROSSHAIR,
                id,
                (context, _) -> BookInfoRenderer.hudRender(context, Minecraft.getInstance()));

        ChiseledBookshelfVisualizerMod.LOGGER.info("Bookshelf Visualizer client initialized on Fabric");
    }

    private static void registerClientPacketHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(
                BookInventoryPacket.TYPE,
                (packet, context) -> context.client().execute(() -> ClientPacketHandlers.handleBookInventory(packet)));
        ClientPlayNetworking.registerGlobalReceiver(
                ModStatusPacket.TYPE,
                (packet, context) -> context.client().execute(() -> ClientPacketHandlers.handleModStatus(packet)));
    }
}
