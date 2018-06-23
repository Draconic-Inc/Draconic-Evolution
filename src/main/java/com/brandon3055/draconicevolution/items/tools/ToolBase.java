package com.brandon3055.draconicevolution.items.tools;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakedmodels.OverrideListModel;
import com.brandon3055.brandonscore.items.ItemEnergyBase;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.registry.IRenderOverride;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.api.IHudDisplay;
import com.brandon3055.draconicevolution.api.itemconfig.*;
import com.brandon3055.draconicevolution.api.itemupgrade.IUpgradableItem;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.client.model.tool.IToolModelProvider;
import com.brandon3055.draconicevolution.client.model.tool.ToolOverrideList;
import com.brandon3055.draconicevolution.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.brandon3055.draconicevolution.items.ToolUpgrade.ATTACK_DAMAGE;

/**
 * Created by brandon3055 on 2/06/2016.
 */
public abstract class ToolBase extends ItemEnergyBase implements IRenderOverride, IUpgradableItem, IConfigurableItem, IHudDisplay, IToolModelProvider {

    private float baseAttackDamage;
    private float baseAttackSpeed;
    protected int energyPerOperation = 1024;//TODO Energy Cost

    public ToolBase(/*double attackDamage, double attackSpeed*/) {
//        this.baseAttackDamage = (float) attackDamage;
//        this.baseAttackSpeed = (float) attackSpeed;
        setMaxStackSize(1);
    }

    public abstract double getBaseAttackSpeedConfig();

    public abstract double getBaseAttackDamageConfig();

    public abstract void loadEnergyStats();

    public void loadStatConfig() {
        baseAttackDamage = (float) getBaseAttackDamageConfig();
        baseAttackSpeed = (float) getBaseAttackSpeedConfig();
        loadEnergyStats();
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

    protected void addEnchantConfig(ItemStack stack, ItemConfigFieldRegistry registry) {
        Map<Enchantment, Integer> enchants = getAllEnchants(stack);
        Map<Enchantment, Integer> disEnchants = getDisabledEnchants(stack);
        enchants.forEach((enchantment, integer) -> {
            ToolConfigHelper.getFieldStorage(stack).removeTag(enchantment.getName());
            registry.register(stack, new BooleanConfigField(enchantment.getRegistryName() + "", false/*!disEnchants.containsKey(enchantment)*/, "config.field.toggleEnchant.description"){
                @Override
                public String getUnlocalizedName() {
                    return enchantment.getTranslatedName(integer);
                }

                @Override
                public void readFromNBT(NBTTagCompound compound) {
                    super.readFromNBT(compound);
                }

                @Override
                public void writeToNBT(NBTTagCompound compound) {
                    super.writeToNBT(compound);
                }

                @Override
                public Integer getValue() {
//                    return EnchantmentHelper.getEnchantments(stack).containsKey(enchantment) ? 1 : 0;
                    return super.getValue();
                }

                @Override
                public String getReadableValue() {
                    return super.getReadableValue();
                }
            });
        });
    }

    public Map<Enchantment, Integer> getDisabledEnchants(ItemStack stack) {
        NBTTagList list = ItemNBTHelper.getCompound(stack).getTagList("disableEnchants", 10);
        Map<Enchantment, Integer> disEnch = new HashMap<>();
        for (int i = 0; i < list.tagCount(); i++) {
            Enchantment enchantment = Enchantment.getEnchantmentByID(list.getCompoundTagAt(i).getShort("id"));
            int level = list.getCompoundTagAt(i).getShort("lvl");
            disEnch.put(enchantment, level);
        }
        return disEnch;
    }

    public Map<Enchantment, Integer> getAllEnchants(ItemStack stack) {
        Map<Enchantment, Integer> enchants = new HashMap<>();
        enchants.putAll(getDisabledEnchants(stack));
        enchants.putAll(EnchantmentHelper.getEnchantments(stack));
        return enchants;
    }

    @Override
    public void onFieldChanged(ItemStack stack, IItemConfigField field) {
        if (field instanceof BooleanConfigField && field.getDescription().equals("config.field.toggleEnchant.description")) {
            Enchantment target = Enchantment.REGISTRY.getObject(new ResourceLocation(field.getName()));

            if (EnchantmentHelper.getEnchantments(stack).containsKey(target)) {
                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
                for (Enchantment enchantment : enchants.keySet()) {
                    if (enchantment == target) {
                        NBTTagList list = ItemNBTHelper.getCompound(stack).getTagList("disableEnchants", 10);
                        NBTTagCompound ench = new NBTTagCompound();
                        ench.setShort("id", (short) Enchantment.getEnchantmentID(enchantment));
                        ench.setShort("lvl", enchants.get(enchantment).shortValue());
                        list.appendTag(ench);
                        ItemNBTHelper.getCompound(stack).setTag("disableEnchants", list);
                        enchants.remove(enchantment);
                        EnchantmentHelper.setEnchantments(enchants, stack);

                        ToolConfigHelper.getFieldStorage(stack).setBoolean(field.getName(), false);
                        return;
                    }
                }
            }
            else {
                Map<Enchantment, Integer> enchants = getDisabledEnchants(stack);
                for (Enchantment enchantment : enchants.keySet()) {
                    if (enchantment == target) {
                        NBTTagList list = ItemNBTHelper.getCompound(stack).getTagList("disableEnchants", 10);
                        for (int i = 0; i < list.tagCount(); i++) {
                            Enchantment e = Enchantment.getEnchantmentByID(list.getCompoundTagAt(i).getShort("id"));
                            if (e == enchantment) {
                                list.removeTag(i);
                                break;
                            }
                        }

                        stack.addEnchantment(enchantment, enchants.get(enchantment));
                        ToolConfigHelper.getFieldStorage(stack).setBoolean(field.getName(), true);
                        return;
                    }
                }
            }


//            if (((BooleanConfigField) field).getValue() == 0) {
//                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
//                for (Enchantment enchantment : enchants.keySet()) {
//                    if (enchantment.getName().equals(target)) {
//                        NBTTagList list = ItemNBTHelper.getCompound(stack).getTagList("disableEnchants", 10);
//                        NBTTagCompound ench = new NBTTagCompound();
//                        ench.setShort("id", (short) Enchantment.getEnchantmentID(enchantment));
//                        ench.setShort("lvl", enchants.get(enchantment).shortValue());
//                        list.appendTag(ench);
//                        ItemNBTHelper.getCompound(stack).setTag("disableEnchants", list);
//                        enchants.remove(enchantment);
//                        EnchantmentHelper.setEnchantments(enchants, stack);
//
//                        ToolConfigHelper.getFieldStorage(stack).setBoolean(field.getName(), false);
//                        return;
//                    }
//                }
//            }
//            else {
//                Map<Enchantment, Integer> enchants = getDisabledEnchants(stack);
//                for (Enchantment enchantment : enchants.keySet()) {
//                    if (enchantment.getName().equals(target)) {
//                        NBTTagList list = ItemNBTHelper.getCompound(stack).getTagList("disableEnchants", 10);
//                        for (int i = 0; i < list.tagCount(); i++) {
//                            Enchantment e = Enchantment.getEnchantmentByID(list.getCompoundTagAt(i).getShort("id"));
//                            if (e == enchantment) {
//                                list.removeTag(i);
//                                break;
//                            }
//                        }
//
//                        stack.addEnchantment(enchantment, enchants.get(enchantment));
//                        ToolConfigHelper.getFieldStorage(stack).setBoolean(field.getName(), true);
//                        return;
//                    }
//                }
//            }
        }
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return !getDisabledEnchants(stack).containsKey(enchantment);
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

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
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
        ModelRegistryHelper.register(modelLocation, new OverrideListModel(new ToolOverrideList()));
    }

    //endregion

    //region Attack

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (this instanceof IAOEWeapon && player.getCooledAttackStrength(0.5F) >= 0.95F && ((IAOEWeapon) this).getWeaponAOE(stack) > 0) {

            List<EntityLivingBase> entities = player.world.getEntitiesWithinAABB(EntityLivingBase.class, entity.getEntityBoundingBox().grow(((IAOEWeapon) this).getWeaponAOE(stack), 0.25D, ((IAOEWeapon) this).getWeaponAOE(stack)));

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
            if (field instanceof ExternalConfigField) continue;
            displayList.add(InfoHelper.ITC() + I18n.format(field.getUnlocalizedName()) + ": " + InfoHelper.HITC() + field.getReadableValue());
        }
    }

    //endregion
}
