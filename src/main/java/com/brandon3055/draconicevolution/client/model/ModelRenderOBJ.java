package com.brandon3055.draconicevolution.client.model;

import com.brandon3055.brandonscore.utils.ModelUtils;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 16/9/2015.
 */
public class ModelRenderOBJ extends ModelRenderer {
    private ResourceLocation customModel;
    //    private CCModel model;
    public ResourceLocation texture;
    private int displayList;
    private boolean compiled = false;
    private IBakedModel objModel;
    public float scale = 0;

    public ModelRenderOBJ(Model baseModel, ResourceLocation customModel, ResourceLocation texture) {
        super(baseModel);
        this.customModel = customModel;

        try {
//            objModel = OBJLoader.INSTANCE.loadModel(customModel).bake(TransformUtils.DEFAULT_TOOL, DefaultVertexFormats.ITEM, TextureUtils::getTexture);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

//        if (model == null) {
//            Map<String, CCModel> map = CCModel.parseObjModels(customModel);
//            model = CCModel.combine(map.values());
//        }

        this.texture = texture;
    }

//    @Override
//    public void render(float scale) {
//        if (!this.isHidden && this.showModel) {
//            if (!this.compiled) {
//                this.compileDisplayList(scale);
//            }
//
//            RenderSystem.translatef(this.offsetX, this.offsetY, this.offsetZ);
//
//            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
//                if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
//                    RenderSystem.callList(this.displayList);
//                }
//                else {
//                    RenderSystem.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
//                    RenderSystem.callList(this.displayList);
//                    RenderSystem.translatef(-this.rotationPointX * scale, -this.rotationPointY * scale, -this.rotationPointZ * scale);
//                }
//            }
//            else {
//                RenderSystem.pushMatrix();
//                RenderSystem.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
//                if (this.rotateAngleZ != 0.0F) {
//                    RenderSystem.rotatef(this.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
//                }
//
//                if (this.rotateAngleY != 0.0F) {
//                    RenderSystem.rotatef(this.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
//                }
//
//                if (this.rotateAngleX != 0.0F) {
//                    RenderSystem.rotatef(this.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
//                }
//
//                RenderSystem.callList(this.displayList);
//                RenderSystem.popMatrix();
//            }
//
//            RenderSystem.translatef(-this.offsetX, -this.offsetY, -this.offsetZ);
//        }
//    }
//
//    private void compileDisplayList(float scale) {
//        if (this.scale == 0) {
//            this.scale = scale;
//        }
//
//        if (objModel == null) {
//            compiled = true;
//            LogHelper.bigError("Armor Model Display List could not be compiled!!! Armor model is broken!");
//            return;
//        }
//
//        scale = this.scale;
//        this.displayList = GLAllocation.generateDisplayLists(1);
//        RenderSystem.newList(this.displayList, GL11.GL_COMPILE);
//
//        RenderSystem.pushMatrix();
//        RenderSystem.scalef(scale, scale, scale);
//        RenderSystem.rotated(180, -1, 0, 1);
//
//        RenderSystem.bindTexture(Minecraft.getInstance().getTextureMap().getGlTextureId());
//        ModelUtils.renderQuads(objModel.getQuads(null, null, ModelUtils.rand));
//
//        RenderSystem.popMatrix();
//
//        RenderSystem.endList();
//        this.compiled = true;
//    }
//
//    @Override
//    public void renderWithRotation(float scale) {
//        if (!this.isHidden && this.showModel) {
//            if (!this.compiled) {
//                this.compileDisplayList(scale);
//            }
//
//            RenderSystem.pushMatrix();
//            RenderSystem.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
//            if (this.rotateAngleY != 0.0F) {
//                RenderSystem.rotatef(this.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
//            }
//
//            if (this.rotateAngleX != 0.0F) {
//                RenderSystem.rotatef(this.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
//            }
//
//            if (this.rotateAngleZ != 0.0F) {
//                RenderSystem.rotatef(this.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
//            }
//
//            RenderSystem.callList(this.displayList);
//            RenderSystem.popMatrix();
//        }
//
//    }
//
//    @Override
//    public void postRender(float scale) {
//        if (!this.isHidden && this.showModel) {
////			if(!this.compiled) {
////				this.compileDisplayList(scale);
////			}
//
//            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
//                if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
//                    RenderSystem.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
//                }
//            }
//            else {
//                RenderSystem.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
//                if (this.rotateAngleZ != 0.0F) {
//                    RenderSystem.rotatef(this.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
//                }
//
//                if (this.rotateAngleY != 0.0F) {
//                    RenderSystem.rotatef(this.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
//                }
//
//                if (this.rotateAngleX != 0.0F) {
//                    RenderSystem.rotatef(this.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
//                }
//            }
//        }
//
//    }
}
