package com.brandon3055.draconicevolution.client.render.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.items.weapons.BowHandler;

/**
 * Created by brandon3055 on 29/10/2015.
 */
public class RenderBowModel implements IItemRenderer {

    private boolean draconic;
    private IModelCustom[] wyvernModels = new IModelCustom[4];
    private IModelCustom[] draconicModels = new IModelCustom[4];
    private IModelCustom arrow;

    public RenderBowModel(boolean draconic) {
        this.draconic = draconic;

        for (int i = 0; i < 4; i++) wyvernModels[i] = AdvancedModelLoader
                .loadModel(ResourceHandler.getResource("models/tools/WyvernBow0" + i + ".obj"));
        for (int i = 0; i < 4; i++) draconicModels[i] = AdvancedModelLoader
                .loadModel(ResourceHandler.getResource("models/tools/DraconicBow0" + i + ".obj"));
        this.arrow = AdvancedModelLoader.loadModel(ResourceHandler.getResource("models/tools/ArrowCommon.obj"));
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false; // type == ItemRenderType.ENTITY && helper == ItemRendererHelper.ENTITY_ROTATION;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();

        // String currentMode = ItemNBTHelper.getString(item, "mode", "rapidfire");
        float j = 0F;
        int selection = 0;
        BowHandler.BowProperties properties = null;

        if (data.length >= 2 && (data[1] instanceof EntityPlayer)) {
            EntityPlayer player = (EntityPlayer) data[1];
            j = (float) player.getItemInUseDuration();
            if (j > 0) {
                properties = new BowHandler.BowProperties(item, player);
                if (j > properties.getDrawTicks()) j = properties.getDrawTicks();
                j /= (float) properties.getDrawTicks();
                int j2 = (int) (j * 3F);

                if (j2 < 0) j2 = 0;
                else if (j2 > 3) j2 = 3;

                selection = j2;
            }
        }

        IModelCustom activeModel;

        if (draconic) {
            activeModel = draconicModels[selection];
            ResourceHandler.bindResource("textures/models/tools/DraconicBow0" + selection + ".png");
        } else {
            activeModel = draconicModels[selection];
            ResourceHandler.bindResource("textures/models/tools/WyvernBow0" + selection + ".png");
        }

        if (activeModel != null) doRender(activeModel, type, j > 0 ? selection : -1, properties);

        GL11.glPopMatrix();
    }

    private void doRender(IModelCustom modelCustom, ItemRenderType type, int drawState,
            BowHandler.BowProperties properties) {

        if (type == ItemRenderType.EQUIPPED) {
            GL11.glScaled(0.8, 0.8, 0.8);
            GL11.glTranslated(0.7, 0, 0.2);
            GL11.glRotatef(87, 0, 0, 1);
            GL11.glRotatef(190, 1, 0, 0);
        } else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glScaled(0.8, 0.8, 0.8);
            GL11.glTranslated(0.7, 0.7, 0.2);
            GL11.glRotatef(130, 0, 0, 1);
            GL11.glRotatef(-90, 1, 0, 0);
        } else if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            GL11.glScalef(6F, 6F, 6F);
            GL11.glRotatef(90, 1, 0, 0);
            GL11.glRotatef(135, 0, 1, 0);
            GL11.glTranslated(0, 0, 1.5);
        } else if (type == IItemRenderer.ItemRenderType.ENTITY) {
            GL11.glScaled(0.8, 0.8, 0.8);
            GL11.glTranslated(0.25, 0.7, 0.2);
            GL11.glRotatef(130, 0, 0, 1);
            GL11.glRotatef(-90, 1, 0, 0);
        }

        modelCustom.renderAll();

        if (drawState != -1) {
            GL11.glTranslated(
                    0.3,
                    0.151,
                    -0.2 + (drawState == 1 ? 0 : drawState == 2 ? 0.55 : drawState == 3 ? 1 : -0.7));
            GL11.glRotatef(90, 0, 0, 1);

            if (properties != null && properties.energyBolt) {
                ResourceHandler.bindResource("textures/models/reactorCore.png");
                arrow.renderAll();

                GL11.glTranslated(0, -0.025, 0);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glColor4f(1F, 1F, 1F, 0.6F);
                GL11.glScaled(1.05, 1.05, 1.05);

                GL11.glColor4f(1F, 1F, 1F, 0.4F);
                GL11.glScaled(1.05, 1.05, 1.05);
                arrow.renderAll();
            } else {
                ResourceHandler.bindResource("textures/models/tools/ArrowCommon.png");
                arrow.renderAll();
            }
        }
    }
}
