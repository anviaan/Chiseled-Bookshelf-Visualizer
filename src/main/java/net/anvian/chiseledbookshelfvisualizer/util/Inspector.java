package net.anvian.chiseledbookshelfvisualizer.util;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerClient;
import net.anvian.chiseledbookshelfvisualizer.data.BookData;
import net.anvian.chiseledbookshelfvisualizer.mixin.BookshelfInvoker;
import net.anvian.chiseledbookshelfvisualizer.network.BookShelfInventoryRequestPayload;
import net.anvian.chiseledbookshelfvisualizer.network.LecternInventoryRequestPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.OptionalInt;

@Environment(EnvType.CLIENT)
public class Inspector {
    public void inspect(MinecraftClient client) {
        if (!ChiseledBookshelfVisualizerClient.modAvailable) return;

        if (client.cameraEntity == null || client.player == null) return;

        HitResult hit = client.cameraEntity.raycast(5f, 0f, false);
        final HitResult.Type type = hit.getType();
        if (type != HitResult.Type.BLOCK) {
            ChiseledBookshelfVisualizerClient.bookShelfData.isCurrentBookDataToggled = false;
            ChiseledBookshelfVisualizerClient.currentBookData = BookData.empty();
        }
        final BlockHitResult blockHitResult = (BlockHitResult) hit;
        BlockPos pos = blockHitResult.getBlockPos();
        if (client.player.getWorld().getBlockState(pos).isOf(Blocks.CHISELED_BOOKSHELF)) {
            bookShelfInspect(pos, blockHitResult, client);
        } else if (client.player.getWorld().getBlockState(pos).isOf(Blocks.LECTERN) && ChiseledBookshelfVisualizerClient.CONFIG.lecternToggle()) {
            lecternInspect(pos, client);
        } else {
            ChiseledBookshelfVisualizerClient.bookShelfData.isCurrentBookDataToggled = false;
            ChiseledBookshelfVisualizerClient.currentBookData = BookData.empty();
            ChiseledBookshelfVisualizerClient.bookShelfData.latestPos = null;
            ChiseledBookshelfVisualizerClient.bookShelfData.requestSent = false;
        }
    }


    private void lecternInspect(BlockPos pos, MinecraftClient client) {
        Optional<LecternBlockEntity> optionalLecternBlockEntity = client.player.getWorld().getBlockEntity(pos, BlockEntityType.LECTERN);
        if (optionalLecternBlockEntity.isEmpty()) {
            ChiseledBookshelfVisualizerClient.bookShelfData.isCurrentBookDataToggled = false;
            ChiseledBookshelfVisualizerClient.currentBookData = BookData.empty();
            return;
        }

        if (ChiseledBookshelfVisualizerClient.bookShelfData.latestPos != null && ChiseledBookshelfVisualizerClient.bookShelfData.latestPos.equals(pos)) {
            return;
        }

        if (!ChiseledBookshelfVisualizerClient.bookShelfData.requestSent) {
            ChiseledBookshelfVisualizerClient.bookShelfData.requestSent = true;
            ClientPlayNetworking.send(new LecternInventoryRequestPayload(pos));
            ChiseledBookshelfVisualizerClient.bookShelfData.latestPos = pos;
        }
    }


    private void bookShelfInspect(BlockPos pos, BlockHitResult blockHitResult, MinecraftClient client) {
        Optional<ChiseledBookshelfBlockEntity> optionalChiseledBookshelfBlockEntity = client.player.getWorld().getBlockEntity(pos, BlockEntityType.CHISELED_BOOKSHELF);
        if (optionalChiseledBookshelfBlockEntity.isEmpty()) {
            ChiseledBookshelfVisualizerClient.bookShelfData.isCurrentBookDataToggled = false;
            ChiseledBookshelfVisualizerClient.currentBookData = BookData.empty();
            return;
        }

        final BlockState blockState = client.player.getWorld().getBlockState(pos);

        ChiseledBookshelfBlock bookshelfBlock = (ChiseledBookshelfBlock) blockState.getBlock();

        OptionalInt optionalInt = ((BookshelfInvoker) bookshelfBlock).invokerGetSlotForHitPos(blockHitResult, blockState);
        if (optionalInt.isEmpty()) {
            ChiseledBookshelfVisualizerClient.bookShelfData.isCurrentBookDataToggled = false;
            return;
        }

        final BookData currentBookData = ChiseledBookshelfVisualizerClient.currentBookData;

        int temp = ChiseledBookshelfVisualizerClient.bookShelfData.currentSlotInt;
        final int slotNum = optionalInt.getAsInt();
        ChiseledBookshelfVisualizerClient.bookShelfData.currentSlotInt = slotNum;

        if (currentBookData.slotId != slotNum && currentBookData.slotId != -2 && !ChiseledBookshelfVisualizerClient.bookShelfData.requestSent) {
            ChiseledBookshelfVisualizerClient.bookShelfData.requestSent = true;
            ClientPlayNetworking.send(new BookShelfInventoryRequestPayload(pos, slotNum));
        } else {
            if (temp == slotNum)
                ChiseledBookshelfVisualizerClient.bookShelfData.isCurrentBookDataToggled = currentBookData.slotId != -2;
            else {
                ChiseledBookshelfVisualizerClient.bookShelfData.isCurrentBookDataToggled = false;
                ChiseledBookshelfVisualizerClient.currentBookData = BookData.empty();
            }
        }
    }
}
