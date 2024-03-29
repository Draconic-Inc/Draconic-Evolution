//package com.brandon3055.draconicevolution.items.tools;
//
//import codechicken.lib.raytracer.RayTracer;
//import com.brandon3055.brandonscore.items.ItemBCore;
//import com.brandon3055.brandonscore.utils.FacingUtils;
//import com.brandon3055.brandonscore.utils.InfoHelper;
//import com.brandon3055.brandonscore.utils.ItemNBTHelper;
//import com.brandon3055.brandonscore.api.hud.IHudDisplay;
//import com.brandon3055.draconicevolution.api.itemconfig_dep.*;
//import net.minecraft.block.BlockState;
//import net.minecraft.client.resources.I18n;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.ItemUseContext;
//import net.minecraft.util.*;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.BlockRayTraceResult;
//import net.minecraft.util.text.StringTextComponent;
//import net.minecraft.util.text.TextFormatting;
//import net.minecraft.world.World;
//
//import javax.annotation.Nullable;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by brandon3055 on 19/07/2016.
// */
//public class CreativeExchanger extends ItemBCore implements IConfigurableItem, IHudDisplay {
//
//    public CreativeExchanger(Properties properties) {
//        super(properties);
////        setMaxStackSize(1);
//    }
//
//    //region Basic
//
//    @Override
//    public boolean isFoil(ItemStack stack) {
//        return true;
//    }
//
////    @OnlyIn(Dist.CLIENT)
////    @Override
////    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
////        tooltip.add(TextFormatting.BLUE + "Use tool config gui to configure. " + TextFormatting.GOLD + "Key: \"" + KeyBindings.toolConfig.getDisplayName() + "\"");
////        tooltip.add(TextFormatting.BLUE + "To cycle config profile press: " + TextFormatting.GOLD + KeyBindings.toolProfileChange.getDisplayName());
////        tooltip.add(TextFormatting.BLUE + "Shift+Right Click to select block.");
////        tooltip.add(TextFormatting.BLUE + "Shift+Right Click air for clear mode.");
////        tooltip.add("");
////        tooltip.add(TextFormatting.AQUA + "Right Click to replace blocks in matching configuration.");
////        tooltip.add(TextFormatting.AQUA + "Left Click to replace single block.");
////        tooltip.add("");
////        tooltip.add(TextFormatting.DARK_RED + "This item may not be completely stable.");
////        tooltip.add(TextFormatting.DARK_RED + "Please be responsible and don't try anything stupid!");
////        tooltip.add(TextFormatting.DARK_PURPLE + "Added for BTM will probably be refined for survival later.");
////    }
//
//    //endregion
//
//    //region Interaction
//
//    @Override
//    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
//        ItemStack stack = player.getItemInHand(hand);
//        BlockRayTraceResult traceResult = RayTracer.retrace(player);
//
//        if (traceResult != null) {
//            return super.use(world, player, hand);
//        }
//
//        if (world.isClientSide) {
//            return super.use(world, player, hand);
//        }
//
//        if (player.isShiftKeyDown()) {
//            player.sendMessage(new StringTextComponent(TextFormatting.DARK_RED + "Clear Mode"), Util.NIL_UUID);
//            ItemNBTHelper.setString(stack, "BlockName", "");
//            ItemNBTHelper.setByte(stack, "BlockData", (byte) 0);
//            return super.use(world, player, hand);
//        }
//
//        return super.use(world, player, hand);
//    }
//
//    @Override
//    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
//        World world = context.getLevel();
//        PlayerEntity player = context.getPlayer();
//        BlockPos pos = context.getClickedPos();
//
//        if (world.isClientSide) {
//            return ActionResultType.PASS;
//        }
//
//        BlockState prevState = world.getBlockState(pos);
//
//        if (player.isShiftKeyDown()) {
//            String name = prevState.getBlock().getRegistryName().toString();
////            int data = prevState.getBlock().getMetaFromState(prevState);
//
//            ItemNBTHelper.setString(stack, "BlockName", name);
////            ItemNBTHelper.setByte(stack, "BlockData", (byte) data);
//
//            Item item = Item.byBlock(prevState.getBlock());
//
//            if (item != null) {
////                player.sendMessage(new StringTextComponent("Selected: " + new TranslationTextComponent(item.getTranslationKey(new ItemStack(item, 1, data)) + ".name").getFormattedText()).setStyle(new Style().setColor(TextFormatting.GREEN)));
//            }
//            return ActionResultType.SUCCESS;
//        }
//        else {
////            Block newBlock = Block.REGISTRY.getObject(new ResourceLocation(ItemNBTHelper.getString(stack, "BlockName", "")));
////            BlockState newState = newBlock.getStateFromMeta(ItemNBTHelper.getByte(stack, "BlockData", (byte) 0));
////            List<BlockPos> toReplace = getBlocksToReplace(stack, pos, world, side);
//
//            boolean replaced = false;
////            for (BlockPos replacePos : toReplace) {
////                world.setBlockState(replacePos, newState);
////                replaced = true;
////            }
//
////            if (replaced) {
////                if (newState.getBlock() == Blocks.AIR) {
////                    world.playEvent(2001, pos, Block.getStateId(prevState));
////                }
////                else {
////                    world.playEvent(2001, pos, Block.getStateId(newState));
////                }
////            }
//        }
//
//
//        return ActionResultType.SUCCESS;
//    }
//
//    @Override
//    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
////        Block newBlock = Block.REGISTRY.getObject(new ResourceLocation(ItemNBTHelper.getString(stack, "BlockName", "")));
////        if (newBlock == Blocks.AIR) {
////            if (player.world.isRemote) {
////                player.sendMessage(new StringTextComponent("[ERROR-404] Set Block not Found").setStyle(new Style().setColor(TextFormatting.DARK_RED)));
////            }
////            return false;
////        }
////        BlockState currentState = player.world.getBlockState(pos);
////        BlockState newState = newBlock.getStateFromMeta(ItemNBTHelper.getByte(stack, "BlockData", (byte) 0));
////        if (newState == currentState) {
////            return false;
////        }
////
////        player.world.setBlockState(pos, newState);
////        player.world.playEvent(2001, pos, Block.getStateId(newState));
////        player.world.notifyNeighborsOfStateChange(pos, newBlock, true);
//
//        return true;
//    }
//
//    @Override
//    public boolean isCorrectToolForDrops(BlockState blockIn) {
//        return false;
//    }
//
//    //endregion
//
//    //region Config
//
//    @Override
//    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry) {
//        registry.register(stack, new AOEConfigField("AOE", 0, 0, 20, "Sets the replace AOE for the item"));
//        registry.register(stack, new BooleanConfigField("replaceSame", false, "Only replace blocks that are the same as the one you right clicked."));
//        registry.register(stack, new BooleanConfigField("replaceVisible", false, "Only replace blocks that are not hidden under another block. Logic: If you click on the north side of a block. It will only replace blocks within the AOE that have air on the north side of them"));
//        registry.register(stack, new BooleanConfigField("fillLogic", false, "\"Fills\" the area. e.g. if you have an area of blocks with an air gap around them (Or another block type with ReplaceSame enabled) it will only replace blocks in that area."));
//
//        return registry;
//    }
//
//    @Override
//    public int getProfileCount(ItemStack stack) {
//        return 5;
//    }
//
//    //endregion
//
//    //region Block Selection
//
//    public static List<BlockPos> getBlocksToReplace(ItemStack stack, BlockPos pos, World world, Direction side) {
//        int range = ToolConfigHelper.getIntegerField("AOE", stack);
//        boolean replaceSame = ToolConfigHelper.getBooleanField("replaceSame", stack);
//        boolean replaceVisible = ToolConfigHelper.getBooleanField("replaceVisible", stack);
//        boolean fillLogic = ToolConfigHelper.getBooleanField("fillLogic", stack);
//        BlockState state = world.getBlockState(pos);
//
//        List<BlockPos> toReplace = new ArrayList<BlockPos>();
//        List<BlockPos> scanned = new ArrayList<BlockPos>();
//        toReplace.add(pos);
//        scanned.add(pos);
//
//        scanBlocks(world, pos, pos, state, side, range, replaceSame, replaceVisible, fillLogic, toReplace, scanned);
//
//        return toReplace;
//    }
//
//    private static void scanBlocks(World world, BlockPos pos, BlockPos origin, BlockState originState, Direction side, int range, boolean replaceSame, boolean replaceVisible, boolean fillLogic, List<BlockPos> toReplace, List<BlockPos> scanned) {
//
//        for (Direction dir : FacingUtils.getFacingsAroundAxis(side.getAxis())) {
//            BlockPos newPos = pos.relative(dir);
//
//            if (scanned.contains(newPos) || !isInRange(origin, newPos, range)) {
//                continue;
//            }
//
//            scanned.add(newPos);
//            BlockState state = world.getBlockState(newPos);
//
////            boolean validReplace = !world.isAirBlock(newPos) && (!replaceSame || state == originState) && (!replaceVisible || world.isAirBlock(newPos.offset(side)) || state.getBlock().isReplaceable(world, newPos.offset(side))) && (!fillLogic || state == originState);
//
////            if (validReplace) {
////                toReplace.add(newPos);
////            }
//
////            if (!fillLogic || validReplace) {
////                scanBlocks(world, newPos, origin, originState, side, range, replaceSame, replaceVisible, fillLogic, toReplace, scanned);
////            }
//        }
//    }
//
//    private static boolean isInRange(BlockPos origin, BlockPos pos, int range) {
//        BlockPos diff = pos.subtract(origin);
//        return Math.abs(diff.getX()) <= range && Math.abs(diff.getY()) <= range && Math.abs(diff.getZ()) <= range;
//    }
//
//    @Override
//    public void addDisplayData(@Nullable ItemStack stack, World world, @Nullable BlockPos pos, List<String> displayList) {
//        ItemConfigFieldRegistry registry = new ItemConfigFieldRegistry();
//        getFields(stack, registry);
//
////        Block newBlock = Block.REGISTRY.getObject(new ResourceLocation(ItemNBTHelper.getString(stack, "BlockName", "")));
//        String opMode = TextFormatting.DARK_RED + "Clear Mode";
//
////        if (newBlock != Blocks.AIR) {
////            opMode = TextFormatting.GREEN + "Block: " + TextFormatting.GOLD + I18n.format(newBlock.getStateFromMeta(ItemNBTHelper.getByte(stack, "BlockData", (byte) 0)).getBlock().getUnlocalizedName() + ".name");
////        }
//
//        displayList.add(TextFormatting.DARK_PURPLE + ToolConfigHelper.getProfileName(stack, ToolConfigHelper.getProfile(stack)));
//        displayList.add(opMode);
//
//        for (IItemConfigField field : registry.getFields()) {
//            displayList.add(InfoHelper.ITC() + I18n.get(field.getUnlocalizedName()) + ": " + InfoHelper.HITC() + field.getReadableValue());
//        }
//    }
//
//    //endregion
//}
