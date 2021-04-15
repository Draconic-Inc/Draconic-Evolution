package com.brandon3055.draconicevolution.items.tools.old;

import com.brandon3055.brandonscore.items.ItemEnergyBase;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.api.IHudDisplay;
import com.brandon3055.draconicevolution.api.itemconfig_dep.*;
import com.brandon3055.draconicevolution.api.itemupgrade_dep.IUpgradableItem;
import com.brandon3055.draconicevolution.api.itemupgrade_dep.UpgradeHelper;
import com.brandon3055.draconicevolution.client.model.tool.IToolModelProvider;

import com.brandon3055.draconicevolution.entity.PersistentItemEntity;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.brandon3055.draconicevolution.items.ToolUpgrade.ATTACK_DAMAGE;

/**
 * Created by brandon3055 on 2/06/2016.
 */
@Deprecated
public abstract class ToolBase extends ItemEnergyBase implements /*IRenderOverride,*/ IUpgradableItem, IConfigurableItem, IHudDisplay, IToolModelProvider {

    private float baseAttackDamage;
    private float baseAttackSpeed;
    public int energyPerOperation = 1024;//TODO Energy Cost

    public ToolBase(Properties properties) {
        super(properties);
//        setMaxStackSize(1);
    }

    public abstract double getBaseAttackSpeedConfig();

    public abstract double getBaseAttackDamageConfig();

    public abstract void loadEnergyStats();

    public void loadStatConfig() {
        baseAttackDamage = (float) getBaseAttackDamageConfig();
        baseAttackSpeed = (float) getBaseAttackSpeedConfig();
        loadEnergyStats();
    }

//    @Override
//    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
//        super.getSubItems(tab, subItems);
//
//        if (isInCreativeTab(tab)) {
//            ItemStack uberStack = new ItemStack(this);
//
//            for (String upgrade : getValidUpgrades(uberStack)) {
//                UpgradeHelper.setUpgradeLevel(uberStack, upgrade, getMaxUpgradeLevel(uberStack, upgrade));
//            }
//
//            setEnergy(uberStack, getCapacity(uberStack));
//            subItems.add(uberStack);
//        }
//    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return newStack.getItem() != oldStack.getItem();
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
        return new PersistentItemEntity(world, location, itemstack);
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
            ToolConfigHelper.getFieldStorage(stack).remove(enchantment.getDescriptionId());
            registry.register(stack, new BooleanConfigField(enchantment.getRegistryName() + "", false/*!disEnchants.containsKey(enchantment)*/, "config.field.toggleEnchant.description") {
                @Override
                public String getUnlocalizedName() {
                    return enchantment.getFullname(integer).getString();
                }

                @Override
                public void readFromNBT(CompoundNBT compound) {
                    super.readFromNBT(compound);
                }

                @Override
                public void writeToNBT(CompoundNBT compound) {
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
        ListNBT list = ItemNBTHelper.getCompound(stack).getList("disableEnchants", 10);
        Map<Enchantment, Integer> disEnch = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Enchantment enchantment = Enchantment.byId(list.getCompound(i).getShort("id"));
            int level = list.getCompound(i).getShort("lvl");
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
            Enchantment target = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(field.getName()));

            if (EnchantmentHelper.getEnchantments(stack).containsKey(target)) {
                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
                for (Enchantment enchantment : enchants.keySet()) {
                    if (enchantment == target) {
                        ListNBT list = ItemNBTHelper.getCompound(stack).getList("disableEnchants", 10);
                        CompoundNBT ench = new CompoundNBT();
                        ench.putString("id", enchantment.getRegistryName().toString());
                        ench.putShort("lvl", enchants.get(enchantment).shortValue());
                        list.add(ench);
                        ItemNBTHelper.getCompound(stack).put("disableEnchants", list);
                        enchants.remove(enchantment);
                        EnchantmentHelper.setEnchantments(enchants, stack);

                        ToolConfigHelper.getFieldStorage(stack).putBoolean(field.getName(), false);
                        return;
                    }
                }
            } else {
                Map<Enchantment, Integer> enchants = getDisabledEnchants(stack);
                for (Enchantment enchantment : enchants.keySet()) {
                    if (enchantment == target) {
                        ListNBT list = ItemNBTHelper.getCompound(stack).getList("disableEnchants", 10);
                        for (int i = 0; i < list.size(); i++) {
                            Enchantment e = Enchantment.byId(list.getCompound(i).getShort("id"));
                            if (e == enchantment) {
                                list.remove(i);
                                break;
                            }
                        }

                        stack.enchant(enchantment, enchants.get(enchantment));
                        ToolConfigHelper.getFieldStorage(stack).putBoolean(field.getName(), true);
                        return;
                    }
                }
            }


//            if (((BooleanConfigField) field).getValue() == 0) {
//                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
//                for (Enchantment enchantment : enchants.keySet()) {
//                    if (enchantment.getName().equals(target)) {
//                        ListNBT list = ItemNBTHelper.getCompound(stack).getTagList("disableEnchants", 10);
//                        CompoundNBT ench = new CompoundNBT();
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
//                        ListNBT list = ItemNBTHelper.getCompound(stack).getTagList("disableEnchants", 10);
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        holdCTRLForUpgrades(tooltip, stack);
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    public static void holdCTRLForUpgrades(List<ITextComponent> list, ItemStack stack) {
        if (!(stack.getItem() instanceof IUpgradableItem)) return;
        if (!Screen.hasControlDown()) {
            list.add(new TranslationTextComponent("upgrade.de.holdCtrlForUpgrades.info", TextFormatting.AQUA + "" + TextFormatting.ITALIC, TextFormatting.RESET + "" + TextFormatting.GRAY));
        } else {
            list.add(new TranslationTextComponent("upgrade.de.upgrades.info").withStyle(TextFormatting.GOLD));
//            list.addAll(UpgradeHelper.getUpgradeStats(stack));//TODO Maybe?
        }
    }

    @Override
    public long getCapacity(ItemStack stack) {
        int level = UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.RF_CAPACITY);

        if (level == 0) {
            return super.getCapacity(stack);
        } else {
            return super.getCapacity(stack) * (int) Math.pow(2, level + 1);
        }
    }

    //endregion

    //region Custom Item Rendering

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void registerRenderer(Feature feature) {
//        ModelResourceLocation modelLocation = new ModelResourceLocation("draconicevolution:" + feature.getName(), "inventory");
//        ModelLoader.setCustomModelResourceLocation(this, 0, modelLocation);
//        ModelLoader.setCustomMeshDefinition(this, stack -> modelLocation);
//        ModelRegistryHelper.register(modelLocation, new OverrideListModel(new ToolOverrideList()));
//    }

    //endregion

    //region Attack

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (this instanceof IAOEWeapon && player.getAttackStrengthScale(0.5F) >= 0.95F && ((IAOEWeapon) this).getWeaponAOE(stack) > 0) {
            List<LivingEntity> entities = player.level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(((IAOEWeapon) this).getWeaponAOE(stack), 0.25D, ((IAOEWeapon) this).getWeaponAOE(stack)));

            for (LivingEntity aoeEntity : entities) {
                if (aoeEntity != player && aoeEntity != entity && !player.isAlliedTo(entity) && extractAttackEnergy(stack, aoeEntity, player)) {
//                    aoeEntity.knockBack(player, 0.4F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                    aoeEntity.hurt(DamageSource.playerAttack(player), getAttackDamage(stack));
                }
            }

            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
            player.sweepAttack();
        }

        extractAttackEnergy(stack, entity, player);
        return super.onLeftClickEntity(stack, player, entity);
    }

    protected boolean extractAttackEnergy(ItemStack stack, Entity entity, PlayerEntity player) {
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
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            multimap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double) getAttackDamage(stack) - 1, AttributeModifier.Operation.ADDITION));
            multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double) getAttackSpeed(stack), AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }

    //endregion

    //region Helpers

    public abstract int getToolTier(ItemStack stack);

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addDisplayData(@Nullable ItemStack stack, World world, @Nullable BlockPos pos, List<String> displayList) {
        ItemConfigFieldRegistry registry = new ItemConfigFieldRegistry();
        getFields(stack, registry);

        displayList.add(TextFormatting.DARK_PURPLE + ToolConfigHelper.getProfileName(stack, ToolConfigHelper.getProfile(stack)));

        for (IItemConfigField field : registry.getFields()) {
            if (field instanceof ExternalConfigField) continue;
            displayList.add(InfoHelper.ITC() + I18n.get(field.getUnlocalizedName()) + ": " + InfoHelper.HITC() + field.getReadableValue());
        }
    }

    //endregion
}
