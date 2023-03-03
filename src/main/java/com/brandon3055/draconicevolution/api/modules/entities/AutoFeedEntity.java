package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.api.render.GuiHelper;
import com.brandon3055.brandonscore.client.BCGuiSprites;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElement;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.AutoFeedData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AutoFeedEntity extends ModuleEntity<AutoFeedData> {

    private BooleanProperty consumeFood;
    private float storedFood = 0;

    public AutoFeedEntity(Module<AutoFeedData> module) {
        super(module);
        addProperty(consumeFood = new BooleanProperty("feed_mod.consume_food", true).setFormatter(ConfigProperty.BooleanFormatter.YES_NO));
        this.savePropertiesToItem = true;
    }

    @Override
    public void tick(ModuleContext context) {
        AutoFeedData data = module.getData();
        if (context instanceof StackModuleContext) {
            LivingEntity entity = ((StackModuleContext) context).getEntity();
            if (entity instanceof ServerPlayer player && entity.tickCount % 10 == 0 && ((StackModuleContext) context).isEquipped()) {
                if (storedFood < data.foodStorage() && consumeFood.getValue()) {
                    //Do food consumption
                    for (ItemStack stack : player.getInventory().items) {
                        if (!stack.isEmpty() && stack.isEdible()) {
                            FoodProperties food = stack.getItem().getFoodProperties(stack, player);
                            if (food != null && food.getNutrition() > 0 && food.getEffects().isEmpty()) {
                                double val = food.getNutrition() + food.getSaturationModifier();
                                double rem = storedFood + val - data.foodStorage();
                                if (rem <= val * 0.25) {
                                    storedFood = (float) Math.min(storedFood + val, data.foodStorage());
                                    entity.level.playSound(null, entity.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.25F, (0.95F + (entity.level.random.nextFloat() * 0.1F)));
                                    stack.shrink(1);
                                    break;
                                }
                            }
                        }
                    }
                }
                FoodData foodStats = player.getFoodData();
                if (storedFood > 0 && (foodStats.getFoodLevel() < 20 || foodStats.getSaturationLevel() < 20)) {
                    //Feed player
                    TechLevel tech = module.getModuleTechLevel();
                    double maxSat = entity.tickCount % 20 == 0 && tech == TechLevel.DRACONIC ? 20 : 1;//tech == TechLevel.DRACONIUM ? 1 : tech == TechLevel.WYVERN ? 2 : 4; //Problem is i'm not sure if i want this to essentially be a "Regeneration module"
                    if (foodStats.needsFood() && storedFood > 1) {
                        foodStats.eat((int)consumeFood(Math.min(1, 20 - foodStats.getFoodLevel())), 0);
                    }else if (foodStats.getSaturationLevel() < maxSat && storedFood > 0) {
                        foodStats.saturationLevel += consumeFood(Math.min(1, maxSat - foodStats.getSaturationLevel()));
                    }
                }
            }
        }
    }

    private double consumeFood(double amount) {
        amount = Math.min(amount, storedFood);
        storedFood -= amount;
        return amount;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderModule(GuiElement<?> parent, MultiBufferSource getter, PoseStack poseStack, int x, int y, int width, int height, double mouseX, double mouseY, boolean renderStack, float partialTicks) {
        super.renderModule(parent, getter, poseStack, x, y, width, height, mouseX, mouseY, renderStack, partialTicks);
        VertexConsumer builder = BCGuiSprites.builder(getter, poseStack);
        AutoFeedData data = module.getData();
        double progress = storedFood / data.foodStorage();
        progress = (int) (progress * 21F);
        progress = (20 - progress) - 1;
        for (int i = 0; i < 10; i++){
            float size = (width - 3) / 10F;
            GuiHelper.drawSprite(builder, x + 1 + i * size, y + height - size - 2, size + 1, size + 1, BCGuiSprites.get("bars/food_empty").sprite());
            if (progress / 2F <= i){
                if (progress / 2F < i){
                    GuiHelper.drawSprite(builder, x + 1 + i * size, y + height - size - 2, size + 1, size + 1, BCGuiSprites.get("bars/food_full").sprite());
                } else {
                    GuiHelper.drawSprite(builder, x + 1 + i * size, y + height - size - 2, size + 1, size + 1, BCGuiSprites.get("bars/food_half").sprite());
                }
            }
        }
        RenderUtils.endBatch(getter);
    }

    @Override
    public void addToolTip(List<Component> list) {
        list.add(new TranslatableComponent("module.draconicevolution.auto_feed.stored").withStyle(ChatFormatting.GRAY).append(" ").append(new TranslatableComponent("module.draconicevolution.auto_feed.stored.value", (int)storedFood).withStyle(ChatFormatting.DARK_GREEN)));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addHostHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(new TranslatableComponent("module.draconicevolution.auto_feed.stored").withStyle(ChatFormatting.GRAY).append(" ").append(new TranslatableComponent("module.draconicevolution.auto_feed.stored.value", (int)storedFood).withStyle(ChatFormatting.DARK_GREEN)));
        }
    }

    @Override
    protected void readExtraData(CompoundTag tag) {
        storedFood = tag.getFloat("food");
    }

    @Override
    protected CompoundTag writeExtraData(CompoundTag tag) {
        tag.putFloat("food", storedFood);
        return tag;
    }
}
