package com.brandon3055.draconicevolution.handlers;

import cofh.redstoneflux.api.IEnergyContainerItem;
import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.itemconfig.ToolConfigHelper;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.entity.EntityCustomArrow;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import com.brandon3055.draconicevolution.items.tools.WyvernBow;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Random;

public class BowHandler {

    public static ActionResult<ItemStack> onBowRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        BowHandler.BowProperties properties = new BowHandler.BowProperties(stack, player);
        if (properties.canFire()) {
            ActionResult<ItemStack> ret = ForgeEventFactory.onArrowNock(stack, world, player, hand, true);
            if (ret != null) {
                return ret;
            }

            player.setActiveHand(hand);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    public static void onBowUsingTick(ItemStack stack, EntityPlayer player, int count) {
        BowHandler.BowProperties properties = new BowHandler.BowProperties(stack, player);
        int j = 72000 - count;
        if (properties.autoFire && j >= properties.getDrawTicks()) player.stopActiveHand();
    }

    public static void onPlayerStoppedUsingBow(ItemStack stack, World world, EntityPlayer player, int timeLeft) {
        BowHandler.BowProperties properties = new BowHandler.BowProperties(stack, player);
        if (!properties.canFire() || !(stack.getItem() instanceof IEnergyContainerItem)) {
            return;
        }

        int charge = stack.getMaxItemUseDuration() - timeLeft;
        charge = ForgeEventFactory.onArrowLoose(stack, world, player, charge, true);

        if (charge < 0) {
            return;
        }

        float drawArrowSpeedModifier = Math.min((float) charge / (float) properties.getDrawTicks(), 1F);

        if (drawArrowSpeedModifier < 0.1) {
            return;
        }

        float velocity = properties.arrowSpeed * drawArrowSpeedModifier * 3F; //2F is the speed of a vanilla arrow

        EntityCustomArrow customArrow = new EntityCustomArrow(properties, world, player);
        customArrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, velocity, 1.0F);
        customArrow.bowProperties = properties;

        if (drawArrowSpeedModifier == 1.0F) {
            customArrow.setIsCritical(true);
        }

        if (properties.consumeArrowAndEnergy()) {
            customArrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
        }
        else {
            customArrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
        }

        if (!world.isRemote) {
            world.spawnEntity(customArrow);
        }

        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, (1.0F / (world.rand.nextFloat() * 0.4F + 1.2F) + (drawArrowSpeedModifier + (velocity / 40F)) * 0.5F));
    }

    public static void enderShot(ItemStack stack, World world, EntityPlayer player, int count, Random itemRand, float pullSpeedModifier, float speedModifier, float soundPitchModifier, int minRelease) {
//        int j = 72000 - count;
//        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, j);
//        MinecraftForge.EVENT_BUS.post(event);
//        if (event.isCanceled()) {
//            return;
//        }
//        j = event.charge;
//
//        if (player.inventory.hasItem(ModItems.enderArrow)) {
//            float f = j / pullSpeedModifier;
//            f = (f * f + f * 2.0F) / 3.0F;
//
//            if ((j < minRelease) || f < 0.1D) return;
//
//            if (f > 1.0F) f = 1.0F;
//
//            f *= speedModifier;
//
//            EntityEnderArrow entityArrow = new EntityEnderArrow(world, player, f * 2.0F);
//
//
//            stack.damageItem(1, player);                                                                            //
//            world.playSoundAtEntity(player, "random.bow", 1.0F, soundPitchModifier * (1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.3F));
//
//            if (player.inventory.hasItem(ModItems.enderArrow))
//                player.inventory.consumeInventoryItem(ModItems.enderArrow);
//
//            if (!world.isRemote) {
//                world.spawnEntityInWorld(entityArrow);
//                player.mountEntity(entityArrow);
//            }
//
//        }
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
            this.bow = new ItemStack(DEFeatures.wyvernBow);
            this.player = null;
        }

        public BowProperties(ItemStack bow, EntityPlayer player) {
            this.bow = bow;
            this.player = player;
            updateValues();
        }

        public int calculateEnergyCost() {
            updateValues();
            double rfCost = 80;//(bow.getItem() instanceof IEnergyContainerWeaponItem) ? ((IEnergyContainerWeaponItem) bow.getItem()).getEnergyPerAttack() : 80;

            rfCost *= 1 + arrowDamage;
            rfCost *= (1 + arrowSpeed) * (1 + arrowSpeed) * (1 + arrowSpeed);
            rfCost *= 1 + explosionPower * 20;
            rfCost *= 1 + shockWavePower * 10;
            if (energyBolt) {
                rfCost *= 30;
            }

            return (int) rfCost;
        }

        public boolean canFire() {
            updateValues();

            ItemStack ammo = new ItemStack(Items.ARROW);

            if (player == null) {
                return false;
            }
            if (!(bow.getItem() instanceof IEnergyContainerItem)) {
                cantFireMessage = "[Error] This bow is not a valid energy container (This is a bug, Please report on the Draconic Evolution github)";
                return false;
            }
            else if (!energyBolt && shockWavePower > 0) {
                cantFireMessage = "msg.de.shockWaveForEnergyBoltsOnly.txt";
                return false;
            }
            else if (energyBolt && explosionPower > 0) {
                cantFireMessage = "msg.de.explosiveNotForEnergyBolts.txt";
                return false;
            }
            else if (calculateEnergyCost() > ((IEnergyContainerItem) bow.getItem()).getEnergyStored(bow) && !player.capabilities.isCreativeMode) {
                cantFireMessage = "msg.de.insufficientPowerToFire.txt";
                return false;
            }
            else if (!energyBolt && !player.inventory.hasItemStack(ammo) && EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByLocation("infinity"), bow) == 0 && !player.capabilities.isCreativeMode) {
                cantFireMessage = "msg.de.outOfArrows.txt";
                return false;
            }


            cantFireMessage = null;
            return true;
        }

        private void updateValues() {
            arrowDamage = (float) ToolConfigHelper.getDoubleField("bowArrowDamage", bow);// IConfigurableItem.ProfileHelper.getFloat(bow, "BowArrowDamage", IUpgradableItem.EnumUpgrade.ARROW_DAMAGE.getUpgradePoints(bow));
            arrowSpeed = 1F + (float) ToolConfigHelper.getIntegerField("bowArrowSpeedModifier", bow) / 100F;// IConfigurableItem.ProfileHelper.getFloat(bow, "BowArrowSpeedModifier", 0F);
            explosionPower = (float) ToolConfigHelper.getDoubleField("bowExplosionPower", bow);// IConfigurableItem.ProfileHelper.getFloat(bow, "BowExplosionPower", 0F);
            shockWavePower = (float) ToolConfigHelper.getDoubleField("bowShockPower", bow);// IConfigurableItem.ProfileHelper.getFloat(bow, "BowShockWavePower", 0F);
            drawTimeReduction = UpgradeHelper.getUpgradeLevel(bow, ToolUpgrade.DRAW_SPEED);// IUpgradableItemd.EnumUpgrade.DRAW_SPEED.getUpgradePoints(bow);
            zoomModifier = (float) ToolConfigHelper.getIntegerField("bowZoomModifier", bow) / 100F;// IConfigurableItem.ProfileHelper.getFloat(bow, "BowZoomModifier", 0F);
            autoFire = ToolConfigHelper.getBooleanField("bowAutoFire", bow);// IConfigurableItem.ProfileHelper.getBoolean(bow, "BowAutoFire", false);
            energyBolt = ToolConfigHelper.getBooleanField("bowFireArrow", bow);// IConfigurableItem.ProfileHelper.getBoolean(bow, "BowEnergyBolt", false);
//            arrowDamage = IConfigurableItem.ProfileHelper.getFloat(bow, "BowArrowDamage", IUpgradableItem.EnumUpgrade.ARROW_DAMAGE.getUpgradePoints(bow));
//            arrowSpeed = 1F + IConfigurableItem.ProfileHelper.getFloat(bow, "BowArrowSpeedModifier", 0F);
//            explosionPower = IConfigurableItem.ProfileHelper.getFloat(bow, "BowExplosionPower", 0F);
//            shockWavePower = IConfigurableItem.ProfileHelper.getFloat(bow, "BowShockWavePower", 0F);
//            drawTimeReduction = IUpgradableItem.EnumUpgrade.DRAW_SPEED.getUpgradePoints(bow);
//            zoomModifier = IConfigurableItem.ProfileHelper.getFloat(bow, "BowZoomModifier", 0F);
//            autoFire = IConfigurableItem.ProfileHelper.getBoolean(bow, "BowAutoFire", false);
//            energyBolt = IConfigurableItem.ProfileHelper.getBoolean(bow, "BowEnergyBolt", false);
        }

        public int getDrawTicks() {
            int reduction = Math.min(drawTimeReduction, 4);
            double d = reduction * reduction * reduction * 0.25;
            return (int) (20D / (1 + d));
        }

        /**
         * Consumes energy for the shot and also consumes an arrow if the bow dose not have infinity
         * Returns true if an arrow was consumed.
         */
        public boolean consumeArrowAndEnergy() {

            if (!player.capabilities.isCreativeMode) {
                ((WyvernBow) bow.getItem()).modifyEnergy(bow, -calculateEnergyCost());
            }

            if (!energyBolt && EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByLocation("infinity"), bow) == 0 && !player.capabilities.isCreativeMode) {
                InventoryUtils.consumeStack(new ItemStack(Items.ARROW), player.inventory);
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
