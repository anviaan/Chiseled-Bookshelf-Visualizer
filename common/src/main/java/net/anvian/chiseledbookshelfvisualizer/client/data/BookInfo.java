package net.anvian.chiseledbookshelfvisualizer.client.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public class BookInfo {
    public ItemStack itemStack;
    public BlockPos pos;
    public int slotId;

    public BookInfo(ItemStack itemStack, BlockPos pos, int slotId) {
        this.itemStack = itemStack;
        this.pos = pos;
        this.slotId = slotId;
    }

    public static BookInfo empty() {
        return new BookInfo(ItemStack.EMPTY, null, -1);
    }
}
