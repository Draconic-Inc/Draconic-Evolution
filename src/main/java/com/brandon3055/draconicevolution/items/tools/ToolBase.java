package com.brandon3055.draconicevolution.items.tools;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.generation.IBakery;
import codechicken.lib.render.CCIconRegister;
import com.brandon3055.brandonscore.items.ItemEnergyBase;
import com.brandon3055.brandonscore.lib.Set3;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.registry.IRenderOverride;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.IHudDisplay;
import com.brandon3055.draconicevolution.api.itemconfig.IConfigurableItem;
import com.brandon3055.draconicevolution.api.itemconfig.IItemConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemconfig.ToolConfigHelper;
import com.brandon3055.draconicevolution.api.itemupgrade.IUpgradableItem;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.client.model.ToolModelBakery;
import com.brandon3055.draconicevolution.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.brandon3055.draconicevolution.items.ToolUpgrade.ATTACK_DAMAGE;

/**
 * Created by brandon3055 on 2/06/2016.
 */
public abstract class ToolBase extends ItemEnergyBase implements IRenderOverride, IUpgradableItem, IConfigurableItem, IHudDisplay, IBakeryProvider {

    private float baseAttackDamage;
    private float baseAttackSpeed;
    protected int energyPerOperation = 1024;//TODO Energy Cost

    public ToolBase(double attackDamage, double attackSpeed) {
        this.baseAttackDamage = (float) attackDamage;
        this.baseAttackSpeed = (float) attackSpeed;
        setMaxStackSize(1);
    }

    //region Basic Item

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return 23 + (getToolTier(stack) * 3);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityPersistentItem(world, location, itemstack);
    }

    //endregion

    //region Config

    @Override
    public int getProfileCount(ItemStack stack) {
        return 5;
    }

    //endregion

    //region Upgrade

    @Override
    public List<String> getValidUpgrades(ItemStack stack) {
        return new ArrayList<String>() {{
            add(ToolUpgrade.RF_CAPACITY);
        }};
    }

    @Override
    public abstract int getMaxUpgradeLevel(ItemStack stack, String upgrade);

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        holdCTRLForUpgrades(tooltip, stack);
        super.addInformation(stack, playerIn, tooltip, advanced);
    }

    public static void holdCTRLForUpgrades(List<String> list, ItemStack stack) {
        if (!(stack.getItem() instanceof IUpgradableItem)) return;
        if (!InfoHelper.isCtrlKeyDown()) list.add(I18n.format("upgrade.de.holdCtrlForUpgrades.info", TextFormatting.AQUA + "" + TextFormatting.ITALIC, TextFormatting.RESET + "" + TextFormatting.GRAY));
        else {
            list.add(TextFormatting.GOLD + I18n.format("upgrade.de.upgrades.info"));
            list.addAll(UpgradeHelper.getUpgradeStats(stack));
        }
    }

    @Override
    public int getCapacity(ItemStack stack) {
        int level = UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.RF_CAPACITY);

        if (level == 0) {
            return super.getCapacity(stack);
        }
        else {
            return super.getCapacity(stack) * (int) Math.pow(2, level + 1);
        }
    }

    //endregion

    //region Custom Item Rendering


    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        ModelResourceLocation modelLocation = new ModelResourceLocation("draconicevolution:" + feature.getName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, modelLocation);
        ModelLoader.setCustomMeshDefinition(this, stack -> modelLocation);
        ModelRegistryHelper.register(modelLocation, new CCBakeryModel(""));
        ModelBakery.registerItemKeyGenerator(this, stack -> ModelBakery.defaultItemKeyGenerator.generateKey(stack) + "|" +  DEConfig.disable3DModels);

        Set3<String, String, String> texLocs = getTextureLocations();
        ToolModelBakery.createBakery(this, texLocs);
        CCIconRegister.registerTexture(DraconicEvolution.MOD_PREFIX + texLocs.getA());
        CCIconRegister.registerTexture(DraconicEvolution.MOD_PREFIX + texLocs.getB());
        LogHelper.dev("Register Tool Model Texture: " + DraconicEvolution.MOD_PREFIX + texLocs.getB());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IBakery getBakery() {
        return ToolModelBakery.getBakery(this);
    }

    protected abstract Set3<String, String, String> getTextureLocations();

    //endregion

    //region Attack

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (this instanceof IAOEWeapon && player.getCooledAttackStrength(0.5F) >= 0.95F && ((IAOEWeapon) this).getWeaponAOE(stack) > 0) {

            List<EntityLivingBase> entities = player.world.getEntitiesWithinAABB(EntityLivingBase.class, entity.getEntityBoundingBox().expand(((IAOEWeapon) this).getWeaponAOE(stack), 0.25D, ((IAOEWeapon) this).getWeaponAOE(stack)));

            for (EntityLivingBase aoeEntity : entities) {
                if (aoeEntity != player && aoeEntity != entity && !player.isOnSameTeam(entity) && extractAttackEnergy(stack, aoeEntity, player)) {
                    aoeEntity.knockBack(player, 0.4F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                    aoeEntity.attackEntityFrom(DamageSource.causePlayerDamage(player), getAttackDamage(stack));
                }
            }

            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
            player.spawnSweepParticles();
        }

        extractAttackEnergy(stack, entity, player);
        return super.onLeftClickEntity(stack, player, entity);
    }

    protected boolean extractAttackEnergy(ItemStack stack, Entity entity, EntityPlayer player) {
        if (getEnergyStored(stack) > energyPerOperation) {
            modifyEnergy(stack, -energyPerOperation);
            return true;
        }
        return false;
    }

    public float getAttackDamage(ItemStack stack) {
        float damage = baseAttackDamage + (UpgradeHelper.getUpgradeLevel(stack, ATTACK_DAMAGE) * (baseAttackDamage / 4F));
        if (getEnergyStored(stack) < energyPerOperation) {
            damage /= 10;
        }
        return damage;
    }

    private float getAttackSpeed(ItemStack stack) {
        return baseAttackSpeed;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) getAttackDamage(stack) - 1, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double) getAttackSpeed(stack), 0));
        }

        return multimap;
    }

    //endregion

    //region Helpers

    public abstract int getToolTier(ItemStack stack);

    @SideOnly(Side.CLIENT)
    @Override
    public void addDisplayData(@Nullable ItemStack stack, World world, @Nullable BlockPos pos, List<String> displayList) {
        ItemConfigFieldRegistry registry = new ItemConfigFieldRegistry();
        getFields(stack, registry);

        displayList.add(TextFormatting.DARK_PURPLE + ToolConfigHelper.getProfileName(stack, ToolConfigHelper.getProfile(stack)));

        for (IItemConfigField field : registry.getFields()) {
            displayList.add(InfoHelper.ITC() + I18n.format(field.getUnlocalizedName()) + ": " + InfoHelper.HITC() + field.getReadableValue());
        }
    }

    //endregion
}
