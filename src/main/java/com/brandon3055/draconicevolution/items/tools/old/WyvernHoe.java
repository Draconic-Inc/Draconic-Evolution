package com.brandon3055.draconicevolution.items.tools.old;

import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.draconicevolution.api.itemconfig_dep.AOEConfigField;
import com.brandon3055.draconicevolution.api.itemconfig_dep.BooleanConfigField;
import com.brandon3055.draconicevolution.api.itemconfig_dep.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemconfig_dep.ToolConfigHelper;
import com.brandon3055.draconicevolution.api.itemupgrade_dep.UpgradeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;

import static com.brandon3055.draconicevolution.items.ToolUpgrade.DIG_AOE;

/**
 * Created by brandon3055 on 5/06/2016.
 */
@Deprecated
public abstract class WyvernHoe extends ToolBase {
    protected int baseAOE;

    public WyvernHoe(Properties properties) {
        super(properties);
    }

    //    public WyvernHoe(/*double attackDamage, double attackSpeed*/) {
////        super(attackDamage, attackSpeed);
//        this.baseAOE = 2;
//    }

//    public WyvernHoe() {
////        super(ToolStats.WYV_HOE_ATTACK_DAMAGE, ToolStats.WYV_HOE_ATTACK_SPEED);
////        setEnergyStats(ToolStats.WYVERN_BASE_CAPACITY, 512000, 0);
//        this.baseAOE = 1;
//    }

    @Override
    public double getBaseAttackSpeedConfig() {
        return ToolStats.WYV_HOE_ATTACK_SPEED;
    }

    @Override
    public double getBaseAttackDamageConfig() {
        return ToolStats.WYV_HOE_ATTACK_DAMAGE;
    }

    @Override
    public void loadEnergyStats() {
        setEnergyStats(ToolStats.WYVERN_BASE_CAPACITY, 512000, 0);
    }

    //region Hoe


    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack stack = context.getItem();
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction facing = context.getFace();

        if (!hoeBlock(stack, player, world, pos, facing, context)) {
            if (world.getBlockState(pos).getBlock() != Blocks.FARMLAND) {
                return ActionResultType.FAIL;
            }
        } else {
            modifyEnergy(stack, -energyPerOperation);
        }

        if (player.isSneaking()) {
            return ActionResultType.SUCCESS;
        }

        int AOE = ToolConfigHelper.getIntegerField("digAOE", stack);
        boolean fill = ToolConfigHelper.getBooleanField("landFill", stack);

        Iterable<BlockPos> blocks = BlockPos.getAllInBoxMutable(pos.add(-AOE, 0, -AOE), pos.add(AOE, 0, AOE));

        for (BlockPos aoePos : blocks) {
            if (aoePos.equals(pos)) {
                continue;
            }

            if (!fill && (world.isAirBlock(aoePos) || !world.isAirBlock(aoePos.up()))) {
                continue;
            }


            boolean airOrReplaceable = world.isAirBlock(aoePos) || world.getBlockState(aoePos).getMaterial().isReplaceable();
            //TODO Solid Stuff
            boolean lowerBlockOk = world.getBlockState(aoePos.down()).isSolid() || world.getBlockState(aoePos.down()).getBlock() == Blocks.FARMLAND;

            if (fill && airOrReplaceable && lowerBlockOk && (player.abilities.isCreativeMode || player.inventory.hasItemStack(new ItemStack(Blocks.DIRT)))) {
                boolean canceled = ForgeEventFactory.onBlockPlace(player, new BlockSnapshot(world, aoePos, Blocks.DIRT.getDefaultState()), Direction.UP);

                if (!canceled && (player.abilities.isCreativeMode || InventoryUtils.consumeStack(new ItemStack(Blocks.DIRT), player.inventory))) {
                    world.setBlockState(aoePos, Blocks.DIRT.getDefaultState());
                }
            }

            boolean canDropAbove = world.getBlockState(aoePos.up()).getBlock() == Blocks.DIRT || world.getBlockState(aoePos.up()).getBlock() == Blocks.GRASS || world.getBlockState(aoePos.up()).getBlock() == Blocks.FARMLAND;
            boolean canRemoveAbove = canDropAbove || world.getBlockState(aoePos.up()).getMaterial().isReplaceable();
            boolean up2OK = world.isAirBlock(aoePos.up().up()) || world.getBlockState(aoePos.up().up()).getMaterial().isReplaceable();

            if (fill && !world.isAirBlock(aoePos.up()) && canRemoveAbove && up2OK) {
                if (canDropAbove) {
                    world.addEntity(new ItemEntity(world, player.posX, player.posY, player.posZ, new ItemStack(Blocks.DIRT)));
                }
                world.removeBlock(aoePos.up(), false);
            }

            if (hoeBlock(stack, player, world, aoePos, facing, context)) {
                modifyEnergy(stack, -energyPerOperation);
            }
        }

        return ActionResultType.SUCCESS;
    }

    private boolean hoeBlock(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, ItemUseContext context) {

        if (getEnergyStored(stack) < energyPerOperation && !player.abilities.isCreativeMode) {
            return false;
        }

        if (!player.canPlayerEdit(pos, face, stack)) {
            return false;
        } else {
            int hook = ForgeEventFactory.onHoeUse(context);
            if (hook != 0) {
                return hook > 0;
            }

            BlockState iblockstate = world.getBlockState(pos);
            Block block = iblockstate.getBlock();

            if (face != Direction.DOWN && world.isAirBlock(pos.up())) {
                if (block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
                    this.setBlock(player, world, pos, Blocks.FARMLAND.getDefaultState());
                    return true;
                }

                if (block == Blocks.DIRT) {
                    this.setBlock(player, world, pos, Blocks.FARMLAND.getDefaultState());
                } else if (block == Blocks.COARSE_DIRT) {
                    this.setBlock(player, world, pos, Blocks.DIRT.getDefaultState());
                }
            }

            return false;
        }
    }

    protected void setBlock(PlayerEntity player, World worldIn, BlockPos pos, BlockState state) {
        worldIn.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

        if (!worldIn.isRemote) {
            worldIn.setBlockState(pos, state, 11);
        }
    }

    //endregion

    //region Other

    @Override
    public List<String> getValidUpgrades(ItemStack stack) {
        List<String> list = super.getValidUpgrades(stack);
        list.add(DIG_AOE);
        return list;
    }

    @Override
    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry) {
        int maxAOE = baseAOE + UpgradeHelper.getUpgradeLevel(stack, DIG_AOE);
        registry.register(stack, new AOEConfigField("digAOE", 0, 0, maxAOE, "config.field.digAOE.description"));
        registry.register(stack, new BooleanConfigField("landFill", false, "config.field.landFill.description"));
        return registry;
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
        return enchantment.type == EnchantmentType.ALL;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 0;
    }

    //endregion

    //region Rendering
//
//    @Override
//    protected Set3<String, String, String> getTextureLocations() {
//        return null;
//    }

    //endregion
}
