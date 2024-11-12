package com.brandon3055.draconicevolution.client.render.hud;

import codechicken.lib.gui.modular.elements.GuiContextMenu;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.sprite.Material;
import com.brandon3055.brandonscore.api.hud.AbstractHudElement;
import com.brandon3055.brandonscore.api.math.Vector2;
import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;
import com.brandon3055.draconicevolution.api.modules.entities.UndyingEntity;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.items.equipment.IModularArmor;
import com.brandon3055.draconicevolution.items.tools.DraconiumCapacitor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by brandon3055 on 3/8/21
 */
public class ShieldHudElement extends AbstractHudElement {
    private static int TOTEM_EFFECT_TIME = 32;

    private static Random rand = new Random();
    private Minecraft mc = Minecraft.getInstance();
    private double shieldCharge = 0;
    private String shieldText = "";
    private double coolDown = 0;
    private double energyBar = 0;
    private String energyText = "";
    private double[] totemStatus = new double[0];
    private int totemEffect = 0;
    private long totemEffectSeed = 0;
    private int totemEffectIndex = 0;
    private float hudOpacity = 1;
    private int lastTotemCount = 0;
    private int lastChargedTotemCount = 0;

    private boolean renderHud = false;

    //User Settings
    private boolean numericEnergy = true;
    private boolean showUndying = true;
    private int energyMode = 2;
    private float scale = 1;

    private Material[] _effectMats = null;

    public ShieldHudElement() {
        super(new Vector2(0.0136, 0.9787));
        this.width = 119;
    }

    @Override
    public void addConfigElements(GuiContextMenu menu) {
        super.addConfigElements(menu);
        menu.addOption(() -> Component.translatable("hud_armor.draconicevolution.numeric." + numericEnergy), runDirty(() -> numericEnergy = !numericEnergy), Component.translatable("hud_armor.draconicevolution.numeric.info"));
        menu.addOption(() -> Component.translatable("hud_armor.draconicevolution.undying." + showUndying), runDirty(() -> showUndying = !showUndying), Component.translatable("hud_armor.draconicevolution.undying.info"));
        menu.addOption(() -> Component.translatable("hud_armor.draconicevolution.energy." + energyMode), runDirty(() -> energyMode = energyMode == 2 ? 0 : energyMode + 1), Component.translatable("hud_armor.draconicevolution.energy.info"));
//        Scale needs work... Problem is i am scaling size and then scaling the elements that are rendered based on that size. So i am scaling twice.
//        list.add(createButton(() -> I18n.get("Scale") + ": " + Math.round(scale * 100) + "%", parent, runDirty(() -> scale = scale <= 0.5F ? 1F : scale - 0.1F)).setHoverText(I18n.get("hud_armor.draconicevolution.scale.info")));
    }

    public void popTotem() {
        totemEffect = TOTEM_EFFECT_TIME;
        if (mc.level != null) {
            totemEffectSeed = mc.level.random.nextLong();
        }
        totemEffectIndex = 0;
        for (double totem : totemStatus) {
            if (totem == -1) break;
            totemEffectIndex++;
        }
    }

    private boolean extended() {
        return numericEnergy || totemStatus.length > 0;
    }

    @Override
    public double height() {
        return ((17 * scale) + (extended() ? (10 * scale) : 0));
    }

    @Override
    public double width() {
        return 115 * scale;
    }

    @Override
    public void tick(boolean configuring) {
        if (mc.player == null || !enabled) {
            renderHud = false;
            if (configuring) setupExample();
            return;
        }

        if (totemEffect > 0) {
            totemEffect--;
        }

        //Get and validate the armor chestpiece
        ItemStack chestStack = IModularArmor.getArmor(mc.player);
        ModuleHost host = chestStack.getCapability(DECapabilities.Host.ITEM);
        IOPStorage opStorage = chestStack.getCapability(CapabilityOP.ITEM);
        if (chestStack.isEmpty() || host == null || opStorage == null) {
            renderHud = false; //The storage check is just a safety check. If the item has a ModuleHost it should always have storage unless something is broken (even without storage modules the capacity is just zero)
            if (configuring) setupExample();
            return;
        }

        //Update Shield
        ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
        if (shieldControl == null) {
            shieldCharge = 0;
            shieldText = I18n.get("hud_armor.draconicevolution.no_shield");
        } else if (!shieldControl.isShieldEnabled()) {
            shieldCharge = 0;
            shieldText = I18n.get("hud_armor.draconicevolution.shield_disabled");
        } else {
            double capacity = shieldControl.getShieldCapacity() + shieldControl.getMaxShieldBoost();
            if (capacity == 0 && shieldControl.getMaxShieldBoost() > 0) {
                capacity = shieldControl.getMaxShieldBoost();
            }
            double points = shieldControl.getShieldPoints();
            shieldCharge = capacity > 0 ? points / capacity : 0;
            shieldText = (int) points + "/" + (int) capacity;
            double maxCooldown = shieldControl.getMaxShieldCoolDown();
            coolDown = maxCooldown > 0 ? shieldControl.getShieldCoolDown() / maxCooldown : 0;
        }

        //Energy
        long energy = opStorage.getOPStored();
        long maxEnergy = opStorage.getMaxOPStored();

        if (energyMode > 0) {
            List<ItemStack> capacitors = new ArrayList<>(EquipmentManager.findItems(e -> e.getItem() instanceof DraconiumCapacitor, mc.player));
            for (ItemStack stack : mc.player.getInventory().items) {
                if (stack.getItem() instanceof DraconiumCapacitor) {
                    capacitors.add(stack);
                }
            }
            long capMax = 0;
            long capEnergy = 0;

            for (ItemStack stack : capacitors) {
                IOPStorage storage = stack.getCapability(CapabilityOP.ITEM);
                if (storage != null) {
                    capMax = Utils.safeAdd(storage.getMaxOPStored(), capMax);
                    capEnergy = Utils.safeAdd(storage.getOPStored(), capEnergy);
                }
            }

            if (energyMode == 1) {
                energy = capEnergy;
                maxEnergy = capMax;
            } else {
                energy = Utils.safeAdd(capEnergy, energy);
                maxEnergy = Utils.safeAdd(capMax, maxEnergy);
            }
        }

        energyBar = maxEnergy > 0 ? energy / (double) maxEnergy : 0;
        if (numericEnergy) {
            energyText = I18n.get("op.brandonscore.op") + ": " + Utils.formatNumber(energy);
        }

        //Totems
        if (showUndying) {
            List<UndyingEntity> totems = host.getEntitiesByType(ModuleTypes.UNDYING)
                    .map(e -> (UndyingEntity) e)
//                    .sorted(Comparator.comparing(e -> e.isCharged() ? -1 : e.getCharge()))
                    .sorted(Comparator.comparing(e -> e.getModule().getModuleTechLevel().index))
                    .collect(Collectors.toList());
            int chargedTotems = 0;
            totemStatus = new double[totems.size()];
            for (int i = 0; i < totems.size(); i++) {
                UndyingEntity entity = totems.get(i);
                if (entity.isCharged()) {
                    chargedTotems++;
                }
                totemStatus[i] = entity.isCharged() ? -1 : entity.getCharge();
            }
            if (lastTotemCount != totems.size()) {
                lastTotemCount = totems.size();
            } else if (chargedTotems > lastChargedTotemCount && mc.level != null) {
                mc.level.playLocalSound(mc.player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1, 2, false);
            }
            lastChargedTotemCount = chargedTotems;
        } else {
            totemStatus = new double[0];
        }

        renderHud = true;
    }

    private void setupExample() {
        renderHud = enabled;
        shieldCharge = 1624/3055D;
        shieldText = "1624/3055";
        energyBar = 0.75;
        energyText = I18n.get("op.brandonscore.op") + ": 42M";
        totemStatus = showUndying ? new double[] {-1, 0.5, 0.75} : new double[0];
    }

    @Override
    public void render(GuiRender render, float partialTicks, boolean configuring) {
        if (!renderHud) return;
        render.pose().pushPose();
        render.pose().translate(xPos(), yPos(), 0);
        render.pose().scale(scale, scale, scale);
        MultiBufferSource.BufferSource getter = RenderUtils.getGuiBuffers();

        hudOpacity = 1;

        double width = width() - (16 * scale);
        double height = height() - (extended() ? (10 * scale) : 0);
        double iconSize = height - 2;

        render.tex(DEGuiTextures.get("hud/shield_icon"), 0, (height / 2) - (iconSize / 2), iconSize, iconSize, 1F, 1F, 1F, hudOpacity);
        render.pose().translate(iconSize, 0, 0);

        boolean xl = false; //size == 2;
        int shH = xl ? 11 : 7;
        int reH = xl ? 3 : 2;
        int divH = xl ? 2 : 1;

        //Draw Background
        int backgroundColor = scaleAlpha(0xFF01001b);
        int borderColor = scaleAlpha(0xFF450f57);
        int borderColorEnd = (borderColor & 0xFEFEFE) >> 1 | borderColor & 0xFF000000;
        render.toolTipBackground(0, 0, width, height, backgroundColor, borderColor, borderColorEnd);

        //Draw Shield
        float charge = (float) (shieldCharge >= 0 ? shieldCharge : 0);
        double bw = width - 4;
        Material mat = DEGuiTextures.get("hud/ryg_bar");
        render.partialSprite(mat.renderType(GuiRender::texColType), 2, 2, 2 + (bw * charge), 2 + shH, mat.sprite(), 0, 0, charge, 1, 1F, 1F, 1F, hudOpacity);
        render.rect(2D + (bw * charge), 2, bw * (1D - charge), shH, scaleAlpha(0xFF01001b));

        render.rect(2, 2 + shH, width - 4, divH, scaleAlpha(0xff22072b));

        //Draw Shield Cool Down
        render.rect(2D, 2 + shH + divH, bw * coolDown, reH, scaleAlpha(0xFF840000));
        render.rect(2D + (bw * coolDown), 2 + shH + divH, bw * (1D - coolDown), reH, scaleAlpha(0xFF00b014));

        render.rect(2, 2 + shH + divH + reH, width - 4, divH, scaleAlpha(0xff22072b));

        //Draw Energy Bar
        render.rect(2D, 2 + shH + divH + reH + divH, bw * energyBar, reH, scaleAlpha(0xFF07ced8));
        render.rect(2D + (bw * energyBar), 2 + shH + divH + reH + divH, bw * (1D - energyBar), reH, scaleAlpha(0xFF01001b));

        //Draw Totems
        if (totemStatus.length > 0) {
            double x = width - 8;
            for (double state : totemStatus) {
                render.texRect(DEGuiTextures.get("hud/undying"), x, height + 1, 8, 8, scaleAlpha(state != -1 ? 0xFFFF0000 : 0xFFFFFFFF));
                if (state != -1) {
                    RenderUtils.drawPieProgress(render, x, height + 1, 8, state, 0, 0x80FFFFFF);
                }
                x -= 9;
            }
        }

        if (totemEffect > 0) {
            float progress = 1F - ((totemEffect - partialTicks) / (float) TOTEM_EFFECT_TIME);
            particleExplosion(render, width - 4 - (totemEffectIndex * 9), height + 5, progress, rand);
        }

        getter.endBatch();

        //Draw Text (after end batch otherwise font rendering will break)
        double tPos = width / 2D - mc.font.width(shieldText) / 2D;
//        mc.font.drawInBatch(shieldText, (float) tPos, xl ? 4 : 2, scaleAlpha(0xFF0000FF));
        render.drawString(shieldText, (float) tPos, xl ? 4 : 2, scaleAlpha(0xFF0000FF), false);
        if (numericEnergy && !energyText.isEmpty()) {
//            mc.font.drawShadow(mStack, energyText, 2, (float) height + 1F, scaleAlpha(0xFFFFFFFF));
            render.drawString(energyText, 2, (float) height + 1F, scaleAlpha(0xFFFFFFFF), true);
        }
        render.pose().popPose();
    }

    public void particleExplosion(GuiRender render, double x, double y, float progress, Random rand) {
        rand.setSeed(totemEffectSeed);
        int pCount = 128;
        int size = 100;
        double scale = 6;
        for (int p = 0; p < pCount; p++) {
            float life = (0.5F + (rand.nextFloat() * 0.5F));
            float age = Mth.clamp(progress / life, 0F, 1F);
            float fadeOut = Math.min(1F, (1F - age) * 10F);
            boolean altColour = rand.nextInt(4) == 0;
            float red = (altColour ? 0.6F : 0.1F) + rand.nextFloat() * 0.2F;
            float green = (altColour ? 0.6F : 0.4F) + rand.nextFloat() * 0.3F;
            float blue = rand.nextFloat() * 0.2F;
            double dir = rand.nextDouble() * Math.PI * 2;
            double dist = rand.nextGaussian() * size;
            double pX = x + (Mth.sin((float) dir) * dist * progress);
            double pY = y + (Mth.cos((float) dir) * dist * progress);
            double ps = scale * (0.75 + rand.nextDouble() * 0.5) * fadeOut;
            int index = Math.min((int) (age * 8), 7);
            if (age >= 1) continue; //Still need to do the math above, so we don't throw off the seeded random
            render.tex(getEffectMats()[index], pX - (ps/2), pY-(ps/2), ps, ps, red, green, blue, 1F);
        }
    }

    private int scaleAlpha(int colour) {
        colour &= 0x00FFFFFF;
        int modifier = (((int) (hudOpacity * 240F) + 15) << 24);
        return modifier | colour;
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        super.writeNBT(nbt);
        nbt.putBoolean("show_numeric", numericEnergy);
        nbt.putBoolean("show_undying", showUndying);
        nbt.putFloat("scale", scale);
    }

    @Override
    public void readNBT(CompoundTag nbt) {
        super.readNBT(nbt);
        numericEnergy = nbt.getBoolean("show_numeric");
        showUndying = nbt.getBoolean("show_undying");
        scale = nbt.getFloat("scale");
    }

    private Material[] getEffectMats() {
        if (_effectMats == null) {
            _effectMats = new Material[8];
            for (int i = 0; i < 8; i++) {
                _effectMats[i] = DEGuiTextures.get("effect/glitter_" + i);
            }
        }
        return _effectMats;
    }
}