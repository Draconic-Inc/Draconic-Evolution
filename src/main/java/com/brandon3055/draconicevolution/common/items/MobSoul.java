package com.brandon3055.draconicevolution.common.items;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Brandon on 7/07/2014.
 */
public class MobSoul extends ItemDE {
    public MobSoul() {
        this.setUnlocalizedName(Strings.MobSoulName);
        ModItems.register(this);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        String name = ItemNBTHelper.getString(stack, "Name", "Pig");
        list.add("" + EnumChatFormatting.WHITE + StatCollector.translateToLocal("info.mobSoul1.txt"));
        list.add("" + EnumChatFormatting.WHITE + StatCollector.translateToLocal("info.mobSoul2.txt"));
        list.add("" + EnumChatFormatting.WHITE + StatCollector.translateToLocal("info.mobSoul3.txt"));
        list.add("" + EnumChatFormatting.DARK_PURPLE + name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {}

    @Override
    public boolean onItemUse(
            ItemStack stack,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z,
            int side,
            float p_77648_8_,
            float p_77648_9_,
            float p_77648_10_) {
        if (!player.isSneaking()) return false;
        String name = ItemNBTHelper.getString(stack, "Name", "Pig");
        Entity entity = EntityList.createEntityByName(name, world);
        double sX = x + ForgeDirection.getOrientation(side).offsetX + 0.5;
        double sY = y + ForgeDirection.getOrientation(side).offsetY + 0.5;
        double sZ = z + ForgeDirection.getOrientation(side).offsetZ + 0.5;
        if (entity == null) {
            LogHelper.error("Mob Soul bound entity = null");
            return false;
        }
        entity.setLocationAndAngles(sX, sY, sZ, player.rotationYaw, 0F);

        if (entity instanceof EntityLivingBase && !world.isRemote) {
            ((EntityLiving) entity).onSpawnWithEgg((IEntityLivingData) null);
            world.spawnEntityInWorld(entity);
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return true;
    }
}
