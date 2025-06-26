package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.brandonscore.api.BCStreamCodec;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.IntegerProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.AutoFeedData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ItemData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class NightVisionEntity extends ModuleEntity<AutoFeedData> {

    private BooleanProperty enabled = new BooleanProperty("night_vision.enabled", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED).setChangeListener((stack, prop) -> wasJustDisabled = !prop.getValue());
    private IntegerProperty enableInLight = new IntegerProperty("night_vision.light_level", 15).min(0).max(15).setFormatter(ConfigProperty.IntegerFormatter.RAW);

    //Not serialised
    private boolean wasJustDisabled = false;
    private boolean appliedByModule = false;
    private int tick;
    private int disableTimer = 0;

    public static final Codec<NightVisionEntity> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DEModules.codec().fieldOf("module").forGetter(NightVisionEntity::getModule),
            Codec.INT.fieldOf("gridx").forGetter(ModuleEntity::getGridX),
            Codec.INT.fieldOf("gridy").forGetter(ModuleEntity::getGridY),
            BooleanProperty.CODEC.fieldOf("enabled").forGetter(e -> e.enabled),
            IntegerProperty.CODEC.fieldOf("enable_in_light").forGetter(e -> e.enableInLight)
    ).apply(builder, NightVisionEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, NightVisionEntity> STREAM_CODEC = BCStreamCodec.composite(
            DEModules.streamCodec(), NightVisionEntity::getModule,
            ByteBufCodecs.INT, ModuleEntity::getGridX,
            ByteBufCodecs.INT, ModuleEntity::getGridY,
            BooleanProperty.STREAM_CODEC, e -> e.enabled,
            IntegerProperty.STREAM_CODEC, e -> e.enableInLight,
            NightVisionEntity::new
    );

    public NightVisionEntity(Module<AutoFeedData> module) {
        super(module);
//        addProperty(enabled = new BooleanProperty("night_vision.enabled", true).setFormatter(ConfigProperty.BooleanFormatter.ENABLED_DISABLED));
//        addProperty(enableInLight = new IntegerProperty("night_vision.light_level", 15).min(0).max(15).setFormatter(ConfigProperty.IntegerFormatter.RAW));
//        enabled.setChangeListener(() -> wasJustDisabled = !enabled.getValue());
//        this.savePropertiesToItem = true;
    }

    NightVisionEntity(Module<?> module, int gridX, int gridY, BooleanProperty enabled, IntegerProperty enableInLight) {
        super((Module<AutoFeedData>) module, gridX, gridY);
        this.enabled = enabled;
        this.enableInLight = enableInLight;
    }

    @Override
    public ModuleEntity<?> copy() {
        return new NightVisionEntity(module, getGridX(), getGridY(), enabled.copy(), enableInLight.copy());
    }

    @Override
    public void getEntityProperties(List<ConfigProperty> properties) {
        super.getEntityProperties(properties);
        properties.add(enabled);
        properties.add(enableInLight);
    }

    @Override
    public void tick(ModuleContext context) {
        if ((tick++ % 20 != 0) || !(context instanceof StackModuleContext ctx)) return;
        if (!(ctx.getEntity() instanceof ServerPlayer player) || player.level().isClientSide() || !ctx.isEquipped()) return;

        if (!enabled.getValue()) {
            if (wasJustDisabled && appliedByModule) {
                player.removeEffect(MobEffects.NIGHT_VISION);
                wasJustDisabled = appliedByModule = false;
                markDirty();
            }
            return;
        }

        IOPStorage opStorage = ctx.getOpStorage();
        if (opStorage == null || opStorage.getOPStored() < EquipCfg.nightVisionEnergy * 20L) {
            if (appliedByModule && disableTimer++ >= 3) {
                player.removeEffect(MobEffects.NIGHT_VISION);
                appliedByModule = false;
                markDirty();
            }
            return;
        }

        boolean shouldApply = player.level().getRawBrightness(player.blockPosition(), 0) <= enableInLight.getValue();
        if (shouldApply) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 302, 0, false, false));
            ctx.getOpStorage().modifyEnergyStored(-EquipCfg.nightVisionEnergy * 20L);
            appliedByModule = true;
            disableTimer = 0;
        } else if (appliedByModule && disableTimer++ >= 3){
            player.removeEffect(MobEffects.NIGHT_VISION);
            appliedByModule = true;
        }
        markDirty();
    }

    @Override
    public void saveEntityToStack(ItemStack stack, ModuleContext context) {
        stack.set(ItemData.BOOL_ITEM_PROP_1, enabled.copy());
        stack.set(ItemData.INT_ITEM_PROP_1, enableInLight.copy());
    }

    @Override
    public void loadEntityFromStack(ItemStack stack, ModuleContext context) {
        enabled = stack.getOrDefault(ItemData.BOOL_ITEM_PROP_1, enabled).copy();
        enableInLight = stack.getOrDefault(ItemData.INT_ITEM_PROP_1, enableInLight).copy();
    }
}
