package net.anvian.chiseledbookshelfvisualizer.client.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerMod;

@Config(name = ChiseledBookshelfVisualizerMod.MOD_ID)
public class ClientConfig implements ConfigData {
    public double scale = 1.0;
    public boolean useRoman = true;
}
