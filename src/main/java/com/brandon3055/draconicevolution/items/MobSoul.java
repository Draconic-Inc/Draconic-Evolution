package com.brandon3055.draconicevolution.items;

import codechicken.lib.model.ModelRegistryHelper;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.item.RenderItemMobSoul;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class MobSoul extends ItemBCore implements ICustomRender {

    private static Map<String, Entity> renderEntityMap = new HashMap<>();

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ModelRegistryHelper.registerItemRenderer(this, new RenderItemMobSoul());
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return false;
    }

    public String getEntityString(ItemStack stack) {
        return ItemNBTHelper.getString(stack, "EntityName", "Pig");
    }

    public void setEntityString(String entityString, ItemStack stack) {
        ItemNBTHelper.setString(stack, "EntityName", "entityString");
    }

    @Nullable
    public NBTTagCompound getEntityData(ItemStack stack) {
        NBTTagCompound compound = ItemNBTHelper.getCompound(stack);
        if (compound.hasKey("EntityData")) {
            return compound.getCompoundTag("EntityData");
        }
        return null;
    }

    public void setEntityData(NBTTagCompound compound, ItemStack stack) {
        compound.removeTag("UUID");
        compound.removeTag("Motion");
        ItemNBTHelper.getCompound(stack).setTag("EntityData", compound);
    }

    public Entity createEntity(World world, ItemStack stack) {
        String eName = getEntityString(stack);
        NBTTagCompound entityData = getEntityData(stack);
        Entity entity = EntityList.createEntityByName(eName, world);
        if (entity == null) {
            entity = new EntityPig(world);
        }
        else {
            if (entityData != null) {
                entity.readFromNBT(entityData);
            }
        }
        return entity;
    }

    @SideOnly(Side.CLIENT)
    public Entity getRenderEntity(ItemStack stack) {
        String eName = getEntityString(stack);

        if (eName.equals("[Random-Display]")) {
            eName = EntityList.getEntityNameList().get((ClientEventHandler.elapsedTicks / 20) % EntityList.getEntityNameList().size());
        }

        if (!renderEntityMap.containsKey(eName)) {
            World world = Minecraft.getMinecraft().theWorld;
            Entity entity = EntityList.createEntityByName(eName, world);
            if (entity == null) {
                entity = new EntityPig(world);
            }
            renderEntityMap.put(eName, entity);
        }
        return renderEntityMap.get(eName);
    }
}
