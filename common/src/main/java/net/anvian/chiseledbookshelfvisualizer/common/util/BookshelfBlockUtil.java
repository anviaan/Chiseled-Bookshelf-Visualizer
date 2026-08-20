package net.anvian.chiseledbookshelfvisualizer.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;

import java.util.Optional;

public final class BookshelfBlockUtil {
    private BookshelfBlockUtil() {}

    public static ItemStack getItemById(BlockPos pos, int slotNum, ServerPlayer player) {
        return getItemFromBookshelf(player.level(), pos, slotNum);
    }

    public static ItemStack getItemFromBookshelf(ServerLevel world, BlockPos pos, int slotNum) {
        if (slotNum < 0 || slotNum >= 6) {
            return null;
        }

        Optional<ChiseledBookShelfBlockEntity> blockEntityOptional =
                world.getBlockEntity(pos, BlockEntityTypes.CHISELED_BOOKSHELF);
        if (blockEntityOptional.isEmpty()) {
            return null;
        }

        ItemStack stack = blockEntityOptional.get().getItems().get(slotNum);
        return stack.isEmpty() ? null : stack;
    }
}
