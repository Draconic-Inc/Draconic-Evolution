package com.brandon3055.draconicevolution.api.config;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by brandon3055 on 2/5/20.
 */
public abstract class AbstractProperty<T extends AbstractProperty<T>> implements ConfigProperty {

    private String name;
    private ITextComponent displayName;
    private boolean showOnHud = true;
    private BiConsumer<ItemStack, T> changeListener;

    public AbstractProperty(String name) {
        this.name = name;
    }

    public AbstractProperty(String name, ITextComponent displayName) {
        this(name);
        this.displayName = displayName;
    }

    @Override
    public ITextComponent getDisplayName() {
        return displayName == null ? new TranslationTextComponent("item_property.draconicevolution." + name + ".name") : displayName;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setShowOnHud(boolean showOnHud) {
        this.showOnHud = showOnHud;
    }

    @Override
    public boolean showOnHud() {
        return showOnHud;
    }

    @Override
    public void onValueChanged(ItemStack stack) {
        if (changeListener != null) {
            changeListener.accept(stack, (T) this);
        }
    }

    public void setChangeListener(Runnable changeListener) {
        this.changeListener = (stack, t) -> changeListener.run();
    }

    public void setChangeListener(BiConsumer<ItemStack, T> changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("hud", showOnHud);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        showOnHud = nbt.getBoolean("hud");
    }

    @Override
    public void serializeMCData(MCDataOutput output) {
        output.writeBoolean(showOnHud);
    }

    @Override
    public void deSerializeMCData(MCDataInput input) {
        showOnHud = input.readBoolean();
    }
}
