package net.anvian.chiseledbookshelfvisualizer.client.input;

import net.anvian.chiseledbookshelfvisualizer.client.render.BookInfoRenderer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static KeyBinding activateKey;
    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of("chiseled-bookshelf-visualizer", "category"));

    public static void register() {
        activateKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.chiseled-bookshelf-visualizer.title",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                CATEGORY
        ));
        registerKeyInputs();
    }

    private static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (activateKey.wasPressed()) {
                BookInfoRenderer.toggleCrosshair();

                if (BookInfoRenderer.shouldRenderCrosshair()) {
                    client.player.sendMessage(net.minecraft.text.Text.translatable("key.chiseled-bookshelf-visualizer.enabled"), true);
                } else {
                    client.player.sendMessage(net.minecraft.text.Text.translatable("key.chiseled-bookshelf-visualizer.disabled"), true);
                }
            }
        });
    }
}
