//TODO, Figure out if this is still needed. Its probably not.
//package com.brandon3055.draconicevolution.mixin;
//
//import com.brandon3055.draconicevolution.items.equipment.IModularItem;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
//import net.minecraft.world.item.ItemStack;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
///**
// * Created by brandon3055 on 15/11/2022
// */
//@Mixin(ServerboundSetCreativeModeSlotPacket.class)
//public class ServerboundSetCreativeModeSlotPacketMixin {
//
//    @Final
//    @Shadow
//    private int slotNum;
//
//    @Final
//    @Shadow
//    private ItemStack itemStack;
//
//    @Inject(
//            method = "write(Lnet/minecraft/network/FriendlyByteBuf;)V",
//            at = @At("HEAD"),
//            cancellable = true
//    )
//    public void write(FriendlyByteBuf byteBuf, CallbackInfo ci) {
//        if (!itemStack.isEmpty() && itemStack.getItem() instanceof IModularItem) {
//            byteBuf.writeShort(this.slotNum);
//            byteBuf.writeItem(this.itemStack);
//            ci.cancel();
//        }
//    }
//}
