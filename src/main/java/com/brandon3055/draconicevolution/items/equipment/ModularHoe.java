package com.brandon3055.draconicevolution.items.equipment;

import codechicken.lib.util.ItemUtils;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.inventory.BlockToStackHelper;
import com.brandon3055.draconicevolution.api.IDraconicMelee;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.IntegerProperty;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.AOEData;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import com.brandon3055.draconicevolution.init.TechProperties;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ToolActions;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class ModularHoe extends HoeItem implements IModularTieredItem, IDraconicMelee, IModularEnergyItem {
    private final TechLevel techLevel;
    private final DETier itemTier;

    public ModularHoe(DETier tier, TechProperties props) {
        super(tier, 0, 0, props);
        this.techLevel = props.getTechLevel();
        this.itemTier = (DETier) getTier();
    }

    @Override
    public TechLevel getTechLevel() {
        return techLevel;
    }

    @Override
    public DETier getItemTier() {
        return itemTier;
    }

    @Override
    public double getSwingSpeedMultiplier() {
        return EquipCfg.hoeSwingSpeedMultiplier;
    }

    @Override
    public double getDamageMultiplier() {
        return EquipCfg.hoeDamageMultiplier;
    }

    @Override
    public ModuleHost createHostCapForRegistration(ItemStack stack) {
        ModuleHost host = IModularTieredItem.super.createHostCapForRegistration(stack);
        if (host instanceof ModuleHostImpl provider) {
            provider.addPropertyBuilder(props -> {
                AOEData aoe = host.getModuleData(ModuleTypes.AOE);
                if (aoe != null) {
                    props.add(new IntegerProperty("tool_aoe", aoe.aoe()).range(0, aoe.aoe()).setFormatter(ConfigProperty.IntegerFormatter.AOE));
                }
            });
        }
        return host;
    }

    @Override
    public @NotNull ModuleHostImpl instantiateHost(ItemStack stack) {
        ModuleHostImpl host = new ModuleHostImpl(techLevel, ModuleCfg.toolWidth(techLevel), ModuleCfg.toolHeight(techLevel), "hoe", ModuleCfg.removeInvalidModules);
        host.addCategories(ModuleCategory.TOOL_HOE);
        return host;
    }

    @Override
    public @NotNull ModularOPStorage instantiateOPStorage(ItemStack stack, Supplier<ModuleHost> hostSupplier) {
        return new ModularOPStorage(hostSupplier, EquipCfg.getBaseToolEnergy(techLevel), EquipCfg.getBaseToolTransfer(techLevel));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return IModularTieredItem.super.getDestroySpeed(stack, state);
    }

    @Override
    public float getBaseEfficiency() {
        return getTier().getSpeed();
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        addModularItemInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return damageBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return damageBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return damageBarColour(stack);
    }

    @Override
    public boolean canBeHurtBy(DamageSource source) {
        return source.is(DamageTypes.FELL_OUT_OF_WORLD);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.getAge() >= 0) {
            entity.setExtendedLifetime();
        }
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public boolean isEnchantable(ItemStack p_41456_) {
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();

        ModuleHost host = stack.getCapability(DECapabilities.Host.ITEM);
        assert host != null;
        int aoe = host.getModuleData(ModuleTypes.AOE, new AOEData(0)).aoe();
        if (host instanceof PropertyProvider) {
            if (((PropertyProvider) host).hasInt("tool_aoe")) {
                aoe = ((PropertyProvider) host).getInt("tool_aoe").getValue();
            }
        }

        Level level = context.getLevel();
        if (!attemptTillOp(context) && !level.getBlockState(context.getClickedPos()).is(Blocks.FARMLAND)) {
            return InteractionResult.FAIL;
        }

        Player player = context.getPlayer();
        if (player == null || player.isShiftKeyDown()) {
            return InteractionResult.SUCCESS;
        }

        BlockPos origin = context.getClickedPos();

        int aoe_range = aoe;
        BlockToStackHelper.startItemCapture();
        for (BlockPos aoePos : BlockPos.betweenClosed(origin.offset(-aoe_range, 0, -aoe_range), origin.offset(aoe_range, 0, aoe_range))) {
            if (aoePos.equals(origin)) {
                continue;
            }

            BlockState aoeState = level.getBlockState(aoePos);
            boolean airOrReplaceable = level.isEmptyBlock(aoePos) || aoeState.is(BlockTags.REPLACEABLE);
            boolean lowerBlockOk = level.getBlockState(aoePos.below()).isFaceSturdy(level, aoePos.below(), Direction.UP, SupportType.CENTER) || level.getBlockState(aoePos.below()).is(Blocks.FARMLAND);

            //Fill
            if (airOrReplaceable && lowerBlockOk && (player.getAbilities().instabuild || player.getInventory().contains(new ItemStack(Items.DIRT)))) {
                boolean canceled = EventHooks.onBlockPlace(player, BlockSnapshot.create(level.dimension(), level, aoePos), Direction.UP);

                if (!canceled && (player.getAbilities().instabuild || consumeItem(Items.DIRT, player.getInventory()))) {
                    level.setBlockAndUpdate(aoePos, Blocks.DIRT.defaultBlockState());
                }
            }

            boolean canDropAbove = level.getBlockState(aoePos.above()).is(Blocks.DIRT) || level.getBlockState(aoePos.above()).is(Blocks.GRASS_BLOCK) || level.getBlockState(aoePos.above()).is(Blocks.FARMLAND);
            boolean canRemoveAbove = canDropAbove || level.getBlockState(aoePos.above()).is(BlockTags.REPLACEABLE);
            boolean up2OK = level.isEmptyBlock(aoePos.above().above()) || level.getBlockState(aoePos.above().above()).is(BlockTags.REPLACEABLE);

            if (!level.isEmptyBlock(aoePos.above()) && canRemoveAbove && up2OK) {
                if (!level.isClientSide && canDropAbove) {
                    level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), new ItemStack(Blocks.DIRT)));
                }
                level.removeBlock(aoePos.above(), false);
            }
            attemptTillOp(updateContext(context, aoePos));
        }

        Set<ItemStack> drops = BlockToStackHelper.collectAndEndCapture();
        if (!level.isClientSide) {
            for (ItemStack drop : drops) {
                ItemUtils.dropItem(drop, level, Vector3.fromEntityCenter(player));
            }
        }

        return InteractionResult.SUCCESS;
    }

    public boolean attemptTillOp(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState toolModifiedState = level.getBlockState(blockpos).getToolModifiedState(context, ToolActions.HOE_TILL, false);
        Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = toolModifiedState == null ? null : Pair.of(ctx -> true, changeIntoState(toolModifiedState));
        if (pair == null) {
            return false;
        }

        Predicate<UseOnContext> predicate = pair.getFirst();
        Consumer<UseOnContext> consumer = pair.getSecond();
        if (predicate.test(context)) {
            Player player = context.getPlayer();
            level.playSound(player, blockpos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!level.isClientSide) {
                consumer.accept(context);
                if (player != null) {
                    context.getItemInHand().hurtAndBreak(1, player, (p_150845_) -> {
                        p_150845_.broadcastBreakEvent(context.getHand());
                    });
                }
            }

            return true;
        }

        return false;
    }

    public boolean consumeItem(Item target, Inventory inventory) {
        for (ItemStack item : inventory.items) {
            if (!item.isEmpty() && item.is(target)) {
                item.shrink(1);
                inventory.setChanged();
                return true;
            }
        }

        return false;
    }

    private static UseOnContext updateContext(UseOnContext context, BlockPos newPos) {
        BlockPos pos = context.getClickedPos();
        Vec3 newLocation = context.getClickLocation().add(newPos.getX() - pos.getX(), newPos.getY() - pos.getY(), newPos.getZ() - pos.getZ());
        return new UseOnContext(context.getLevel(), context.getPlayer(), context.getHand(), context.getItemInHand(), new BlockHitResult(newLocation, context.getClickedFace(), newPos, context.isInside()));
    }
}
