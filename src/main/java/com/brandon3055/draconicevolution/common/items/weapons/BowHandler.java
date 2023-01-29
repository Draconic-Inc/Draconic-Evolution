package com.brandon3055.draconicevolution.common.items.weapons;

import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

import cofh.api.energy.IEnergyContainerItem;

import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityCustomArrow;
import com.brandon3055.draconicevolution.common.entity.EntityEnderArrow;
import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem;

public class BowHandler {

    public static ItemStack onBowRightClick(Item bow, ItemStack stack, World world, EntityPlayer player) {
        BowHandler.BowProperties properties = new BowHandler.BowProperties(stack, player);
        if (properties.canFire()) {
            ArrowNockEvent event = new ArrowNockEvent(player, stack);
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                return event.result;
            }

            player.setItemInUse(stack, bow.getMaxItemUseDuration(stack));
        }

        return stack;
    }

    public static void onBowUsingTick(ItemStack stack, EntityPlayer player, int count) {
        BowHandler.BowProperties properties = new BowHandler.BowProperties(stack, player);
        int j = 72000 - count;
        if (properties.autoFire && j >= properties.getDrawTicks()) player.stopUsingItem();
    }

    public static void onPlayerStoppedUsingBow(ItemStack stack, World world, EntityPlayer player, int count) {
        BowHandler.BowProperties properties = new BowHandler.BowProperties(stack, player);
        if (!properties.canFire() || !(stack.getItem() instanceof IEnergyContainerItem)) return;

        int j = 72000 - count;
        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, j);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            return;
        }

        j = event.charge;

        float drawArrowSpeedModifier = Math.min((float) j / (float) properties.getDrawTicks(), 1F);

        if (drawArrowSpeedModifier < 0.1) {
            return;
        }

        float velocity = properties.arrowSpeed * drawArrowSpeedModifier * 2F; // 2F is the speed of a vanilla arrow

        EntityCustomArrow customArrow = new EntityCustomArrow(world, player, velocity);
        customArrow.bowProperties = properties;

        if (drawArrowSpeedModifier == 1.0F) {
            customArrow.setIsCritical(true);
        }

        if (properties.consumeArrowAndEnergy()) {
            customArrow.canBePickedUp = 1;
        } else {
            customArrow.canBePickedUp = 2;
        }

        if (!world.isRemote) world.spawnEntityInWorld(customArrow);

        world.playSoundAtEntity(
                player,
                "random.bow",
                1.0F,
                (1.0F / (world.rand.nextFloat() * 0.4F + 1.2F) + (drawArrowSpeedModifier + (velocity / 40F)) * 0.5F));
    }

    public static void enderShot(ItemStack stack, World world, EntityPlayer player, int count, Random itemRand,
            float pullSpeedModifier, float speedModifier, float soundPitchModifier, int minRelease) {
        int j = 72000 - count;
        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, j);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return;
        }
        j = event.charge;

        if (player.inventory.hasItem(ModItems.enderArrow)) {
            float f = j / pullSpeedModifier;
            f = (f * f + f * 2.0F) / 3.0F;

            if ((j < minRelease) || f < 0.1D) return;

            if (f > 1.0F) f = 1.0F;

            f *= speedModifier;

            EntityEnderArrow entityArrow = new EntityEnderArrow(world, player, f * 2.0F);

            stack.damageItem(1, player); //
            world.playSoundAtEntity(
                    player,
                    "random.bow",
                    1.0F,
                    soundPitchModifier * (1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.3F));

            if (player.inventory.hasItem(ModItems.enderArrow))
                player.inventory.consumeInventoryItem(ModItems.enderArrow);

            if (!world.isRemote) {
                world.spawnEntityInWorld(entityArrow);
                player.mountEntity(entityArrow);
            }
        }
    }

    public static class BowProperties {

        public ItemStack bow;
        public EntityPlayer player;

        public float arrowDamage = 0F;
        public float arrowSpeed = 0F;
        public float explosionPower = 0F;
        public float shockWavePower = 0F;
        public float zoomModifier = 0F;
        private int drawTimeReduction = 0;
        public boolean autoFire = false;
        public boolean energyBolt = false;

        public String cantFireMessage = null;

        public BowProperties() {
            this.bow = new ItemStack(ModItems.wyvernBow);
            this.player = null;
        }

        public BowProperties(ItemStack bow, EntityPlayer player) {
            this.bow = bow;
            this.player = player;
            updateValues();
        }

        public int calculateEnergyCost() {
            updateValues();
            double rfCost = (bow.getItem() instanceof IEnergyContainerWeaponItem)
                    ? ((IEnergyContainerWeaponItem) bow.getItem()).getEnergyPerAttack()
                    : 80;

            rfCost *= 1 + arrowDamage;
            rfCost *= (1 + arrowSpeed) * (1 + arrowSpeed) * (1 + arrowSpeed);
            rfCost *= 1 + explosionPower * 20;
            rfCost *= 1 + shockWavePower * 10;
            if (energyBolt) rfCost *= BalanceConfigHandler.draconicFireEnergyCostMultiptier;

            return (int) rfCost;
        }

        public boolean canFire() {
            updateValues();

            if (player == null) return false;
            if (!(bow.getItem() instanceof IEnergyContainerWeaponItem)) {
                cantFireMessage = "[Error] This bow is not a valid energy container (This is a bug, Please report on the Draconic Evolution github)";
                return false;
            } else if (!energyBolt && shockWavePower > 0) {
                cantFireMessage = "msg.de.shockWaveForEnergyBoltsOnly.txt";
                return false;
            } else if (energyBolt && explosionPower > 0) {
                cantFireMessage = "msg.de.explosiveNotForEnergyBolts.txt";
                return false;
            } else if (calculateEnergyCost() > ((IEnergyContainerWeaponItem) bow.getItem()).getEnergyStored(bow)
                    && !player.capabilities.isCreativeMode) {
                        cantFireMessage = "msg.de.insufficientPowerToFire.txt";
                        return false;
                    } else
                if (!energyBolt && !player.inventory.hasItem(Items.arrow)
                        && EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, bow) == 0
                        && !player.capabilities.isCreativeMode) {
                            cantFireMessage = "msg.de.outOfArrows.txt";
                            return false;
                        }

            cantFireMessage = null;
            return true;
        }

        private void updateValues() {
            arrowDamage = IConfigurableItem.ProfileHelper
                    .getFloat(bow, "BowArrowDamage", IUpgradableItem.EnumUpgrade.ARROW_DAMAGE.getUpgradePoints(bow));
            arrowSpeed = 1F + IConfigurableItem.ProfileHelper.getFloat(bow, "BowArrowSpeedModifier", 0F);
            explosionPower = IConfigurableItem.ProfileHelper.getFloat(bow, "BowExplosionPower", 0F);
            shockWavePower = IConfigurableItem.ProfileHelper.getFloat(bow, "BowShockWavePower", 0F);
            drawTimeReduction = IUpgradableItem.EnumUpgrade.DRAW_SPEED.getUpgradePoints(bow);
            zoomModifier = IConfigurableItem.ProfileHelper.getFloat(bow, "BowZoomModifier", 0F);
            autoFire = IConfigurableItem.ProfileHelper.getBoolean(bow, "BowAutoFire", false);
            energyBolt = IConfigurableItem.ProfileHelper.getBoolean(bow, "BowEnergyBolt", false);
        }

        public int getDrawTicks() {
            return Math.max(62 - (drawTimeReduction * 10), 1);
        }

        /**
         * Consumes energy for the shot and also consumes an arrow if the bow dose not have infinity Returns true if an
         * arrow was consumed.
         */
        public boolean consumeArrowAndEnergy() {

            if (!player.capabilities.isCreativeMode)
                ((IEnergyContainerWeaponItem) bow.getItem()).extractEnergy(bow, calculateEnergyCost(), false);

            if (!energyBolt && EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, bow) == 0
                    && !player.capabilities.isCreativeMode) {
                player.inventory.consumeInventoryItem(Items.arrow);
                return true;
            }

            return false;
        }

        public void writeToNBT(NBTTagCompound compound) {
            compound.setFloat("ArrowDamage", arrowDamage);
            compound.setFloat("ArrowExplosive", explosionPower);
            compound.setFloat("ArrowShock", shockWavePower);
            compound.setBoolean("ArrowEnergy", energyBolt);
        }

        public void readFromNBT(NBTTagCompound compound) {
            arrowDamage = compound.getFloat("ArrowDamage");
            explosionPower = compound.getFloat("ArrowExplosive");
            shockWavePower = compound.getFloat("ArrowShock");
            energyBolt = compound.getBoolean("ArrowEnergy");
        }
    }
}
