package net.anvian.chiseledbookshelfvisualizer.client.raycast;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerClient;
import net.anvian.chiseledbookshelfvisualizer.client.data.BookInfo;
import net.anvian.chiseledbookshelfvisualizer.client.data.BookshelfState;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryRequestPacket;
import net.anvian.chiseledbookshelfvisualizer.mixin.accessor.BookshelfBlockAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.OptionalInt;

@Environment(EnvType.CLIENT)
public class BlockInspector {
    public void inspect(MinecraftClient client) {
        if (!ChiseledBookshelfVisualizerClient.isModAvailable() || client.cameraEntity == null || client.player == null)
            return;

        HitResult hit = client.cameraEntity.raycast(5f, 0f, false);
        if (hit.getType() != HitResult.Type.BLOCK) {
            resetBookShelfData();
            return;
        }

        BlockHitResult blockHitResult = (BlockHitResult) hit;
        BlockPos pos = blockHitResult.getBlockPos();
        var bookshelfState = ChiseledBookshelfVisualizerClient.getBookshelfState();

        if (!pos.equals(bookshelfState.latestPos)) {
            resetBookShelfData();
            ChiseledBookshelfVisualizerClient.setCurrentBookInfo(BookInfo.empty());
        }
        bookshelfState.latestPos = pos;

        BlockState blockState = client.player.getWorld().getBlockState(pos);
        if (blockState.isOf(Blocks.CHISELED_BOOKSHELF)) {
            inspectBookshelf(pos, blockHitResult, client, bookshelfState);
        } else {
            bookshelfState.requestSent = false;
            if (bookshelfState.isCurrentBookDataToggled) {
                resetBookShelfData();
            }
        }
    }

    private void inspectBookshelf(BlockPos pos, BlockHitResult blockHitResult, MinecraftClient client, BookshelfState bookshelfState) {
        BlockState blockState = client.player.getWorld().getBlockState(pos);
        ChiseledBookshelfBlock bookshelfBlock = (ChiseledBookshelfBlock) blockState.getBlock();
        OptionalInt optionalInt = ((BookshelfBlockAccessor) bookshelfBlock).invokerGetSlotForHitPos(blockHitResult, blockState);

        if (optionalInt.isEmpty()) {
            resetBookShelfData();
            return;
        }

        int slotNum = optionalInt.getAsInt();
        BookInfo currentBookInfo = ChiseledBookshelfVisualizerClient.getCurrentBookInfo();
        int prevSlot = bookshelfState.currentSlotInt;
        bookshelfState.currentSlotInt = slotNum;

        if (currentBookInfo.slotId != slotNum && currentBookInfo.slotId != -2 && !bookshelfState.requestSent) {
            bookshelfState.requestSent = true;
            ClientPlayNetworking.send(new BookInventoryRequestPacket(pos, slotNum));
        } else if (prevSlot == slotNum) {
            bookshelfState.isCurrentBookDataToggled = currentBookInfo.slotId != -2;
        } else {
            bookshelfState.isCurrentBookDataToggled = false;
            ChiseledBookshelfVisualizerClient.setCurrentBookInfo(BookInfo.empty());
        }
    }

    private void resetBookShelfData() {
        if (!ChiseledBookshelfVisualizerClient.getBookshelfState().isCurrentBookDataToggled) return;

        ChiseledBookshelfVisualizerClient.getBookshelfState().isCurrentBookDataToggled = false;
        ChiseledBookshelfVisualizerClient.setCurrentBookInfo(BookInfo.empty());
    }
}