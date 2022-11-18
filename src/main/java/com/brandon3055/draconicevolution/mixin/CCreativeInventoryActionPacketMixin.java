package com.brandon3055.draconicevolution.mixin;

import com.brandon3055.draconicevolution.items.equipment.IModularItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCreativeInventoryActionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by brandon3055 on 15/11/2022
 */
@Mixin(CCreativeInventoryActionPacket.class)
public class CCreativeInventoryActionPacketMixin {

    @Shadow
    private int slotNum;

    @Shadow
    private ItemStack itemStack;

    @Inject(
            method = "write(Lnet/minecraft/network/PacketBuffer;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void write(PacketBuffer byteBuf, CallbackInfo ci) {
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof IModularItem) {
            byteBuf.writeShort(this.slotNum);
            byteBuf.writeItemStack(this.itemStack, true);
            ci.cancel();
        }
    }
}
