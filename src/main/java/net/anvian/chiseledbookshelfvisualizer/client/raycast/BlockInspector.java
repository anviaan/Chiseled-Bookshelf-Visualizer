package net.anvian.chiseledbookshelfvisualizer.client.raycast;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerClient;
import net.anvian.chiseledbookshelfvisualizer.client.data.BookInfo;
import net.anvian.chiseledbookshelfvisualizer.client.data.BookshelfState;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryRequestPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.OptionalInt;

@Environment(EnvType.CLIENT)
public class BlockInspector {
    public void inspect(Minecraft client) {
        var cameraEntity = client.getCameraEntity();
        if (!ChiseledBookshelfVisualizerClient.isModAvailable() || cameraEntity == null || client.player == null || client.level == null)
            return;

        HitResult hit = cameraEntity.pick(5f, 0f, false);
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

        BlockState blockState = client.level.getBlockState(pos);
        if (blockState.is(Blocks.CHISELED_BOOKSHELF)) {
            inspectBookshelf(pos, blockHitResult, client, bookshelfState);
        } else {
            bookshelfState.requestSent = false;
            if (bookshelfState.isCurrentBookDataToggled) {
                resetBookShelfData();
            }
        }
    }

    private void inspectBookshelf(BlockPos pos, BlockHitResult blockHitResult, Minecraft client, BookshelfState bookshelfState) {
        BlockState blockState = client.level.getBlockState(pos);
        ChiseledBookShelfBlock bookshelfBlock = (ChiseledBookShelfBlock) blockState.getBlock();
        OptionalInt optionalInt = bookshelfBlock.getHitSlot(
                blockHitResult,
                blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        );

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