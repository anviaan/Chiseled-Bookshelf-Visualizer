package net.anvian.chiseledbookshelfvisualizer.network.client;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizer;
import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerClient;
import net.anvian.chiseledbookshelfvisualizer.network.packets.ModCheckPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class ModPayloadHandler implements ClientPlayNetworking.PlayPayloadHandler<ModCheckPayload> {
    @Override
    public void receive(ModCheckPayload modCheckPayload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            ChiseledBookshelfVisualizer.LOGGER.info("[" + ChiseledBookshelfVisualizer.MOD_ID + "] Connected to server");
            ChiseledBookshelfVisualizerClient.modAvailable = true;
        });
    }
}