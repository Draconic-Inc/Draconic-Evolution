package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.utils.InventoryUtils;
import com.brandon3055.draconicevolution.api.itemconfig.AOEConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.BooleanConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemconfig.ToolConfigHelper;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;

import java.util.List;

import static com.brandon3055.draconicevolution.items.ToolUpgrade.DIG_AOE;

/**
 * Created by brandon3055 on 5/06/2016.
 */
public class WyvernHoe extends ToolBase {
    protected int baseAOE;

    public WyvernHoe(double attackDamage, double attackSpeed) {
        super(attackDamage, attackSpeed);
        this.baseAOE = 2;
    }

    public WyvernHoe() {
        super(ToolStats.WYV_HOE_ATTACK_DAMAGE, ToolStats.WYV_HOE_ATTACK_SPEED);
        setEnergyStats(ToolStats.WYVERN_BASE_CAPACITY, 512000, 0);
        this.baseAOE = 1;
    }

    //region Hoe

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!hoeBlock(stack, player, world, pos, facing)) {
            if (world.getBlockState(pos).getBlock() != Blocks.FARMLAND) {
                return EnumActionResult.FAIL;
            }
        }
        else {
            modifyEnergy(stack, -energyPerOperation);
        }

        if (player.isSneaking()) {
            return EnumActionResult.SUCCESS;
        }

        int AOE = ToolConfigHelper.getIntegerField("digAOE", stack);
        boolean fill = ToolConfigHelper.getBooleanField("landFill", stack);

        Iterable<BlockPos> blocks = BlockPos.getAllInBox(pos.add(-AOE, 0, -AOE), pos.add(AOE, 0, AOE));

        for (BlockPos aoePos : blocks) {
            if (aoePos.equals(pos)) {
                continue;
            }

            if (!fill && (world.isAirBlock(aoePos) || !world.isAirBlock(aoePos.up()))) {
                continue;
            }

            boolean airOrReplaceable = world.isAirBlock(aoePos) || world.getBlockState(aoePos).getBlock().isReplaceable(world, aoePos);
            boolean lowerBlockOk = world.isSideSolid(aoePos.down(), EnumFacing.UP) || world.getBlockState(aoePos.down()).getBlock() == Blocks.FARMLAND;

            if (fill && airOrReplaceable && lowerBlockOk && (player.capabilities.isCreativeMode || player.inventory.hasItemStack(new ItemStack(Blocks.DIRT)))) {
                BlockEvent.PlaceEvent event = ForgeEventFactory.onPlayerBlockPlace(player, new BlockSnapshot(world, aoePos, Blocks.DIRT.getDefaultState()), EnumFacing.UP);

                if (!event.isCanceled() && (player.capabilities.isCreativeMode || InventoryUtils.conumeStack(new ItemStack(Blocks.DIRT), player.inventory))) {
                    world.setBlockState(aoePos, Blocks.DIRT.getDefaultState());
                }
            }

            boolean canRemoveAbove = world.getBlockState(aoePos.up()).getBlock() == Blocks.DIRT || world.getBlockState(aoePos.up()).getBlock() == Blocks.GRASS || world.getBlockState(aoePos.up()).getBlock() == Blocks.FARMLAND || world.getBlockState(aoePos.up()).getBlock().isReplaceable(world, aoePos.up());
            boolean up2OK = world.isAirBlock(aoePos.up().up()) || world.getBlockState(aoePos.up().up()).getBlock().isReplaceable(world, aoePos.up().up());

            if (fill && !world.isAirBlock(aoePos.up()) && canRemoveAbove && up2OK) {
                if (world.getBlockState(aoePos.up()).getBlock() == Blocks.DIRT) {
                    world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, new ItemStack(Blocks.DIRT)));
                }
                world.setBlockToAir(aoePos.up());
            }

            if (hoeBlock(stack, player, world, aoePos, facing)) {
                modifyEnergy(stack, -energyPerOperation);
            }
        }

        return EnumActionResult.SUCCESS;
    }

    private boolean hoeBlock(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing face) {

        if (getEnergyStored(stack) < energyPerOperation && !player.capabilities.isCreativeMode) {
            return false;
        }

        if (!player.canPlayerEdit(pos, face, stack)) {
            return false;
        }
        else {
            int hook = ForgeEventFactory.onHoeUse(stack, player, world, pos);
            if (hook != 0) {
                return hook > 0;
            }

            IBlockState iblockstate = world.getBlockState(pos);
            Block block = iblockstate.getBlock();

            if (face != EnumFacing.DOWN && world.isAirBlock(pos.up()))
            {
                if (block == Blocks.GRASS || block == Blocks.GRASS_PATH)
                {
                    this.setBlock(player, world, pos, Blocks.FARMLAND.getDefaultState());
                    return true;
                }

                if (block == Blocks.DIRT)
                {
                    switch (iblockstate.getValue(BlockDirt.VARIANT))
                    {
                        case DIRT:
                            this.setBlock(player, world, pos, Blocks.FARMLAND.getDefaultState());
                            return true;
                        case COARSE_DIRT:
                            this.setBlock(player, world, pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
                            return true;
                    }
                }
            }

            return false;
        }
    }

    protected void setBlock(EntityPlayer player, World worldIn, BlockPos pos, IBlockState state)
    {
        worldIn.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

        if (!worldIn.isRemote)
        {
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
    public boolean checkEnchantTypeValid(EnumEnchantmentType type) {
        return type == EnumEnchantmentType.ALL;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 0;
    }

    //endregion
}
