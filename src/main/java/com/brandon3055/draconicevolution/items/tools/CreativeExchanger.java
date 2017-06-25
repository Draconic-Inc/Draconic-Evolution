package com.brandon3055.draconicevolution.items.tools;

import codechicken.lib.raytracer.RayTracer;
import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.api.IHudDisplay;
import com.brandon3055.draconicevolution.api.itemconfig.*;
import com.brandon3055.draconicevolution.client.keybinding.KeyBindings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 19/07/2016.
 */
public class CreativeExchanger extends ItemBCore implements IConfigurableItem, IHudDisplay {

    public CreativeExchanger() {
        setMaxStackSize(1);
    }

    //region Basic

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        tooltip.add(TextFormatting.BLUE + "Use tool config gui to configure. " + TextFormatting.GOLD + "Key: \"" + KeyBindings.toolConfig.getDisplayName() + "\"");
        tooltip.add(TextFormatting.BLUE + "To cycle config profile press: " + TextFormatting.GOLD + KeyBindings.toolProfileChange.getDisplayName());
        tooltip.add(TextFormatting.BLUE + "Shift+Right Click to select block.");
        tooltip.add(TextFormatting.BLUE + "Shift+Right Click air for clear mode.");
        tooltip.add("");
        tooltip.add(TextFormatting.AQUA + "Right Click to replace blocks in matching configuration.");
        tooltip.add(TextFormatting.AQUA + "Left Click to replace single block.");
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_RED + "This item may not be completely stable.");
        tooltip.add(TextFormatting.DARK_RED + "Please be responsible and don't try anything stupid!");
        tooltip.add(TextFormatting.DARK_PURPLE + "Added for BTM will probably be refined for survival later.");
    }

    //endregion

    //region Interaction

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        RayTraceResult traceResult = RayTracer.retrace(player);

        if (traceResult != null && traceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
            return super.onItemRightClick(world, player, hand);
        }

        if (world.isRemote) {
            return super.onItemRightClick(world, player, hand);
        }

        if (player.isSneaking()) {
            player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "Clear Mode"));
            ItemNBTHelper.setString(stack, "BlockName", "");
            ItemNBTHelper.setByte(stack, "BlockData", (byte) 0);
            return super.onItemRightClick(world, player, hand);
        }

        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }

        ItemStack stack = player.getHeldItem(hand);
        IBlockState prevState = world.getBlockState(pos);

        if (player.isSneaking()) {
            String name = Block.REGISTRY.getNameForObject(prevState.getBlock()).toString();
            int data = prevState.getBlock().getMetaFromState(prevState);

            ItemNBTHelper.setString(stack, "BlockName", name);
            ItemNBTHelper.setByte(stack, "BlockData", (byte) data);

            Item item = Item.getItemFromBlock(prevState.getBlock());

            if (item != null) {
                player.sendMessage(new TextComponentString("Selected: " + new TextComponentTranslation(item.getUnlocalizedName(new ItemStack(item, 1, data)) + ".name").getFormattedText()).setStyle(new Style().setColor(TextFormatting.GREEN)));
            }
            return EnumActionResult.SUCCESS;
        }
        else {
            Block newBlock = Block.REGISTRY.getObject(new ResourceLocation(ItemNBTHelper.getString(stack, "BlockName", "")));
            IBlockState newState = newBlock.getStateFromMeta(ItemNBTHelper.getByte(stack, "BlockData", (byte) 0));
            List<BlockPos> toReplace = getBlocksToReplace(stack, pos, world, side);

            boolean replaced = false;
            for (BlockPos replacePos : toReplace) {
                world.setBlockState(replacePos, newState);
                replaced = true;
            }

            if (replaced) {
                if (newState.getBlock() == Blocks.AIR) {
                    world.playEvent(2001, pos, Block.getStateId(prevState));
                }
                else {
                    world.playEvent(2001, pos, Block.getStateId(newState));
                }
            }
        }


        return EnumActionResult.SUCCESS;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        Block newBlock = Block.REGISTRY.getObject(new ResourceLocation(ItemNBTHelper.getString(stack, "BlockName", "")));
        if (newBlock == Blocks.AIR) {
            if (player.world.isRemote) {
                player.sendMessage(new TextComponentString("[ERROR-404] Set Block not Found").setStyle(new Style().setColor(TextFormatting.DARK_RED)));
            }
            return false;
        }
        IBlockState currentState = player.world.getBlockState(pos);
        IBlockState newState = newBlock.getStateFromMeta(ItemNBTHelper.getByte(stack, "BlockData", (byte) 0));
        if (newState == currentState) {
            return false;
        }

        player.world.setBlockState(pos, newState);
        player.world.playEvent(2001, pos, Block.getStateId(newState));
        player.world.notifyNeighborsOfStateChange(pos, newBlock, true);

        return true;
    }

    @Override
    public boolean canHarvestBlock(IBlockState blockIn) {
        return false;
    }

    //endregion

    //region Config

    @Override
    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry) {
        registry.register(stack, new AOEConfigField("AOE", 0, 0, 20, "Sets the replace AOE for the item"));
        registry.register(stack, new BooleanConfigField("replaceSame", false, "Only replace blocks that are the same as the one you right clicked."));
        registry.register(stack, new BooleanConfigField("replaceVisible", false, "Only replace blocks that are not hidden under another block. Logic: If you click on the north side of a block. It will only replace blocks within the AOE that have air on the north side of them"));
        registry.register(stack, new BooleanConfigField("fillLogic", false, "\"Fills\" the area. e.g. if you have an area of blocks with an air gap around them (Or another block type with ReplaceSame enabled) it will only replace blocks in that area."));

        return registry;
    }

    @Override
    public int getProfileCount(ItemStack stack) {
        return 5;
    }

    //endregion

    //region Block Selection

    public static List<BlockPos> getBlocksToReplace(ItemStack stack, BlockPos pos, World world, EnumFacing side) {
        int range = ToolConfigHelper.getIntegerField("AOE", stack);
        boolean replaceSame = ToolConfigHelper.getBooleanField("replaceSame", stack);
        boolean replaceVisible = ToolConfigHelper.getBooleanField("replaceVisible", stack);
        boolean fillLogic = ToolConfigHelper.getBooleanField("fillLogic", stack);
        IBlockState state = world.getBlockState(pos);

        List<BlockPos> toReplace = new ArrayList<BlockPos>();
        List<BlockPos> scanned = new ArrayList<BlockPos>();
        toReplace.add(pos);
        scanned.add(pos);

        scanBlocks(world, pos, pos, state, side, range, replaceSame, replaceVisible, fillLogic, toReplace, scanned);

        return toReplace;
    }

    private static void scanBlocks(World world, BlockPos pos, BlockPos origin, IBlockState originState, EnumFacing side, int range, boolean replaceSame, boolean replaceVisible, boolean fillLogic, List<BlockPos> toReplace, List<BlockPos> scanned) {

        for (EnumFacing dir : FacingUtils.getFacingsAroundAxis(side.getAxis())) {
            BlockPos newPos = pos.offset(dir);

            if (scanned.contains(newPos) || !isInRange(origin, newPos, range)) {
                continue;
            }

            scanned.add(newPos);
            IBlockState state = world.getBlockState(newPos);

            boolean validReplace = !world.isAirBlock(newPos) && (!replaceSame || state == originState) && (!replaceVisible || world.isAirBlock(newPos.offset(side)) || state.getBlock().isReplaceable(world, newPos.offset(side))) && (!fillLogic || state == originState);

            if (validReplace) {
                toReplace.add(newPos);
            }

            if (!fillLogic || validReplace) {
                scanBlocks(world, newPos, origin, originState, side, range, replaceSame, replaceVisible, fillLogic, toReplace, scanned);
            }
        }
    }

    private static boolean isInRange(BlockPos origin, BlockPos pos, int range) {
        BlockPos diff = pos.subtract(origin);
        return Math.abs(diff.getX()) <= range && Math.abs(diff.getY()) <= range && Math.abs(diff.getZ()) <= range;
    }

    @Override
    public void addDisplayData(@Nullable ItemStack stack, World world, @Nullable BlockPos pos, List<String> displayList) {
        ItemConfigFieldRegistry registry = new ItemConfigFieldRegistry();
        getFields(stack, registry);

        Block newBlock = Block.REGISTRY.getObject(new ResourceLocation(ItemNBTHelper.getString(stack, "BlockName", "")));
        String opMode = TextFormatting.DARK_RED + "Clear Mode";

        if (newBlock != Blocks.AIR) {
            opMode = TextFormatting.GREEN + "Block: " + TextFormatting.GOLD + I18n.format(newBlock.getStateFromMeta(ItemNBTHelper.getByte(stack, "BlockData", (byte) 0)).getBlock().getUnlocalizedName() + ".name");
        }

        displayList.add(TextFormatting.DARK_PURPLE + ToolConfigHelper.getProfileName(stack, ToolConfigHelper.getProfile(stack)));
        displayList.add(opMode);

        for (IItemConfigField field : registry.getFields()) {
            displayList.add(InfoHelper.ITC() + I18n.format(field.getUnlocalizedName()) + ": " + InfoHelper.HITC() + field.getReadableValue());
        }
    }

    //endregion
}
