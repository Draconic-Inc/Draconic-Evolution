package com.brandon3055.draconicevolution.common.items.tools;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.items.ItemDE;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 25/09/2014.
 */
public class AdminSpawnEgg extends ItemDE {

    public AdminSpawnEgg() {
        this.setUnlocalizedName(Strings.adminSpawnEggName);
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        ModItems.register(this);
    }

    public String getItemStackDisplayName(ItemStack stack) {
        NBTTagCompound compound = (NBTTagCompound) ItemNBTHelper.getCompound(stack).copy();
        if (compound == null) return null;
        String name = compound.getString("EntityName");
        return "Spawn " + name;
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "admin_egg");
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity) {
        ItemStack stack1 = player.getHeldItem();
        if (stack1 == null) return false;
        if (ItemNBTHelper.getBoolean(stack1, "IsSet", false)) return false;
        NBTTagCompound compound = ItemNBTHelper.getCompound(stack1);
        entity.writeToNBT(compound);
        compound.setString("EntityName", EntityList.getEntityString(entity));
        stack1.setTagCompound(compound);
        ItemNBTHelper.setBoolean(stack1, "IsSet", true);
        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        ItemStack stack1 = player.getHeldItem();
        if (stack1 == null) return true;
        if (ItemNBTHelper.getBoolean(stack1, "IsSet", false)) return true;
        NBTTagCompound compound = ItemNBTHelper.getCompound(stack1);
        entity.writeToNBT(compound);
        if (EntityList.getEntityString(entity) != null)
            compound.setString("EntityName", EntityList.getEntityString(entity));
        stack1.setTagCompound(compound);
        ItemNBTHelper.setBoolean(stack1, "IsSet", true);
        entity.setDead();
        return true;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float p_77648_8_, float p_77648_9_, float p_77648_10_) {
        NBTTagCompound compound = (NBTTagCompound) ItemNBTHelper.getCompound(stack).copy();

        if (compound == null) return false;

        String name = compound.getString("EntityName");

        Entity entity = EntityList.createEntityByName(name, world);
        compound.removeTag("EntityName");
        compound.removeTag("IsSet");
        if (entity instanceof EntityLivingBase) ((EntityLivingBase) entity).readFromNBT(compound);
        double sX = x + ForgeDirection.getOrientation(side).offsetX + 0.5;
        double sY = y + ForgeDirection.getOrientation(side).offsetY + 0.5;
        double sZ = z + ForgeDirection.getOrientation(side).offsetZ + 0.5;
        if (entity == null) {
            LogHelper.error("bound entity = null");
            return false;
        }
        entity.setLocationAndAngles(sX, sY, sZ, player.rotationYaw, 0F);

        if (entity instanceof EntityLivingBase && !world.isRemote) {
            world.spawnEntityInWorld(entity);
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        if (ItemNBTHelper.getBoolean(stack, "IsSet", false))
            list.add(ItemNBTHelper.getString(stack, "EntityName", "null"));
        else list.add("Unset");
    }
}
