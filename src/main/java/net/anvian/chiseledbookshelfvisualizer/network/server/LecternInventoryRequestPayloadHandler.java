package net.anvian.chiseledbookshelfvisualizer.network.server;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizer;
import net.anvian.chiseledbookshelfvisualizer.network.packets.BookShelfInventoryPayload;
import net.anvian.chiseledbookshelfvisualizer.network.packets.LecternInventoryRequestPayload;
import net.anvian.chiseledbookshelfvisualizer.util.LecternTools;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class LecternInventoryRequestPayloadHandler implements ServerPlayNetworking.PlayPayloadHandler<LecternInventoryRequestPayload> {
    @Override
    public void receive(LecternInventoryRequestPayload lecternInventoryRequestPayload, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if (ChiseledBookshelfVisualizer.serverInstance == null) return;

            ItemStack stack = LecternTools.getItemStack(lecternInventoryRequestPayload.pos(), context.player());

            if (stack == null) {
                ServerPlayNetworking.send(context.player(), new BookShelfInventoryPayload(Items.AIR.getDefaultStack(), lecternInventoryRequestPayload.pos(), 0));
                return;
            }
            ServerPlayNetworking.send(context.player(), new BookShelfInventoryPayload(stack, lecternInventoryRequestPayload.pos(), 0));
        });
    }
}