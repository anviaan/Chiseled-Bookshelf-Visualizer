package net.anvian.chiseledbookshelfvisualizer.common.network.server;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerMod;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.BookInventoryPacket;
import net.anvian.chiseledbookshelfvisualizer.common.network.packets.LecternInventoryRequestPacket;
import net.anvian.chiseledbookshelfvisualizer.common.util.LecternBlockUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class LecternInventoryRequestHandler implements ServerPlayNetworking.PlayPayloadHandler<LecternInventoryRequestPacket> {
    @Override
    public void receive(LecternInventoryRequestPacket lecternInventoryRequestPacket, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if (ChiseledBookshelfVisualizerMod.getServerInstance() == null) return;

            ItemStack stack = LecternBlockUtil.getItemStack(lecternInventoryRequestPacket.pos(), context.player());

            if (stack == null) {
                ServerPlayNetworking.send(context.player(), new BookInventoryPacket(Items.AIR.getDefaultStack(), lecternInventoryRequestPacket.pos(), 0));
                return;
            }
            ServerPlayNetworking.send(context.player(), new BookInventoryPacket(stack, lecternInventoryRequestPacket.pos(), 0));
        });
    }
}