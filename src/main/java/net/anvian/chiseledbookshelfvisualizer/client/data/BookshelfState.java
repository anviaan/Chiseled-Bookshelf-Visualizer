package net.anvian.chiseledbookshelfvisualizer.client.data;

import net.minecraft.core.BlockPos;

public class BookshelfState {
    public boolean isCurrentBookDataToggled = false;
    public BlockPos latestPos = null;
    public boolean requestSent = false;
    public int currentSlotInt = -1;
}
