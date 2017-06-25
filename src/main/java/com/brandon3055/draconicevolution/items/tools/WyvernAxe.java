package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.brandonscore.lib.Set3;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.api.itemconfig.BooleanConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemconfig.ToolConfigHelper;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.entity.EntityLootCore;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Set;

/**
 * Created by brandon3055 on 2/06/2016.
 */
public class WyvernAxe extends MiningToolBase {

    public WyvernAxe(double attackDamage, double attackSpeed, Set effectiveBlocks) {
        super(attackDamage, attackSpeed, effectiveBlocks);
    }

    public WyvernAxe() {
        super(ToolStats.WYV_AXE_ATTACK_DAMAGE, ToolStats.WYV_AXE_ATTACK_SPEED, AXE_OVERRIDES);
        this.baseMiningSpeed = (float) ToolStats.WYV_AXE_MINING_SPEED;
        this.baseAOE = ToolStats.BASE_WYVERN_MINING_AOE;
        setEnergyStats(ToolStats.WYVERN_BASE_CAPACITY, 512000, 0);
        this.setHarvestLevel("axe", 10);
    }

    //region Item

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 7000;
    }

    //endregion

    //region Upgrade & Config

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
        return 2;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 0;
    }

    @Override
    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry) {
        registry.register(stack, new BooleanConfigField("showHarvestIndicator", true, "config.field.showHarvestIndicator.description"));
        return super.getFields(stack, registry);
    }

    //endregion

    //region Harvest

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.getBlockState(pos).getBlock().isWood(world, pos) && !player.isSneaking()) {
            player.setActiveHand(hand);
            if (!world.isRemote) {
                SelectionController controller = new SelectionController(player, stack, pos, true, 2, this);
                ProcessHandler.addProcess(controller);
            }
            return EnumActionResult.PASS;
        }


        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
    }

    protected static boolean isTree(World world, BlockPos pos) {
        IBlockState blockState = world.getBlockState(pos);
        if (!blockState.getBlock().isWood(world, pos)) {
            return false;
        }
        else {
            int treeTop = 0;
            for (int y = 0; y <= 50; y++) {
                IBlockState state = world.getBlockState(pos.add(0, y, 0));
                if (!state.getBlock().isWood(world, pos.add(0, y, 0)) && !state.getBlock().isLeaves(state, world, pos.add(0, y, 0))) {
                    treeTop = y;
                    break;
                }
            }

            Iterable<BlockPos> list = BlockPos.getAllInBox(pos.add(-1, 0, -1), pos.add(1, treeTop, 1));

            int leaves = 0;
            for (BlockPos checkPos : list) {
                IBlockState state = world.getBlockState(checkPos);
                if (state.getBlock().isLeaves(state, world, checkPos) && ++leaves >= 3) {
                    return true;
                }
            }
        }

        return false;
    }

    protected int getHarvestRange() {
        return 1;
    }

    protected int getMaxHarvest() {
        return 512;
    }

    //endregion

    //region SelectorControl

    private static class SelectionController implements IProcess, CollectorCallBack {

        private final EntityPlayer player;
        private final ItemStack stack;
        private final WyvernAxe axe;
        private final EnumHand hand;
        private boolean isDead = false;
        private TreeCollector collector;
        private boolean hasFinished = false;
        private boolean showHarvest = false;

        public SelectionController(EntityPlayer player, ItemStack stack, BlockPos clicked, boolean breakDown, int connectRad, WyvernAxe axe) {
            this.player = player;
            this.stack = stack;
            this.axe = axe;
            this.collector = new TreeCollector(player.world, breakDown, connectRad, stack, player, axe);
            this.collector.setCollectionCallback(this);
            this.collector.collectTree(clicked);
            this.hand = player.getActiveHand();
            LogHelper.dev("StartSelector");
            showHarvest = ToolConfigHelper.getBooleanField("showHarvestIndicator", stack);
        }

        @Override
        public void call(BlockPos pos) {
            if (showHarvest) {
                BCEffectHandler.spawnFX(DEParticles.AXE_SELECTION, player.world, new Vec3D(pos), new Vec3D(), 64D);
            }
        }

        @Override
        public void updateProcess() {
            if (!player.isEntityAlive() || player.getHeldItem(hand) != stack || collector.collected >= axe.getMaxHarvest() || collector.isCollectionComplete()) {
                collector.killCollector();
                LogHelper.dev("Finish " + collector.collected);
                finishHarvest();
                return;
            }

            if (player.isHandActive() && player.getActiveItemStack() == stack) {
                return;
            }

            finishHarvest();
        }

        @Override
        public boolean isDead() {
            return isDead;
        }

        private void finishHarvest() {
            if (hasFinished || !(player.world instanceof WorldServer)) {
                return;
            }

            DESoundHandler.playSoundFromServer(player.world, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1, 0.9F + player.world.rand.nextFloat() * 0.2F, false, 16);

            if (!collector.isCollectionComplete()) {
                collector.killCollector();
            }

            axe.modifyEnergy(stack, -collector.energyUsed);
            collector.energyUsed = 0;

            hasFinished = true;

            InventoryDynamic inventory = collector.getCollected();

            if (inventory.getSizeInventory() > 2) {
                EntityLootCore lootCore = new EntityLootCore(player.world, inventory);
                lootCore.setPosition(player.posX, player.posY, player.posZ);
                player.world.spawnEntity(lootCore);
            }
            else {
                for (int i = 0; i < inventory.getSizeInventory(); i++) {
                    ItemStack s = inventory.removeStackFromSlot(i);
                    if (s != null) {
                        EntityItem item = new EntityItem(player.world, player.posX, player.posY, player.posZ, s);
                        player.world.spawnEntity(item);
                    }
                }
            }
            isDead = true;
        }
    }

    //endregion

    //region Rendering

    @Override
    protected Set3<String, String, String> getTextureLocations() {
        return Set3.of("items/tools/wyvern_axe", "items/tools/obj/wyvern_axe", "models/item/tools/wyvern_axe.obj");
    }

    //endregion
}
