package net.anvian.chiseledbookshelfvisualizer;

import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.anvian.chiseledbookshelfvisualizer.client.config.ClientConfig;
import net.anvian.chiseledbookshelfvisualizer.client.raycast.BlockInspector;
import net.anvian.chiseledbookshelfvisualizer.client.render.BookInfoRenderer;
import net.anvian.chiseledbookshelfvisualizer.common.network.client.ClientPacketHandlers;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.ModStatusPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

public final class NeoForgeClient {
    private static final KeyMapping.Category CATEGORY =
            new KeyMapping.Category(Identifier.fromNamespaceAndPath(ChiseledBookshelfVisualizerMod.MOD_ID, "category"));
    private static KeyMapping activateKey;
    private static final BlockInspector BLOCK_INSPECTOR = new BlockInspector(ClientPacketDistributor::sendToServer);

    private NeoForgeClient() {}

    public static void init(IEventBus modEventBus, ModContainer modContainer) {
        ChiseledBookshelfVisualizerClient.initConfig(FMLPaths.CONFIGDIR.get());
        modEventBus.addListener(NeoForgeClient::registerKeyMappings);
        modEventBus.addListener(NeoForgeClient::registerGuiLayers);
        modEventBus.addListener(NeoForgeClient::registerClientPayloadHandlers);
        NeoForge.EVENT_BUS.addListener(NeoForgeClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(NeoForgeClient::onLoggingOut);
        modContainer.registerExtensionPoint(
                IConfigScreenFactory.class,
                (_, parent) -> AutoConfigClient.getConfigScreen(ClientConfig.class, parent)
                        .get());
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        activateKey = new KeyMapping(
                "key.chiseledbookshelfvisualizer.title", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY);
        event.registerCategory(CATEGORY);
        event.register(activateKey);
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        Identifier id = Identifier.fromNamespaceAndPath(ChiseledBookshelfVisualizerMod.MOD_ID, "book_info");
        event.registerAbove(
                VanillaGuiLayers.CROSSHAIR,
                id,
                (context, _) -> BookInfoRenderer.hudRender(context, Minecraft.getInstance()));
    }

    private static void registerClientPayloadHandlers(RegisterClientPayloadHandlersEvent event) {
        event.register(
                BookInventoryPacket.TYPE,
                (packet, context) -> context.enqueueWork(() -> ClientPacketHandlers.handleBookInventory(packet)));
        event.register(
                ModStatusPacket.TYPE,
                (packet, context) -> context.enqueueWork(() -> ClientPacketHandlers.handleModStatus(packet)));
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        BLOCK_INSPECTOR.inspect(client);
        if (activateKey == null) {
            return;
        }
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
    }

    private static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ChiseledBookshelfVisualizerClient.reset();
    }
}
