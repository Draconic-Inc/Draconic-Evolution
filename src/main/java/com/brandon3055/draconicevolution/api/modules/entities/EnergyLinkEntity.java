package com.brandon3055.draconicevolution.api.modules.entities;

import codechicken.lib.gui.modular.elements.GuiElement;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.BCStreamCodec;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.EnergyLinkData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.init.ItemData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EnergyLinkEntity extends ModuleEntity<EnergyLinkData> {

    private Optional<GlobalPos> linkedPos = Optional.empty();
    private Optional<UUID> linkId = Optional.empty();
    //This shouldn't be saved to item, or maybe just clear on insert?
    private long linkCharge = 0;
    private Optional<Double> flow = Optional.empty();

    //Component data can probably still be set from entity constructor.
    private BooleanProperty enabled = new BooleanProperty("energy_link_mod.enabled", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED);

    //Not Serialised
//    private boolean coreEnergyLow = false;

    public static final Codec<EnergyLinkEntity> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DEModules.codec().fieldOf("module").forGetter(EnergyLinkEntity::getModule),
            Codec.INT.fieldOf("gridx").forGetter(ModuleEntity::getGridX),
            Codec.INT.fieldOf("gridy").forGetter(ModuleEntity::getGridY),
            GlobalPos.CODEC.optionalFieldOf("linked_pos").forGetter(e -> e.linkedPos),
            UUIDUtil.CODEC.optionalFieldOf("link_id").forGetter(e -> e.linkId),
            Codec.LONG.fieldOf("link_charge").forGetter(e -> e.linkCharge),
            Codec.DOUBLE.optionalFieldOf("flow").forGetter(e -> e.flow),
            BooleanProperty.CODEC.fieldOf("enabled").forGetter(e -> e.enabled)
            ).apply(builder, EnergyLinkEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnergyLinkEntity> STREAM_CODEC = BCStreamCodec.composite(
            DEModules.streamCodec(), EnergyLinkEntity::getModule,
            ByteBufCodecs.INT, ModuleEntity::getGridX,
            ByteBufCodecs.INT, ModuleEntity::getGridY,
            ByteBufCodecs.optional(GlobalPos.STREAM_CODEC), energyLinkEntity -> energyLinkEntity.linkedPos,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), energyLinkEntity -> energyLinkEntity.linkId,
            ByteBufCodecs.VAR_LONG, energyLinkEntity -> energyLinkEntity.linkCharge,
            ByteBufCodecs.optional(ByteBufCodecs.DOUBLE), energyLinkEntity -> energyLinkEntity.flow,
            BooleanProperty.STREAM_CODEC, energyLinkEntity -> energyLinkEntity.enabled,
            EnergyLinkEntity::new
    );

    public EnergyLinkEntity(Module<EnergyLinkData> module) {
        super(module);
    }

    EnergyLinkEntity(Module<?> module, int gridX, int gridY, Optional<GlobalPos> linkedPos, Optional<UUID> linkId, long linkCharge, Optional<Double> flow, BooleanProperty enabled) {
        super((Module<EnergyLinkData>) module, gridX, gridY);
        this.linkedPos = linkedPos;
        this.linkId = linkId;
        this.linkCharge = linkCharge;
        this.flow = flow;
        this.enabled = enabled;
    }

    @Override
    public ModuleEntity<?> copy() {
        return new EnergyLinkEntity(module, getGridX(), getGridY(), linkedPos, linkId, linkCharge, flow, enabled.copy());
    }

    @Override
    public void getEntityProperties(List<ConfigProperty> properties) {
        properties.add(enabled);
    }

    @Override
    public Module<EnergyLinkData> getModule() {
        return super.getModule();
    }

    @Override
    public void tick(ModuleContext moduleContext) {
        if (linkId.isEmpty() || linkedPos.isEmpty()) return;
        GlobalPos linkedPos = this.linkedPos.get();

        IOPStorage storage = moduleContext.getOpStorage();
        if (!(moduleContext instanceof StackModuleContext context) || storage == null) return;

        Level level = context.getEntity().level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        boolean crossDimension = false;
        if (!level.dimension().equals(linkedPos.dimension())) {
            crossDimension = true;
            level = serverLevel.getServer().getLevel(linkedPos.dimension());
            if (level == null) return;
        }

        if (!level.isLoaded(linkedPos.pos())) return;

        BlockEntity entity = level.getBlockEntity(linkedPos.pos());
        if (!(entity instanceof TileEnergyCore core) || !linkId.get().equals(core.linkUUID.get())) {
            clear();
            return;
        }

        EnergyLinkData data = module.getData();
        if (!enabled.getValue() || (crossDimension && !data.xDimensional())) {
            disconnect();
            return;
        }

        if (!updateConnection(data, storage, core)) return;

        long inserted = core.energy.extractOP(storage.modifyEnergyStored(core.energy.extractOP(data.transferLimit(), true)), false);
        flow = Optional.of(MathHelper.approachExp(flow.orElse(0D), inserted, 1 / 20D));
        markDirty();
    }

    /**
     * Minimum charge time is 30 seconds.
     * Maintenance cost varies between 10% and 100% depending on flow.
     */
    private boolean updateConnection(EnergyLinkData data, IOPStorage storage, TileEnergyCore core) {
        if (core.energy.getOPStored() < data.operationEnergy() * 20) {
//            coreEnergyLow = true;
            disconnect();
            return false;
        }
//        coreEnergyLow = false;

        if (linkCharge < data.activationEnergy()) {
            long chargeRate = Math.max(1, data.activationEnergy() / (20 * 30));
            long remaining = data.activationEnergy() - linkCharge;
            linkCharge += storage.modifyEnergyStored(-Math.min(remaining, chargeRate));
            markDirty();
            return false;
        }

        long maintenanceCost = (long) ((data.operationEnergy() * 0.1) + (data.operationEnergy() * 0.9 * (flow.orElse(0D) / data.transferLimit())));
        long extracted = core.energy.extractOP(maintenanceCost, false);
        if (extracted < maintenanceCost) {
            disconnect();
            return false;
        }

        return true;
    }

    private void disconnect() {
        linkCharge = 0;
        flow = Optional.of(0D);
        markDirty();
    }

    private void clear() {
        linkId = Optional.empty();
        linkedPos = Optional.empty();
        markDirty();
    }

    @Override
    public void onInstalled(ModuleContext context) {
        super.onInstalled(context);
        linkCharge = 0;
        markDirty();
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    public void renderModule(GuiElement<?> parent, GuiRender render, int x, int y, int width, int height, double mouseX, double mouseY, boolean stackRender, float partialTicks) {
        super.renderModule(parent, render, x, y, width, height, mouseX, mouseY, stackRender, partialTicks);

        EnergyLinkData data = module.getData();
        if (linkCharge >= data.activationEnergy()) return;
        double progress = linkCharge / (double) data.activationEnergy();

        boolean crossDimension = linkedPos.isPresent() && render.mc().level.dimension() != linkedPos.get().dimension();
        if (crossDimension && !data.xDimensional()) {
            render.rect(x, y, width, height, 0x60FF0000);
            return;
        }

        String pText = (int) (progress * 100) + "%";
        Component progressText = Component.translatable("module.draconicevolution.energy_link.charging").append(StringUtils.repeat(".", (int) ((System.currentTimeMillis() / 500) % 4)));
        drawChargeProgress(render, x, y, width, height, progress, Component.literal(pText), progressText);
    }

    @Override
    public void saveEntityToStack(ItemStack stack, ModuleContext context) {
        if (linkId.isPresent() && linkedPos.isPresent()) {
            stack.set(ItemData.LINK_MODULE_LINK_POS, linkedPos.get());
            stack.set(ItemData.LINK_MODULE_LINK_ID, linkId.get());
        }
    }

    @Override
    public void loadEntityFromStack(ItemStack stack, ModuleContext context) {
        linkedPos = Optional.ofNullable(stack.get(ItemData.LINK_MODULE_LINK_POS));
        linkId = Optional.ofNullable(stack.get(ItemData.LINK_MODULE_LINK_ID));
    }

    @Override
    public void addToolTip(List<Component> list) {
        if (linkId.isPresent() && linkedPos.isPresent()) {
            GlobalPos gPos = linkedPos.get();
            list.add(Component.translatable("module.draconicevolution.energy_link.linked_core")
                    .withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(Component.literal("X:" + gPos.pos().getX() + ", Y:" + gPos.pos().getY() + ", Z:" + gPos.pos().getZ() + ", " + gPos.dimension().location()).withStyle(ChatFormatting.DARK_GREEN)));
        } else {
            list.add(Component.translatable("module.draconicevolution.energy_link.link_to_core").withStyle(ChatFormatting.GRAY));
        }
    }
}