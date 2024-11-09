package net.anvian.chiseledbookshelfvisualizer.util;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class LecternTools {
    public static ItemStack getItemStack(BlockPos pos, PlayerEntity player) {
        final World world = ChiseledBookshelfVisualizer.serverInstance.getPlayerManager().getPlayer(player.getUuid()).getWorld();

        if (world == null) return null;
        Optional<LecternBlockEntity> blockEntityOptional = world.getBlockEntity(pos, BlockEntityType.LECTERN);
        if (blockEntityOptional.isEmpty()) return null;

        LecternBlockEntity lecternBlock = blockEntityOptional.get();

        return lecternBlock.getBook();
    }
}
