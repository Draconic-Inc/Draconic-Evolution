package com.brandon3055.draconicevolution.items.tools;

import codechicken.lib.model.CCOverrideBakedModel;
import codechicken.lib.render.ModelRegistryHelper;
import com.brandon3055.brandonscore.asm.IEnchantmentOverride;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.config.ICustomRender;
import com.brandon3055.brandonscore.items.ItemEnergyBase;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.IHudDisplay;
import com.brandon3055.draconicevolution.api.itemconfig.IConfigurableItem;
import com.brandon3055.draconicevolution.api.itemconfig.IItemConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemconfig.ToolConfigHelper;
import com.brandon3055.draconicevolution.api.itemupgrade.IUpgradableItem;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.client.model.toolold.IDualModel;
import com.brandon3055.draconicevolution.entity.EntityPersistentItem;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
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
public abstract class ToolBase extends ItemEnergyBase implements ICustomRender, IDualModel, IUpgradableItem, IConfigurableItem, IEnchantmentOverride, IHudDisplay {

    private float baseAttackDamage;
    private float baseAttackSpeed;
    protected int energyPerOperation = 1024;//TODO Energy Cost

    public ToolBase(double attackDamage, double attackSpeed) {
        this.baseAttackDamage = (float)attackDamage;
        this.baseAttackSpeed = (float)attackSpeed;
        setMaxStackSize(1);
    }

    //region Basic Item

    @Override
    public boolean isItemTool(ItemStack stack) {
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

    @Override
    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry) {
//        registry.register(stack, new DoubleConfigField("Test1", 0, 0, 100, "This is a description!", PLUS1_MINUS1));
//        registry.register(stack, new IntegerConfigField("Test2", 0, 0, 100, "This is a description!", PLUS2_MINUS2));
//        registry.register(stack, new BooleanConfigField("Test3", false, "This is a description!"));
//        registry.register(stack, new BooleanConfigField("Test4", true, "This is a description!"));
//        registry.register(stack, new DoubleConfigField("Test5", 22.53, 0, 100, "This is a description!", PLUS3_MINUS3));
//        registry.register(stack, new IntegerConfigField("Slide", 43, 25, 100, "This is a description!", SLIDER));
//        registry.register(stack, new DoubleConfigField("Slide2", 10, 0, 3453, "This is a description!", SLIDER));
//        registry.register(stack, new DoubleConfigField("Slide3", 0, 0, 1, "This is a description!", SLIDER).setExtension(" Some extension here"));
//        registry.register(stack, new BooleanConfigField("Test7", false, "This is a description!"));
//        registry.register(stack, new BooleanConfigField("Test8", true, "This is a description!"));
//        registry.register(stack, new BooleanConfigField("Test9", true, "This is a description!"));
//        registry.register(stack, new BooleanConfigField("Test10", true, "This is a description!").setOnOffTxt("Some On Text Here...", "Some Off Text Here..."));
//
//        registry.register(stack, new BooleanConfigField("Test11", true, "This is a description!"));
//        registry.register(stack, new BooleanConfigField("Test12", true, "This is a description!"));
//
//        String s = "This is going to be a very long description that will allow me to test how well line wrapping works. Now... I have no idea what to add to this description to make it longer... Perhaps i could talk about my plans for the config system? Naa that would take too long... Not to mention i dont want to have to support hundred line descriptions... Oh hay... This should be long enough :P";
//
//        registry.register(stack, new BooleanConfigField("Test13", true, s));
//        registry.register(stack, new AOEConfigField("TestAOE", 1, 0, 50, s));
//        registry.register(stack, new IntegerConfigField("TestSI", 43, 0, 150, "This is a description!", SELECTIONS));
//        registry.register(stack, new IntegerConfigField("Test.", 43, 0, 150, s, PLUS3_MINUS3));
        return registry;
    }


    //endregion

    //region Upgrade

    @Override
    public List<String> getValidUpgrades(ItemStack stack) {
        return new ArrayList<String>() {{ add(ToolUpgrade.RF_CAPACITY); }};
    }

    @Override
    public abstract int getMaxUpgradeLevel(ItemStack stack, String upgrade);

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        holdCTRLForUpgrades(tooltip, stack);
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(TextFormatting.DARK_RED+"[WIP] This item is not finished yet!");
    }

    public static void holdCTRLForUpgrades(List<String> list, ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof IUpgradableItem)) return;
        if (!InfoHelper.isCtrlKeyDown())
            list.add(I18n.format("upgrade.de.holdCtrlForUpgrades.info", TextFormatting.AQUA + "" + TextFormatting.ITALIC, TextFormatting.RESET + "" + TextFormatting.GRAY));
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
            return super.getCapacity(stack) * (int)Math.pow(2, level + 1);
        }
    }

    //endregion

    //region Custom Item Rendering

    public ModelResourceLocation modelLocation;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        if (!DEConfig.disable3DModels) {
            modelLocation = new ModelResourceLocation("draconicevolution:" + feature.name(), "inventory");
            ModelLoader.setCustomModelResourceLocation(this, 0, modelLocation);
            ModelRegistryHelper.register(new ModelResourceLocation("draconicevolution:" + feature.name(), "inventory"), new CCOverrideBakedModel());
        }
    }

    @Override
    public boolean registerNormal(Feature feature) {
        return DEConfig.disable3DModels;
    }

    @Override
    public ModelResourceLocation getModelLocation() {
        return modelLocation;
    }

    //endregion

    //region Attack

    public float getAttackDamage(ItemStack stack) {
        UpgradeHelper.getUpgradeLevel(stack, ATTACK_DAMAGE);
        return baseAttackDamage;
    }

    private float getAttackSpeed(ItemStack stack) {
        return baseAttackSpeed;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack)
    {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", (double)getAttackDamage(stack) - 1, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)getAttackSpeed(stack), 0));
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
