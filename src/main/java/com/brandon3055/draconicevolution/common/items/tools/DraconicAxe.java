package com.brandon3055.draconicevolution.common.items.tools;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.render.IRenderTweak;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.handler.BalanceConfigHandler;
import com.brandon3055.draconicevolution.common.items.tools.baseclasses.MiningTool;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.IConfigurableItem;
import com.brandon3055.draconicevolution.common.utills.IInventoryTool;
import com.brandon3055.draconicevolution.common.utills.IUpgradableItem;
import com.brandon3055.draconicevolution.common.utills.ItemConfigField;

public class DraconicAxe extends MiningTool implements IInventoryTool, IRenderTweak {

    public DraconicAxe() {
        super(ModItems.WYVERN);
        this.setHarvestLevel("axe", 10);
        this.setUnlocalizedName(Strings.draconicAxeName);
        this.setCapacity(BalanceConfigHandler.draconicToolsBaseStorage);
        this.setMaxExtract(BalanceConfigHandler.draconicToolsMaxTransfer);
        this.setMaxReceive(BalanceConfigHandler.draconicToolsMaxTransfer);
        this.energyPerOperation = BalanceConfigHandler.draconicToolsEnergyPerAction;
        ModItems.register(this);
    }

    @Override
    public List<ItemConfigField> getFields(ItemStack stack, int slot) {
        List<ItemConfigField> list = super.getFields(stack, slot);

        list.add(
                new ItemConfigField(References.INT_ID, slot, References.DIG_AOE)
                        .setMinMaxAndIncromente(0, EnumUpgrade.DIG_AOE.getUpgradePoints(stack), 1)
                        .readFromItem(stack, 0).setModifier("AOE"));
        list.add(
                new ItemConfigField(References.INT_ID, slot, References.DIG_DEPTH)
                        .setMinMaxAndIncromente(1, EnumUpgrade.DIG_DEPTH.getUpgradePoints(stack), 1)
                        .readFromItem(stack, 1));
        list.add(new ItemConfigField(References.BOOLEAN_ID, slot, References.TREE_MODE).readFromItem(stack, false));
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
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        if (IConfigurableItem.ProfileHelper.getBoolean(stack, References.TREE_MODE, false)
                && isTree(player.worldObj, x, y, z)) {
            trimLeavs(x, y, z, player, player.worldObj, stack);
            for (int i = 0; i < 9; i++) player.worldObj.playAuxSFX(
                    2001,
                    x,
                    y,
                    z,
                    Block.getIdFromBlock(player.worldObj.getBlock(x, y, z))
                            + (player.worldObj.getBlockMetadata(x, y, z) << 12));
            chopTree(x, y, z, player, player.worldObj, stack);
            return false;
        }

        return super.onBlockStartBreak(stack, x, y, z, player);
    }

    @Override
    public int getUpgradeCap(ItemStack itemstack) {
        return BalanceConfigHandler.draconicToolsMaxUpgrades;
    }

    @Override
    public int getMaxTier(ItemStack itemstack) {
        return 2;
    }

    @Override
    public List<String> getUpgradeStats(ItemStack stack) {
        return super.getUpgradeStats(stack);
    }

    @Override
    public int getCapacity(ItemStack stack) {
        int points = IUpgradableItem.EnumUpgrade.RF_CAPACITY.getUpgradePoints(stack);
        return BalanceConfigHandler.draconicToolsBaseStorage
                + points * BalanceConfigHandler.draconicToolsStoragePerUpgrade;
    }

    @Override
    public int getMaxUpgradePoints(int upgradeIndex) {
        if (upgradeIndex == EnumUpgrade.RF_CAPACITY.index) {
            return BalanceConfigHandler.draconicToolsMaxCapacityUpgradePoints;
        }
        if (upgradeIndex == EnumUpgrade.DIG_AOE.index) {
            return BalanceConfigHandler.draconicToolsMaxDigAOEUpgradePoints;
        }
        if (upgradeIndex == EnumUpgrade.DIG_DEPTH.index) {
            return BalanceConfigHandler.draconicToolsMaxDigDepthUpgradePoints + 2;
        }
        if (upgradeIndex == EnumUpgrade.DIG_SPEED.index) {
            return BalanceConfigHandler.draconicToolsMaxDigSpeedUpgradePoints;
        }
        return BalanceConfigHandler.draconicToolsMaxUpgradePoints;
    }

    @Override
    public int getBaseUpgradePoints(int upgradeIndex) {
        if (upgradeIndex == EnumUpgrade.DIG_AOE.index) {
            return BalanceConfigHandler.draconicToolsMinDigAOEUpgradePoints;
        }
        if (upgradeIndex == EnumUpgrade.DIG_DEPTH.index) {
            return BalanceConfigHandler.draconicToolsMinDigDepthUpgradePoints;
        }
        if (upgradeIndex == EnumUpgrade.DIG_SPEED.index) {
            return BalanceConfigHandler.draconicToolsMinDigSpeedUpgradePoints;
        }
        return 0;
    }

    // @Override
    // public boolean onBlockStartBreak(ItemStack stack, int X, int Y, int Z, EntityPlayer player) {
    // World world = player.worldObj;
    // boolean tree = isTree(world, X, Y, Z);
    //
    // if (player.isSneaking()) {
    // return false;
    // }
    //
    // Block block = world.getBlock(X, Y, Z);
    // Material mat = block.getMaterial();
    // if (!ToolHandler.isRightMaterial(mat, ToolHandler.materialsAxe)) {
    // return false;
    // }
    //
    // if (!tree) {
    // ToolHandler.disSquare(X, Y, Z, player, world, false, 0, ToolHandler.materialsAxe, stack);
    // return false;
    // }
    //
    // if (!world.isRemote) world.playAuxSFX(2001, X, Y, Z, Block.getIdFromBlock(world.getBlock(X, Y, Z)));
    // trimLeavs(X, Y, Z, player, world, stack);
    // chopTree(X, Y, Z, player, world, stack);
    //
    // return true;
    // }
    //
    private boolean isTree(World world, int X, int Y, int Z) {
        final Block wood = world.getBlock(X, Y, Z);
        if (wood == null || !wood.isWood(world, X, Y, Z)) {
            return false;
        } else {
            int top = Y;
            for (int y = Y; y <= Y + 50; y++) {
                if (!world.getBlock(X, y, Z).isWood(world, X, y, Z)
                        && !world.getBlock(X, y, Z).isLeaves(world, X, y, Z)) {
                    top += y;
                    break;
                }
            }

            int leaves = 0;
            for (int xPos = X - 1; xPos <= X + 1; xPos++) {
                for (int yPos = Y; yPos <= top; yPos++) {
                    for (int zPos = Z - 1; zPos <= Z + 1; zPos++) {
                        if (world.getBlock(xPos, yPos, zPos).isLeaves(world, xPos, yPos, zPos)) leaves++;
                    }
                }
            }
            if (leaves >= 3) return true;
        }

        return false;
    }

    void chopTree(int X, int Y, int Z, EntityPlayer player, World world, ItemStack stack) {
        for (int xPos = X - 1; xPos <= X + 1; xPos++) {
            for (int yPos = Y; yPos <= Y + 1; yPos++) {
                for (int zPos = Z - 1; zPos <= Z + 1; zPos++) {
                    Block block = world.getBlock(xPos, yPos, zPos);
                    int meta = world.getBlockMetadata(xPos, yPos, zPos);
                    if (block.isWood(world, xPos, yPos, zPos)) {
                        world.setBlockToAir(xPos, yPos, zPos);
                        if (!player.capabilities.isCreativeMode) {
                            if (block.removedByPlayer(world, player, xPos, yPos, zPos, false)) {
                                block.onBlockDestroyedByPlayer(world, xPos, yPos, zPos, meta);
                            }
                            block.harvestBlock(world, player, xPos, yPos, zPos, meta);
                            block.onBlockHarvested(world, xPos, yPos, zPos, meta, player);
                            onBlockDestroyed(stack, world, block, xPos, yPos, zPos, player);
                        }
                        chopTree(xPos, yPos, zPos, player, world, stack);
                    } // else
                      // trimLeavs(xPos, yPos, zPos, player, world, stack);
                }
            }
        }
    }

    @SuppressWarnings("all")
    void trimLeavs(int X, int Y, int Z, EntityPlayer player, World world, ItemStack stack) {
        scedualUpdates(X, Y, Z, player, world, stack);
    }

    @SuppressWarnings("all")
    void scedualUpdates(int X, int Y, int Z, EntityPlayer player, World world, ItemStack stack) {
        for (int xPos = X - 15; xPos <= X + 15; xPos++) {
            for (int yPos = Y; yPos <= Y + 50; yPos++) {
                for (int zPos = Z - 15; zPos <= Z + 15; zPos++) {
                    Block block = world.getBlock(xPos, yPos, zPos);
                    if (block.isLeaves(world, xPos, yPos, zPos)) {
                        world.scheduleBlockUpdate(xPos, yPos, zPos, block, 2 + world.rand.nextInt(10));
                    }
                }
            }
        }
    }

    @Override
    public void tweakRender(IItemRenderer.ItemRenderType type) {
        GL11.glTranslated(0.34, 0.69, 0.1);
        GL11.glRotatef(90, 1, 0, 0);
        GL11.glRotatef(140, 0, -1, 0);
        GL11.glRotatef(180, 0, 0, 1);
        GL11.glScaled(0.7, 0.7, 0.7);

        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            GL11.glScalef(11, 11, 11);
            GL11.glRotatef(180, 0, 1, 0);
            GL11.glTranslated(-1.3, 0, -0.45);
        } else if (type == IItemRenderer.ItemRenderType.ENTITY) {
            GL11.glRotatef(90.5F, 0, 1, 0);
            GL11.glTranslated(0, 0, -0.9);
        }
    }

    // @SuppressWarnings({"rawtypes", "unchecked"})
    // @Override
    // public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean
    // extraInformation) {
    // if (InfoHelper.holdShiftForDetails(list)){
    // InfoHelper.addEnergyInfo(stack, list);
    // list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.draconicAxe2.txt"));
    // list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.draconicAxe3.txt"));
    // list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.draconicAxe4.txt"));
    // InfoHelper.addLore(stack, list);
    //
    // }else list.add(InfoHelper.ITC() + StatCollector.translateToLocal("info.draconicAxe1.txt"));
    // }

}
