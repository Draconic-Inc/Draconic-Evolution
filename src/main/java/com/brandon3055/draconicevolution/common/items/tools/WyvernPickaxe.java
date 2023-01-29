package com.brandon3055.draconicevolution.common.items.tools;

import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.render.IRenderTweak;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;
import com.brandon3055.draconicevolution.common.items.tools.baseclasses.MiningTool;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.IInventoryTool;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;

public class WyvernPickaxe extends MiningTool implements IInventoryTool, IRenderTweak {

    public WyvernPickaxe() {
        super(ModItems.WYVERN);
        this.setUnlocalizedName(Strings.wyvernPickaxeName);
        this.setHarvestLevel("pickaxe", 10);
        this.setCapacity(BalanceConfigHandler.wyvernToolsBaseStorage);
        this.setMaxExtract(BalanceConfigHandler.wyvernToolsMaxTransfer);
        this.setMaxReceive(BalanceConfigHandler.wyvernToolsMaxTransfer);
        this.energyPerOperation = BalanceConfigHandler.wyvernToolsEnergyPerAction;
        ModItems.register(this);
    }

    @Override
    public List<ItemConfigField> getFields(ItemStack stack, int slot) {
        List<ItemConfigField> list = super.getFields(stack, slot);
        list.add(
                new ItemConfigField(References.INT_ID, slot, References.DIG_AOE)
                        .setMinMaxAndIncromente(0, EnumUpgrade.DIG_AOE.getUpgradePoints(stack), 1)
                        .readFromItem(stack, 0).setModifier("AOE"));
        return list;
    }

    @Override
    public String getInventoryName() {
        return StatCollector.translateToLocal("info.de.toolInventoryEnch.txt");
    }

    @Override
    public int getInventorySlots() {
        return 0;
    }

    @Override
    public boolean isEnchantValid(Enchantment enchant) {
        return enchant.type == EnumEnchantmentType.digger;
    }

    @Override
    public void tweakRender(IItemRenderer.ItemRenderType type) {

        GL11.glTranslated(0.4, 0.65, 0.1);

        GL11.glRotatef(90, 1, 0, 0);
        GL11.glRotatef(140, 0, -1, 0);
        GL11.glRotatef(180, 0, 0, 1);
        GL11.glScaled(0.6, 0.6, 0.6);

        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            GL11.glScalef(13.8F, 13.8F, 13.8F);
            GL11.glRotatef(180, 0, 1, 0);
            GL11.glTranslated(-1.2, 0, -0.35);
        } else if (type == IItemRenderer.ItemRenderType.ENTITY) {
            GL11.glRotatef(90.5F, 0, 1, 0);
            GL11.glTranslated(-0.1, 0, -0.9);
        }
    }

    @Override
    public int getUpgradeCap(ItemStack itemstack) {
        return BalanceConfigHandler.wyvernToolsMaxUpgrades;
    }

    @Override
    public int getMaxTier(ItemStack itemstack) {
        return 1;
    }

    @Override
    public List<String> getUpgradeStats(ItemStack stack) {
        return super.getUpgradeStats(stack);
    }

    @Override
    public int getCapacity(ItemStack stack) {
        int points = IUpgradableItem.EnumUpgrade.RF_CAPACITY.getUpgradePoints(stack);
        return BalanceConfigHandler.wyvernToolsBaseStorage + points * BalanceConfigHandler.wyvernToolsStoragePerUpgrade;
    }

    @Override
    public int getMaxUpgradePoints(int upgradeIndex) {
        if (upgradeIndex == EnumUpgrade.RF_CAPACITY.index) {
            return BalanceConfigHandler.wyvernToolsMaxCapacityUpgradePoints;
        }
        if (upgradeIndex == EnumUpgrade.DIG_AOE.index) {
            return BalanceConfigHandler.wyvernToolsMaxDigAOEUpgradePoints;
        }
        if (upgradeIndex == EnumUpgrade.DIG_SPEED.index) {
            return BalanceConfigHandler.wyvernToolsMaxDigSpeedUpgradePoints;
        }
        return BalanceConfigHandler.wyvernToolsMaxUpgradePoints;
    }

    @Override
    public int getBaseUpgradePoints(int upgradeIndex) {
        if (upgradeIndex == EnumUpgrade.DIG_AOE.index) {
            return BalanceConfigHandler.wyvernToolsMinDigAOEUpgradePoints;
        }
        if (upgradeIndex == EnumUpgrade.DIG_SPEED.index) {
            return BalanceConfigHandler.wyvernToolsMinDigSpeedUpgradePoints;
        }
        return 0;
    }

    @Override
    public List<EnumUpgrade> getUpgrades(ItemStack itemstack) {
        List<EnumUpgrade> list = super.getUpgrades(itemstack);
        list.remove(EnumUpgrade.DIG_DEPTH);
        return list;
    }

    // @Override
    // public boolean isItemTool(ItemStack p_77616_1_) {
    // return true;
    // }
    //
    // @SideOnly(Side.CLIENT)
    // @SuppressWarnings("unchecked")
    // @Override
    // public void getSubItems(Item item, CreativeTabs tab, List list) {
    // list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 0), "Energy", 0));
    // list.add(ItemNBTHelper.setInteger(new ItemStack(item, 1, 0), "Energy", capacity));
    // }
    //
    // @Override
    // public String getUnlocalizedName(){
    //
    // return String.format("item.%s%s", References.MODID.toLowerCase() + ":",
    // super.getUnlocalizedName().substring(super.getUnlocalizedName().indexOf(".") + 1));
    // }
    //
    // @Override
    // public String getUnlocalizedName(final ItemStack itemStack){
    // return getUnlocalizedName();
    // }
    //
    // @Override
    // @SideOnly(Side.CLIENT)
    // public void registerIcons(final IIconRegister iconRegister)
    // {
    // this.itemIcon0 = iconRegister.registerIcon(References.RESOURCESPREFIX + "wyvern_pick");
    // this.itemIcon1 = iconRegister.registerIcon(References.RESOURCESPREFIX + "wyvern_pick_active");
    // }
    //
    // @Override
    // @SideOnly(Side.CLIENT)
    // public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    // {
    // if (ItemNBTHelper.getShort(stack, "size", (short)0) > 0)
    // return itemIcon1;
    // else
    // return itemIcon0;
    // }
    //
    // @Override
    // @SideOnly(Side.CLIENT)
    // public IIcon getIconIndex(ItemStack stack)
    // {
    // if (ItemNBTHelper.getShort(stack, "size", (short)0) > 0)
    // return itemIcon1;
    // else
    // return itemIcon0;
    // }
    //
    // @Override
    // public boolean onBlockStartBreak(final ItemStack stack, final int x, final int y, final int z, final EntityPlayer
    // player)
    // {
    // World world = player.worldObj;
    // Block block = world.getBlock(x, y, z);
    // Material mat = block.getMaterial();
    // if (!ToolHandler.isRightMaterial(mat, ToolHandler.materialsPick)) {
    // return false;
    // }
    // int fortune = EnchantmentHelper.getFortuneModifier(player);
    // boolean silk = EnchantmentHelper.getSilkTouchModifier(player);
    // ToolHandler.disSquare(x, y, z, player, world, silk, fortune, ToolHandler.materialsPick, stack);
    // return false;
    // }
    //
    // @Override
    // public ItemStack onItemRightClick(final ItemStack stack, final World world, final EntityPlayer player)
    // {
    // return ToolHandler.changeMode(stack, player, false, 1);
    // }
    //
    // @Override
    // @SideOnly(Side.CLIENT)
    // @SuppressWarnings({ "unchecked", "rawtypes" })
    // public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean
    // extraInformation)
    // {
    // int size = (ItemNBTHelper.getShort(stack, "size", (short) 0) * 2) + 1;
    // if (InfoHelper.holdShiftForDetails(list)){
    // InfoHelper.addEnergyInfo(stack, list);
    //
    // list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.miningMode.txt") + ": " + InfoHelper.HITC()
    // + size + "x" + size);
    // list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.de.changeMiningMode.txt"));
    //
    // InfoHelper.addLore(stack, list);
    // }
    // }
    //
    // @Override
    // public EnumRarity getRarity(ItemStack stack)
    // {
    // return EnumRarity.uncommon;
    // }
    //
    // @Override
    // public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
    //
    // if (container.stackTagCompound == null) {
    // container.stackTagCompound = new NBTTagCompound();
    // }
    // int energy = container.stackTagCompound.getInteger("Energy");
    // int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
    //
    // if (!simulate) {
    // energy += energyReceived;
    // container.stackTagCompound.setInteger("Energy", energy);
    // }
    // return energyReceived;
    // }
    //
    // @Override
    // public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
    //
    // if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy")) {
    // return 0;
    // }
    // int energy = container.stackTagCompound.getInteger("Energy");
    // int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
    //
    // if (!simulate) {
    // energy -= energyExtracted;
    // container.stackTagCompound.setInteger("Energy", energy);
    // }
    // return energyExtracted;
    // }
    //
    // @Override
    // public int getEnergyStored(ItemStack container) {
    // if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy")) {
    // return 0;
    // }
    // return container.stackTagCompound.getInteger("Energy");
    // }
    //
    // @Override
    // public int getMaxEnergyStored(ItemStack container) {
    // return capacity;
    // }
    //
    // @Override
    // public boolean showDurabilityBar(ItemStack stack) {
    // return !(getEnergyStored(stack) == getMaxEnergyStored(stack));
    // }
    //
    // @Override
    // public double getDurabilityForDisplay(ItemStack stack) {
    // return 1D - ((double)getEnergyStored(stack) / (double)getMaxEnergyStored(stack));
    // }
    //
    // @Override
    // public float getDigSpeed(ItemStack stack, Block block, int meta) {
    // if ((stack.getItem() instanceof IEnergyContainerItem) &&
    // ((IEnergyContainerItem)stack.getItem()).getEnergyStored(stack) >= energyPerOperation)
    // return super.getDigSpeed(stack, block, meta);
    // else
    // return 1F;
    // }
    //
    // @Override
    // public boolean hasCustomEntity(ItemStack stack) {
    // return true;
    // }
    //
    // @Override
    // public Entity createEntity(World world, Entity location, ItemStack itemstack) {
    // return new EntityPersistentItem(world, location, itemstack);
    // }
}
