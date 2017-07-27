package com.brandon3055.draconicevolution.items;

import codechicken.lib.model.ModelRegistryHelper;
import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.registry.IRenderOverride;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by brandon3055 on 25/09/2016.
 */
public class MobSoul extends ItemBCore implements IRenderOverride {

    private static Map<String, Entity> renderEntityMap = new HashMap<>();
    private static Map<String, String> entityNameCache = new HashMap<>();
    public static List<String> randomDisplayList = null;
    private static Map<String, ResourceLocation> rlCache = new WeakHashMap<>();

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {

            Entity entity = createEntity(world, stack);
            double sX = pos.getX() + facing.getFrontOffsetX() + 0.5;
            double sY = pos.getY() + facing.getFrontOffsetY() + 0.5;
            double sZ = pos.getZ() + facing.getFrontOffsetZ() + 0.5;
            if (entity == null) {
                LogHelper.error("Mob Soul bound entity = null");
                return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
            }
            entity.setLocationAndAngles(sX, sY, sZ, player.rotationYaw, 0F);

            if (!world.isRemote) {
//                NBTTagCompound compound = ItemNBTHelper.getCompound(stack);
//                if (!compound.hasKey("EntityData") && entity instanceof EntityLivingBase) {
//                    ((EntityLiving) entity).onInitialSpawn(world.getDifficultyForLocation(pos) ,null);
//                }
                world.spawnEntity(entity);
                if (!player.capabilities.isCreativeMode) {
                    InventoryUtils.consumeHeldItem(player, stack, hand);
                }
            }
        }

        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String eString = getEntityString(stack);

        if (eString.equals("[Random-Display]")) {
            return "Mob " + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name");
        }

        String eModifier = "";
        if (ItemNBTHelper.verifyExistance(stack, "ZombieVillagerType")) {
            eModifier = "-" + ItemNBTHelper.getString(stack, "ZombieVillagerType", "minecraft:farmer");
        }
        else if (ItemNBTHelper.verifyExistance(stack, "ZombieTypeHusk")) {
            eModifier = "-Husk";
        }

        entityNameCache.clear();

        String localizedName = entityNameCache.computeIfAbsent(eString + eModifier, s -> {
            try {
                Entity entity = EntityList.createEntityByIDFromName(getCachedRegName(eString), null);
                if (entity == null) {
                    entity = new EntityPig(null);
                }

                loadAdditionalEntityInfo(stack, entity);
                return entity.getName();
            }
            catch (Throwable e) {
                return "Name-Error";
            }
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

    public void setEntity(ResourceLocation entityName, ItemStack stack) {
        ItemNBTHelper.setString(stack, "EntityName", String.valueOf(entityName));
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
            Entity entity = EntityList.createEntityByIDFromName(getCachedRegName(eName), world);
            if (entity == null) {
                entity = new EntityPig(world);
            }
            else {
                if (entityData != null) {
                    entity.readFromNBT(entityData);
                }
                else {
                    loadAdditionalEntityInfo(stack, entity);
                    if (entity instanceof EntityLiving) {
                        if (!ForgeEventFactory.doSpecialSpawn((EntityLiving) entity, world, (float) entity.posX, (float) entity.posY, (float) entity.posZ)) {
                            ((EntityLiving) entity).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
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

        String registryName = EntityList.getKey(entity) + "";
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

    public Entity getRenderEntity(ItemStack stack) {
        return getRenderEntity(getEntityString(stack));
    }

    @SideOnly(Side.CLIENT)
    public Entity getRenderEntity(String name) {
        if (name.equals("[Random-Display]")) {
            if (randomDisplayList == null) {
                randomDisplayList = new ArrayList<>();
                EntityList.ENTITY_EGGS.forEach((resourceLocation, entityEggInfo) -> randomDisplayList.add(entityEggInfo.spawnedID+""));
            }

            if (randomDisplayList.size() > 0) {
                name = randomDisplayList.get((ClientEventHandler.elapsedTicks / 20) % randomDisplayList.size());
            }
        }

        if (!renderEntityMap.containsKey(name)) {
            World world = Minecraft.getMinecraft().world;
            Entity entity;
            try {
                entity = EntityList.createEntityByIDFromName(getCachedRegName(name), world);
                if (entity == null) {
                    entity = new EntityPig(world);
                }
            }
            catch (Throwable e) {
                entity = new EntityPig(world);
            }
            renderEntityMap.put(name, entity);
        }

        return renderEntityMap.get(name);
    }

    private void saveAditionalEntityInfo(Entity entity, ItemStack stack) {
//        if (entity instanceof EntitySkeleton) {TODO Is this not needed now? Mojang? Did you do a good thing?
//            ItemNBTHelper.setInteger(stack, "EntityTypeID", ((EntitySkeleton) entity).getSkeletonType().getId());
//        }
//        else if (entity instanceof EntityZombie) {
//            VillagerRegistry.VillagerProfession prof = ((EntityZombie) entity).getVillagerTypeForge();
//            if (prof != null && prof.getRegistryName() != null) {
//                ItemNBTHelper.setString(stack, "ZombieVillagerType", prof.getRegistryName().toString());
//            }
//            else if (((EntityZombie) entity).getZombieType() == ZombieType.HUSK) {
//                ItemNBTHelper.setBoolean(stack, "ZombieTypeHusk", true);
//            }
//        }
    }

    private void loadAdditionalEntityInfo(ItemStack stack, Entity entity) {
//        if (entity instanceof EntitySkeleton && ItemNBTHelper.verifyExistance(stack, "EntityTypeID")) {
//            ((EntitySkeleton) entity).setSkeletonType(SkeletonType.getByOrdinal(ItemNBTHelper.getInteger(stack, "EntityTypeID", ((EntitySkeleton) entity).getSkeletonType().getId())));
//        }
//        else if (entity instanceof EntityZombie) {
//            if (ItemNBTHelper.verifyExistance(stack, "ZombieVillagerType")) {
//                String name = ItemNBTHelper.getString(stack, "ZombieVillagerType", "minecraft:farmer");
//                VillagerRegistry.VillagerProfession p = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(name));
//                if (p == null) {
//                    p = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft:farmer"));
//                }
//                ((EntityZombie) entity).setVillagerType(p);
//            }
//            else if (ItemNBTHelper.verifyExistance(stack, "ZombieTypeHusk")) {
//                ((EntityZombie) entity).setZombieType(ZombieType.HUSK);
//            }
//        }
    }

    public static ResourceLocation getCachedRegName(String name) {
        return rlCache.computeIfAbsent(name, ResourceLocation::new);
    }
}
