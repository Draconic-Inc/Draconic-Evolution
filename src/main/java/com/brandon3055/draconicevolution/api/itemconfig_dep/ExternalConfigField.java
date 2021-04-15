package com.brandon3055.draconicevolution.api.itemconfig_dep;

import com.brandon3055.brandonscore.inventory.PlayerSlot;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by brandon3055 on 1/06/2016.
 * This can be used to create a config button that opens a separate gui.
 */
@Deprecated
public class ExternalConfigField extends IntegerConfigField {

    private final Object mod;
    private final int guiID;
    private final String unloacalizedButtonText;

    public ExternalConfigField(String name, String description, Object mod, int guiID, String buttonText) {
        super(name, 0, 0, 1, description, EnumControlType.TOGGLE);
        this.mod = mod;
        this.guiID = guiID;
        this.unloacalizedButtonText = buttonText;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public String getReadableValue() {
        return I18n.get(unloacalizedButtonText);
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {}

    @Override
    public void readFromNBT(CompoundNBT compound) {}

    @Override
    public void handleButton(EnumButton button, int data, PlayerEntity player, PlayerSlot slot) {
        //TODO Gui Stuff
//        player.openGui(mod, guiID, player.world, slot.getSlotIndex(), slot.getCatIndex(), 0);
    }
}
