package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.IntegerProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.AutoFeedData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class NightVisionEntity extends ModuleEntity<AutoFeedData> {

	private BooleanProperty enabled;
	private IntegerProperty enableInLight;
	private long energyToExtract = 20;	

	public NightVisionEntity(Module<AutoFeedData> module) {
		super(module);
		addProperty(enabled = new BooleanProperty("night_vision.enabled", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED));
		addProperty(enableInLight = new IntegerProperty("night_vision.light_level", 15).min(0).max(15).setFormatter(ConfigProperty.IntegerFormatter.RAW));
		this.savePropertiesToItem = true;
	}

	@Override
	public void tick(ModuleContext context) {
		if (!(context instanceof StackModuleContext)) { return; }
		StackModuleContext ctx = (StackModuleContext) context;
		if (!(ctx.getEntity() instanceof ServerPlayer) || !ctx.isEquipped() || !enabled.getValue()) { return; }
		ServerPlayer player = (ServerPlayer) ctx.getEntity();
		if (player.level.getRawBrightness(player.blockPosition(), 0) > enableInLight.getValue()) { 
			player.removeEffect(MobEffects.NIGHT_VISION);
		}
		else if (ctx.getOpStorage() != null) {
			long extractedEnergy = ctx.getOpStorage().modifyEnergyStored(-energyToExtract);
			if (extractedEnergy == energyToExtract) {
				player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 302, 0, false, false));
				return;
			}
			ctx.getOpStorage().modifyEnergyStored(extractedEnergy);
		}
	}
}
