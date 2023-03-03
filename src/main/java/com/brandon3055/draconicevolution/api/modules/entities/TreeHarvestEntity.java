package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.IntegerProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleHelper;
import com.brandon3055.draconicevolution.api.modules.data.TreeHarvestData;
import com.brandon3055.draconicevolution.api.modules.entities.logic.ForestHarvestHandler;
import com.brandon3055.draconicevolution.api.modules.entities.logic.IHarvestHandler;
import com.brandon3055.draconicevolution.api.modules.entities.logic.TreeHarvestHandler;
import com.brandon3055.draconicevolution.api.modules.lib.EntityOverridesItemUse;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

public class TreeHarvestEntity extends ModuleEntity<TreeHarvestData> implements EntityOverridesItemUse {

    private IHarvestHandler activeHandler = null;
    private InventoryDynamic itemBuffer = new InventoryDynamic();

    private BooleanProperty harvestLeaves;
    private IntegerProperty harvestRange;

    public TreeHarvestEntity(Module<TreeHarvestData> module) {
        super(module);
        addProperty(harvestLeaves = new BooleanProperty("tree_harvest_mod.leaves", true).setFormatter(ConfigProperty.BooleanFormatter.YES_NO));
        addProperty(harvestRange = new IntegerProperty("tree_harvest_mod.range", module.getData().range()).setFormatter(ConfigProperty.IntegerFormatter.RAW).range(0, module.getData().range()));
    }

    private void useTick(LivingEntityUseItemEvent.Tick event) {
        if (activeHandler == null || !(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack stack = event.getItem();
        LazyOptional<IOPStorage> optional = stack.getCapability(DECapabilities.OP_STORAGE);
        if (!optional.isPresent()) {
            return;
        }
        IOPStorage storage = optional.orElseThrow(IllegalStateException::new);

        activeHandler.tick(player.level, player, stack, storage, itemBuffer);

        if (itemBuffer.getStacks().size() > 8) {
            dropContents(player, stack);
        }
    }

    private void endUse(LivingEntityUseItemEvent event) {
        if (activeHandler != null && event.getEntity() instanceof ServerPlayer player) {
            activeHandler.stop(player.level, player);
        }
        activeHandler = null;

        dropContents(event.getEntity(), event.getItem());
    }

    private void dropContents(Entity entity, ItemStack stack) {
        if (entity instanceof ServerPlayer serverPlayer && !itemBuffer.isEmpty()) {
            ModuleHelper.handleItemCollection(serverPlayer, host, EnergyUtils.getStorage(stack), itemBuffer);
            itemBuffer.clearContent();
        }
    }

    @Override
    public void onEntityUseItem(LivingEntityUseItemEvent useEvent) {
        if (useEvent.isCanceled()) return;
        if (useEvent instanceof LivingEntityUseItemEvent.Start event) {
            event.setDuration(72000);
        } else if (useEvent instanceof LivingEntityUseItemEvent.Tick event) {
            useTick(event);
        } else if (useEvent instanceof LivingEntityUseItemEvent.Stop || useEvent instanceof LivingEntityUseItemEvent.Finish) {
            endUse(useEvent);
        }
    }

    @Override
    public void onPlayerInteractEvent(PlayerInteractEvent playerEvent) {
        if (playerEvent.isCanceled()) return;
        TreeHarvestData data = getModule().getData();
        if (playerEvent instanceof PlayerInteractEvent.RightClickItem event && activeHandler == null) {
            if (data.range() <= 0) return;
            if (event.getPlayer() instanceof ServerPlayer player) {
                activeHandler = new ForestHarvestHandler(data.speed(), harvestRange.getValue(), harvestLeaves.getValue());
                activeHandler.start(event.getPos(), event.getWorld(), player);
            }
        } else if (playerEvent instanceof PlayerInteractEvent.RightClickBlock event) {
            if (event.getPlayer() instanceof ServerPlayer player) {
                activeHandler = new TreeHarvestHandler(data.speed(), event.getHitVec().getDirection(), harvestLeaves.getValue());
                activeHandler.start(event.getPos(), event.getWorld(), player);
            }
        } else {
            return;
        }

        playerEvent.setCanceled(true);
        playerEvent.getPlayer().startUsingItem(playerEvent.getHand());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addHostHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(new TranslatableComponent("module." + MODID + ".tree_harvest.single").withStyle(ChatFormatting.DARK_GRAY));
            if (getModule().getData().range() > 0) {
                tooltip.add(new TranslatableComponent("module." + MODID + ".tree_harvest.area").withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void modifyFirstPersonUsingPose(RenderHandEvent event, boolean leftHand) {
        PoseStack poseStack = event.getPoseStack();
        Player player = Minecraft.getInstance().player;
        int handOffset = !leftHand ? 1 : -1;

        poseStack.translate((float) handOffset * -0.2785682F, 0.18344387F, 0.15731531F);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-13.935F));
        poseStack.mulPose(Vector3f.YP.rotationDegrees((float) handOffset * 35.3F));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees((float) handOffset * -9.785F));
        float drawTime = (float) 72000 - ((float) player.getUseItemRemainingTicks() - event.getPartialTicks() + 1.0F);
        float charge = drawTime / 20.0F;
        charge = (charge * charge + charge * 2.0F) / 3.0F;
        if (charge > 1.0F) {
            charge = 1.0F;
        }

        if (charge > 0.1F) {
            float f15 = Mth.sin((drawTime - 0.1F) * 1.3F);
            float f18 = charge - 0.1F;
            float animOffset = f15 * f18;
            poseStack.translate(animOffset * 0.0F, animOffset * 0.004F, animOffset * 0.0F);
        }

        poseStack.translate(charge * 0.0F, charge * 0.0F, charge * 0.04F);
        poseStack.scale(1.0F, 1.0F, 1.0F + charge * 0.2F);
        poseStack.mulPose(Vector3f.YN.rotationDegrees((float) handOffset * 45.0F));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void modifyPlayerModelPose(Player player, PlayerModel<?> model, boolean leftHand) {
        if (!leftHand) {
            model.rightArm.yRot = -0.1F + model.head.yRot;
            model.leftArm.yRot = 0.1F + model.head.yRot + 0.4F;
            model.rightArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
            model.leftArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
        } else {
            model.rightArm.yRot = -0.1F + model.head.yRot - 0.4F;
            model.leftArm.yRot = 0.1F + model.head.yRot;
            model.rightArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
            model.leftArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
        }

        model.leftPants.copyFrom(model.leftLeg);
        model.rightPants.copyFrom(model.rightLeg);
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightSleeve.copyFrom(model.rightArm);
        model.jacket.copyFrom(model.body);
    }

    @Override
    protected CompoundTag writeExtraData(CompoundTag nbt) {
        itemBuffer.writeToNBT(nbt);
        return super.writeExtraData(nbt);
    }

    @Override
    protected void readExtraData(CompoundTag nbt) {
        itemBuffer.readFromNBT(nbt);
        super.readExtraData(nbt);
    }
}
