package com.brandon3055.draconicevolution.api.modules.entities;

import codechicken.lib.gui.modular.elements.GuiElement;
import codechicken.lib.gui.modular.lib.GuiRender;
import com.brandon3055.brandonscore.api.BCStreamCodec;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.draconicevolution.api.config.BooleanProperty;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.AutoFeedData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.init.ItemData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class AutoFeedEntity extends ModuleEntity<AutoFeedData> {

    private BooleanProperty consumeFood = new BooleanProperty("feed_mod.consume_food", true).setFormatter(ConfigProperty.BooleanFormatter.YES_NO);
    private double storedFood = 0;

    public static final Codec<AutoFeedEntity> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DEModules.codec().fieldOf("module").forGetter(ModuleEntity::getModule),
            Codec.INT.fieldOf("gridx").forGetter(ModuleEntity::getGridX),
            Codec.INT.fieldOf("gridy").forGetter(ModuleEntity::getGridY),
            Codec.DOUBLE.fieldOf("stored_food").forGetter(e -> e.storedFood),
            BooleanProperty.CODEC.fieldOf("consume_food").forGetter(e -> e.consumeFood)
    ).apply(builder, AutoFeedEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AutoFeedEntity> STREAM_CODEC = BCStreamCodec.composite(
            DEModules.streamCodec(), ModuleEntity::getModule,
            ByteBufCodecs.INT, ModuleEntity::getGridX,
            ByteBufCodecs.INT, ModuleEntity::getGridY,
            ByteBufCodecs.DOUBLE, e -> e.storedFood,
            BooleanProperty.STREAM_CODEC, e -> e.consumeFood,
            AutoFeedEntity::new
    );

    public AutoFeedEntity(Module<AutoFeedData> module) {
        super(module);
//        addProperty(consumeFood = new BooleanProperty("feed_mod.consume_food", true).setFormatter(ConfigProperty.BooleanFormatter.YES_NO));
//        this.savePropertiesToItem = true;
    }

    AutoFeedEntity(Module<?> module, int gridX, int gridY, double storedFood, BooleanProperty consumeFood) {
        super((Module<AutoFeedData>) module, gridX, gridY);
        this.consumeFood = consumeFood;
        this.storedFood = storedFood;
    }

    @Override
    public ModuleEntity<?> copy() {
        return new AutoFeedEntity(module, getGridX(), getGridY(), getStoredFood(), consumeFood.copy());
    }

    public void setStoredFood(double storedFood) {
        if (this.storedFood != storedFood) markDirty();
        this.storedFood = storedFood;
    }

    public double getStoredFood() {
        return storedFood;
    }

    @Override
    public void getEntityProperties(List<ConfigProperty> properties) {
        super.getEntityProperties(properties);
        properties.add(consumeFood);
    }

    @Override
    public void tick(ModuleContext context) {
        AutoFeedData data = module.getData();
        if (context instanceof StackModuleContext) {
            LivingEntity entity = ((StackModuleContext) context).getEntity();
            if (entity instanceof ServerPlayer player && entity.tickCount % 10 == 0 && ((StackModuleContext) context).isEquipped()) {
                if (getStoredFood() < data.foodStorage() && consumeFood.getValue()) {
                    //Do food consumption
                    for (ItemStack stack : player.getInventory().items) {
                        FoodProperties food = stack.getFoodProperties(player);
                        if (!stack.isEmpty() && food != null) {
                            if (food.nutrition() > 0 && food.effects().isEmpty()) {
                                double val = food.nutrition() + food.saturation();
                                double rem = getStoredFood() + val - data.foodStorage();
                                if (rem <= val * 0.25) {
                                    setStoredFood((float) Math.min(getStoredFood() + val, data.foodStorage()));
                                    entity.level().playSound(null, entity.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.25F, (0.95F + (entity.level().random.nextFloat() * 0.1F)));
                                    stack.shrink(1);
                                    break;
                                }
                            }
                        }
                    }
                }
                FoodData foodStats = player.getFoodData();
                if (getStoredFood() > 0 && (foodStats.getFoodLevel() < 20 || foodStats.getSaturationLevel() < 20)) {
                    //Feed player
                    TechLevel tech = module.getModuleTechLevel();
                    double maxSat = entity.tickCount % 20 == 0 && tech == TechLevel.DRACONIC ? 20 : 1;//tech == TechLevel.DRACONIUM ? 1 : tech == TechLevel.WYVERN ? 2 : 4; //Problem is i'm not sure if i want this to essentially be a "Regeneration module"
                    if (foodStats.needsFood() && getStoredFood() > 1) {
                        foodStats.eat((int)consumeFood(Math.min(1, 20 - foodStats.getFoodLevel())), 0);
                    }else if (foodStats.getSaturationLevel() < maxSat && getStoredFood() > 0) {
                        foodStats.setSaturation(foodStats.getSaturationLevel() + (float) consumeFood(Math.min(1, maxSat - foodStats.getSaturationLevel())));
                    }
                }
            }
        }
    }

    private double consumeFood(double amount) {
        amount = Math.min(amount, getStoredFood());
        setStoredFood(getStoredFood() - amount);
        return amount;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderModule(GuiElement<?> parent, GuiRender render, int x, int y, int width, int height, double mouseX, double mouseY, boolean renderStack, float partialTicks) {
        super.renderModule(parent, render, x, y, width, height, mouseX, mouseY, renderStack, partialTicks);
        AutoFeedData data = module.getData();
        double progress = getStoredFood() / data.foodStorage();
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
        list.add(Component.translatable("module.draconicevolution.auto_feed.stored").withStyle(ChatFormatting.GRAY).append(" ").append(Component.translatable("module.draconicevolution.auto_feed.stored.value", (int)getStoredFood()).withStyle(ChatFormatting.DARK_GREEN)));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addHostHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("module.draconicevolution.auto_feed.stored").withStyle(ChatFormatting.GRAY).append(" ").append(Component.translatable("module.draconicevolution.auto_feed.stored.value", (int)getStoredFood()).withStyle(ChatFormatting.DARK_GREEN)));
        }
    }

    @Override
    public void saveEntityToStack(ItemStack stack, ModuleContext context) {
        stack.set(ItemData.AUTO_FEED_MODULE_FOOD, getStoredFood());
        stack.set(ItemData.BOOL_ITEM_PROP_1, consumeFood.copy());
    }

    @Override
    public void loadEntityFromStack(ItemStack stack, ModuleContext context) {
        setStoredFood(stack.getOrDefault(ItemData.AUTO_FEED_MODULE_FOOD, 0D));
        consumeFood = stack.getOrDefault(ItemData.BOOL_ITEM_PROP_1, consumeFood).copy();
    }
}
