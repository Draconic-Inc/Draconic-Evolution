package com.brandon3055.draconicevolution.api.modules.entities;

import codechicken.lib.gui.modular.elements.GuiElement;
import codechicken.lib.gui.modular.lib.GuiRender;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.AutoFeedData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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
                                    entity.level().playSound(null, entity.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.25F, (0.95F + (entity.level().random.nextFloat() * 0.1F)));
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
                        foodStats.setSaturation(foodStats.getSaturationLevel() + (float) consumeFood(Math.min(1, maxSat - foodStats.getSaturationLevel())));
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
    public void renderModule(GuiElement<?> parent, GuiRender render, int x, int y, int width, int height, double mouseX, double mouseY, boolean renderStack, float partialTicks) {
        super.renderModule(parent, render, x, y, width, height, mouseX, mouseY, renderStack, partialTicks);
        AutoFeedData data = module.getData();
        double progress = storedFood / data.foodStorage();
        progress = (int) (progress * 21F);
        progress = (20 - progress) - 1;
        for (int i = 0; i < 10; i++){
            float size = (width - 3) / 10F;
            render.texRect(BCGuiTextures.get("bars/food_empty"), x + 1 + i * size, y + height - size - 2, size + 1, size + 1);
            if (progress / 2F <= i){
                if (progress / 2F < i){
                    render.texRect(BCGuiTextures.get("bars/food_full"), x + 1 + i * size, y + height - size - 2, size + 1, size + 1);
                } else {
                    render.texRect(BCGuiTextures.get("bars/food_half"), x + 1 + i * size, y + height - size - 2, size + 1, size + 1);
                }
            }
        }
    }

    @Override
    public void addToolTip(List<Component> list) {
        list.add(Component.translatable("module.draconicevolution.auto_feed.stored").withStyle(ChatFormatting.GRAY).append(" ").append(Component.translatable("module.draconicevolution.auto_feed.stored.value", (int)storedFood).withStyle(ChatFormatting.DARK_GREEN)));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addHostHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("module.draconicevolution.auto_feed.stored").withStyle(ChatFormatting.GRAY).append(" ").append(Component.translatable("module.draconicevolution.auto_feed.stored.value", (int)storedFood).withStyle(ChatFormatting.DARK_GREEN)));
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
