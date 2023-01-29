package com.brandon3055.draconicevolution.client.model.special;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

/**
 * Staff of the Great One - brandon3055 Created using Tabula 5.0.0
 */
public class ModelStaffOfTheGreatOne extends ModelBase {

    public double[] modelScale = new double[] { 2.0D, 2.0D, 2.0D };
    public ModelRenderer staff;
    public ModelRenderer edge1;
    public ModelRenderer edge1_1;
    public ModelRenderer edge1_2;
    public ModelRenderer edge1_3;
    public ModelRenderer edge2;
    public ModelRenderer edge2_1;
    public ModelRenderer edge2_2;
    public ModelRenderer edge2_3;
    public ModelRenderer edge3;
    public ModelRenderer edge3_1;
    public ModelRenderer edge3_2;
    public ModelRenderer edge3_3;
    public ModelRenderer edge4;
    public ModelRenderer edge4_1;
    public ModelRenderer edge4_2;
    public ModelRenderer edge5;
    public ModelRenderer edge5_1;
    public ModelRenderer edge5_2;
    public ModelRenderer edge5_3;
    public ModelRenderer edge5_4;
    public ModelRenderer topedge1;
    public ModelRenderer topedge1_1;
    public ModelRenderer topedge1_2;
    public ModelRenderer topedge1_3;
    public ModelRenderer topedge2;
    public ModelRenderer topedge2_1;
    public ModelRenderer topedge2_2;
    public ModelRenderer topedge2_3;
    public ModelRenderer topedge3;
    public ModelRenderer topedge3_1;
    public ModelRenderer topedge3_2;
    public ModelRenderer topedge3_3;
    public ModelRenderer topedge4;
    public ModelRenderer topedge4_1;
    public ModelRenderer topedge4_2;
    public ModelRenderer topedge4_3;
    public ModelRenderer topedge5;
    public ModelRenderer topedge5_1;
    public ModelRenderer topedge5_2;
    public ModelRenderer topedge5_3;
    public ModelRenderer tope1;
    public ModelRenderer tope2;
    public ModelRenderer shape2;
    public ModelRenderer staffTopQ;
    public ModelRenderer staffTopQ_1;
    public ModelRenderer staffTopQ_2;
    public ModelRenderer staffTopM;
    public ModelRenderer staffTopM_1;
    public ModelRenderer staffTopM_2;
    public ModelRenderer staffTopM_3;
    public ModelRenderer staffTopM_4;
    public ModelRenderer shape2_1;
    public ModelRenderer shape2_2;
    public ModelRenderer staffTopQ_3;
    public ModelRenderer shape2_3;
    public ModelRenderer shape3;
    public ModelRenderer shape3_1;
    public ModelRenderer shape3_2;
    public ModelRenderer shape3_3;
    public ModelRenderer shape4;
    public ModelRenderer shape4_1;
    public ModelRenderer shape4_2;
    public ModelRenderer shape4_3;
    public ModelRenderer shape5;
    public ModelRenderer shape5_1;
    public ModelRenderer shape5_2;
    public ModelRenderer shape5_3;
    public ModelRenderer shape6;
    public ModelRenderer shape6_1;
    public ModelRenderer shape6_2;
    public ModelRenderer shape6_3;
    public ModelRenderer shape7;
    public ModelRenderer shape7_1;
    public ModelRenderer shape7_2;
    public ModelRenderer shape7_3;
    public ModelRenderer tope3;

    public ModelStaffOfTheGreatOne() {
        this.textureWidth = 32;
        this.textureHeight = 64;
        this.edge1 = new ModelRenderer(this, 6, 0);
        this.edge1.setRotationPoint(1.05F, -4.5F, -1.05F);
        this.edge1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.staffTopM_3 = new ModelRenderer(this, 12, 19);
        this.staffTopM_3.setRotationPoint(0.7F, 0.7F, 0.0F);
        this.staffTopM_3.addBox(-0.5F, -4.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(staffTopM_3, 0.0F, 0.0F, -0.2617993877991494F);
        this.edge2_1 = new ModelRenderer(this, 26, 0);
        this.edge2_1.setRotationPoint(-1.15F, -5.2F, -1.15F);
        this.edge2_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape5_1 = new ModelRenderer(this, 12, 26);
        this.shape5_1.setRotationPoint(-0.85F, -2.4F, -0.85F);
        this.shape5_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape4_2 = new ModelRenderer(this, 19, 25);
        this.shape4_2.setRotationPoint(-0.8F, -1.7F, 0.8F);
        this.shape4_2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.tope2 = new ModelRenderer(this, 27, 13);
        this.tope2.setRotationPoint(0.0F, -10.9F, 0.0F);
        this.tope2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.tope1 = new ModelRenderer(this, 24, 12);
        this.tope1.setRotationPoint(0.0F, -10.4F, 0.0F);
        this.tope1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape7_2 = new ModelRenderer(this, 19, 29);
        this.shape7_2.setRotationPoint(-0.95F, -3.8F, 0.95F);
        this.shape7_2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape4 = new ModelRenderer(this, 16, 24);
        this.shape4.setRotationPoint(0.8F, -1.7F, 0.8F);
        this.shape4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape2_2 = new ModelRenderer(this, 23, 20);
        this.shape2_2.setRotationPoint(0.7F, -0.3F, 0.7F);
        this.shape2_2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge2_2 = new ModelRenderer(this, 16, 8);
        this.topedge2_2.setRotationPoint(1.15F, -8.4F, 1.15F);
        this.topedge2_2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge4_1 = new ModelRenderer(this, 20, 10);
        this.topedge4_1.setRotationPoint(0.73F, -9.4F, 0.73F);
        this.topedge4_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge5 = new ModelRenderer(this, 8, 12);
        this.topedge5.setRotationPoint(-0.5F, -9.9F, 0.5F);
        this.topedge5.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge4_2 = new ModelRenderer(this, 24, 10);
        this.topedge4_2.setRotationPoint(0.73F, -9.4F, -0.73F);
        this.topedge4_2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape6_2 = new ModelRenderer(this, 8, 28);
        this.shape6_2.setRotationPoint(-0.9F, -3.1F, -0.9F);
        this.shape6_2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.edge3 = new ModelRenderer(this, 16, 2);
        this.edge3.setRotationPoint(1.25F, -5.9F, 1.25F);
        this.edge3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge4_3 = new ModelRenderer(this, 27, 11);
        this.topedge4_3.setRotationPoint(-0.77F, -9.4F, -0.73F);
        this.topedge4_3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape5_3 = new ModelRenderer(this, 22, 26);
        this.shape5_3.setRotationPoint(0.85F, -2.4F, -0.85F);
        this.shape5_3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape4_3 = new ModelRenderer(this, 25, 25);
        this.shape4_3.setRotationPoint(0.8F, -1.7F, -0.8F);
        this.shape4_3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge3 = new ModelRenderer(this, 24, 8);
        this.topedge3.setRotationPoint(-0.95F, -8.9F, 0.95F);
        this.topedge3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge4 = new ModelRenderer(this, 16, 10);
        this.topedge4.setRotationPoint(-0.73F, -9.4F, 0.73F);
        this.topedge4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.edge5_2 = new ModelRenderer(this, 27, 5);
        this.edge5_2.setRotationPoint(1.45F, -7.3F, 1.45F);
        this.edge5_2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.edge5 = new ModelRenderer(this, 20, 4);
        this.edge5.setRotationPoint(-1.35F, -6.6F, 1.35F);
        this.edge5.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge2_3 = new ModelRenderer(this, 20, 8);
        this.topedge2_3.setRotationPoint(1.15F, -8.4F, -1.15F);
        this.topedge2_3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape6_3 = new ModelRenderer(this, 12, 28);
        this.shape6_3.setRotationPoint(-0.9F, -3.1F, 0.9F);
        this.shape6_3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.edge1_1 = new ModelRenderer(this, 10, 0);
        this.edge1_1.setRotationPoint(-1.05F, -4.5F, -1.05F);
        this.edge1_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape5_2 = new ModelRenderer(this, 16, 26);
        this.shape5_2.setRotationPoint(-0.85F, -2.4F, 0.85F);
        this.shape5_2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge5_2 = new ModelRenderer(this, 16, 12);
        this.topedge5_2.setRotationPoint(0.5F, -9.9F, -0.5F);
        this.topedge5_2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge2 = new ModelRenderer(this, 8, 8);
        this.topedge2.setRotationPoint(-1.15F, -8.4F, -1.15F);
        this.topedge2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.staffTopQ_3 = new ModelRenderer(this, 8, 21);
        this.staffTopQ_3.setRotationPoint(0.65F, -0.3F, 0.65F);
        this.staffTopQ_3.addBox(-0.5F, -3.2F, -0.5F, 1, 4, 1, 0.0F);
        this.edge4_2 = new ModelRenderer(this, 16, 4);
        this.edge4_2.setRotationPoint(-1.35F, -6.6F, -1.35F);
        this.edge4_2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.staffTopQ_1 = new ModelRenderer(this, 16, 14);
        this.staffTopQ_1.setRotationPoint(-0.65F, -0.3F, -0.65F);
        this.staffTopQ_1.addBox(-0.5F, -3.2F, -0.5F, 1, 4, 1, 0.0F);
        this.edge3_3 = new ModelRenderer(this, 27, 3);
        this.edge3_3.setRotationPoint(-1.25F, -5.9F, 1.25F);
        this.edge3_3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape3 = new ModelRenderer(this, 23, 22);
        this.shape3.setRotationPoint(0.75F, -1.0F, 0.75F);
        this.shape3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape2_3 = new ModelRenderer(this, 20, 21);
        this.shape2_3.setRotationPoint(0.7F, -0.3F, -0.7F);
        this.shape2_3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge1 = new ModelRenderer(this, 16, 6);
        this.topedge1.setRotationPoint(-1.35F, -7.9F, 1.35F);
        this.topedge1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge5_1 = new ModelRenderer(this, 12, 12);
        this.topedge5_1.setRotationPoint(0.5F, -9.9F, 0.5F);
        this.topedge5_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape7_3 = new ModelRenderer(this, 25, 29);
        this.shape7_3.setRotationPoint(-0.95F, -3.8F, -0.95F);
        this.shape7_3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.staffTopM_2 = new ModelRenderer(this, 27, 18);
        this.staffTopM_2.setRotationPoint(0.0F, 0.7F, 0.7F);
        this.staffTopM_2.addBox(-0.5F, -4.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(staffTopM_2, 0.2617993877991494F, 0.0F, 0.0F);
        this.shape3_3 = new ModelRenderer(this, 12, 24);
        this.shape3_3.setRotationPoint(0.75F, -1.0F, -0.75F);
        this.shape3_3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape3_1 = new ModelRenderer(this, 19, 23);
        this.shape3_1.setRotationPoint(-0.75F, -1.0F, -0.75F);
        this.shape3_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape6_1 = new ModelRenderer(this, 25, 27);
        this.shape6_1.setRotationPoint(0.9F, -3.1F, -0.9F);
        this.shape6_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape7_1 = new ModelRenderer(this, 22, 28);
        this.shape7_1.setRotationPoint(0.95F, -3.8F, -0.95F);
        this.shape7_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge1_3 = new ModelRenderer(this, 27, 7);
        this.topedge1_3.setRotationPoint(1.35F, -7.9F, 1.35F);
        this.topedge1_3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.tope3 = new ModelRenderer(this, 8, 30);
        this.tope3.setRotationPoint(0.0F, -11.4F, 0.0F);
        this.tope3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape5 = new ModelRenderer(this, 8, 26);
        this.shape5.setRotationPoint(0.85F, -2.4F, 0.85F);
        this.shape5.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge3_1 = new ModelRenderer(this, 27, 9);
        this.topedge3_1.setRotationPoint(0.95F, -8.9F, -0.95F);
        this.topedge3_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge5_3 = new ModelRenderer(this, 20, 12);
        this.topedge5_3.setRotationPoint(-0.5F, -9.9F, -0.5F);
        this.topedge5_3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.edge5_3 = new ModelRenderer(this, 8, 6);
        this.edge5_3.setRotationPoint(-1.45F, -7.3F, 1.45F);
        this.edge5_3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.edge3_1 = new ModelRenderer(this, 20, 2);
        this.edge3_1.setRotationPoint(1.25F, -5.9F, -1.25F);
        this.edge3_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape7 = new ModelRenderer(this, 16, 28);
        this.shape7.setRotationPoint(0.95F, -3.8F, 0.95F);
        this.shape7.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge1_1 = new ModelRenderer(this, 20, 6);
        this.topedge1_1.setRotationPoint(-1.35F, -7.9F, -1.35F);
        this.topedge1_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape2_1 = new ModelRenderer(this, 20, 19);
        this.shape2_1.setRotationPoint(-0.7F, -0.3F, -0.7F);
        this.shape2_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.edge2_3 = new ModelRenderer(this, 12, 2);
        this.edge2_3.setRotationPoint(-1.15F, -5.2F, 1.15F);
        this.edge2_3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge3_2 = new ModelRenderer(this, 8, 10);
        this.topedge3_2.setRotationPoint(-0.95F, -8.9F, -0.95F);
        this.topedge3_2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.edge1_2 = new ModelRenderer(this, 14, 0);
        this.edge1_2.setRotationPoint(1.05F, -4.5F, 1.05F);
        this.edge1_2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.edge2_2 = new ModelRenderer(this, 8, 2);
        this.edge2_2.setRotationPoint(1.15F, -5.2F, 1.15F);
        this.edge2_2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.edge2 = new ModelRenderer(this, 22, 0);
        this.edge2.setRotationPoint(1.15F, -5.2F, -1.15F);
        this.edge2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.staffTopM = new ModelRenderer(this, 24, 14);
        this.staffTopM.setRotationPoint(-0.7F, 0.7F, 0.0F);
        this.staffTopM.addBox(-0.5F, -4.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(staffTopM, 0.0F, 0.0F, 0.2617993877991494F);
        this.edge5_1 = new ModelRenderer(this, 24, 4);
        this.edge5_1.setRotationPoint(-1.45F, -7.3F, -1.45F);
        this.edge5_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.staffTopQ = new ModelRenderer(this, 12, 14);
        this.staffTopQ.setRotationPoint(-0.65F, -0.3F, 0.65F);
        this.staffTopQ.addBox(-0.5F, -3.2F, -0.5F, 1, 4, 1, 0.0F);
        this.edge4_1 = new ModelRenderer(this, 12, 4);
        this.edge4_1.setRotationPoint(1.35F, -6.6F, -1.35F);
        this.edge4_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.staffTopM_1 = new ModelRenderer(this, 8, 16);
        this.staffTopM_1.setRotationPoint(0.7F, 0.7F, 0.0F);
        this.staffTopM_1.addBox(-0.5F, -4.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(staffTopM_1, 0.006283185307179587F, 0.0F, -0.2617993877991494F);
        this.edge3_2 = new ModelRenderer(this, 24, 2);
        this.edge3_2.setRotationPoint(-1.25F, -5.9F, -1.25F);
        this.edge3_2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.staffTopQ_2 = new ModelRenderer(this, 20, 14);
        this.staffTopQ_2.setRotationPoint(0.65F, -0.3F, -0.65F);
        this.staffTopQ_2.addBox(-0.5F, -3.2F, -0.5F, 1, 4, 1, 0.0F);
        this.staffTopM_4 = new ModelRenderer(this, 16, 19);
        this.staffTopM_4.setRotationPoint(0.0F, 0.7F, -0.7F);
        this.staffTopM_4.addBox(-0.5F, -4.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(staffTopM_4, -0.2617993877991494F, 0.0F, 0.0F);
        this.topedge3_3 = new ModelRenderer(this, 12, 10);
        this.topedge3_3.setRotationPoint(0.95F, -8.9F, 0.95F);
        this.topedge3_3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape3_2 = new ModelRenderer(this, 26, 23);
        this.shape3_2.setRotationPoint(-0.75F, -1.0F, 0.75F);
        this.shape3_2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.edge1_3 = new ModelRenderer(this, 18, 0);
        this.edge1_3.setRotationPoint(-1.05F, -4.5F, 1.05F);
        this.edge1_3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge2_1 = new ModelRenderer(this, 12, 8);
        this.topedge2_1.setRotationPoint(-1.15F, -8.4F, 1.15F);
        this.topedge2_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape2 = new ModelRenderer(this, 8, 14);
        this.shape2.setRotationPoint(-0.7F, -0.3F, 0.7F);
        this.shape2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.staff = new ModelRenderer(this, 0, 0);
        this.staff.setRotationPoint(0.0F, 0.5F, 0.0F);
        this.staff.addBox(-1.0F, 0.0F, -1.0F, 2, 40, 2, 0.0F);
        this.shape6 = new ModelRenderer(this, 19, 27);
        this.shape6.setRotationPoint(0.9F, -3.1F, 0.9F);
        this.shape6.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.edge4 = new ModelRenderer(this, 8, 4);
        this.edge4.setRotationPoint(1.35F, -6.6F, 1.35F);
        this.edge4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.topedge1_2 = new ModelRenderer(this, 24, 6);
        this.topedge1_2.setRotationPoint(1.35F, -7.9F, -1.35F);
        this.topedge1_2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.shape4_1 = new ModelRenderer(this, 22, 24);
        this.shape4_1.setRotationPoint(-0.8F, -1.7F, -0.8F);
        this.shape4_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.edge5_4 = new ModelRenderer(this, 12, 6);
        this.edge5_4.setRotationPoint(1.45F, -7.3F, -1.45F);
        this.edge5_4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        GL11.glPushMatrix();
        GL11.glScaled(1D / modelScale[0], 1D / modelScale[1], 1D / modelScale[2]);
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge1.offsetX, this.edge1.offsetY, this.edge1.offsetZ);
        GL11.glTranslated(
                this.edge1.rotationPointX * f5,
                this.edge1.rotationPointY * f5,
                this.edge1.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge1.offsetX, -this.edge1.offsetY, -this.edge1.offsetZ);
        GL11.glTranslated(
                -this.edge1.rotationPointX * f5,
                -this.edge1.rotationPointY * f5,
                -this.edge1.rotationPointZ * f5);
        this.edge1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.staffTopM_3.offsetX, this.staffTopM_3.offsetY, this.staffTopM_3.offsetZ);
        GL11.glTranslated(
                this.staffTopM_3.rotationPointX * f5,
                this.staffTopM_3.rotationPointY * f5,
                this.staffTopM_3.rotationPointZ * f5);
        GL11.glScaled(0.6D, 1.06D, 0.6D);
        GL11.glTranslated(-this.staffTopM_3.offsetX, -this.staffTopM_3.offsetY, -this.staffTopM_3.offsetZ);
        GL11.glTranslated(
                -this.staffTopM_3.rotationPointX * f5,
                -this.staffTopM_3.rotationPointY * f5,
                -this.staffTopM_3.rotationPointZ * f5);
        this.staffTopM_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge2_1.offsetX, this.edge2_1.offsetY, this.edge2_1.offsetZ);
        GL11.glTranslated(
                this.edge2_1.rotationPointX * f5,
                this.edge2_1.rotationPointY * f5,
                this.edge2_1.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge2_1.offsetX, -this.edge2_1.offsetY, -this.edge2_1.offsetZ);
        GL11.glTranslated(
                -this.edge2_1.rotationPointX * f5,
                -this.edge2_1.rotationPointY * f5,
                -this.edge2_1.rotationPointZ * f5);
        this.edge2_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape5_1.offsetX, this.shape5_1.offsetY, this.shape5_1.offsetZ);
        GL11.glTranslated(
                this.shape5_1.rotationPointX * f5,
                this.shape5_1.rotationPointY * f5,
                this.shape5_1.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape5_1.offsetX, -this.shape5_1.offsetY, -this.shape5_1.offsetZ);
        GL11.glTranslated(
                -this.shape5_1.rotationPointX * f5,
                -this.shape5_1.rotationPointY * f5,
                -this.shape5_1.rotationPointZ * f5);
        this.shape5_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape4_2.offsetX, this.shape4_2.offsetY, this.shape4_2.offsetZ);
        GL11.glTranslated(
                this.shape4_2.rotationPointX * f5,
                this.shape4_2.rotationPointY * f5,
                this.shape4_2.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape4_2.offsetX, -this.shape4_2.offsetY, -this.shape4_2.offsetZ);
        GL11.glTranslated(
                -this.shape4_2.rotationPointX * f5,
                -this.shape4_2.rotationPointY * f5,
                -this.shape4_2.rotationPointZ * f5);
        this.shape4_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.tope2.offsetX, this.tope2.offsetY, this.tope2.offsetZ);
        GL11.glTranslated(
                this.tope2.rotationPointX * f5,
                this.tope2.rotationPointY * f5,
                this.tope2.rotationPointZ * f5);
        GL11.glScaled(0.9D, 0.5D, 0.9D);
        GL11.glTranslated(-this.tope2.offsetX, -this.tope2.offsetY, -this.tope2.offsetZ);
        GL11.glTranslated(
                -this.tope2.rotationPointX * f5,
                -this.tope2.rotationPointY * f5,
                -this.tope2.rotationPointZ * f5);
        this.tope2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.tope1.offsetX, this.tope1.offsetY, this.tope1.offsetZ);
        GL11.glTranslated(
                this.tope1.rotationPointX * f5,
                this.tope1.rotationPointY * f5,
                this.tope1.rotationPointZ * f5);
        GL11.glScaled(1.2D, 0.5D, 1.2D);
        GL11.glTranslated(-this.tope1.offsetX, -this.tope1.offsetY, -this.tope1.offsetZ);
        GL11.glTranslated(
                -this.tope1.rotationPointX * f5,
                -this.tope1.rotationPointY * f5,
                -this.tope1.rotationPointZ * f5);
        this.tope1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape7_2.offsetX, this.shape7_2.offsetY, this.shape7_2.offsetZ);
        GL11.glTranslated(
                this.shape7_2.rotationPointX * f5,
                this.shape7_2.rotationPointY * f5,
                this.shape7_2.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape7_2.offsetX, -this.shape7_2.offsetY, -this.shape7_2.offsetZ);
        GL11.glTranslated(
                -this.shape7_2.rotationPointX * f5,
                -this.shape7_2.rotationPointY * f5,
                -this.shape7_2.rotationPointZ * f5);
        this.shape7_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape4.offsetX, this.shape4.offsetY, this.shape4.offsetZ);
        GL11.glTranslated(
                this.shape4.rotationPointX * f5,
                this.shape4.rotationPointY * f5,
                this.shape4.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape4.offsetX, -this.shape4.offsetY, -this.shape4.offsetZ);
        GL11.glTranslated(
                -this.shape4.rotationPointX * f5,
                -this.shape4.rotationPointY * f5,
                -this.shape4.rotationPointZ * f5);
        this.shape4.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape2_2.offsetX, this.shape2_2.offsetY, this.shape2_2.offsetZ);
        GL11.glTranslated(
                this.shape2_2.rotationPointX * f5,
                this.shape2_2.rotationPointY * f5,
                this.shape2_2.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape2_2.offsetX, -this.shape2_2.offsetY, -this.shape2_2.offsetZ);
        GL11.glTranslated(
                -this.shape2_2.rotationPointX * f5,
                -this.shape2_2.rotationPointY * f5,
                -this.shape2_2.rotationPointZ * f5);
        this.shape2_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge2_2.offsetX, this.topedge2_2.offsetY, this.topedge2_2.offsetZ);
        GL11.glTranslated(
                this.topedge2_2.rotationPointX * f5,
                this.topedge2_2.rotationPointY * f5,
                this.topedge2_2.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge2_2.offsetX, -this.topedge2_2.offsetY, -this.topedge2_2.offsetZ);
        GL11.glTranslated(
                -this.topedge2_2.rotationPointX * f5,
                -this.topedge2_2.rotationPointY * f5,
                -this.topedge2_2.rotationPointZ * f5);
        this.topedge2_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge4_1.offsetX, this.topedge4_1.offsetY, this.topedge4_1.offsetZ);
        GL11.glTranslated(
                this.topedge4_1.rotationPointX * f5,
                this.topedge4_1.rotationPointY * f5,
                this.topedge4_1.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge4_1.offsetX, -this.topedge4_1.offsetY, -this.topedge4_1.offsetZ);
        GL11.glTranslated(
                -this.topedge4_1.rotationPointX * f5,
                -this.topedge4_1.rotationPointY * f5,
                -this.topedge4_1.rotationPointZ * f5);
        this.topedge4_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge5.offsetX, this.topedge5.offsetY, this.topedge5.offsetZ);
        GL11.glTranslated(
                this.topedge5.rotationPointX * f5,
                this.topedge5.rotationPointY * f5,
                this.topedge5.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge5.offsetX, -this.topedge5.offsetY, -this.topedge5.offsetZ);
        GL11.glTranslated(
                -this.topedge5.rotationPointX * f5,
                -this.topedge5.rotationPointY * f5,
                -this.topedge5.rotationPointZ * f5);
        this.topedge5.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge4_2.offsetX, this.topedge4_2.offsetY, this.topedge4_2.offsetZ);
        GL11.glTranslated(
                this.topedge4_2.rotationPointX * f5,
                this.topedge4_2.rotationPointY * f5,
                this.topedge4_2.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge4_2.offsetX, -this.topedge4_2.offsetY, -this.topedge4_2.offsetZ);
        GL11.glTranslated(
                -this.topedge4_2.rotationPointX * f5,
                -this.topedge4_2.rotationPointY * f5,
                -this.topedge4_2.rotationPointZ * f5);
        this.topedge4_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape6_2.offsetX, this.shape6_2.offsetY, this.shape6_2.offsetZ);
        GL11.glTranslated(
                this.shape6_2.rotationPointX * f5,
                this.shape6_2.rotationPointY * f5,
                this.shape6_2.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape6_2.offsetX, -this.shape6_2.offsetY, -this.shape6_2.offsetZ);
        GL11.glTranslated(
                -this.shape6_2.rotationPointX * f5,
                -this.shape6_2.rotationPointY * f5,
                -this.shape6_2.rotationPointZ * f5);
        this.shape6_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge3.offsetX, this.edge3.offsetY, this.edge3.offsetZ);
        GL11.glTranslated(
                this.edge3.rotationPointX * f5,
                this.edge3.rotationPointY * f5,
                this.edge3.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge3.offsetX, -this.edge3.offsetY, -this.edge3.offsetZ);
        GL11.glTranslated(
                -this.edge3.rotationPointX * f5,
                -this.edge3.rotationPointY * f5,
                -this.edge3.rotationPointZ * f5);
        this.edge3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge4_3.offsetX, this.topedge4_3.offsetY, this.topedge4_3.offsetZ);
        GL11.glTranslated(
                this.topedge4_3.rotationPointX * f5,
                this.topedge4_3.rotationPointY * f5,
                this.topedge4_3.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge4_3.offsetX, -this.topedge4_3.offsetY, -this.topedge4_3.offsetZ);
        GL11.glTranslated(
                -this.topedge4_3.rotationPointX * f5,
                -this.topedge4_3.rotationPointY * f5,
                -this.topedge4_3.rotationPointZ * f5);
        this.topedge4_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape5_3.offsetX, this.shape5_3.offsetY, this.shape5_3.offsetZ);
        GL11.glTranslated(
                this.shape5_3.rotationPointX * f5,
                this.shape5_3.rotationPointY * f5,
                this.shape5_3.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape5_3.offsetX, -this.shape5_3.offsetY, -this.shape5_3.offsetZ);
        GL11.glTranslated(
                -this.shape5_3.rotationPointX * f5,
                -this.shape5_3.rotationPointY * f5,
                -this.shape5_3.rotationPointZ * f5);
        this.shape5_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape4_3.offsetX, this.shape4_3.offsetY, this.shape4_3.offsetZ);
        GL11.glTranslated(
                this.shape4_3.rotationPointX * f5,
                this.shape4_3.rotationPointY * f5,
                this.shape4_3.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape4_3.offsetX, -this.shape4_3.offsetY, -this.shape4_3.offsetZ);
        GL11.glTranslated(
                -this.shape4_3.rotationPointX * f5,
                -this.shape4_3.rotationPointY * f5,
                -this.shape4_3.rotationPointZ * f5);
        this.shape4_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge3.offsetX, this.topedge3.offsetY, this.topedge3.offsetZ);
        GL11.glTranslated(
                this.topedge3.rotationPointX * f5,
                this.topedge3.rotationPointY * f5,
                this.topedge3.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge3.offsetX, -this.topedge3.offsetY, -this.topedge3.offsetZ);
        GL11.glTranslated(
                -this.topedge3.rotationPointX * f5,
                -this.topedge3.rotationPointY * f5,
                -this.topedge3.rotationPointZ * f5);
        this.topedge3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge4.offsetX, this.topedge4.offsetY, this.topedge4.offsetZ);
        GL11.glTranslated(
                this.topedge4.rotationPointX * f5,
                this.topedge4.rotationPointY * f5,
                this.topedge4.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge4.offsetX, -this.topedge4.offsetY, -this.topedge4.offsetZ);
        GL11.glTranslated(
                -this.topedge4.rotationPointX * f5,
                -this.topedge4.rotationPointY * f5,
                -this.topedge4.rotationPointZ * f5);
        this.topedge4.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge5_2.offsetX, this.edge5_2.offsetY, this.edge5_2.offsetZ);
        GL11.glTranslated(
                this.edge5_2.rotationPointX * f5,
                this.edge5_2.rotationPointY * f5,
                this.edge5_2.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge5_2.offsetX, -this.edge5_2.offsetY, -this.edge5_2.offsetZ);
        GL11.glTranslated(
                -this.edge5_2.rotationPointX * f5,
                -this.edge5_2.rotationPointY * f5,
                -this.edge5_2.rotationPointZ * f5);
        this.edge5_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge5.offsetX, this.edge5.offsetY, this.edge5.offsetZ);
        GL11.glTranslated(
                this.edge5.rotationPointX * f5,
                this.edge5.rotationPointY * f5,
                this.edge5.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge5.offsetX, -this.edge5.offsetY, -this.edge5.offsetZ);
        GL11.glTranslated(
                -this.edge5.rotationPointX * f5,
                -this.edge5.rotationPointY * f5,
                -this.edge5.rotationPointZ * f5);
        this.edge5.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge2_3.offsetX, this.topedge2_3.offsetY, this.topedge2_3.offsetZ);
        GL11.glTranslated(
                this.topedge2_3.rotationPointX * f5,
                this.topedge2_3.rotationPointY * f5,
                this.topedge2_3.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge2_3.offsetX, -this.topedge2_3.offsetY, -this.topedge2_3.offsetZ);
        GL11.glTranslated(
                -this.topedge2_3.rotationPointX * f5,
                -this.topedge2_3.rotationPointY * f5,
                -this.topedge2_3.rotationPointZ * f5);
        this.topedge2_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape6_3.offsetX, this.shape6_3.offsetY, this.shape6_3.offsetZ);
        GL11.glTranslated(
                this.shape6_3.rotationPointX * f5,
                this.shape6_3.rotationPointY * f5,
                this.shape6_3.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape6_3.offsetX, -this.shape6_3.offsetY, -this.shape6_3.offsetZ);
        GL11.glTranslated(
                -this.shape6_3.rotationPointX * f5,
                -this.shape6_3.rotationPointY * f5,
                -this.shape6_3.rotationPointZ * f5);
        this.shape6_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge1_1.offsetX, this.edge1_1.offsetY, this.edge1_1.offsetZ);
        GL11.glTranslated(
                this.edge1_1.rotationPointX * f5,
                this.edge1_1.rotationPointY * f5,
                this.edge1_1.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge1_1.offsetX, -this.edge1_1.offsetY, -this.edge1_1.offsetZ);
        GL11.glTranslated(
                -this.edge1_1.rotationPointX * f5,
                -this.edge1_1.rotationPointY * f5,
                -this.edge1_1.rotationPointZ * f5);
        this.edge1_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape5_2.offsetX, this.shape5_2.offsetY, this.shape5_2.offsetZ);
        GL11.glTranslated(
                this.shape5_2.rotationPointX * f5,
                this.shape5_2.rotationPointY * f5,
                this.shape5_2.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape5_2.offsetX, -this.shape5_2.offsetY, -this.shape5_2.offsetZ);
        GL11.glTranslated(
                -this.shape5_2.rotationPointX * f5,
                -this.shape5_2.rotationPointY * f5,
                -this.shape5_2.rotationPointZ * f5);
        this.shape5_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge5_2.offsetX, this.topedge5_2.offsetY, this.topedge5_2.offsetZ);
        GL11.glTranslated(
                this.topedge5_2.rotationPointX * f5,
                this.topedge5_2.rotationPointY * f5,
                this.topedge5_2.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge5_2.offsetX, -this.topedge5_2.offsetY, -this.topedge5_2.offsetZ);
        GL11.glTranslated(
                -this.topedge5_2.rotationPointX * f5,
                -this.topedge5_2.rotationPointY * f5,
                -this.topedge5_2.rotationPointZ * f5);
        this.topedge5_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge2.offsetX, this.topedge2.offsetY, this.topedge2.offsetZ);
        GL11.glTranslated(
                this.topedge2.rotationPointX * f5,
                this.topedge2.rotationPointY * f5,
                this.topedge2.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge2.offsetX, -this.topedge2.offsetY, -this.topedge2.offsetZ);
        GL11.glTranslated(
                -this.topedge2.rotationPointX * f5,
                -this.topedge2.rotationPointY * f5,
                -this.topedge2.rotationPointZ * f5);
        this.topedge2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.staffTopQ_3.offsetX, this.staffTopQ_3.offsetY, this.staffTopQ_3.offsetZ);
        GL11.glTranslated(
                this.staffTopQ_3.rotationPointX * f5,
                this.staffTopQ_3.rotationPointY * f5,
                this.staffTopQ_3.rotationPointZ * f5);
        GL11.glScaled(0.7D, 1.0D, 0.7D);
        GL11.glTranslated(-this.staffTopQ_3.offsetX, -this.staffTopQ_3.offsetY, -this.staffTopQ_3.offsetZ);
        GL11.glTranslated(
                -this.staffTopQ_3.rotationPointX * f5,
                -this.staffTopQ_3.rotationPointY * f5,
                -this.staffTopQ_3.rotationPointZ * f5);
        this.staffTopQ_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge4_2.offsetX, this.edge4_2.offsetY, this.edge4_2.offsetZ);
        GL11.glTranslated(
                this.edge4_2.rotationPointX * f5,
                this.edge4_2.rotationPointY * f5,
                this.edge4_2.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge4_2.offsetX, -this.edge4_2.offsetY, -this.edge4_2.offsetZ);
        GL11.glTranslated(
                -this.edge4_2.rotationPointX * f5,
                -this.edge4_2.rotationPointY * f5,
                -this.edge4_2.rotationPointZ * f5);
        this.edge4_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.staffTopQ_1.offsetX, this.staffTopQ_1.offsetY, this.staffTopQ_1.offsetZ);
        GL11.glTranslated(
                this.staffTopQ_1.rotationPointX * f5,
                this.staffTopQ_1.rotationPointY * f5,
                this.staffTopQ_1.rotationPointZ * f5);
        GL11.glScaled(0.7D, 1.0D, 0.7D);
        GL11.glTranslated(-this.staffTopQ_1.offsetX, -this.staffTopQ_1.offsetY, -this.staffTopQ_1.offsetZ);
        GL11.glTranslated(
                -this.staffTopQ_1.rotationPointX * f5,
                -this.staffTopQ_1.rotationPointY * f5,
                -this.staffTopQ_1.rotationPointZ * f5);
        this.staffTopQ_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge3_3.offsetX, this.edge3_3.offsetY, this.edge3_3.offsetZ);
        GL11.glTranslated(
                this.edge3_3.rotationPointX * f5,
                this.edge3_3.rotationPointY * f5,
                this.edge3_3.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge3_3.offsetX, -this.edge3_3.offsetY, -this.edge3_3.offsetZ);
        GL11.glTranslated(
                -this.edge3_3.rotationPointX * f5,
                -this.edge3_3.rotationPointY * f5,
                -this.edge3_3.rotationPointZ * f5);
        this.edge3_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape3.offsetX, this.shape3.offsetY, this.shape3.offsetZ);
        GL11.glTranslated(
                this.shape3.rotationPointX * f5,
                this.shape3.rotationPointY * f5,
                this.shape3.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape3.offsetX, -this.shape3.offsetY, -this.shape3.offsetZ);
        GL11.glTranslated(
                -this.shape3.rotationPointX * f5,
                -this.shape3.rotationPointY * f5,
                -this.shape3.rotationPointZ * f5);
        this.shape3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape2_3.offsetX, this.shape2_3.offsetY, this.shape2_3.offsetZ);
        GL11.glTranslated(
                this.shape2_3.rotationPointX * f5,
                this.shape2_3.rotationPointY * f5,
                this.shape2_3.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape2_3.offsetX, -this.shape2_3.offsetY, -this.shape2_3.offsetZ);
        GL11.glTranslated(
                -this.shape2_3.rotationPointX * f5,
                -this.shape2_3.rotationPointY * f5,
                -this.shape2_3.rotationPointZ * f5);
        this.shape2_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge1.offsetX, this.topedge1.offsetY, this.topedge1.offsetZ);
        GL11.glTranslated(
                this.topedge1.rotationPointX * f5,
                this.topedge1.rotationPointY * f5,
                this.topedge1.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge1.offsetX, -this.topedge1.offsetY, -this.topedge1.offsetZ);
        GL11.glTranslated(
                -this.topedge1.rotationPointX * f5,
                -this.topedge1.rotationPointY * f5,
                -this.topedge1.rotationPointZ * f5);
        this.topedge1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge5_1.offsetX, this.topedge5_1.offsetY, this.topedge5_1.offsetZ);
        GL11.glTranslated(
                this.topedge5_1.rotationPointX * f5,
                this.topedge5_1.rotationPointY * f5,
                this.topedge5_1.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge5_1.offsetX, -this.topedge5_1.offsetY, -this.topedge5_1.offsetZ);
        GL11.glTranslated(
                -this.topedge5_1.rotationPointX * f5,
                -this.topedge5_1.rotationPointY * f5,
                -this.topedge5_1.rotationPointZ * f5);
        this.topedge5_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape7_3.offsetX, this.shape7_3.offsetY, this.shape7_3.offsetZ);
        GL11.glTranslated(
                this.shape7_3.rotationPointX * f5,
                this.shape7_3.rotationPointY * f5,
                this.shape7_3.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape7_3.offsetX, -this.shape7_3.offsetY, -this.shape7_3.offsetZ);
        GL11.glTranslated(
                -this.shape7_3.rotationPointX * f5,
                -this.shape7_3.rotationPointY * f5,
                -this.shape7_3.rotationPointZ * f5);
        this.shape7_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.staffTopM_2.offsetX, this.staffTopM_2.offsetY, this.staffTopM_2.offsetZ);
        GL11.glTranslated(
                this.staffTopM_2.rotationPointX * f5,
                this.staffTopM_2.rotationPointY * f5,
                this.staffTopM_2.rotationPointZ * f5);
        GL11.glScaled(0.6D, 1.06D, 0.6D);
        GL11.glTranslated(-this.staffTopM_2.offsetX, -this.staffTopM_2.offsetY, -this.staffTopM_2.offsetZ);
        GL11.glTranslated(
                -this.staffTopM_2.rotationPointX * f5,
                -this.staffTopM_2.rotationPointY * f5,
                -this.staffTopM_2.rotationPointZ * f5);
        this.staffTopM_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape3_3.offsetX, this.shape3_3.offsetY, this.shape3_3.offsetZ);
        GL11.glTranslated(
                this.shape3_3.rotationPointX * f5,
                this.shape3_3.rotationPointY * f5,
                this.shape3_3.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape3_3.offsetX, -this.shape3_3.offsetY, -this.shape3_3.offsetZ);
        GL11.glTranslated(
                -this.shape3_3.rotationPointX * f5,
                -this.shape3_3.rotationPointY * f5,
                -this.shape3_3.rotationPointZ * f5);
        this.shape3_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape3_1.offsetX, this.shape3_1.offsetY, this.shape3_1.offsetZ);
        GL11.glTranslated(
                this.shape3_1.rotationPointX * f5,
                this.shape3_1.rotationPointY * f5,
                this.shape3_1.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape3_1.offsetX, -this.shape3_1.offsetY, -this.shape3_1.offsetZ);
        GL11.glTranslated(
                -this.shape3_1.rotationPointX * f5,
                -this.shape3_1.rotationPointY * f5,
                -this.shape3_1.rotationPointZ * f5);
        this.shape3_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape6_1.offsetX, this.shape6_1.offsetY, this.shape6_1.offsetZ);
        GL11.glTranslated(
                this.shape6_1.rotationPointX * f5,
                this.shape6_1.rotationPointY * f5,
                this.shape6_1.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape6_1.offsetX, -this.shape6_1.offsetY, -this.shape6_1.offsetZ);
        GL11.glTranslated(
                -this.shape6_1.rotationPointX * f5,
                -this.shape6_1.rotationPointY * f5,
                -this.shape6_1.rotationPointZ * f5);
        this.shape6_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape7_1.offsetX, this.shape7_1.offsetY, this.shape7_1.offsetZ);
        GL11.glTranslated(
                this.shape7_1.rotationPointX * f5,
                this.shape7_1.rotationPointY * f5,
                this.shape7_1.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape7_1.offsetX, -this.shape7_1.offsetY, -this.shape7_1.offsetZ);
        GL11.glTranslated(
                -this.shape7_1.rotationPointX * f5,
                -this.shape7_1.rotationPointY * f5,
                -this.shape7_1.rotationPointZ * f5);
        this.shape7_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge1_3.offsetX, this.topedge1_3.offsetY, this.topedge1_3.offsetZ);
        GL11.glTranslated(
                this.topedge1_3.rotationPointX * f5,
                this.topedge1_3.rotationPointY * f5,
                this.topedge1_3.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge1_3.offsetX, -this.topedge1_3.offsetY, -this.topedge1_3.offsetZ);
        GL11.glTranslated(
                -this.topedge1_3.rotationPointX * f5,
                -this.topedge1_3.rotationPointY * f5,
                -this.topedge1_3.rotationPointZ * f5);
        this.topedge1_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.tope3.offsetX, this.tope3.offsetY, this.tope3.offsetZ);
        GL11.glTranslated(
                this.tope3.rotationPointX * f5,
                this.tope3.rotationPointY * f5,
                this.tope3.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.6D, 0.6D);
        GL11.glTranslated(-this.tope3.offsetX, -this.tope3.offsetY, -this.tope3.offsetZ);
        GL11.glTranslated(
                -this.tope3.rotationPointX * f5,
                -this.tope3.rotationPointY * f5,
                -this.tope3.rotationPointZ * f5);
        this.tope3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape5.offsetX, this.shape5.offsetY, this.shape5.offsetZ);
        GL11.glTranslated(
                this.shape5.rotationPointX * f5,
                this.shape5.rotationPointY * f5,
                this.shape5.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape5.offsetX, -this.shape5.offsetY, -this.shape5.offsetZ);
        GL11.glTranslated(
                -this.shape5.rotationPointX * f5,
                -this.shape5.rotationPointY * f5,
                -this.shape5.rotationPointZ * f5);
        this.shape5.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge3_1.offsetX, this.topedge3_1.offsetY, this.topedge3_1.offsetZ);
        GL11.glTranslated(
                this.topedge3_1.rotationPointX * f5,
                this.topedge3_1.rotationPointY * f5,
                this.topedge3_1.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge3_1.offsetX, -this.topedge3_1.offsetY, -this.topedge3_1.offsetZ);
        GL11.glTranslated(
                -this.topedge3_1.rotationPointX * f5,
                -this.topedge3_1.rotationPointY * f5,
                -this.topedge3_1.rotationPointZ * f5);
        this.topedge3_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge5_3.offsetX, this.topedge5_3.offsetY, this.topedge5_3.offsetZ);
        GL11.glTranslated(
                this.topedge5_3.rotationPointX * f5,
                this.topedge5_3.rotationPointY * f5,
                this.topedge5_3.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge5_3.offsetX, -this.topedge5_3.offsetY, -this.topedge5_3.offsetZ);
        GL11.glTranslated(
                -this.topedge5_3.rotationPointX * f5,
                -this.topedge5_3.rotationPointY * f5,
                -this.topedge5_3.rotationPointZ * f5);
        this.topedge5_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge5_3.offsetX, this.edge5_3.offsetY, this.edge5_3.offsetZ);
        GL11.glTranslated(
                this.edge5_3.rotationPointX * f5,
                this.edge5_3.rotationPointY * f5,
                this.edge5_3.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge5_3.offsetX, -this.edge5_3.offsetY, -this.edge5_3.offsetZ);
        GL11.glTranslated(
                -this.edge5_3.rotationPointX * f5,
                -this.edge5_3.rotationPointY * f5,
                -this.edge5_3.rotationPointZ * f5);
        this.edge5_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge3_1.offsetX, this.edge3_1.offsetY, this.edge3_1.offsetZ);
        GL11.glTranslated(
                this.edge3_1.rotationPointX * f5,
                this.edge3_1.rotationPointY * f5,
                this.edge3_1.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge3_1.offsetX, -this.edge3_1.offsetY, -this.edge3_1.offsetZ);
        GL11.glTranslated(
                -this.edge3_1.rotationPointX * f5,
                -this.edge3_1.rotationPointY * f5,
                -this.edge3_1.rotationPointZ * f5);
        this.edge3_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape7.offsetX, this.shape7.offsetY, this.shape7.offsetZ);
        GL11.glTranslated(
                this.shape7.rotationPointX * f5,
                this.shape7.rotationPointY * f5,
                this.shape7.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape7.offsetX, -this.shape7.offsetY, -this.shape7.offsetZ);
        GL11.glTranslated(
                -this.shape7.rotationPointX * f5,
                -this.shape7.rotationPointY * f5,
                -this.shape7.rotationPointZ * f5);
        this.shape7.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge1_1.offsetX, this.topedge1_1.offsetY, this.topedge1_1.offsetZ);
        GL11.glTranslated(
                this.topedge1_1.rotationPointX * f5,
                this.topedge1_1.rotationPointY * f5,
                this.topedge1_1.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge1_1.offsetX, -this.topedge1_1.offsetY, -this.topedge1_1.offsetZ);
        GL11.glTranslated(
                -this.topedge1_1.rotationPointX * f5,
                -this.topedge1_1.rotationPointY * f5,
                -this.topedge1_1.rotationPointZ * f5);
        this.topedge1_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape2_1.offsetX, this.shape2_1.offsetY, this.shape2_1.offsetZ);
        GL11.glTranslated(
                this.shape2_1.rotationPointX * f5,
                this.shape2_1.rotationPointY * f5,
                this.shape2_1.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape2_1.offsetX, -this.shape2_1.offsetY, -this.shape2_1.offsetZ);
        GL11.glTranslated(
                -this.shape2_1.rotationPointX * f5,
                -this.shape2_1.rotationPointY * f5,
                -this.shape2_1.rotationPointZ * f5);
        this.shape2_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge2_3.offsetX, this.edge2_3.offsetY, this.edge2_3.offsetZ);
        GL11.glTranslated(
                this.edge2_3.rotationPointX * f5,
                this.edge2_3.rotationPointY * f5,
                this.edge2_3.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge2_3.offsetX, -this.edge2_3.offsetY, -this.edge2_3.offsetZ);
        GL11.glTranslated(
                -this.edge2_3.rotationPointX * f5,
                -this.edge2_3.rotationPointY * f5,
                -this.edge2_3.rotationPointZ * f5);
        this.edge2_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge3_2.offsetX, this.topedge3_2.offsetY, this.topedge3_2.offsetZ);
        GL11.glTranslated(
                this.topedge3_2.rotationPointX * f5,
                this.topedge3_2.rotationPointY * f5,
                this.topedge3_2.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge3_2.offsetX, -this.topedge3_2.offsetY, -this.topedge3_2.offsetZ);
        GL11.glTranslated(
                -this.topedge3_2.rotationPointX * f5,
                -this.topedge3_2.rotationPointY * f5,
                -this.topedge3_2.rotationPointZ * f5);
        this.topedge3_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge1_2.offsetX, this.edge1_2.offsetY, this.edge1_2.offsetZ);
        GL11.glTranslated(
                this.edge1_2.rotationPointX * f5,
                this.edge1_2.rotationPointY * f5,
                this.edge1_2.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge1_2.offsetX, -this.edge1_2.offsetY, -this.edge1_2.offsetZ);
        GL11.glTranslated(
                -this.edge1_2.rotationPointX * f5,
                -this.edge1_2.rotationPointY * f5,
                -this.edge1_2.rotationPointZ * f5);
        this.edge1_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge2_2.offsetX, this.edge2_2.offsetY, this.edge2_2.offsetZ);
        GL11.glTranslated(
                this.edge2_2.rotationPointX * f5,
                this.edge2_2.rotationPointY * f5,
                this.edge2_2.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge2_2.offsetX, -this.edge2_2.offsetY, -this.edge2_2.offsetZ);
        GL11.glTranslated(
                -this.edge2_2.rotationPointX * f5,
                -this.edge2_2.rotationPointY * f5,
                -this.edge2_2.rotationPointZ * f5);
        this.edge2_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge2.offsetX, this.edge2.offsetY, this.edge2.offsetZ);
        GL11.glTranslated(
                this.edge2.rotationPointX * f5,
                this.edge2.rotationPointY * f5,
                this.edge2.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge2.offsetX, -this.edge2.offsetY, -this.edge2.offsetZ);
        GL11.glTranslated(
                -this.edge2.rotationPointX * f5,
                -this.edge2.rotationPointY * f5,
                -this.edge2.rotationPointZ * f5);
        this.edge2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.staffTopM.offsetX, this.staffTopM.offsetY, this.staffTopM.offsetZ);
        GL11.glTranslated(
                this.staffTopM.rotationPointX * f5,
                this.staffTopM.rotationPointY * f5,
                this.staffTopM.rotationPointZ * f5);
        GL11.glScaled(0.6D, 1.06D, 0.6D);
        GL11.glTranslated(-this.staffTopM.offsetX, -this.staffTopM.offsetY, -this.staffTopM.offsetZ);
        GL11.glTranslated(
                -this.staffTopM.rotationPointX * f5,
                -this.staffTopM.rotationPointY * f5,
                -this.staffTopM.rotationPointZ * f5);
        this.staffTopM.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge5_1.offsetX, this.edge5_1.offsetY, this.edge5_1.offsetZ);
        GL11.glTranslated(
                this.edge5_1.rotationPointX * f5,
                this.edge5_1.rotationPointY * f5,
                this.edge5_1.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge5_1.offsetX, -this.edge5_1.offsetY, -this.edge5_1.offsetZ);
        GL11.glTranslated(
                -this.edge5_1.rotationPointX * f5,
                -this.edge5_1.rotationPointY * f5,
                -this.edge5_1.rotationPointZ * f5);
        this.edge5_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.staffTopQ.offsetX, this.staffTopQ.offsetY, this.staffTopQ.offsetZ);
        GL11.glTranslated(
                this.staffTopQ.rotationPointX * f5,
                this.staffTopQ.rotationPointY * f5,
                this.staffTopQ.rotationPointZ * f5);
        GL11.glScaled(0.7D, 1.0D, 0.7D);
        GL11.glTranslated(-this.staffTopQ.offsetX, -this.staffTopQ.offsetY, -this.staffTopQ.offsetZ);
        GL11.glTranslated(
                -this.staffTopQ.rotationPointX * f5,
                -this.staffTopQ.rotationPointY * f5,
                -this.staffTopQ.rotationPointZ * f5);
        this.staffTopQ.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge4_1.offsetX, this.edge4_1.offsetY, this.edge4_1.offsetZ);
        GL11.glTranslated(
                this.edge4_1.rotationPointX * f5,
                this.edge4_1.rotationPointY * f5,
                this.edge4_1.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge4_1.offsetX, -this.edge4_1.offsetY, -this.edge4_1.offsetZ);
        GL11.glTranslated(
                -this.edge4_1.rotationPointX * f5,
                -this.edge4_1.rotationPointY * f5,
                -this.edge4_1.rotationPointZ * f5);
        this.edge4_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.staffTopM_1.offsetX, this.staffTopM_1.offsetY, this.staffTopM_1.offsetZ);
        GL11.glTranslated(
                this.staffTopM_1.rotationPointX * f5,
                this.staffTopM_1.rotationPointY * f5,
                this.staffTopM_1.rotationPointZ * f5);
        GL11.glScaled(0.6D, 1.0D, 0.6D);
        GL11.glTranslated(-this.staffTopM_1.offsetX, -this.staffTopM_1.offsetY, -this.staffTopM_1.offsetZ);
        GL11.glTranslated(
                -this.staffTopM_1.rotationPointX * f5,
                -this.staffTopM_1.rotationPointY * f5,
                -this.staffTopM_1.rotationPointZ * f5);
        this.staffTopM_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge3_2.offsetX, this.edge3_2.offsetY, this.edge3_2.offsetZ);
        GL11.glTranslated(
                this.edge3_2.rotationPointX * f5,
                this.edge3_2.rotationPointY * f5,
                this.edge3_2.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge3_2.offsetX, -this.edge3_2.offsetY, -this.edge3_2.offsetZ);
        GL11.glTranslated(
                -this.edge3_2.rotationPointX * f5,
                -this.edge3_2.rotationPointY * f5,
                -this.edge3_2.rotationPointZ * f5);
        this.edge3_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.staffTopQ_2.offsetX, this.staffTopQ_2.offsetY, this.staffTopQ_2.offsetZ);
        GL11.glTranslated(
                this.staffTopQ_2.rotationPointX * f5,
                this.staffTopQ_2.rotationPointY * f5,
                this.staffTopQ_2.rotationPointZ * f5);
        GL11.glScaled(0.7D, 1.0D, 0.7D);
        GL11.glTranslated(-this.staffTopQ_2.offsetX, -this.staffTopQ_2.offsetY, -this.staffTopQ_2.offsetZ);
        GL11.glTranslated(
                -this.staffTopQ_2.rotationPointX * f5,
                -this.staffTopQ_2.rotationPointY * f5,
                -this.staffTopQ_2.rotationPointZ * f5);
        this.staffTopQ_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.staffTopM_4.offsetX, this.staffTopM_4.offsetY, this.staffTopM_4.offsetZ);
        GL11.glTranslated(
                this.staffTopM_4.rotationPointX * f5,
                this.staffTopM_4.rotationPointY * f5,
                this.staffTopM_4.rotationPointZ * f5);
        GL11.glScaled(0.6D, 1.06D, 0.6D);
        GL11.glTranslated(-this.staffTopM_4.offsetX, -this.staffTopM_4.offsetY, -this.staffTopM_4.offsetZ);
        GL11.glTranslated(
                -this.staffTopM_4.rotationPointX * f5,
                -this.staffTopM_4.rotationPointY * f5,
                -this.staffTopM_4.rotationPointZ * f5);
        this.staffTopM_4.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge3_3.offsetX, this.topedge3_3.offsetY, this.topedge3_3.offsetZ);
        GL11.glTranslated(
                this.topedge3_3.rotationPointX * f5,
                this.topedge3_3.rotationPointY * f5,
                this.topedge3_3.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge3_3.offsetX, -this.topedge3_3.offsetY, -this.topedge3_3.offsetZ);
        GL11.glTranslated(
                -this.topedge3_3.rotationPointX * f5,
                -this.topedge3_3.rotationPointY * f5,
                -this.topedge3_3.rotationPointZ * f5);
        this.topedge3_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape3_2.offsetX, this.shape3_2.offsetY, this.shape3_2.offsetZ);
        GL11.glTranslated(
                this.shape3_2.rotationPointX * f5,
                this.shape3_2.rotationPointY * f5,
                this.shape3_2.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape3_2.offsetX, -this.shape3_2.offsetY, -this.shape3_2.offsetZ);
        GL11.glTranslated(
                -this.shape3_2.rotationPointX * f5,
                -this.shape3_2.rotationPointY * f5,
                -this.shape3_2.rotationPointZ * f5);
        this.shape3_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge1_3.offsetX, this.edge1_3.offsetY, this.edge1_3.offsetZ);
        GL11.glTranslated(
                this.edge1_3.rotationPointX * f5,
                this.edge1_3.rotationPointY * f5,
                this.edge1_3.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge1_3.offsetX, -this.edge1_3.offsetY, -this.edge1_3.offsetZ);
        GL11.glTranslated(
                -this.edge1_3.rotationPointX * f5,
                -this.edge1_3.rotationPointY * f5,
                -this.edge1_3.rotationPointZ * f5);
        this.edge1_3.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge2_1.offsetX, this.topedge2_1.offsetY, this.topedge2_1.offsetZ);
        GL11.glTranslated(
                this.topedge2_1.rotationPointX * f5,
                this.topedge2_1.rotationPointY * f5,
                this.topedge2_1.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge2_1.offsetX, -this.topedge2_1.offsetY, -this.topedge2_1.offsetZ);
        GL11.glTranslated(
                -this.topedge2_1.rotationPointX * f5,
                -this.topedge2_1.rotationPointY * f5,
                -this.topedge2_1.rotationPointZ * f5);
        this.topedge2_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape2.offsetX, this.shape2.offsetY, this.shape2.offsetZ);
        GL11.glTranslated(
                this.shape2.rotationPointX * f5,
                this.shape2.rotationPointY * f5,
                this.shape2.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape2.offsetX, -this.shape2.offsetY, -this.shape2.offsetZ);
        GL11.glTranslated(
                -this.shape2.rotationPointX * f5,
                -this.shape2.rotationPointY * f5,
                -this.shape2.rotationPointZ * f5);
        this.shape2.render(f5);
        GL11.glPopMatrix();
        this.staff.render(f5);
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape6.offsetX, this.shape6.offsetY, this.shape6.offsetZ);
        GL11.glTranslated(
                this.shape6.rotationPointX * f5,
                this.shape6.rotationPointY * f5,
                this.shape6.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape6.offsetX, -this.shape6.offsetY, -this.shape6.offsetZ);
        GL11.glTranslated(
                -this.shape6.rotationPointX * f5,
                -this.shape6.rotationPointY * f5,
                -this.shape6.rotationPointZ * f5);
        this.shape6.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge4.offsetX, this.edge4.offsetY, this.edge4.offsetZ);
        GL11.glTranslated(
                this.edge4.rotationPointX * f5,
                this.edge4.rotationPointY * f5,
                this.edge4.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge4.offsetX, -this.edge4.offsetY, -this.edge4.offsetZ);
        GL11.glTranslated(
                -this.edge4.rotationPointX * f5,
                -this.edge4.rotationPointY * f5,
                -this.edge4.rotationPointZ * f5);
        this.edge4.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.topedge1_2.offsetX, this.topedge1_2.offsetY, this.topedge1_2.offsetZ);
        GL11.glTranslated(
                this.topedge1_2.rotationPointX * f5,
                this.topedge1_2.rotationPointY * f5,
                this.topedge1_2.rotationPointZ * f5);
        GL11.glScaled(0.6D, 0.5D, 0.6D);
        GL11.glTranslated(-this.topedge1_2.offsetX, -this.topedge1_2.offsetY, -this.topedge1_2.offsetZ);
        GL11.glTranslated(
                -this.topedge1_2.rotationPointX * f5,
                -this.topedge1_2.rotationPointY * f5,
                -this.topedge1_2.rotationPointZ * f5);
        this.topedge1_2.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.shape4_1.offsetX, this.shape4_1.offsetY, this.shape4_1.offsetZ);
        GL11.glTranslated(
                this.shape4_1.rotationPointX * f5,
                this.shape4_1.rotationPointY * f5,
                this.shape4_1.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.shape4_1.offsetX, -this.shape4_1.offsetY, -this.shape4_1.offsetZ);
        GL11.glTranslated(
                -this.shape4_1.rotationPointX * f5,
                -this.shape4_1.rotationPointY * f5,
                -this.shape4_1.rotationPointZ * f5);
        this.shape4_1.render(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.edge5_4.offsetX, this.edge5_4.offsetY, this.edge5_4.offsetZ);
        GL11.glTranslated(
                this.edge5_4.rotationPointX * f5,
                this.edge5_4.rotationPointY * f5,
                this.edge5_4.rotationPointZ * f5);
        GL11.glScaled(0.7D, 0.7D, 0.7D);
        GL11.glTranslated(-this.edge5_4.offsetX, -this.edge5_4.offsetY, -this.edge5_4.offsetZ);
        GL11.glTranslated(
                -this.edge5_4.rotationPointX * f5,
                -this.edge5_4.rotationPointY * f5,
                -this.edge5_4.rotationPointZ * f5);
        this.edge5_4.render(f5);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
