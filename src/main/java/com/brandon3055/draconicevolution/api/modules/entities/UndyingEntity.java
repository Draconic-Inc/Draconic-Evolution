package com.brandon3055.draconicevolution.api.modules.entities;

import codechicken.lib.gui.modular.elements.GuiElement;
import codechicken.lib.gui.modular.lib.GuiRender;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.UndyingData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.brandon3055.draconicevolution.init.DEDamage;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.init.ItemData;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.Iterator;

public class UndyingEntity extends ModuleEntity<UndyingData> {

    private int charge;
    private int invulnerableTime = 0;

    public static final Codec<UndyingEntity> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DEModules.codec().fieldOf("module").forGetter(UndyingEntity::getModule),
            Codec.INT.fieldOf("gridx").forGetter(ModuleEntity::getGridX),
            Codec.INT.fieldOf("gridy").forGetter(ModuleEntity::getGridY),
            Codec.INT.fieldOf("charge").forGetter(e -> e.charge),
            Codec.INT.fieldOf("invul_time").forGetter(e -> e.invulnerableTime)
    ).apply(builder, UndyingEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, UndyingEntity> STREAM_CODEC = StreamCodec.composite(
            DEModules.streamCodec(), ModuleEntity::getModule,
            ByteBufCodecs.INT, ModuleEntity::getGridX,
            ByteBufCodecs.INT, ModuleEntity::getGridY,
            ByteBufCodecs.INT, e -> e.charge,
            ByteBufCodecs.INT, e -> e.invulnerableTime,
            UndyingEntity::new
    );

    public UndyingEntity(Module<UndyingData> module) {
        super(module);
    }

    UndyingEntity(Module<?> module, int gridX, int gridY, int charge, int invulnerableTime) {
        super((Module<UndyingData>) module, gridX, gridY);
        this.charge = charge;
        this.invulnerableTime = invulnerableTime;
    }

    @Override
    public ModuleEntity<?> copy() {
        return new UndyingEntity(module, getGridX(), getGridY(), charge, invulnerableTime);
    }

    @Override
    public void onInstalled(ModuleContext context) {
        super.onInstalled(context);
        invulnerableTime = 0;
    }

    @Override
    public void tick(ModuleContext moduleContext) {
        if (invulnerableTime > 0) {
            invulnerableTime--;
            markDirty();
            if (moduleContext instanceof StackModuleContext) {
                LivingEntity entity = ((StackModuleContext) moduleContext).getEntity();
                if (entity instanceof Player) {
                    if (invulnerableTime == 0) {
                        ((Player) entity).displayClientMessage(Component.literal(""), true);
                    } else {
                        ((Player) entity).displayClientMessage(Component.translatable("module.draconicevolution.undying.invuln.active", MathUtils.round(invulnerableTime / 20D, 10)).withStyle(ChatFormatting.GOLD), true);
                    }
                }
            }
        }

        IOPStorage storage = moduleContext.getOpStorage();
        if (!(moduleContext instanceof StackModuleContext && EffectiveSide.get().isServer() && storage != null)) {
            return;
        }

        StackModuleContext context = (StackModuleContext) moduleContext;
        UndyingData data = module.getData();
        if (!context.isEquipped() || charge >= data.chargeTime()) {
            return;
        }

        if (storage.getOPStored() >= data.getChargeEnergyRate()) {
            storage.modifyEnergyStored(-data.getChargeEnergyRate());
            charge++;
            markDirty();
        }
    }

    public boolean tryBlockDamage(LivingIncomingDamageEvent event) {
        if (invulnerableTime > 0) {
            event.setCanceled(true);
            return true;
        }
        return false;
    }

    public boolean tryBlockDamage(LivingDamageEvent.Pre event) {
        if (invulnerableTime > 0) {
            event.setNewDamage(0); //TODO, Theoretically nothing should get past LivingIncomingDamageEvent
            return true;
        }
        return false;
    }

    public boolean isCharged() {
        UndyingData data = module.getData();
        return charge >= data.chargeTime();
    }

    public double getCharge() {
        UndyingData data = module.getData();
        return charge / (double) data.chargeTime();
    }

    public boolean tryBlockDeath(LivingDeathEvent event) {
        /*
         * If you die, you die. The invulnerability does not block death only damage. So if someone really wants to kill the player they can.
         * This is to allow this module to block full power guardian beam damage.
         * The undying module is intended to be the only way to have *some* change of *maybe* surviving a full power beam hit.
         * */
        if (event.getSource().is(DEDamage.GUARDIAN_LASER) && invulnerableTime > 0) {
            event.getEntity().setHealth(event.getEntity().getHealth() + 1);
            return true;
        }

        UndyingData data = module.getData();
        if (charge >= data.chargeTime()) {
            LivingEntity entity = event.getEntity();
            entity.setHealth(entity.getHealth() + data.healthBoost());
            ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
            if (!stack.isEmpty()) {
                try (ModuleHost stackHost = DECapabilities.getHost(stack)) {
                    if (stackHost != null) {
                        ShieldControlEntity shield = stackHost.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
                        if (shield != null) {
                            shield.boost(data.shieldBoost(), data.shieldBoostTime());
                        }
                    }
                }
            }
            if (module.getModuleTechLevel().index >= 2) {
                entity.clearFire();
                Iterator<MobEffectInstance> iterator = entity.getActiveEffectsMap().values().iterator();
                while (iterator.hasNext()) {
                    MobEffectInstance effect = iterator.next();
                    if (!effect.getEffect().value().isBeneficial()) {
                        entity.onEffectRemoved(effect);
                        iterator.remove();
                    }
                }
            }
            charge = 0;
            DraconicNetwork.sendUndyingActivation(entity, module.getItem());
            entity.level().playSound(null, entity.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 5F, (0.95F + (entity.level().random.nextFloat() * 0.1F)));
            invulnerableTime = data.invulnerableTime();
            markDirty();
            return true;
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderModule(GuiElement<?> parent, GuiRender render, int x, int y, int width, int height, double mouseX, double mouseY, boolean renderStack, float partialTicks) {
        super.renderModule(parent, render, x, y, width, height, mouseX, mouseY, renderStack, partialTicks);

        UndyingData data = module.getData();
        if (charge >= data.chargeTime()) return;
        double progress = charge / Math.max(1D, data.chargeTime());

        String pText = (int) (progress * 100) + "%";
        String tText = ((data.chargeTime() - charge) / 20) + "s";
        drawChargeProgress(render, x, y, width, height, progress, pText, tText);
    }

    @Override
    public void saveEntityToStack(ItemStack stack, ModuleContext context) {
        stack.set(ItemData.UNDYING_MODULE_CHARGE, charge);
    }

    @Override
    public void loadEntityFromStack(ItemStack stack, ModuleContext context) {
        charge = stack.getOrDefault(ItemData.UNDYING_MODULE_CHARGE, 0);
    }
}
