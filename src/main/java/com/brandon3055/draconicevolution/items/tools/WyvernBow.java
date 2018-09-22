package com.brandon3055.draconicevolution.items.tools;

import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.util.ItemNBTUtils;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.api.IFOVModifierItem;
import com.brandon3055.brandonscore.lib.PairKV;
import com.brandon3055.brandonscore.registry.Feature;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.api.itemconfig.*;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.client.model.tool.ToolModelBakery;
import com.brandon3055.draconicevolution.handlers.BowHandler;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.brandon3055.draconicevolution.api.itemconfig.IItemConfigField.EnumControlType.SLIDER;

/**
 * Created by brandon3055 on 2/06/2016.
 */
public class WyvernBow extends ToolBase implements IFOVModifierItem, IReaperItem {

    public WyvernBow() {
//        super(1, 0);
//        setEnergyStats(ToolStats.WYVERN_BASE_CAPACITY, 512000, 0);
    }

    @Override
    public double getBaseAttackSpeedConfig() {
        return 0;
    }

    @Override
    public double getBaseAttackDamageConfig() {
        return 1;
    }

    @Override
    public void loadEnergyStats() {
        setEnergyStats(ToolStats.WYVERN_BASE_CAPACITY, 512000, 0);
    }

    //region Bow Stuff

    private ItemStack findAmmo(EntityPlayer player) {
        if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND))) {
            return player.getHeldItem(EnumHand.OFF_HAND);
        }
        else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND))) {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        }
        else {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (this.isArrow(itemstack)) {
                    return itemstack;
                }
            }

            return ItemStack.EMPTY;
        }
    }

    protected boolean isArrow(@Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemArrow;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
        if (entityLiving instanceof EntityPlayer) {
            BowHandler.onPlayerStoppedUsingBow(stack, world, (EntityPlayer) entityLiving, timeLeft);
        }
    }

    public static float getArrowVelocity(int charge) {
        float f = (float) charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;

        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        return BowHandler.onBowRightClick(player.getHeldItem(hand), world, player, hand);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entityLivingBase, int count) {
        if (entityLivingBase instanceof EntityPlayer) {
            BowHandler.onBowUsingTick(stack, (EntityPlayer) entityLivingBase, count);
        }
    }

    //endregion

    //region Render

    @Override
    public void registerRenderer(Feature feature) {
        super.registerRenderer(feature);
        ToolModelBakery.registerItemKeyGenerator(this, stack -> ModelBakery.defaultItemKeyGenerator.generateKey(stack) + "|" + ItemNBTUtils.getByte(stack, "render:bow_pull"));
    }

    @Override
    public PairKV<TextureAtlasSprite, ResourceLocation> getModels(ItemStack stack) {
        byte pull = ItemNBTUtils.getByte(stack, "render:bow_pull");
        return new PairKV<>(DETextures.WYVERN_BOW[pull], new ResourceLocation("draconicevolution", String.format("models/item/tools/wyvern_bow0%s.obj", pull)));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addDisplayData(@Nullable ItemStack stack, World world, @Nullable BlockPos pos, List<String> displayList)  {
        super.addDisplayData(stack, world, pos, displayList);

        if (BrandonsCore.proxy.getClientPlayer() != null) {
            BowHandler.BowProperties properties = new BowHandler.BowProperties(stack, BrandonsCore.proxy.getClientPlayer());
            displayList.add(InfoHelper.ITC() + I18n.format("gui.de.rfPerShot.txt") + ": " + InfoHelper.HITC() + Utils.addCommas(properties.calculateEnergyCost()));
            if (!properties.canFire() && properties.cantFireMessage != null) {
                displayList.add(TextFormatting.DARK_RED + I18n.format(properties.cantFireMessage));
            }
        }
    }

    //endregion

    //region Upgrade & Config

    @Override
    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry) {
        double maxDamage = 2 + getToolTier(stack) + (UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.ARROW_DAMAGE) * 2);
        int maxSpeed = 100 + (getToolTier(stack) * 100) + UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.ARROW_SPEED) * 100;

        registry.register(stack, new DoubleConfigField("bowArrowDamage", maxDamage, 0, maxDamage, "config.field.bowArrowDamage.description", SLIDER));
        registry.register(stack, new IntegerConfigField("bowArrowSpeedModifier", maxSpeed, 0, maxSpeed, "config.field.bowArrowSpeedModifier.description", SLIDER).setPrefix("+").setExtension("%"));
        registry.register(stack, new BooleanConfigField("bowAutoFire", false, "config.field.bowAutoFire.description"));
        registry.register(stack, new DoubleConfigField("bowExplosionPower", 0, 0, 4, "config.field.bowExplosionPower.description", SLIDER));
        registry.register(stack, new IntegerConfigField("bowZoomModifier", 0, 0, (int) (getMaxZoomModifier(stack) * 100), "config.field.bowZoomModifier.description", SLIDER));

        return registry;
    }

    public float getMaxZoomModifier(ItemStack stack) {
        return 3;
    }

    @Override
    public List<String> getValidUpgrades(ItemStack stack) {
        List<String> list = super.getValidUpgrades(stack);
        list.add(ToolUpgrade.ARROW_DAMAGE);
        list.add(ToolUpgrade.ARROW_SPEED);
        list.add(ToolUpgrade.DRAW_SPEED);
        return list;
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
        return 2;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (getDisabledEnchants(stack).containsKey(enchantment)) {
            return false;
        }
        return enchantment.type == EnumEnchantmentType.BOW || enchantment.type == EnumEnchantmentType.ALL;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 0;
    }

    //endregion

    @Override
    public float getNewFOV(EntityPlayer player, ItemStack stack, float currentFOV, float originalFOV, EntityEquipmentSlot slot) {
        float zoom = ((10 + ToolConfigHelper.getIntegerField("bowZoomModifier", stack)) / 605F);

        if (player.getActiveItemStack() == stack) {
            if (currentFOV > 1.5F) {
                currentFOV = 1.5F;
            }

            return currentFOV - zoom;
        }

        return currentFOV;
    }

    @Override
    public int getReaperLevel(ItemStack stack) {
        return 1;
    }
}
