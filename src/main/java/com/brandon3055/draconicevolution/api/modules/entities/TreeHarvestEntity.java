package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.brandonscore.api.BCStreamCodec;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.brandonscore.utils.EnergyUtils;
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
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.init.ItemData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

public class TreeHarvestEntity extends ModuleEntity<TreeHarvestData> implements EntityOverridesItemUse {

    private static final Map<UUID, IHarvestHandler> activeHandlers = new HashMap<>();

    private InventoryDynamic itemBuffer = new InventoryDynamic();
    private BooleanProperty harvestLeaves = new BooleanProperty("tree_harvest_mod.leaves", true).setFormatter(ConfigProperty.BooleanFormatter.YES_NO);
    private IntegerProperty harvestRange = new IntegerProperty("tree_harvest_mod.range", module.getData().range()).setFormatter(ConfigProperty.IntegerFormatter.RAW).range(0, module.getData().range());

    public static final Codec<TreeHarvestEntity> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DEModules.codec().fieldOf("module").forGetter(ModuleEntity::getModule),
            Codec.INT.fieldOf("gridx").forGetter(ModuleEntity::getGridX),
            Codec.INT.fieldOf("gridy").forGetter(ModuleEntity::getGridY),
            InventoryDynamic.CODEC.fieldOf("item_buffer").forGetter(e -> e.itemBuffer),
            BooleanProperty.CODEC.fieldOf("harvest_leaves").forGetter(e -> e.harvestLeaves),
            IntegerProperty.CODEC.fieldOf("harvest_range").forGetter(e -> e.harvestRange)
    ).apply(builder, TreeHarvestEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TreeHarvestEntity> STREAM_CODEC = BCStreamCodec.composite(
            DEModules.streamCodec(), ModuleEntity::getModule,
            ByteBufCodecs.INT, ModuleEntity::getGridX,
            ByteBufCodecs.INT, ModuleEntity::getGridY,
            InventoryDynamic.STREAM_CODEC, e -> e.itemBuffer,
            BooleanProperty.STREAM_CODEC, e -> e.harvestLeaves,
            IntegerProperty.STREAM_CODEC, e -> e.harvestRange,
            TreeHarvestEntity::new
    );

    public TreeHarvestEntity(Module<TreeHarvestData> module) {
        super(module);
    }

    TreeHarvestEntity(Module<?> module, int gridX, int gridY, InventoryDynamic itemBuffer, BooleanProperty harvestLeaves, IntegerProperty harvestRange) {
        super((Module<TreeHarvestData>) module, gridX, gridY);
        this.itemBuffer = itemBuffer;
        this.harvestLeaves = harvestLeaves;
        this.harvestRange = harvestRange;
    }

    @Override
    public ModuleEntity<?> copy() {
        return new TreeHarvestEntity(module, getGridX(), getGridY(), itemBuffer.copy(), harvestLeaves.copy(), harvestRange.copy());
    }

    @Override
    public void getEntityProperties(List<ConfigProperty> properties) {
        super.getEntityProperties(properties);
        properties.add(harvestLeaves);
        properties.add(harvestRange);
    }

    private void useTick(LivingEntityUseItemEvent.Tick event) {
        IHarvestHandler activeHandler = activeHandlers.get(event.getEntity().getUUID());
        if (activeHandler == null || !(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack stack = event.getItem();
        IOPStorage storage = stack.getCapability(CapabilityOP.ITEM);
        if (storage == null) {
            return;
        }

        activeHandler.tick(player.level(), player, stack, storage, itemBuffer);
        markDirty();

        if (itemBuffer.getStacks().size() > 8) {
            dropContents(player, stack);
        }
    }

    private void endUse(LivingEntityUseItemEvent event) {
        IHarvestHandler activeHandler = activeHandlers.get(event.getEntity().getUUID());
        if (activeHandler != null && event.getEntity() instanceof ServerPlayer player) {
            activeHandler.stop(player.level(), player);
        }
        activeHandlers.remove(event.getEntity().getUUID());

        dropContents(event.getEntity(), event.getItem());
    }

    private void dropContents(Entity entity, ItemStack stack) {
        if (entity instanceof ServerPlayer serverPlayer && !itemBuffer.isEmpty()) {
            ModuleHelper.handleItemCollection(serverPlayer, host, EnergyUtils.getStorage(stack), itemBuffer);
            itemBuffer.clearContent();
            markDirty();
        }
    }

    @Override
    public void onEntityUseItem(LivingEntityUseItemEvent useEvent) {
        if (useEvent instanceof ICancellableEvent cancellable && cancellable.isCanceled()) return;
        if (useEvent instanceof LivingEntityUseItemEvent.Start event) {
            event.setDuration(72000);
        } else if (useEvent instanceof LivingEntityUseItemEvent.Tick event) {
            useTick(event);
        } else if (useEvent instanceof LivingEntityUseItemEvent.Stop || useEvent instanceof LivingEntityUseItemEvent.Finish) {
            endUse(useEvent);
        }
    }

    @Override
    public void onPlayerInteractEvent(PlayerInteractEvent.RightClickItem event) {
        if (event.isCanceled()) return;
        TreeHarvestData data = getModule().getData();
        IHarvestHandler activeHandler = activeHandlers.get(event.getEntity().getUUID());
        if (activeHandler == null) {
            if (data.range() <= 0) return;
            if (event.getEntity() instanceof ServerPlayer player) {
                activeHandler = new ForestHarvestHandler(data.speed(), harvestRange.getValue(), harvestLeaves.getValue());
                activeHandler.start(event.getPos(), event.getLevel(), player);
                activeHandlers.put(event.getEntity().getUUID(), activeHandler);
                markDirty();
            }
        } else {
            return;
        }

        event.setCanceled(true);
        event.getEntity().startUsingItem(event.getHand());
    }

    @Override
    public void onPlayerInteractEvent(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled()) return;
        TreeHarvestData data = getModule().getData();
        if (event.getEntity() instanceof ServerPlayer player) {
            IHarvestHandler activeHandler = new TreeHarvestHandler(data.speed(), event.getHitVec().getDirection(), harvestLeaves.getValue());
            activeHandler.start(event.getPos(), event.getLevel(), player);
            activeHandlers.put(event.getEntity().getUUID(), activeHandler);
            markDirty();
        }

        event.setCanceled(true);
        event.getEntity().startUsingItem(event.getHand());
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    public void addHostHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("module." + MODID + ".tree_harvest.single").withStyle(ChatFormatting.DARK_GRAY));
            if (getModule().getData().range() > 0) {
                tooltip.add(Component.translatable("module." + MODID + ".tree_harvest.area").withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    public void modifyFirstPersonUsingPose(RenderHandEvent event, boolean leftHand) {
        PoseStack poseStack = event.getPoseStack();
        Player player = Minecraft.getInstance().player;
        int handOffset = !leftHand ? 1 : -1;

        poseStack.translate((float) handOffset * -0.2785682F, 0.18344387F, 0.15731531F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-13.935F));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) handOffset * 35.3F));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) handOffset * -9.785F));
        float drawTime = (float) 72000 - ((float) player.getUseItemRemainingTicks() - event.getPartialTick() + 1.0F);
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
        poseStack.mulPose(Axis.YN.rotationDegrees((float) handOffset * 45.0F));
    }

    @Override
    @OnlyIn (Dist.CLIENT)
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
    public void saveEntityToStack(ItemStack stack, ModuleContext context) {
        stack.set(ItemData.TREE_MODULE_INVENTORY, itemBuffer.copy());
        stack.set(ItemData.BOOL_ITEM_PROP_1, harvestLeaves.copy());
        stack.set(ItemData.INT_ITEM_PROP_1, harvestRange.copy());
    }

    @Override
    public void loadEntityFromStack(ItemStack stack, ModuleContext context) {
        itemBuffer = stack.getOrDefault(ItemData.TREE_MODULE_INVENTORY, itemBuffer).copy();
        harvestLeaves = stack.getOrDefault(ItemData.BOOL_ITEM_PROP_1, harvestLeaves).copy();
        harvestRange = stack.getOrDefault(ItemData.INT_ITEM_PROP_1, harvestRange).copy();
    }
}
