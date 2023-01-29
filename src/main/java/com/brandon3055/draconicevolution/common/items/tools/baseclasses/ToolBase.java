package com.brandon3055.draconicevolution.common.items.tools.baseclasses;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.common.utills.DataUtills;
import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.keybinding.KeyBindings;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.network.ToolModePacket;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Brandon on 2/01/2015.
 */
public class ToolBase extends RFItemBase {

    private static final Set SHOVEL_OVERRIDES = Sets.newHashSet(
            Blocks.grass,
            Blocks.dirt,
            Blocks.sand,
            Blocks.gravel,
            Blocks.snow_layer,
            Blocks.snow,
            Blocks.clay,
            Blocks.farmland,
            Blocks.soul_sand,
            Blocks.mycelium,
            Material.grass,
            Material.ground,
            Material.sand,
            Material.snow,
            Material.craftedSnow,
            Material.clay);
    private static final Set PICKAXE_OVERRIDES = Sets.newHashSet(
            Blocks.cobblestone,
            Blocks.double_stone_slab,
            Blocks.stone_slab,
            Blocks.stone,
            Blocks.sandstone,
            Blocks.mossy_cobblestone,
            Blocks.iron_ore,
            Blocks.iron_block,
            Blocks.coal_ore,
            Blocks.gold_block,
            Blocks.gold_ore,
            Blocks.diamond_ore,
            Blocks.diamond_block,
            Blocks.ice,
            Blocks.netherrack,
            Blocks.lapis_ore,
            Blocks.lapis_block,
            Blocks.redstone_ore,
            Blocks.lit_redstone_ore,
            Blocks.rail,
            Blocks.detector_rail,
            Blocks.golden_rail,
            Blocks.activator_rail,
            Material.iron,
            Material.anvil,
            Material.rock,
            Material.glass,
            Material.ice,
            Material.packedIce);
    private static final Set AXE_OVERRIDES = Sets.newHashSet(
            Blocks.planks,
            Blocks.bookshelf,
            Blocks.log,
            Blocks.log2,
            Blocks.chest,
            Blocks.pumpkin,
            Blocks.lit_pumpkin,
            Material.wood,
            Material.leaves,
            Material.coral,
            Material.cactus,
            Material.plants,
            Material.vine);

    /**
     * A list of blocks this tool can mine that are not covered by the tool class.
     */
    private Set blockOverrides;

    private float efficiencyOnProperMaterial = 4.0F;
    /**
     * Damage versus entities.
     */
    public float damageVsEntity;
    /**
     * The material this tool is made from.
     */
    protected Item.ToolMaterial toolMaterial;
    /**
     * The amount of energy required to dig one block, damage a mob.
     */
    public int energyPerOperation = 0;

    protected ToolBase(float baseDamage, Item.ToolMaterial material, Set blockOverrides) {
        this.toolMaterial = material;
        this.blockOverrides = blockOverrides == null ? new HashSet() : blockOverrides;
        this.maxStackSize = 1;
        this.setMaxDamage(material.getMaxUses());
        this.efficiencyOnProperMaterial = material.getEfficiencyOnProperMaterial();
        this.damageVsEntity = baseDamage + material.getDamageVsEntity();
        this.setCreativeTab(DraconicEvolution.tabToolsWeapons);
    }

    public float getEfficiency(ItemStack stack) {
        return efficiencyOnProperMaterial;
    }

    @Override
    public void setHarvestLevel(String toolClass, int level) {
        if (toolClass.equals("pickaxe")) blockOverrides.addAll(PICKAXE_OVERRIDES);
        if (toolClass.equals("shovel")) blockOverrides.addAll(SHOVEL_OVERRIDES);
        if (toolClass.equals("axe")) blockOverrides.addAll(AXE_OVERRIDES);
        super.setHarvestLevel(toolClass, level);
    }

    /**
     * Get strength vs block
     */
    @Override
    public float func_150893_a(ItemStack stack, Block block) {
        return blockOverrides.contains(block) || blockOverrides.contains(block.getMaterial()) ? getEfficiency(stack)
                : 1.0F;
    }

    /**
     * Can Harvest Block
     */
    @Override
    public boolean func_150897_b(Block block) {
        if (getToolClasses(null).contains("pickaxe")) return true;
        return blockOverrides.contains(block) || blockOverrides.contains(block.getMaterial());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return true;
    }

    @Override
    public int getItemEnchantability() {
        return this.toolMaterial.getEnchantability();
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        float speed;
        if (ForgeHooks.isToolEffective(stack, block, meta)) {
            speed = getEfficiency(stack);
        } else {
            speed = super.getDigSpeed(stack, block, meta);
        }

        if (getEnergyStored(stack) >= energyPerOperation) {
            float f = IConfigurableItem.ProfileHelper.getFloat(stack, References.DIG_SPEED_MULTIPLIER, 1f);
            if (speed > 50f) f *= f;
            return f * speed;
        } else {
            return 0.5f;
        }
    }

    @Override
    public List<ItemConfigField> getFields(ItemStack stack, int slot) {
        List<ItemConfigField> list = super.getFields(stack, slot);
        if (!getToolClasses(stack).isEmpty()) list.add(
                new ItemConfigField(References.FLOAT_ID, slot, References.DIG_SPEED_MULTIPLIER)
                        .setMinMaxAndIncromente(0f, 1f, 0.01f).readFromItem(stack, 1f).setModifier("PERCENT"));
        if (!getToolClasses(stack).isEmpty()) list.add(
                new ItemConfigField(References.BOOLEAN_ID, slot, References.BASE_SAFE_AOE).readFromItem(stack, false));
        return list;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean extended) {
        boolean show = InfoHelper.holdShiftForDetails(list);
        if (show) {
            if (hasProfiles()) {
                int preset = ItemNBTHelper.getInteger(stack, "ConfigProfile", 0);
                list.add(
                        EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("info.de.capacitorMode.txt")
                                + ": "
                                + ItemNBTHelper.getString(stack, "ProfileName" + preset, "Profile " + preset));
            }
            List<ItemConfigField> l = getFields(stack, 0);
            for (ItemConfigField f : l) list.add(f.getTooltipInfo());
            // if (getCapacity(stack) > 0) list.add(InfoHelper.ITC() +
            // StatCollector.translateToLocal("info.de.charge.txt") + ": " + InfoHelper.HITC() +
            // Utills.formatNumber(getEnergyStored(stack)) + " / " + Utills.formatNumber(getCapacity(stack)));
        }
        holdCTRLForUpgrades(list, stack);

        addAditionalInformation(stack, player, list, extended);
        InfoHelper.addEnergyInfo(stack, list);
        if (show && !ConfigHandler.disableLore) InfoHelper.addLore(stack, list, true);
    }

    @SideOnly(Side.CLIENT)
    public void addAditionalInformation(ItemStack stack, EntityPlayer player, List list, boolean extended) {
        String key;
        if (KeyBindings.toolConfig.getKeyCode() >= 0 && KeyBindings.toolConfig.getKeyCode() < Keyboard.KEYBOARD_SIZE)
            key = Keyboard.getKeyName(KeyBindings.toolConfig.getKeyCode());
        else key = Mouse.getButtonName(KeyBindings.toolConfig.getKeyCode() + 101);

        list.add(
                StatCollector.translateToLocal("info.de.press.txt") + " "
                        + key
                        + " "
                        + StatCollector.translateToLocal("info.de.toOpenConfigGUI.txt"));
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        if (stack.getUnlocalizedName().contains(":wyvern")) return EnumRarity.rare;
        if (stack.getUnlocalizedName().contains(":draconic")) return EnumRarity.epic;
        return EnumRarity.uncommon;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && !BrandonsCore.proxy.isDedicatedServer()) {
            handleModeChange(stack, player, InfoHelper.isShiftKeyDown(), InfoHelper.isCtrlKeyDown());
        } else if (world.isRemote && BrandonsCore.proxy.getMCServer() == null) {
            handleModeChange(stack, player, InfoHelper.isShiftKeyDown(), InfoHelper.isCtrlKeyDown());
            DraconicEvolution.network
                    .sendToServer(new ToolModePacket(InfoHelper.isShiftKeyDown(), InfoHelper.isCtrlKeyDown()));
        }

        return super.onItemRightClick(stack, world, player);
    }

    public static void handleModeChange(ItemStack stack, EntityPlayer player, boolean shift, boolean ctrl) {
        if (stack == null || !(stack.getItem() instanceof IConfigurableItem)) return;
        IConfigurableItem item = (IConfigurableItem) stack.getItem();

        if (shift && !ctrl) {
            List<ItemConfigField> fields = item.getFields(stack, player.inventory.currentItem);
            for (ItemConfigField field : fields) {
                if (field.name.equals(References.DIG_AOE)) {
                    int aoe = (Integer) field.value;
                    aoe++;
                    if (aoe > (Integer) field.max) aoe = (Integer) field.min;
                    field.value = aoe;
                    DataUtills.writeObjectToCompound(
                            IConfigurableItem.ProfileHelper.getProfileCompound(stack),
                            field.value,
                            field.datatype,
                            field.name);
                }
            }
        } else if (ctrl && !shift) {
            List<ItemConfigField> fields = item.getFields(stack, player.inventory.currentItem);
            for (ItemConfigField field : fields) {
                if (field.name.equals(References.DIG_DEPTH)) {
                    int aoe = (Integer) field.value;
                    aoe++;
                    if (aoe > (Integer) field.max) aoe = (Integer) field.min;
                    field.value = aoe;
                    DataUtills.writeObjectToCompound(
                            IConfigurableItem.ProfileHelper.getProfileCompound(stack),
                            field.value,
                            field.datatype,
                            field.name);
                }
            }
        } else if (ctrl && shift) {
            List<ItemConfigField> fields = item.getFields(stack, player.inventory.currentItem);
            for (ItemConfigField field : fields) {
                if (field.name.equals(References.ATTACK_AOE)) {
                    int aoe = (Integer) field.value;
                    aoe++;
                    if (aoe > (Integer) field.max) aoe = (Integer) field.min;
                    field.value = aoe;
                    DataUtills.writeObjectToCompound(
                            IConfigurableItem.ProfileHelper.getProfileCompound(stack),
                            field.value,
                            field.datatype,
                            field.name);
                }
            }
        }
    }

    public ToolMaterial getToolMaterial() {
        return toolMaterial;
    }

    public static void holdCTRLForUpgrades(List list, ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof IUpgradableItem)) return;
        if (!InfoHelper.isCtrlKeyDown()) list.add(
                StatCollector.translateToLocal("info.de.hold.txt") + " "
                        + EnumChatFormatting.AQUA
                        + ""
                        + EnumChatFormatting.ITALIC
                        + StatCollector.translateToLocal("info.de.ctrl.txt")
                        + EnumChatFormatting.RESET
                        + " "
                        + EnumChatFormatting.GRAY
                        + StatCollector.translateToLocal("info.de.forUpgrades.txt"));
        else {
            list.addAll(((IUpgradableItem) stack.getItem()).getUpgradeStats(stack));
            list.add(
                    EnumChatFormatting.GOLD + ""
                            + EnumChatFormatting.ITALIC
                            + StatCollector.translateToLocal("info.de.useUpgradeModifier.txt"));
        }
    }
}
