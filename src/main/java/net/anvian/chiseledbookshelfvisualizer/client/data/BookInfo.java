package net.anvian.chiseledbookshelfvisualizer.client.data;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class BookInfo {
    public ItemStack itemStack;
    public BlockPos pos;
    public int slotId;

    public BookInfo(ItemStack itemStack, BlockPos pos, int slotId) {
        this.itemStack = itemStack;
        this.pos = pos;
        this.slotId = slotId;
    }

    public static BookInfo empty(){
        return new BookInfo(ItemStack.EMPTY, null, -1);
    }
}
