package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCOBJParser;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Scale;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 20/05/2016.
 */
public class RenderTileEnergyPylon extends TESRBase<TileEnergyPylon> {

    private static CCModel model;

    public RenderTileEnergyPylon() {
        Map<String, CCModel> map = CCOBJParser.parseObjModels(ResourceHelperDE.getResource("models/pylonSphere.obj")); //Note dont generate the model evey render frame move this to constructor
        model = CCModel.combine(map.values());
        model.apply(new Scale(0.35, 0.35, 0.35));
    }

    @Override
    public void renderTileEntityAt(TileEnergyPylon te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (!te.structureValid.value){
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + (te.sphereOnTop.value ? 1.5 : - 0.5), z + 0.5);
        ResourceHelperDE.bindTexture("textures/models/pylonSphereTexture.png");
        setLighting(200F);

        GlStateManager.rotate((ClientEventHandler.elapsedTicks + partialTicks) * 2F, 0, 1, 0.5f);

        if (MinecraftForgeClient.getRenderPass() == 0) {
            GlStateManager.disableCull();

            CCRenderState.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);

//            Matrix4 mat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 1.5, z + 0.5), new Rotation((ClientEventHandler.elapsedTicks + partialTicks), 1, 0, 0), 1);

            model.computeNormals();
            model.render();//new Rotation((ClientEventHandler.elapsedTicks + partialTicks) * 2F, 1, 0, 1), new Translation(x + 0.5, y + 1.5, z + 0.5));
            CCRenderState.draw();
            GlStateManager.enableCull();

        } else {
            float f = ((ClientEventHandler.elapsedTicks + partialTicks) % 30F) / 30F;

            if (te.isOutputMode.value){
                f = 1F - f;
            }

            GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
            GlStateManager.color(1F, 1F, 1F, 1F - f);
            GlStateManager.scale(1 + f, 1 + f, 1 + f);
            GlStateManager.enableBlend();

            CCRenderState.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
            model.computeNormals();
            model.render();
            CCRenderState.draw();

            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.disableBlend();
        }

        GlStateManager.popMatrix();
    }
}

/*
* Something to remember http://puu.sh/oXKUf/7038543b55.jpg
        GlStateManager.pushMatrix();
        //GlStateManager.disableCull();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        setLighting(200F);
        //GlStateManager.translate(x, y + 2, z);


        ResourceHelperDE.bindTexture("textures/models/pylonSphereTexture.png");

        CCRenderState.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
        CCRenderState.pullBuffer();
        Map<String, CCModel> map = CCModel.parseObjModels(ResourceHelperDE.getResource("models/pylonSphere.obj")); //Note dont generate the model evey render frame move this to constructor
        CCModel model = CCModel.combine(map.values());
        model.apply(new Scale(0.5, 0.5, 0.5));
        model.render(x + 0.5, y + 1.5, z + 0.5, 0, 0);
        CCRenderState.draw();








        resetLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();


        Because GlStateManager.enableBlend();
* */
