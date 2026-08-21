package net.anvian.chiseledbookshelfvisualizer;

import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryRequestPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.ModStatusPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.server.BookInventoryRequestHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(ChiseledBookshelfVisualizerMod.MOD_ID)
public class NeoForgeMod {
    public NeoForgeMod(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
        ChiseledBookshelfVisualizerMod.init();
        modEventBus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);

        if (dist == Dist.CLIENT) {
            NeoForgeClient.init(modEventBus, modContainer);
        }

        ChiseledBookshelfVisualizerMod.LOGGER.info("Bookshelf Visualizer initialized on NeoForge");
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(ChiseledBookshelfVisualizerMod.MOD_ID);
        registrar.playToServer(
                BookInventoryRequestPacket.TYPE,
                BookInventoryRequestPacket.STREAM_CODEC,
                (packet, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player) {
                        PacketDistributor.sendToPlayer(
                                player, BookInventoryRequestHandler.createResponse(packet, player));
                    }
                }));
        registrar.playToClient(BookInventoryPacket.TYPE, BookInventoryPacket.STREAM_CODEC);
        registrar.playToClient(ModStatusPacket.TYPE, ModStatusPacket.STREAM_CODEC);
    }

    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new ModStatusPacket(true));
        }
    }
}
