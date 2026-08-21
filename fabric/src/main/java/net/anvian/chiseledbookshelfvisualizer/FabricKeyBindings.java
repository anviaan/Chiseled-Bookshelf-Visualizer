package net.anvian.chiseledbookshelfvisualizer;

import com.mojang.blaze3d.platform.InputConstants;
import net.anvian.chiseledbookshelfvisualizer.client.render.BookInfoRenderer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class FabricKeyBindings {
    private static final KeyMapping.Category CATEGORY =
            new KeyMapping.Category(Identifier.fromNamespaceAndPath(ChiseledBookshelfVisualizerMod.MOD_ID, "category"));
    private static KeyMapping activateKey;

    private FabricKeyBindings() {}

    public static void register() {
        activateKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.chiseledbookshelfvisualizer.title", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (activateKey.consumeClick()) {
                BookInfoRenderer.toggleCrosshair();
                if (client.player == null) {
                    continue;
                }
                Component message = BookInfoRenderer.shouldRenderCrosshair()
                        ? Component.translatable("key.chiseledbookshelfvisualizer.enabled")
                        : Component.translatable("key.chiseledbookshelfvisualizer.disabled");
                client.player.sendOverlayMessage(message);
            }
        });
    }
}
