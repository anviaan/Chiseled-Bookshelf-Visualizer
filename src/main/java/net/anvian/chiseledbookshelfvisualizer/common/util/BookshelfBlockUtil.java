package net.anvian.chiseledbookshelfvisualizer.common.util;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;

import java.util.Optional;

public class BookshelfBlockUtil {
    public static ItemStack getItemById(BlockPos pos, int slotNum, Player player) {
        ServerPlayer serverPlayer = ChiseledBookshelfVisualizerMod.getServerInstance()
                .getPlayerList()
                .getPlayer(player.getUUID());
        if (serverPlayer == null) return null;

        return getItemFromBookshelf(serverPlayer.level(), pos, slotNum);
    }

    public static ItemStack getItemFromBookshelf(ServerLevel world, BlockPos pos, int slotNum) {
        Optional<ChiseledBookShelfBlockEntity> blockEntityOptional = world.getBlockEntity(pos, BlockEntityTypes.CHISELED_BOOKSHELF);
        if (blockEntityOptional.isEmpty()) return null;

        ChiseledBookShelfBlockEntity blockEntity = blockEntityOptional.get();

        final ItemStack stack = blockEntity.getItems().get(slotNum);
        if (stack.isEmpty()) return null;

        return stack;
    }
}
