package com.brandon3055.draconicevolution.api.modules.entities;

import codechicken.lib.gui.modular.elements.GuiElement;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.math.MathHelper;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.EnergyLinkData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

public class EnergyLinkEntity extends ModuleEntity<EnergyLinkData> {

    private UUID linkId = null;
    private BlockPos corePos = null;
    private ResourceKey<Level> dimKey = null;
    private long linkCharge = 0;
    private double flow = 0;
    private boolean coreEnergyLow = false;
    private BooleanProperty enabled;

    public EnergyLinkEntity(Module<EnergyLinkData> module) {
        super(module);
        addProperty(enabled = new BooleanProperty("energy_link_mod.enabled", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED));
    }

    @Override
    public void tick(ModuleContext moduleContext) {
        if (linkId == null || dimKey == null || corePos == null) return;

        IOPStorage storage = moduleContext.getOpStorage();
        if (!(moduleContext instanceof StackModuleContext context) || storage == null) return;

        Level level = context.getEntity().level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        boolean crossDimension = false;
        if (!level.dimension().equals(dimKey)) {
            crossDimension = true;
            level = serverLevel.getServer().getLevel(dimKey);
            if (level == null) return;
        }

        if (!level.isLoaded(corePos)) return;

        BlockEntity entity = level.getBlockEntity(corePos);
        if (!(entity instanceof TileEnergyCore core) || !linkId.equals(core.linkUUID.get())) {
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
        flow = MathHelper.approachExp(flow, inserted, 1 / 20D);
    }

    /**
     * Minimum charge time is 30 seconds.
     * Maintenance cost varies between 10% and 100% depending on flow.
     */
    private boolean updateConnection(EnergyLinkData data, IOPStorage storage, TileEnergyCore core) {
        if (core.energy.getOPStored() < data.operationEnergy() * 20) {
            coreEnergyLow = true;
            disconnect();
            return false;
        }
        coreEnergyLow = false;

        if (linkCharge < data.activationEnergy()) {
            long chargeRate = Math.max(1, data.activationEnergy() / (20 * 30));
            long remaining = data.activationEnergy() - linkCharge;
            linkCharge += storage.modifyEnergyStored(-Math.min(remaining, chargeRate));
            return false;
        }

        long maintenanceCost = (long) ((data.operationEnergy() * 0.1) + (data.operationEnergy() * 0.9 * (flow / data.transferLimit())));
        long extracted = core.energy.extractOP(maintenanceCost, false);
        if (extracted < maintenanceCost) {
            disconnect();
            return false;
        }

        return true;
    }

    private void disconnect() {
        linkCharge = 0;
        flow = 0;
    }

    private void clear() {
        linkId = null;
        dimKey = null;
        corePos = null;
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    public void renderModule(GuiElement<?> parent, GuiRender render, int x, int y, int width, int height, double mouseX, double mouseY, boolean stackRender, float partialTicks) {
        super.renderModule(parent, render, x, y, width, height, mouseX, mouseY, stackRender, partialTicks);

        EnergyLinkData data = module.getData();
        if (linkCharge >= data.activationEnergy()) return;
        double progress = linkCharge / (double) data.activationEnergy();

        boolean crossDimension = render.mc().level.dimension() != dimKey;
        if (crossDimension && !data.xDimensional()) {
            render.rect(x, y, width, height, 0x60FF0000);
            return;
        }

        String pText = (int) (progress * 100) + "%";
        String progressText = I18n.get("module.draconicevolution.energy_link.charging") + StringUtils.repeat(".", (int) ((System.currentTimeMillis() / 500) % 4));
        drawChargeProgress(render, x, y, width, height, progress, pText, progressText);
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        super.readFromNBT(compound);
        linkCharge = compound.getLong("link_charge");
    }

    @Override
    public void writeToNBT(CompoundTag compound) {
        super.writeToNBT(compound);
        compound.putLong("link_charge", linkCharge);
    }

    @Override
    protected void readExtraData(CompoundTag nbt) {
        super.readExtraData(nbt);
        if (nbt.contains("link_id")) {
            linkId = nbt.getUUID("link_id");
            corePos = new BlockPos(nbt.getInt("core_x"), nbt.getInt("core_y"), nbt.getInt("core_z"));
            dimKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(nbt.getString("dim")));
        }
    }

    @Override
    protected CompoundTag writeExtraData(CompoundTag nbt) {
        if (linkId != null) {
            nbt.putString("dim", dimKey.location().toString());
            nbt.putInt("core_x", corePos.getX());
            nbt.putInt("core_y", corePos.getY());
            nbt.putInt("core_z", corePos.getZ());
            nbt.putUUID("link_id", linkId);
        }
        return super.writeExtraData(nbt);
    }

    @Override
    public void addToolTip(List<Component> list) {
        if (linkId != null) {
            list.add(Component.translatable("module.draconicevolution.energy_link.linked_core")
                    .withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(Component.literal("X:" + corePos.getX() + ", Y:" + corePos.getY() + ", Z:" + corePos.getZ() + ", " + dimKey.location()).withStyle(ChatFormatting.DARK_GREEN)));
        } else {
            list.add(Component.translatable("module.draconicevolution.energy_link.link_to_core").withStyle(ChatFormatting.GRAY));
        }
    }
}