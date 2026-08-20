package net.anvian.chiseledbookshelfvisualizer.common.network;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerMod;
import net.minecraft.resources.Identifier;

public class NetworkConstants {
    private NetworkConstants() {
        /* This utility class should not be instantiated */
    }

    public static final Identifier BOOK_SHELF_INVENTORY_REQUEST_PACKET_ID =
            Identifier.fromNamespaceAndPath(ChiseledBookshelfVisualizerMod.MOD_ID, "book_shelf_inventory_request");
    public static final Identifier BOOK_SHELF_INVENTORY_PACKET_ID =
            Identifier.fromNamespaceAndPath(ChiseledBookshelfVisualizerMod.MOD_ID, "book_shelf_inventory");
    public static final Identifier MOD_CHECK_PACKET_ID =
            Identifier.fromNamespaceAndPath(ChiseledBookshelfVisualizerMod.MOD_ID, "mod_check");
}
