package net.anvian.chiseledbookshelfvisualizer.common.network.client;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerMod;
import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerClient;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.ModStatusPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class ModStatusHandler implements ClientPlayNetworking.PlayPayloadHandler<ModStatusPacket> {
    @Override
    public void receive(ModStatusPacket modStatusPacket, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            ChiseledBookshelfVisualizerMod.LOGGER.info("[" + ChiseledBookshelfVisualizerMod.MOD_ID + "] Connected to server");
            ChiseledBookshelfVisualizerClient.setModAvailable(true);
        });
    }
}