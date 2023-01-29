package com.brandon3055.draconicevolution.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;

/**
 * Created by brandon3055 on 16/9/2015.
 */
public class ModelRenderOBJ extends ModelRenderer {

    private IModelCustom model;
    private ResourceLocation texture;
    private int displayList;
    private boolean compiled = false;
    public float scale = 0;

    public ModelRenderOBJ(ModelBase baseModel, ResourceLocation customModel, ResourceLocation texture) {
        super(baseModel);
        this.model = AdvancedModelLoader.loadModel(customModel);
        this.texture = texture;
    }

    @Override
    public void render(float scale) {
        if (!this.isHidden && this.showModel) {
            if (!this.compiled) {
                this.compileDisplayList(scale);
            }

            GL11.glTranslatef(this.offsetX, this.offsetY, this.offsetZ);

            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
                if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
                    GL11.glCallList(this.displayList);
                } else {
                    GL11.glTranslatef(
                            this.rotationPointX * scale,
                            this.rotationPointY * scale,
                            this.rotationPointZ * scale);
                    GL11.glCallList(this.displayList);
                    GL11.glTranslatef(
                            -this.rotationPointX * scale,
                            -this.rotationPointY * scale,
                            -this.rotationPointZ * scale);
                }
            } else {
                GL11.glPushMatrix();
                GL11.glTranslatef(
                        this.rotationPointX * scale,
                        this.rotationPointY * scale,
                        this.rotationPointZ * scale);
                if (this.rotateAngleZ != 0.0F) {
                    GL11.glRotatef(this.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
                }

                if (this.rotateAngleY != 0.0F) {
                    GL11.glRotatef(this.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleX != 0.0F) {
                    GL11.glRotatef(this.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
                }

                GL11.glCallList(this.displayList);
                GL11.glPopMatrix();
            }

            GL11.glTranslatef(-this.offsetX, -this.offsetY, -this.offsetZ);
        }
    }

    private void compileDisplayList(float scale) {
        if (this.scale == 0) this.scale = scale;
        scale = this.scale;
        this.displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(this.displayList, GL11.GL_COMPILE);

        GL11.glPushMatrix();
        ResourceHandler.bindTexture(texture);
        // scale = 1F/15F;
        GL11.glScalef(scale, scale, scale);
        GL11.glRotatef(180, -1, 0, 1);
        model.renderAll();
        GL11.glPopMatrix();

        GL11.glEndList();
        this.compiled = true;
    }

    @Override
    public void renderWithRotation(float scale) {
        if (!this.isHidden && this.showModel) {
            if (!this.compiled) {
                this.compileDisplayList(scale);
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
            if (this.rotateAngleY != 0.0F) {
                GL11.glRotatef(this.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
                GL11.glRotatef(this.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
            }

            if (this.rotateAngleZ != 0.0F) {
                GL11.glRotatef(this.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
            }

            GL11.glCallList(this.displayList);
            GL11.glPopMatrix();
        }
    }

    @Override
    public void postRender(float scale) {
        if (!this.isHidden && this.showModel) {
            // if(!this.compiled) {
            // this.compileDisplayList(scale);
            // }

            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
                if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
                    GL11.glTranslatef(
                            this.rotationPointX * scale,
                            this.rotationPointY * scale,
                            this.rotationPointZ * scale);
                }
            } else {
                GL11.glTranslatef(
                        this.rotationPointX * scale,
                        this.rotationPointY * scale,
                        this.rotationPointZ * scale);
                if (this.rotateAngleZ != 0.0F) {
                    GL11.glRotatef(this.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
                }

                if (this.rotateAngleY != 0.0F) {
                    GL11.glRotatef(this.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleX != 0.0F) {
                    GL11.glRotatef(this.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
                }
            }
        }
    }
}
