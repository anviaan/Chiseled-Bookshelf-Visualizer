package net.anvian.chiseledbookshelfvisualizer.client.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RestartRequired;
import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerMod;

@SuppressWarnings("unused")
@Modmenu(modId = ChiseledBookshelfVisualizerMod.MOD_ID)
@Config(name = ChiseledBookshelfVisualizerMod.MOD_ID + "/" + ChiseledBookshelfVisualizerMod.MOD_ID + "-config", wrapperName = "ClientConfigWrapper")
public class ClientConfig {
    @RestartRequired
    public double scale = 1.0;
    public boolean useRoman = true;
}