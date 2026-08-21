package net.anvian.chiseledbookshelfvisualizer;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.anvian.chiseledbookshelfvisualizer.client.config.ClientConfig;
import net.anvian.chiseledbookshelfvisualizer.client.config.LegacyConfigMigration;
import net.anvian.chiseledbookshelfvisualizer.client.data.BookInfo;
import net.anvian.chiseledbookshelfvisualizer.client.data.BookshelfState;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.ModStatusPacket;

import java.nio.file.Path;

public final class ChiseledBookshelfVisualizerClient {
    public static ClientConfig CONFIG = new ClientConfig();

    private static BookInfo currentBookInfo = BookInfo.empty();
    private static BookshelfState bookshelfState = new BookshelfState();
    private static boolean modAvailable;

    private ChiseledBookshelfVisualizerClient() {}

    public static void initConfig(Path configDir) {
        var legacyValues = LegacyConfigMigration.read(configDir);
        AutoConfig.register(ClientConfig.class, Toml4jConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();
        if (legacyValues.isPresent()) {
            LegacyConfigMigration.Values values = legacyValues.get();
            if (values.scale() != null) {
                CONFIG.scale = values.scale();
            }
            if (values.useRoman() != null) {
                CONFIG.useRoman = values.useRoman();
            }
            AutoConfig.getConfigHolder(ClientConfig.class).save();
        }
    }

    public static void handleBookInventory(BookInventoryPacket packet) {
        bookshelfState.requestSent = false;
        if (packet.itemStack().isEmpty()) {
            bookshelfState.isCurrentBookDataToggled = false;
            setCurrentBookInfo(BookInfo.empty());
            currentBookInfo.slotId = -2;
        } else {
            bookshelfState.isCurrentBookDataToggled = true;
            setCurrentBookInfo(new BookInfo(packet.itemStack(), packet.pos(), packet.slotNum()));
        }
    }

    public static void handleModStatus(ModStatusPacket packet) {
        ChiseledBookshelfVisualizerMod.LOGGER.info("[{}] Connected to server", ChiseledBookshelfVisualizerMod.MOD_ID);
        setModAvailable(packet.modActivated());
    }

    public static void reset() {
        modAvailable = false;
        bookshelfState = new BookshelfState();
        currentBookInfo = BookInfo.empty();
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

    public static boolean isNotModAvailable() {
        return !modAvailable;
    }

    public static void setModAvailable(boolean available) {
        modAvailable = available;
    }
}
