package net.anvian.chiseledbookshelfvisualizer;

import net.anvian.anvianslib.util.LibUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChiseledBookshelfVisualizerMod {
    public static final String MOD_ID = "chiseledbookshelfvisualizer";
    public static final String MOD_VERSION = "4.4.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private ChiseledBookshelfVisualizerMod() {}

    public static void init() {
        LibUtil.setupTelemetry(MOD_ID, MOD_VERSION);
    }
}
