package net.anvian.chiseledbookshelfvisualizer.client.raycast;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerClient;
import net.anvian.chiseledbookshelfvisualizer.client.data.BookInfo;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryRequestPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.LecternInventoryRequestPacket;
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
        if (!ChiseledBookshelfVisualizerClient.isModAvailable()) return;

        if (client.cameraEntity == null || client.player == null) return;

        //Send raycast max 5 blocks
        HitResult hit = client.cameraEntity.raycast(5f, 0f, false);

        //find block hit, if not found block returns
        final HitResult.Type type = hit.getType();
        if (type != HitResult.Type.BLOCK) {
            resetBookShelfData();
            return;
        }

        final BlockHitResult blockHitResult = (BlockHitResult) hit;
        BlockPos pos = blockHitResult.getBlockPos();

        if (ChiseledBookshelfVisualizerClient.getBookshelfState().latestPos == null)
            ChiseledBookshelfVisualizerClient.getBookshelfState().latestPos = pos;

        //If you look at a new block
        if (!ChiseledBookshelfVisualizerClient.getBookshelfState().latestPos.equals(pos)) {
            resetBookShelfData();
            ChiseledBookshelfVisualizerClient.setCurrentBookInfo(BookInfo.empty());
        }
        ChiseledBookshelfVisualizerClient.getBookshelfState().latestPos = pos;


        if (client.player.getWorld().getBlockState(pos).isOf(Blocks.CHISELED_BOOKSHELF)) {
            bookShelfInspect(pos, blockHitResult, client);
        } else if (client.player.getWorld().getBlockState(pos).isOf(Blocks.LECTERN) && ChiseledBookshelfVisualizerClient.CONFIG.lecternToggle()) {
            lecternInspect(pos);
        } else {

            ChiseledBookshelfVisualizerClient.getBookshelfState().requestSent = false; // Just for servers that don't have the latest version of mod

            if (!ChiseledBookshelfVisualizerClient.getBookshelfState().isCurrentBookDataToggled) return;
            resetBookShelfData();
        }
    }


    private void lecternInspect(BlockPos pos) {
        //Checks if there is saved data.
        final BookInfo currentBookInfo = ChiseledBookshelfVisualizerClient.getCurrentBookInfo();

        if (currentBookInfo.pos != null && currentBookInfo.pos.equals(pos)) return;

        if (!ChiseledBookshelfVisualizerClient.getBookshelfState().requestSent) {
            ChiseledBookshelfVisualizerClient.getBookshelfState().requestSent = true;
            ClientPlayNetworking.send(new LecternInventoryRequestPacket(pos));
        }
    }


    private void bookShelfInspect(BlockPos pos, BlockHitResult blockHitResult, MinecraftClient client) {
        final BlockState blockState = client.player.getWorld().getBlockState(pos);

        //Gets index position for a book in the bookshelf
        ChiseledBookshelfBlock bookshelfBlock = (ChiseledBookshelfBlock) blockState.getBlock();
        OptionalInt optionalInt = ((BookshelfBlockAccessor) bookshelfBlock).invokerGetSlotForHitPos(blockHitResult, blockState);

        //if the position is empty, return
        if (optionalInt.isEmpty()) {
            resetBookShelfData();
            return;
        }

        //Checks if there is saved data.
        final BookInfo currentBookInfo = ChiseledBookshelfVisualizerClient.getCurrentBookInfo();

        //Changes the id for the new one if it's new.
        final int temp = ChiseledBookshelfVisualizerClient.getBookshelfState().currentSlotInt;
        final int slotNum = optionalInt.getAsInt();
        ChiseledBookshelfVisualizerClient.getBookshelfState().currentSlotInt = slotNum;

        if (currentBookInfo.slotId != slotNum && currentBookInfo.slotId != -2 && !ChiseledBookshelfVisualizerClient.getBookshelfState().requestSent) {
            ChiseledBookshelfVisualizerClient.getBookshelfState().requestSent = true;
            ClientPlayNetworking.send(new BookInventoryRequestPacket(pos, slotNum));
        } else {
            if (temp == slotNum)
                ChiseledBookshelfVisualizerClient.getBookshelfState().isCurrentBookDataToggled = currentBookInfo.slotId != -2;
            else {
                ChiseledBookshelfVisualizerClient.getBookshelfState().isCurrentBookDataToggled = false;
                ChiseledBookshelfVisualizerClient.setCurrentBookInfo(BookInfo.empty());
            }
        }
    }

    private void resetBookShelfData() {
        if (!ChiseledBookshelfVisualizerClient.getBookshelfState().isCurrentBookDataToggled) return;

        ChiseledBookshelfVisualizerClient.getBookshelfState().isCurrentBookDataToggled = false;
        ChiseledBookshelfVisualizerClient.setCurrentBookInfo(BookInfo.empty());
    }
}