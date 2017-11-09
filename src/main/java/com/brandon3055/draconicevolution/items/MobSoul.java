package com.brandon3055.draconicevolution.items;

import codechicken.lib.model.ModelRegistryHelper;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.item.RenderItemMobSoul;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class MobSoul extends ItemBCore implements ICustomRender {

    private static Map<String, Entity> renderEntityMap = new HashMap<>();
    private static Map<String, String> entityNameCache = new HashMap<>();
    public static List<String> randomDisplayList = null;

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {

            Entity entity = createEntity(world, stack);
            double sX = pos.getX() + facing.getFrontOffsetX() + 0.5;
            double sY = pos.getY() + facing.getFrontOffsetY() + 0.5;
            double sZ = pos.getZ() + facing.getFrontOffsetZ() + 0.5;
            if (entity == null) {
                LogHelper.error("Mob Soul bound entity = null");
                return super.onItemUse(stack, player, world, pos, hand, facing, hitX, hitY, hitZ);
            }
            entity.setLocationAndAngles(sX, sY, sZ, player.rotationYaw, 0F);

            if (!world.isRemote) {
//                NBTTagCompound compound = ItemNBTHelper.getCompound(stack);
//                if (!compound.hasKey("EntityData") && entity instanceof EntityLivingBase) {
//                    ((EntityLiving) entity).onInitialSpawn(world.getDifficultyForLocation(pos) ,null);
//                }
                world.spawnEntityInWorld(entity);
                if (!player.capabilities.isCreativeMode) {
                    InventoryUtils.consumeHeldItem(player, stack, hand);
                }
            }
        }

        return super.onItemUse(stack, player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String eString = getEntityString(stack);

        if (eString.equals("[Random-Display]")){
            return "Mob " + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name");
        }

        String eModifier = "";
        if (ItemNBTHelper.verifyExistance(stack, "ZombieVillagerType")) {
            eModifier = "-" + ItemNBTHelper.getString(stack, "ZombieVillagerType", "minecraft:farmer");
        }
        else if (ItemNBTHelper.verifyExistance(stack, "ZombieTypeHusk")) {
            eModifier = "-Husk";
        }

        String localizedName = entityNameCache.computeIfAbsent(eString + eModifier, s -> {
            Entity entity = EntityList.createEntityByName(eString, null);
            if (entity == null) {
                entity = new EntityPig(null);
            }

            loadAdditionalEntityInfo(stack, entity);
            return entity.getName();
        });

        return localizedName + " " + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name");
    }

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
        ItemNBTHelper.setString(stack, "EntityName", entityString);
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
        try {
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
                else {
                    loadAdditionalEntityInfo(stack, entity);
                    if (entity instanceof EntityLiving)
                    {
                        if (!ForgeEventFactory.doSpecialSpawn((EntityLiving) entity, world, (float)entity.posX, (float)entity.posY, (float)entity.posZ)) {
                            ((EntityLiving)entity).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData)null);
                        }
                    }
                }
            }
            entity.addTag("IsSpawnerMob");
            return entity;
        }
        catch (Throwable e) {
            return new EntityPig(world);
        }
    }

    public ItemStack getSoulFromEntity(Entity entity, boolean saveEntityData) {
        ItemStack soul = new ItemStack(DEFeatures.mobSoul);

        String registryName = EntityList.getEntityString(entity);
        ItemNBTHelper.setString(soul, "EntityName", registryName);

        if (saveEntityData) {
            NBTTagCompound compound = new NBTTagCompound();
            entity.writeToNBT(compound);
            setEntityData(compound, soul);
        }
        else {
            saveAditionalEntityInfo(entity, soul);
        }

        return soul;
    }

    @SideOnly(Side.CLIENT)
    public Entity getRenderEntity(ItemStack stack) {
        String eName = getEntityString(stack);
        String eModifier = "";
        if (ItemNBTHelper.verifyExistance(stack, "ZombieVillagerType")) {
            eModifier = "-" + ItemNBTHelper.getString(stack, "ZombieVillagerType", "minecraft:farmer");
        }
        else if (ItemNBTHelper.verifyExistance(stack, "ZombieTypeHusk")) {
            eModifier = "-Husk";
        }

        if (eName.equals("[Random-Display]")) {
            if (randomDisplayList == null) {
                randomDisplayList = new ArrayList<>();
                for (String rName : EntityList.getEntityNameList()) {
                    Class<? extends Entity> clazz = EntityList.NAME_TO_CLASS.get(rName);
                    if (clazz != null && IMob.class.isAssignableFrom(clazz)) {
                        randomDisplayList.add(rName);
                    }
                }
            }

            if (randomDisplayList.size() > 0) {
                eName = randomDisplayList.get((ClientEventHandler.elapsedTicks / 20) % randomDisplayList.size());
            }
        }

        String finalEName = eName;

        if (!renderEntityMap.containsKey(eName + eModifier)) {
            World world = Minecraft.getMinecraft().theWorld;
            Entity entity;
            try {
                entity = EntityList.createEntityByName(finalEName, world);

                if (entity == null) {
                    entity = new EntityPig(world);
                }
                else {
                    loadAdditionalEntityInfo(stack, entity);
                }
            }
            catch (Throwable e) {
                entity = new EntityPig(world);
            }
            renderEntityMap.put(eName + eModifier, entity);
        }

        return renderEntityMap.get(eName + eModifier);
    }

    private void saveAditionalEntityInfo(Entity entity, ItemStack stack) {
        if (entity instanceof EntitySkeleton) {
            ItemNBTHelper.setInteger(stack, "EntityTypeID", ((EntitySkeleton) entity).getSkeletonType().getId());
        }
        else if (entity instanceof EntityZombie) {
            VillagerRegistry.VillagerProfession prof = ((EntityZombie) entity).getVillagerTypeForge();
            if (prof != null && prof.getRegistryName() != null) {
                ItemNBTHelper.setString(stack, "ZombieVillagerType", prof.getRegistryName().toString());
            }
            else if (((EntityZombie) entity).getZombieType() == ZombieType.HUSK) {
                ItemNBTHelper.setBoolean(stack, "ZombieTypeHusk", true);
            }
        }
    }

    private void loadAdditionalEntityInfo(ItemStack stack, Entity entity) {
        if (entity instanceof EntitySkeleton && ItemNBTHelper.verifyExistance(stack, "EntityTypeID")) {
            ((EntitySkeleton) entity).setSkeletonType(SkeletonType.getByOrdinal(ItemNBTHelper.getInteger(stack, "EntityTypeID", ((EntitySkeleton) entity).getSkeletonType().getId())));
        }
        else if (entity instanceof EntityZombie) {
            if (ItemNBTHelper.verifyExistance(stack, "ZombieVillagerType")) {
                String name = ItemNBTHelper.getString(stack, "ZombieVillagerType", "minecraft:farmer");
                VillagerRegistry.VillagerProfession p = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(name));
                if (p == null) {
                    p = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft:farmer"));
                }
                ((EntityZombie) entity).setVillagerType(p);
            }
            else if (ItemNBTHelper.verifyExistance(stack, "ZombieTypeHusk")) {
                ((EntityZombie) entity).setZombieType(ZombieType.HUSK);
            }
        }
    }

}
