package com.brandon3055.draconicevolution.common.items.tools;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;

import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.items.ItemDE;
import com.brandon3055.draconicevolution.common.lib.References;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by brandon3055 on 9/3/2016.
 */
public class Magnet extends ItemDE {

    private IIcon draconium;
    private IIcon awakened;

    public Magnet() {
        this.setUnlocalizedName("magnet");
        this.setCreativeTab(DraconicEvolution.tabBlocksItems);
        this.setMaxStackSize(1);
        ModItems.register(this);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        draconium = iconRegister.registerIcon(References.RESOURCESPREFIX + "magnetWyvern");
        awakened = iconRegister.registerIcon(References.RESOURCESPREFIX + "magnetDraconic");
    }

    @Override
    public IIcon getIconFromDamage(int dmg) {
        return dmg == 0 ? draconium : awakened;
    }

    @Override
    public boolean getHasSubtypes() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs p_150895_2_, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return super.getUnlocalizedName(itemStack) + (itemStack.getItemDamage() == 0 ? ".wyvern" : ".draconic");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return isEnabled(stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean hotbar) {
        if (!entity.isSneaking() && entity.ticksExisted % 5 == 0
                && isEnabled(stack)
                && entity instanceof EntityPlayer) {
            int range = stack.getItemDamage() == 0 ? 8 : 32;

            List<EntityItem> items = world.getEntitiesWithinAABB(
                    EntityItem.class,
                    AxisAlignedBB.getBoundingBox(
                            entity.posX,
                            entity.posY,
                            entity.posZ,
                            entity.posX,
                            entity.posY,
                            entity.posZ).expand(range, range, range));

            boolean playSound = false;

            for (EntityItem item : items) {
                if (item.getEntityItem() == null) {
                    continue;
                }

                String name = Item.itemRegistry.getNameForObject(item.getEntityItem().getItem());
                if (ConfigHandler.itemDislocatorBlacklistMap.containsKey(name)
                        && (ConfigHandler.itemDislocatorBlacklistMap.get(name) == -1
                                || ConfigHandler.itemDislocatorBlacklistMap.get(name)
                                        == item.getEntityItem().getItemDamage())) {
                    continue;
                }
                playSound = true;

                if (item.delayBeforeCanPickup > 0) {
                    item.delayBeforeCanPickup = 0;
                }
                item.motionX = 0;
                item.motionY = 0;
                item.motionZ = 0;
                item.setPosition(
                        entity.posX - 0.2 + (world.rand.nextDouble() * 0.4),
                        entity.posY - 0.6,
                        entity.posZ - 0.2 + (world.rand.nextDouble() * 0.4));
            }
            if (playSound && !ConfigHandler.itemDislocatorDisableSound) {
                world.playSoundAtEntity(
                        entity,
                        "random.orb",
                        0.1F,
                        0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 2F));
            }

            if (!world.isRemote) {
                List<EntityXPOrb> xp = world.getEntitiesWithinAABB(
                        EntityXPOrb.class,
                        AxisAlignedBB.getBoundingBox(
                                entity.posX,
                                entity.posY,
                                entity.posZ,
                                entity.posX,
                                entity.posY,
                                entity.posZ).expand(4, 4, 4));
                EntityPlayer player = (EntityPlayer) entity;
                for (EntityXPOrb orb : xp) {
                    if (orb.field_70532_c == 0 && orb.isEntityAlive()) {
                        if (MinecraftForge.EVENT_BUS.post(new PlayerPickupXpEvent(player, orb))) continue;
                        world.playSoundAtEntity(
                                player,
                                "random.orb",
                                0.1F,
                                0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F));
                        player.onItemPickup(orb, 1);
                        player.addExperience(orb.xpValue);
                        orb.setDead();
                    }
                }
            }
        }
    }

    public static boolean isEnabled(ItemStack itemStack) {
        return ItemNBTHelper.getBoolean(itemStack, "MagnetEnabled", false);
    }

    public static void toggle(ItemStack itemStack) {
        ItemNBTHelper.setBoolean(itemStack, "MagnetEnabled", !isEnabled(itemStack));
    }

    public static void setStatus(ItemStack itemStack, boolean status) {
        ItemNBTHelper.setBoolean(itemStack, "MagnetEnabled", status);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            toggle(stack);
        }
        return stack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
        list.add(StatCollector.translateToLocal("info.de.shiftRightClickToActivate.txt"));
        int range = stack.getItemDamage() == 0 ? 8 : 32;
        list.add(
                InfoHelper.HITC() + range
                        + InfoHelper.ITC()
                        + " "
                        + StatCollector.translateToLocal("info.de.blockRange.txt"));
    }
}
