package net.anvian.chiseledbookshelfvisualizer.mixin.client;

import net.anvian.chiseledbookshelfvisualizer.client.raycast.BlockInspector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerTickMixin {

    @Shadow
    @Final
    protected MinecraftClient client;

    @Unique
    private final BlockInspector blockInspector = new BlockInspector();

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    public void injectTick(CallbackInfo ci) {
        blockInspector.inspect(client);
    }
}
