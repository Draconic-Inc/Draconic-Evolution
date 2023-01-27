package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.IntegerProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.AutoFeedData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;

import com.brandon3055.draconicevolution.init.EquipCfg;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class NightVisionEntity extends ModuleEntity<AutoFeedData> {

    private BooleanProperty enabled;
    private IntegerProperty enableInLight;
    private boolean wasJustDisabled = false;
    private boolean appliedByModule = false;
    private int tick;
    private int disableTimer = 0;

    public NightVisionEntity(Module<AutoFeedData> module) {
        super(module);
        addProperty(enabled = new BooleanProperty("night_vision.enabled", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED));
        addProperty(enableInLight = new IntegerProperty("night_vision.light_level", 15).min(0).max(15).setFormatter(ConfigProperty.IntegerFormatter.RAW));
        enabled.setChangeListener(() -> wasJustDisabled = !enabled.getValue());
        this.savePropertiesToItem = true;
    }

    @Override
    public void tick(ModuleContext context) {
        if ((tick++ % 20 != 0) || !(context instanceof StackModuleContext ctx)) return;
        if (!(ctx.getEntity() instanceof ServerPlayer player) || player.level.isClientSide() || !ctx.isEquipped()) return;

        if (!enabled.getValue()) {
            if (wasJustDisabled && appliedByModule) {
                player.removeEffect(MobEffects.NIGHT_VISION);
                wasJustDisabled = appliedByModule = false;
            }
            return;
        }

        IOPStorage opStorage = ctx.getOpStorage();
        if (opStorage == null || opStorage.getOPStored() < EquipCfg.nightVisionEnergy * 20L) {
            if (appliedByModule && disableTimer++ >= 3) {
                player.removeEffect(MobEffects.NIGHT_VISION);
                appliedByModule = false;
            }
            return;
        }

        boolean shouldApply = player.level.getRawBrightness(player.blockPosition(), 0) <= enableInLight.getValue();
        if (shouldApply) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 302, 0, false, false));
            ctx.getOpStorage().modifyEnergyStored(-EquipCfg.nightVisionEnergy * 20L);
            appliedByModule = true;
            disableTimer = 0;
        } else if (appliedByModule && disableTimer++ >= 3){
            player.removeEffect(MobEffects.NIGHT_VISION);
            appliedByModule = true;
        }
    }
}
