package net.anvian.chiseledbookshelfvisualizer;

import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryRequestPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.ModStatusPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.server.BookInventoryRequestHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ChiseledBookshelfVisualizerMod.init();
        registerPackets();
        registerServerHandlers();
        ServerPlayConnectionEvents.JOIN.register(
                (handler, _, _) -> ServerPlayNetworking.send(handler.player, new ModStatusPacket(true)));
        ChiseledBookshelfVisualizerMod.LOGGER.info("Bookshelf Visualizer initialized on Fabric");
    }

    private static void registerPackets() {
        PayloadTypeRegistry.serverboundPlay()
                .register(BookInventoryRequestPacket.TYPE, BookInventoryRequestPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(BookInventoryPacket.TYPE, BookInventoryPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ModStatusPacket.TYPE, ModStatusPacket.STREAM_CODEC);
    }

    private static void registerServerHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(
                BookInventoryRequestPacket.TYPE,
                (packet, context) -> context.server()
                        .execute(() -> ServerPlayNetworking.send(
                                context.player(),
                                BookInventoryRequestHandler.createResponse(packet, context.player()))));
    }
}
