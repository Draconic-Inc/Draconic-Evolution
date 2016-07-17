package com.brandon3055.draconicevolution.items.armor;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.itemconfig.BooleanConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.IntegerConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemconfig.ToolConfigHelper;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.client.model.ModelDraconicArmor;
import com.brandon3055.draconicevolution.integration.ModHelper;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import com.brandon3055.draconicevolution.items.tools.ToolStats;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;

import static com.brandon3055.draconicevolution.api.itemconfig.IItemConfigField.EnumControlType.SLIDER;
import static net.minecraft.inventory.EntityEquipmentSlot.*;

/**
 * Created by brandon3055 on 6/06/2016.
 */
public class DraconicArmor extends WyvernArmor {

    public DraconicArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
        this.baseProtectionPoints = 512F;
        this.baseRecovery = 4F;
    }


    //region Upgrade

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
        return 3;
    }


    //endregion

    //region Config

    @Override
    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry) {
        if (armorType == HEAD){
            registry.register(stack, new BooleanConfigField("armorNV", false, "config.field.armorNV.description"));
            registry.register(stack, new BooleanConfigField("armorNVLock", false, "config.field.armorNVLock.description"));
            //TODO RE Integrate thaumcraft
        }
        if (armorType == CHEST){
            registry.register(stack, new IntegerConfigField("armorFSpeedModifier", 0, 0, 600, "config.field.armorFSpeedModifier.description", SLIDER).setPrefix("+").setExtension("%"));
            registry.register(stack, new IntegerConfigField("armorVFSpeedModifier", 0, 0, 600, "config.field.armorVFSpeedModifier.description", SLIDER).setPrefix("+").setExtension("%"));
            registry.register(stack, new BooleanConfigField("armorInertiaCancel", false, "config.field.armorInertiaCancel.description"));
        }
        if (armorType == LEGS) {
            registry.register(stack, new IntegerConfigField("armorSpeedModifier", 0, 0, 800, "config.field.armorSpeedModifier.description", SLIDER).setPrefix("+").setExtension("%"));
        }
        if (armorType == FEET){
            registry.register(stack, new IntegerConfigField("armorJumpModifier", 0, 0, 800, "config.field.armorSpeedModifier.description", SLIDER).setPrefix("+").setExtension("%"));
            registry.register(stack, new BooleanConfigField("armorHillStep", true, "config.field.armorHillStep.description"));
        }
        if (armorType == FEET || armorType == LEGS || armorType == CHEST){
            registry.register(stack, new BooleanConfigField("sprintBoost", false, "config.field.sprintBoost.description"));
        }

        return registry;
    }

    //endregion

    //region Rendering

    @SideOnly(Side.CLIENT)
    public ModelBiped model;

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {//TODO Look into that default model and the texture issue
        //return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);

        if (model == null) {
            if (armorType == HEAD) model = new ModelDraconicArmor(1F, true, false, false, false);
            else if (armorType == CHEST) model = new ModelDraconicArmor(1F, false, true, false, false);
            else if (armorType == LEGS) model = new ModelDraconicArmor(1F, false, false, true, false);
            else model = new ModelDraconicArmor(1F, false, false, false, true);
            this.model.bipedHead.showModel = (armorType == HEAD);
            this.model.bipedHeadwear.showModel = (armorType == HEAD);
            this.model.bipedBody.showModel = ((armorType == CHEST) || (armorType == LEGS));
            this.model.bipedLeftArm.showModel = (armorType == CHEST);
            this.model.bipedRightArm.showModel = (armorType == CHEST);
            this.model.bipedLeftLeg.showModel = (armorType == LEGS || armorType == FEET);
            this.model.bipedRightLeg.showModel = (armorType == LEGS || armorType == FEET);
        }


        if (entityLiving == null) {
            return model;
        }

        this.model.isSneak = entityLiving.isSneaking();
        this.model.isRiding = entityLiving.isRiding();
        this.model.isChild = entityLiving.isChild();
        //     this.model.aimedBow = false;
        //     this.model.heldItemRight = (entityLiving.getHeldItem() != null ? 1 : 0);

//                 this.model.bipedHead.showModel = (armorType == EntityEquipmentSlot.HEAD);
        this.model.bipedHeadwear.showModel = (armorType == HEAD);
        this.model.bipedBody.showModel = ((armorType == CHEST) || (armorType == LEGS));
        this.model.bipedLeftArm.showModel = (armorType == CHEST);
        this.model.bipedRightArm.showModel = (armorType == CHEST);
        this.model.bipedLeftLeg.showModel = (armorType == LEGS || armorType == FEET);
        this.model.bipedRightLeg.showModel = (armorType == LEGS || armorType == FEET);


        return model;
    }

    //endregion

    //region ICustomArmor


    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (stack == null) {
            return;
        }
        if (stack.getItem() == DEFeatures.draconicHelm) {

            if (world.isRemote) {
                return;
            }

            if (this.getEnergyStored(stack) >= 5000 && clearNegativeEffects(player)) {
                this.modifyEnergy(stack, -5000);
            }

            Potion nv = Potion.getPotionFromResourceLocation("night_vision");

            if (nv == null) {
                return;
            }

            if (ToolConfigHelper.getBooleanField("armorNV", stack) && (player.worldObj.getLightBrightness(new BlockPos((int) Math.floor(player.posX), (int) player.posY + 1, (int) Math.floor(player.posZ))) < 0.1F || ToolConfigHelper.getBooleanField("armorNVLock", stack))) {
                player.addPotionEffect(new PotionEffect(nv, 419, 0, false, false));
            }
            else if (player.isPotionActive(nv)) {
                player.removePotionEffect(nv);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public boolean clearNegativeEffects(Entity par3Entity) {
        boolean flag = false;
        if (par3Entity.ticksExisted % 20 == 0) {
            if (par3Entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) par3Entity;

                Collection<PotionEffect> potions = player.getActivePotionEffects();

                if (player.isBurning()) {
                    player.extinguish();
                }
                for (PotionEffect potion : potions) {
                    if (potion.getPotion().isBadEffect()) {
                        if (potion.getEffectName().equals("effect.digSlowDown") && ModHelper.isHoldingCleaver(player)) {
                            break;
                        }

                        player.removePotionEffect(potion.getPotion());
                        flag = true;

                        break;
                    }
                }
            }
        }
        return flag;
    }


    @Override
    public boolean hasHillStep(ItemStack stack, EntityPlayer player) {
        return ToolConfigHelper.getBooleanField("armorHillStep", stack);
    }

    @Override
    public float getFireResistance(ItemStack stack) {
        return 1F;
    }

    @Override
    public boolean[] hasFlight(ItemStack stack) {
        return new boolean[]{true, false, ToolConfigHelper.getBooleanField("armorInertiaCancel", stack)};
    }

    @Override
    public float getFlightSpeedModifier(ItemStack stack, EntityPlayer player) {
        float modifier = ToolConfigHelper.getIntegerField("armorFSpeedModifier", stack) / 100F;

        if (ToolConfigHelper.getBooleanField("sprintBoost", stack) && !BrandonsCore.proxy.isSprintKeyDown()){
            modifier /= 5F;
        }

        return modifier;
    }

    @Override
    public float getFlightVModifier(ItemStack stack, EntityPlayer player) {
        float modifier = ToolConfigHelper.getIntegerField("armorVFSpeedModifier", stack) / 100F;

        if (ToolConfigHelper.getBooleanField("sprintBoost", stack) && !BrandonsCore.proxy.isSprintKeyDown()){
            modifier /= 5F;
        }

        return modifier;
    }

    @Override
    public int getEnergyPerProtectionPoint() {
        return 1000;
    }

    //endregion

    //region Energy

    @Override
    protected int getCapacity(ItemStack stack) {
        int level = UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.RF_CAPACITY);

        if (level == 0) {
            return ToolStats.DRACONIC_BASE_CAPACITY;
        }
        else {
            return ToolStats.DRACONIC_BASE_CAPACITY * (int)Math.pow(2, level + 1);
        }
    }

    @Override
    protected int getMaxReceive(ItemStack stack){
        return 1000000;
    }

    //endregion
}
