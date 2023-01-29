package com.brandon3055.draconicevolution.client.render.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerCore;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerRing;

/**
 * Created by brandon3055 on 2/10/2015.
 */
public class RenderStabilizerPart implements IItemRenderer {

    public static ModelReactorStabilizerCore modelBase = new ModelReactorStabilizerCore();
    public static ModelReactorStabilizerCore modelBaseRotors = new ModelReactorStabilizerCore();
    public static ModelReactorStabilizerRing modelRing = new ModelReactorStabilizerRing();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON)
            GL11.glTranslated(0.5, 0.5, 0.5);

        switch (item.getItemDamage()) {
            case 0: // frame
                ResourceHandler.bindResource("textures/models/reactorStabilizerCore.png");
                modelBase.basePlate.render(0.0625F);
                break;
            case 1: // rotor inner
                ResourceHandler.bindResource("textures/models/reactorStabilizerCore.png");
                // GL11.glScaled(2, 2, 2);
                // GL11.glTranslated(-0.2, 0, 0);
                modelBaseRotors.rotor1R.childModels.clear();
                modelBaseRotors.rotor1R.render(0.0625F);
                modelBaseRotors.rotor1R_1.render(0.0625F);
                modelBaseRotors.rotor1R_2.render(0.0625F);
                modelBaseRotors.rotor1R_3.render(0.0625F);
                modelBaseRotors.rotor1R_4.render(0.0625F);
                break;
            case 2: // rotor outer
                ResourceHandler.bindResource("textures/models/reactorStabilizerCore.png");
                // GL11.glScaled(2, 2, 2);
                // GL11.glTranslated(-0.3, 0, 0);
                modelBaseRotors.rotor2R.childModels.clear();
                modelBaseRotors.rotor2R.render(0.0625F);
                modelBaseRotors.rotor2R_1.render(0.0625F);
                modelBaseRotors.rotor2R_2.render(0.0625F);
                modelBaseRotors.rotor2R_3.render(0.0625F);
                modelBaseRotors.rotor2R_4.render(0.0625F);
                break;
            case 3: // rotor assembly
                ResourceHandler.bindResource("textures/models/reactorStabilizerCore.png");
                // GL11.glScaled(1.5, 1.5, 1.5);
                // GL11.glTranslated(-0.05, 0, 0);
                GL11.glRotatef(30F, 0F, 0F, 1F);
                modelBase.rotor1R.render(0.0625F);
                modelBase.hub1.render(0.0625F);
                GL11.glRotatef(60F, 0F, 0F, -1F);
                modelBase.hub2.render(0.0625F);
                modelBase.rotor2R.render(0.0625F);
                break;
            case 4: // stabilizer ring
                ResourceHandler.bindResource("textures/models/reactorStabilizerRing.png");
                GL11.glRotatef(90F, 0F, 0F, 1F);
                modelRing.render(null, -30, 1, 0, 0, 0, 1F / 16F);
                break;
        }

        GL11.glPopMatrix();
    }
}
