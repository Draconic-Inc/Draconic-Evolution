package com.brandon3055.draconicevolution.items.tools.old;

import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.brandonscore.lib.Pair;
import com.brandon3055.draconicevolution.api.itemconfig_dep.BooleanConfigField;
import com.brandon3055.draconicevolution.api.itemconfig_dep.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemconfig_dep.ToolConfigHelper;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.client.DETextures;
import com.brandon3055.draconicevolution.items.tools.CollectorCallBack;
import com.brandon3055.draconicevolution.items.tools.TreeCollector;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/**
 * Created by brandon3055 on 2/06/2016.
 */
@Deprecated
public class WyvernAxe extends MiningToolBase {
    public WyvernAxe(Properties properties) {
        super(properties, MiningToolBase.AXE_OVERRIDES);
    }

    //    public WyvernAxe(/*double attackDamage, double attackSpeed, */Set effectiveBlocks) {
//        super(/*attackDamage, attackSpeed, */effectiveBlocks);
//    }
//
//    public WyvernAxe() {
//        super(/*ToolStats.WYV_AXE_ATTACK_DAMAGE, ToolStats.WYV_AXE_ATTACK_SPEED, */AXE_OVERRIDES);
////        this.baseMiningSpeed = (float) ToolStats.WYV_AXE_MINING_SPEED;
////        this.baseAOE = ToolStats.BASE_WYVERN_MINING_AOE;
////        setEnergyStats(ToolStats.WYVERN_BASE_CAPACITY, 512000, 0);
//        this.setHarvestLevel("axe", 10);
//    }

    @Override
    public double getBaseMinSpeedConfig() {
        return ToolStats.WYV_AXE_MINING_SPEED;
    }

    @Override
    public int getBaseMinAOEConfig() {
        return ToolStats.BASE_WYVERN_MINING_AOE;
    }

    @Override
    public double getBaseAttackSpeedConfig() {
        return ToolStats.WYV_AXE_ATTACK_SPEED;
    }

    @Override
    public double getBaseAttackDamageConfig() {
        return ToolStats.WYV_AXE_ATTACK_DAMAGE;
    }

    @Override
    public void loadEnergyStats() {
        setEnergyStats(ToolStats.WYVERN_BASE_CAPACITY, 512000, 0);
    }

    //region Item

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
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
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();

        ItemStack stack = context.getItem();
        if (world.getBlockState(pos).getMaterial() == Material.WOOD && !player.isSneaking()) {
            player.setActiveHand(context.getHand());
            if (!world.isRemote) {
                SelectionController controller = new SelectionController(player, stack, pos, true, 2, this);
                ProcessHandler.addProcess(controller);
            }
            return ActionResultType.PASS;
        }
        return super.onItemUse(context);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
    }

    protected static boolean isTree(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getMaterial() != Material.WOOD) {
            return false;
        }
        else {
            int treeTop = 0;
            for (int y = 0; y <= 50; y++) {
                BlockState state = world.getBlockState(pos.add(0, y, 0));
                if (state.getMaterial() != Material.WOOD && state.getMaterial() != Material.LEAVES) {
                    treeTop = y;
                    break;
                }
            }

            Iterable<BlockPos> list = BlockPos.getAllInBoxMutable(pos.add(-1, 0, -1), pos.add(1, treeTop, 1));

            int leaves = 0;
            for (BlockPos checkPos : list) {
                BlockState state = world.getBlockState(checkPos);
                if (state.getMaterial() == Material.LEAVES && ++leaves >= 3) {
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

        private final PlayerEntity player;
        private final ItemStack stack;
        private final WyvernAxe axe;
        private final Hand hand;
        private boolean isDead = false;
        private TreeCollector collector;
        private boolean hasFinished = false;
        private boolean showHarvest = false;

        public SelectionController(PlayerEntity player, ItemStack stack, BlockPos clicked, boolean breakDown, int connectRad, WyvernAxe axe) {
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
                //TODO Particles
//                BCEffectHandler.spawnFX(DEParticles.AXE_SELECTION, player.world, new Vec3D(pos), new Vec3D(), 64D);
            }
        }

        @Override
        public void updateProcess() {
            if (!player.isAlive() || player.getHeldItem(hand) != stack || collector.collected >= axe.getMaxHarvest() || collector.isCollectionComplete()) {
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
            if (hasFinished || !(player.world instanceof ServerWorld)) {
                return;
            }

//            DESounds.playSoundFromServer(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1, 0.9F + player.world.rand.nextFloat() * 0.2F, false, 16);

            if (!collector.isCollectionComplete()) {
                collector.killCollector();
            }

            axe.modifyEnergy(stack, -collector.energyUsed);
            collector.energyUsed = 0;

            hasFinished = true;

            InventoryDynamic inventory = collector.getCollected();

            if (inventory.getSizeInventory() > 2) {
//                EntityLootCore lootCore = new EntityLootCore(player.world, inventory);
//                lootCore.setPosition(player.getPosX(), player.getPosY(), player.getPosZ());
//                player.world.addEntity(lootCore); TODO Entity Stuff
            }
            else {
                for (int i = 0; i < inventory.getSizeInventory(); i++) {
                    ItemStack s = inventory.removeStackFromSlot(i);
                    if (s != null) {
                        ItemEntity item = new ItemEntity(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), s);
                        player.world.addEntity(item);
                    }
                }
            }
            isDead = true;
        }
    }

    //endregion

    //region Rendering

    @Override
    public Pair<TextureAtlasSprite, ResourceLocation> getModels(ItemStack stack) {
        return new Pair<>(DETextures.WYVERN_AXE, new ResourceLocation("draconicevolution", "models/item/tools/wyvern_axe.obj"));
    }

    //endregion
}
