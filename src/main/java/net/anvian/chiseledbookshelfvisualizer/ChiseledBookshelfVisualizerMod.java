package net.anvian.chiseledbookshelfvisualizer;

import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryRequestPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.ModStatusPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.server.BookInventoryRequestHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChiseledBookshelfVisualizerMod implements ModInitializer {
    public static final String MOD_ID = "chiseled-bookshelf-visualizer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static MinecraftServer serverInstance;

    @Override
    public void onInitialize() {
        registerPackets();
        registerServerHandlers();
        registerServerEvents();

        LOGGER.info("Bookshelf Visualizer initialized");
    }

    private void registerPackets() {
        // Client to Server packets
        PayloadTypeRegistry.playC2S().register(
                BookInventoryRequestPacket.ID,
                BookInventoryRequestPacket.CODEC
        );

        // Server to Client packets
        PayloadTypeRegistry.playS2C().register(
                BookInventoryPacket.ID,
                BookInventoryPacket.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
                ModStatusPacket.ID,
                ModStatusPacket.CODEC
        );
    }

    private void registerServerHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(
                BookInventoryRequestPacket.ID,
                new BookInventoryRequestHandler()
        );
    }

    private void registerServerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            serverInstance = server;
            ServerPlayNetworking.send(handler.player, new ModStatusPacket(true));
        });
    }

    public static MinecraftServer getServerInstance() {
        return serverInstance;
    }
}
