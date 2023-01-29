package com.brandon3055.draconicevolution.client.render.item;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

/**
 * Created by brandon3055 on 17/8/2015.
 */
public class RenderArmor implements IItemRenderer {

    private ItemArmor armor;

    public RenderArmor() {}

    public RenderArmor(ItemArmor armor) {
        this.armor = armor;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (ConfigHandler.useOldArmorModel) {
            LogHelper.error("You must restart the game for armor model change to effect the armor items!!!");
            return;
        }

        GL11.glPushMatrix();
        ResourceHandler
                .bindResource(armor.getArmorTexture(stack, null, 0, null).replace(References.RESOURCESPREFIX, ""));

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.EQUIPPED) {
            GL11.glTranslated(0.5, 0.5, 0.5);
            GL11.glRotated(180, 0, 1, 0);
        }
        GL11.glTranslated(
                0,
                armor.armorType == 0 ? -0.25 : armor.armorType == 1 ? 0.42 : armor.armorType == 2 ? 1.05 : 1.5,
                0);
        GL11.glRotated(180, -1, 0, 1);
        armor.getArmorModel(null, stack, 0).render(null, 0f, 0f, 0f, 0f, 0f, 0.0625f);

        GL11.glPopMatrix();
    }
}
