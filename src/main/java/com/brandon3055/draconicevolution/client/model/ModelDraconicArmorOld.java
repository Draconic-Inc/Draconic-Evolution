package com.brandon3055.draconicevolution.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Draconic Armor.tcn - TechneToTabulaImporter Created using Tabula 5.0.0
 */
public class ModelDraconicArmorOld extends ModelBiped { //
    // public ModelRenderer bipedHead;
    // public ModelRenderer bipedBody;
    // public ModelRenderer bipedRightArm;
    // public ModelRenderer bipedLeftArm;
    // public ModelRenderer bipedRightLeg;/
    // public ModelRenderer bipedLeftLeg;

    public ModelRenderer MainHelmPieceRight1;
    public ModelRenderer MainHelmPieceRight2;
    public ModelRenderer MainHelmPieceLeft1;
    public ModelRenderer MainHelmPieceLeft2;
    public ModelRenderer MainHelmPieceRight3;
    public ModelRenderer MainHelmPieceLeft3;
    public ModelRenderer MainHelmPieceBack;
    public ModelRenderer MainHelmPieceTop;
    public ModelRenderer MainHelmPieceFrontTop;
    public ModelRenderer MainHelmPieceFrontBottom;
    public ModelRenderer MainHelmPieceBottom;
    public ModelRenderer HelmPieceBack1;
    public ModelRenderer HornPieceRight1;
    public ModelRenderer HornPieceRight2;
    public ModelRenderer HornPieceLeft1;
    public ModelRenderer HornPieceLeft2;
    public ModelRenderer HornPieceRight3;
    public ModelRenderer HornPieceLeft3;
    public ModelRenderer HornPieceRight4;
    public ModelRenderer HornPieceLeft4;
    public ModelRenderer HelmPieceFront1;
    public ModelRenderer HelmPieceFront2;
    public ModelRenderer MainChestPieceBottom;
    public ModelRenderer ChestDecorationPiece2;
    public ModelRenderer ChestDecorationPiece4;
    public ModelRenderer ShoulderPadRight2;
    public ModelRenderer ShoulderPadRight3;
    public ModelRenderer ShoulderPadRight4;
    public ModelRenderer ShoulderPadRight1;
    public ModelRenderer MainArmGuardRight;
    public ModelRenderer ArmGuardPieceRight1;
    public ModelRenderer ArmGuardPieceRight2;
    public ModelRenderer ArmGuardPieceRight3;
    public ModelRenderer ArmGuardPieceRight4;
    public ModelRenderer ArmStrapRightTop;
    public ModelRenderer ArmStrapRightBottom;
    public ModelRenderer ShoulderPadLeft2;
    public ModelRenderer ShoulderPadLeft3;
    public ModelRenderer ShoulderPadLeft4;
    public ModelRenderer MainArmGuardLeft;
    public ModelRenderer ShoulderPadLeft1;
    public ModelRenderer ArmGuardPieceLeft1;
    public ModelRenderer ArmGuardPieceLeft2;
    public ModelRenderer ArmGuardPieceLeft3;
    public ModelRenderer ArmGuardPieceLeft4;
    public ModelRenderer ArmStrapLeftBottom;
    public ModelRenderer ArmStrapLeftTop;
    public ModelRenderer MainLegPieceRight;
    public ModelRenderer LegPieceRight1;
    public ModelRenderer LegPieceRight2;
    public ModelRenderer MainBootPieceRight;
    public ModelRenderer BootPieceRight1;
    public ModelRenderer BootPieceRight2;
    public ModelRenderer MainKneePadRight;
    public ModelRenderer KneePieceRight1;
    public ModelRenderer MainLegPieceLeft;
    public ModelRenderer LegPieceLeft2;
    public ModelRenderer LegPieceLeft1;
    public ModelRenderer MainBootPieceLeft;
    public ModelRenderer BootPieceLeft1;
    public ModelRenderer BootPieceLeft2;
    public ModelRenderer MainKneePadLeft;
    public ModelRenderer KneePieceLeft1;
    public ModelRenderer BootPieceLeft3;
    public ModelRenderer BootPieceLeft4;
    public ModelRenderer BootPieceRight3;
    public ModelRenderer BootPieceRight4;
    public ModelRenderer LeggsTop;

    // Draconic Specific
    public ModelRenderer DrMainChestPieceTop;
    public ModelRenderer DrMainChestPieceMid;
    public ModelRenderer DrChestDecorationPiece1;
    public ModelRenderer DrChestDecorationPiece3;
    public ModelRenderer BeltFront;
    public ModelRenderer BeltBack;
    public ModelRenderer BeltLeft;
    public ModelRenderer BeltRight;
    public ModelRenderer BeltBuckle;
    // Wyvern Specific
    public ModelRenderer WyHelmPieceTop1;
    public ModelRenderer WyHelmPieceTop2;
    public ModelRenderer WyHelmPieceTop3;
    public ModelRenderer WyHelmPieceTop4;
    public ModelRenderer WyMainChestPieceMid;
    public ModelRenderer WyMainChestPieceTop;
    public ModelRenderer WyChestPieceTop1;
    public ModelRenderer WyChestPieceTop2;
    public ModelRenderer WyChestDecorationPiece3;
    public ModelRenderer WyChestPieceTop3;

    private boolean isDraconic;

    public ModelDraconicArmorOld(float f, boolean isHelmet, boolean isChestPiece, boolean isLeggings, boolean isdBoots,
            boolean isDraconic) {
        super(f, 0.0f, 128, 128);
        this.textureWidth = 256;
        this.textureHeight = 128;
        this.isDraconic = isDraconic;

        /* Helm */
        {
            this.bipedHead = new ModelRenderer(this, 0, 0);
            this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);

            this.MainHelmPieceLeft1 = new ModelRenderer(this, 191, 0);
            this.MainHelmPieceLeft1.setRotationPoint(3.5F, -3.0F, -4.5F);
            this.MainHelmPieceLeft1.addBox(0.0F, 0.0F, 0.0F, 1, 3, 9, 0.0F);

            this.HelmPieceFront2 = new ModelRenderer(this, 177, 43);
            this.HelmPieceFront2.setRotationPoint(-0.5F, -5.5F, -4.5F);
            this.HelmPieceFront2.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);

            this.MainHelmPieceRight2 = new ModelRenderer(this, 170, 0);
            this.MainHelmPieceRight2.setRotationPoint(-4.5F, -8.0F, -4.5F);
            this.MainHelmPieceRight2.addBox(0.0F, 0.0F, 0.0F, 1, 2, 9, 0.0F);

            this.MainHelmPieceTop = new ModelRenderer(this, 177, 24);
            this.MainHelmPieceTop.mirror = true;
            this.MainHelmPieceTop.setRotationPoint(-4.0F, -8.300000190734863F, -4.5F);
            this.MainHelmPieceTop.addBox(0.0F, 0.0F, 0.0F, 8, 1, 9, 0.0F);
            this.setRotateAngle(MainHelmPieceTop, 0.05235987901687623F, -0.0F, 0.0F);

            this.MainHelmPieceFrontBottom = new ModelRenderer(this, 174, 35);
            this.MainHelmPieceFrontBottom.setRotationPoint(-4.0F, -3.0F, -5.0F);
            this.MainHelmPieceFrontBottom.addBox(0.0F, 0.0F, 0.0F, 8, 3, 1, 0.0F);

            this.MainHelmPieceLeft2 = new ModelRenderer(this, 170, 0);
            this.MainHelmPieceLeft2.setRotationPoint(3.5F, -8.0F, -4.5F);
            this.MainHelmPieceLeft2.addBox(0.0F, 0.0F, 0.0F, 1, 2, 9, 0.0F);

            this.HelmPieceBack1 = new ModelRenderer(this, 193, 39);
            this.HelmPieceBack1.setRotationPoint(-4.0F, -8.0F, 4.0F);
            this.HelmPieceBack1.addBox(0.0F, 0.0F, 0.0F, 8, 8, 1, 0.0F);

            this.MainHelmPieceLeft3 = new ModelRenderer(this, 195, 13);
            this.MainHelmPieceLeft3.setRotationPoint(3.5F, -6.0F, -2.5F);
            this.MainHelmPieceLeft3.addBox(0.0F, 0.0F, 0.0F, 1, 3, 7, 0.0F);

            this.MainHelmPieceRight1 = new ModelRenderer(this, 191, 0);
            this.MainHelmPieceRight1.setRotationPoint(-4.5F, -3.0F, -4.5F);
            this.MainHelmPieceRight1.addBox(0.0F, 0.0F, 0.0F, 1, 3, 9, 0.0F);

            this.MainHelmPieceBack = new ModelRenderer(this, 178, 13);
            this.MainHelmPieceBack.setRotationPoint(-3.5F, -8.0F, 3.5F);
            this.MainHelmPieceBack.addBox(0.0F, 0.0F, 0.0F, 7, 8, 1, 0.0F);

            this.MainHelmPieceRight3 = new ModelRenderer(this, 195, 13);
            this.MainHelmPieceRight3.setRotationPoint(-4.5F, -6.0F, -2.5F);
            this.MainHelmPieceRight3.addBox(0.0F, 0.0F, 0.0F, 1, 3, 7, 0.0F);

            this.HelmPieceFront1 = new ModelRenderer(this, 175, 40);
            this.HelmPieceFront1.setRotationPoint(-1.0F, -6.0F, -5.0F);
            this.HelmPieceFront1.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);

            this.MainHelmPieceBottom = new ModelRenderer(this, 177, 24);
            this.MainHelmPieceBottom.setRotationPoint(-4.0F, -0.5F, -4.5F);
            this.MainHelmPieceBottom.addBox(0.0F, 0.0F, 0.0F, 8, 1, 9, 0.0F);

            this.MainHelmPieceFrontTop = new ModelRenderer(this, 193, 35);
            this.MainHelmPieceFrontTop.setRotationPoint(-4.0F, -8.0F, -5.0F);
            this.MainHelmPieceFrontTop.addBox(0.0F, 0.0F, 0.0F, 8, 2, 1, 0.0F);

            this.HornPieceLeft1 = new ModelRenderer(this, 182, 40);
            this.HornPieceLeft1.setRotationPoint(4.0F, -7.5F, -1.0F);
            this.HornPieceLeft1.addBox(0.0F, 0.0F, 0.0F, 1, 4, 4, 0.0F);

            this.HornPieceLeft2 = new ModelRenderer(this, 201, 49);
            this.HornPieceLeft2.setRotationPoint(4.6F, -5.5F, 1.03F);
            this.HornPieceLeft2.addBox(0.0F, -1.5F, -1.5F, 2, 3, 3, 0.0F);
            this.setRotateAngle(HornPieceLeft2, 0.0F, -0.0F, -0.13962634015954636F);

            this.HornPieceLeft3 = new ModelRenderer(this, 190, 49);
            this.HornPieceLeft3.setRotationPoint(6.1F, -5.6F, 1.0F);
            this.HornPieceLeft3.addBox(0.0F, -1.0F, -1.0F, 3, 2, 2, 0.0F);
            this.setRotateAngle(HornPieceLeft3, 0.0F, -0.0F, -0.5235987755982988F);

            this.HornPieceRight1 = new ModelRenderer(this, 182, 40);
            this.HornPieceRight1.setRotationPoint(-5.0F, -7.5F, -1.0F);
            this.HornPieceRight1.addBox(0.0F, 0.0F, 0.0F, 1, 4, 4, 0.0F);

            this.HornPieceRight2 = new ModelRenderer(this, 201, 49);
            this.HornPieceRight2.setRotationPoint(-4.6F, -5.5F, 1.03F);
            this.HornPieceRight2.addBox(0.0F, -1.5F, -1.5F, 2, 3, 3, 0.0F);
            this.setRotateAngle(HornPieceRight2, 0.0F, -0.0F, -3.001966313430247F);

            this.HornPieceRight3 = new ModelRenderer(this, 190, 49);
            this.HornPieceRight3.setRotationPoint(-6.1F, -5.6F, 1.0F);
            this.HornPieceRight3.addBox(0.0F, -1.0F, -1.0F, 3, 2, 2, 0.0F);
            this.setRotateAngle(HornPieceRight3, 0.0F, -0.0F, -2.6179938779914944F);

            this.HornPieceLeft4 = new ModelRenderer(this, 181, 49);
            this.HornPieceLeft4.setRotationPoint(8.2F, -6.7F, 1.0F);
            this.HornPieceLeft4.addBox(0.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
            this.setRotateAngle(HornPieceLeft4, 0.0F, -0.0F, -0.8726646259971648F);

            this.HornPieceRight4 = new ModelRenderer(this, 181, 49);
            this.HornPieceRight4.setRotationPoint(-8.2F, -6.7F, 1.0F);
            this.HornPieceRight4.addBox(0.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
            this.setRotateAngle(HornPieceRight4, 0.0F, -0.0F, -2.2689280275926285F);

            this.WyHelmPieceTop1 = new ModelRenderer(this, 181, 49);
            this.WyHelmPieceTop1.setRotationPoint(-0.5F, -8.5F, -4.5F);
            this.WyHelmPieceTop1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
            this.setRotateAngle(WyHelmPieceTop1, 0.2792526803190927F, -0.0F, 0.0F);

            this.WyHelmPieceTop2 = new ModelRenderer(this, 201, 56);
            this.WyHelmPieceTop2.setRotationPoint(-1.0F, -10.0F, -2.0F);
            this.WyHelmPieceTop2.addBox(0.0F, 0.0F, 0.0F, 2, 2, 3, 0.0F);
            this.setRotateAngle(WyHelmPieceTop2, 0.08726646259971647F, -0.0F, 0.0F);

            this.WyHelmPieceTop3 = new ModelRenderer(this, 188, 54);
            this.WyHelmPieceTop3.setRotationPoint(-1.5F, -11.0F, 1.0F);
            this.WyHelmPieceTop3.addBox(0.0F, 0.0F, 0.0F, 3, 3, 3, 0.0F);
            this.setRotateAngle(WyHelmPieceTop3, -0.2792526803190927F, -0.0F, 0.0F);

            this.WyHelmPieceTop4 = new ModelRenderer(this, 179, 54);
            this.WyHelmPieceTop4.setRotationPoint(-0.5F, -10.0F, 1.5F);
            this.WyHelmPieceTop4.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
            this.setRotateAngle(WyHelmPieceTop4, -0.20943951023931953F, -0.0F, 0.0F);
        }

        /* Chest */
        {
            this.bipedBody = new ModelRenderer(this, 16, 16);
            this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);

            this.DrMainChestPieceTop = new ModelRenderer(this, 214, 0);
            this.DrMainChestPieceTop.setRotationPoint(-5.5F, -0.5F, -5.0F);
            this.DrMainChestPieceTop.addBox(0.0F, 0.0F, 0.0F, 11, 4, 10, 0.0F);
            this.setRotateAngle(DrMainChestPieceTop, 0.15707963705062866F, -0.0F, 0.0F);

            this.DrMainChestPieceMid = new ModelRenderer(this, 224, 15);
            this.DrMainChestPieceMid.setRotationPoint(-5.0F, 2.0F, -3.0F);
            this.DrMainChestPieceMid.addBox(0.0F, 0.0F, 0.0F, 10, 5, 6, 0.0F);

            this.WyChestPieceTop3 = new ModelRenderer(this, 239, 0);
            this.WyChestPieceTop3.setRotationPoint(-3.5F, 4.5F, -3.5F);
            this.WyChestPieceTop3.addBox(1.0F, 0.0F, 0.0F, 4, 4, 1, 0.0F);
            this.setRotateAngle(WyChestPieceTop3, 0.0879645943005142F, 0.08621926504851989F, -0.7890633548266364F);

            this.WyMainChestPieceTop = new ModelRenderer(this, 250, 0);
            this.WyMainChestPieceTop.setRotationPoint(-5.7F, 0.0F, -2.6F);
            this.WyMainChestPieceTop.addBox(1.0F, 0.0F, 0.0F, 2, 4, 1, 0.0F);
            this.setRotateAngle(WyMainChestPieceTop, 0.0F, 0.47123889803846897F, 0.0F);

            this.MainChestPieceBottom = new ModelRenderer(this, 228, 27);
            this.MainChestPieceBottom.setRotationPoint(-4.5F, 7.0F, -2.5F);
            this.MainChestPieceBottom.addBox(0.0F, 0.0F, 0.0F, 9, 5, 5, 0.0F);

            this.DrChestDecorationPiece1 = new ModelRenderer(this, 244, 38);
            this.DrChestDecorationPiece1.setRotationPoint(0.0F, -0.5F, -4.0F);
            this.DrChestDecorationPiece1.addBox(0.0F, 0.0F, -0.5F, 5, 5, 1, 0.0F);
            this.setRotateAngle(
                    DrChestDecorationPiece1,
                    0.1426609064282019F,
                    -0.13533507716461618F,
                    0.7946695004338321F);

            this.ChestDecorationPiece2 = new ModelRenderer(this, 229, 38);
            this.ChestDecorationPiece2.setRotationPoint(-3.0F, 7.0F, -3.0F);
            this.ChestDecorationPiece2.addBox(0.0F, 0.0F, 0.0F, 6, 3, 1, 0.0F);
            this.setRotateAngle(ChestDecorationPiece2, 0.12217304855585097F, -0.0F, 0.0F);

            this.DrChestDecorationPiece3 = new ModelRenderer(this, 236, 45);
            this.DrChestDecorationPiece3.setRotationPoint(-4.0F, 1.0F, 3.0F);
            this.DrChestDecorationPiece3.addBox(0.0F, 0.0F, 0.0F, 8, 5, 2, 0.0F);
            this.setRotateAngle(DrChestDecorationPiece3, -0.39211732149124146F, -0.0F, 0.0F);

            this.ChestDecorationPiece4 = new ModelRenderer(this, 229, 38);
            this.ChestDecorationPiece4.setRotationPoint(-3.0F, 6.800000190734863F, 2.0F);
            this.ChestDecorationPiece4.addBox(0.0F, 0.0F, 0.0F, 6, 3, 1, 0.0F);
            this.setRotateAngle(ChestDecorationPiece4, -0.12217304855585097F, -0.0F, 0.0F);

            this.WyMainChestPieceMid = new ModelRenderer(this, 224, 13);
            this.WyMainChestPieceMid.setRotationPoint(-5.0F, -0.1F, -3.0F);
            this.WyMainChestPieceMid.addBox(0.0F, 0.0F, 0.0F, 10, 7, 6, 0.0F);

            this.WyChestPieceTop2 = new ModelRenderer(this, 250, 0);
            this.WyChestPieceTop2.setRotationPoint(2.1F, 0.0F, -4.4F);
            this.WyChestPieceTop2.addBox(1.0F, 0.0F, 0.0F, 2, 4, 1, 0.0F);
            this.setRotateAngle(WyChestPieceTop2, 0.0F, -0.47123889803846897F, 0.0F);

            this.WyChestPieceTop1 = new ModelRenderer(this, 240, 6);
            this.WyChestPieceTop1.setRotationPoint(-4.0F, 0.0F, -4.0F);
            this.WyChestPieceTop1.addBox(1.0F, 0.0F, 0.0F, 6, 4, 2, 0.0F);

            this.WyChestDecorationPiece3 = new ModelRenderer(this, 236, 45);
            this.WyChestDecorationPiece3.setRotationPoint(-3.5F, 0.0F, 2.5F);
            this.WyChestDecorationPiece3.addBox(0.0F, 0.0F, 0.0F, 7, 5, 2, 0.0F);
            this.setRotateAngle(WyChestDecorationPiece3, -0.2617993877991494F, -0.0F, 0.0F);

            this.BeltFront = new ModelRenderer(this, 121, 25);
            this.BeltFront.setRotationPoint(0.5F, 10.01F, -2.0F);
            this.BeltFront.addBox(-5.0F, 0.0F, -0.7F, 9, 2, 1, 0.0F);

            this.BeltBuckle = new ModelRenderer(this, 121, 29);
            this.BeltBuckle.setRotationPoint(3.5F, 10.0F, -2.2F);
            this.BeltBuckle.addBox(-5.0F, 0.0F, -0.7F, 3, 2, 1, 0.0F);

            this.BeltBack = new ModelRenderer(this, 121, 25);
            this.BeltBack.setRotationPoint(0.5F, 10.01F, 2.0F);
            this.BeltBack.addBox(-5.0F, 0.0F, -0.3F, 9, 2, 1, 0.0F);

            this.BeltRight = new ModelRenderer(this, 142, 25);
            this.BeltRight.setRotationPoint(-4.7F, 10.01F, -2.2F);
            this.BeltRight.addBox(0.0F, 0.0F, -0.3F, 1, 2, 5, 0.0F);

            this.BeltLeft = new ModelRenderer(this, 142, 25);
            this.BeltLeft.setRotationPoint(3.7F, 10.01F, -2.2F);
            this.BeltLeft.addBox(0.0F, 0.0F, -0.3F, 1, 2, 5, 0.0F);

            // Left
            this.bipedLeftArm = new ModelRenderer(this, 40, 16);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);

            this.ShoulderPadLeft1 = new ModelRenderer(this, 236, 53);
            this.ShoulderPadLeft1.setRotationPoint(-1.5F, -2.0F, -2.5F);
            this.ShoulderPadLeft1.addBox(0.0F, 0.0F, 0.0F, 5, 4, 5, 0.0F);

            this.ShoulderPadLeft2 = new ModelRenderer(this, 232, 63);
            this.ShoulderPadLeft2.setRotationPoint(-1.0F, -2.5F, -3.0F);
            this.ShoulderPadLeft2.addBox(-0.3F, 0.0F, 0.0F, 6, 2, 6, 0.0F);
            this.setRotateAngle(ShoulderPadLeft2, 0.0F, -0.0F, 0.3665191429188092F);

            this.ShoulderPadLeft3 = new ModelRenderer(this, 215, 53);
            this.ShoulderPadLeft3.mirror = true;
            this.ShoulderPadLeft3.setRotationPoint(-1.0F, -3.0F, -2.5F);
            this.ShoulderPadLeft3.addBox(0.3F, -0.5F, 0.0F, 5, 2, 5, 0.0F);
            this.setRotateAngle(ShoulderPadLeft3, 0.0F, -0.0F, 0.20943951023931953F);

            this.ShoulderPadLeft4 = new ModelRenderer(this, 219, 61);
            this.ShoulderPadLeft4.setRotationPoint(-0.5F, -2.5F, -2.0F);
            this.ShoulderPadLeft4.addBox(0.7F, -2.0F, 0.5F, 3, 2, 3, 0.0F);
            this.setRotateAngle(ShoulderPadLeft4, 0.0F, -0.0F, 0.12217304763960307F);

            this.MainArmGuardLeft = new ModelRenderer(this, 240, 72);
            this.MainArmGuardLeft.setRotationPoint(0.5F, 3.0F, -2.5F);
            this.MainArmGuardLeft.addBox(0.0F, 0.0F, 0.0F, 3, 5, 5, 0.0F);

            this.ArmGuardPieceLeft1 = new ModelRenderer(this, 223, 67);
            this.ArmGuardPieceLeft1.setRotationPoint(2.5F, 6.0F, -1.5F);
            this.ArmGuardPieceLeft1.addBox(0.2F, -0.3F, 0.0F, 1, 2, 3, 0.0F);
            this.setRotateAngle(ArmGuardPieceLeft1, 0.0F, -0.0F, -0.17453292519943295F);

            this.ArmGuardPieceLeft2 = new ModelRenderer(this, 223, 67);
            this.ArmGuardPieceLeft2.setRotationPoint(2.5F, 5.0F, -1.5F);
            this.ArmGuardPieceLeft2.addBox(0.2F, -0.3F, 0.0F, 1, 2, 3, 0.0F);
            this.setRotateAngle(ArmGuardPieceLeft2, 0.0F, -0.0F, -0.17453292519943295F);

            this.ArmGuardPieceLeft3 = new ModelRenderer(this, 223, 67);
            this.ArmGuardPieceLeft3.setRotationPoint(2.5F, 4.0F, -1.5F);
            this.ArmGuardPieceLeft3.addBox(0.2F, -0.3F, 0.0F, 1, 2, 3, 0.0F);
            this.setRotateAngle(ArmGuardPieceLeft3, 0.0F, -0.0F, -0.17453292519943295F);

            this.ArmGuardPieceLeft4 = new ModelRenderer(this, 231, 72);
            this.ArmGuardPieceLeft4.setRotationPoint(2.5F, 3.0F, -1.5F);
            this.ArmGuardPieceLeft4.addBox(0.2F, 0.6F, 0.0F, 1, 1, 3, 0.0F);
            this.setRotateAngle(ArmGuardPieceLeft4, 0.0F, -0.0F, -0.17453292519943295F);

            this.ArmStrapLeftTop = new ModelRenderer(this, 225, 77);
            this.ArmStrapLeftTop.setRotationPoint(-1.43F, 3.0F, -2.5F);
            this.ArmStrapLeftTop.addBox(0.0F, 0.0F, 0.0F, 2, 1, 5, 0.0F);

            this.ArmStrapLeftBottom = new ModelRenderer(this, 225, 77);
            this.ArmStrapLeftBottom.setRotationPoint(-1.5F, 7.0F, -2.5F);
            this.ArmStrapLeftBottom.addBox(0.0F, 0.0F, 0.0F, 2, 1, 5, 0.0F);

            // Right
            this.MainArmGuardRight = new ModelRenderer(this, 240, 72);
            this.MainArmGuardRight.setRotationPoint(-3.5F, 3.0F, -2.5F);
            this.MainArmGuardRight.addBox(0.0F, 0.0F, 0.0F, 3, 5, 5, 0.0F);

            this.ArmGuardPieceRight1 = new ModelRenderer(this, 231, 72);
            this.ArmGuardPieceRight1.setRotationPoint(-3.9F, 2.8F, -1.5F);
            this.ArmGuardPieceRight1.addBox(0.2F, 0.6F, 0.0F, 1, 1, 3, 0.0F);
            this.setRotateAngle(ArmGuardPieceRight1, 0.0F, -0.0F, 0.17453292519943295F);

            this.ArmGuardPieceRight2 = new ModelRenderer(this, 223, 67);
            this.ArmGuardPieceRight2.setRotationPoint(-4.6F, 4.1F, -1.5F);
            this.ArmGuardPieceRight2.addBox(0.8F, -0.7F, 0.0F, 1, 2, 3, 0.0F);
            this.setRotateAngle(ArmGuardPieceRight2, 0.0F, -0.0F, 0.17453292519943295F);

            this.ArmGuardPieceRight3 = new ModelRenderer(this, 223, 67);
            this.ArmGuardPieceRight3.setRotationPoint(-4.03F, 5.3F, -1.5F);
            this.ArmGuardPieceRight3.addBox(0.2F, -0.8F, 0.0F, 1, 2, 3, 0.0F);
            this.setRotateAngle(ArmGuardPieceRight3, 0.0F, -0.0F, 0.17453292519943295F);

            this.ArmGuardPieceRight4 = new ModelRenderer(this, 223, 67);
            this.ArmGuardPieceRight4.setRotationPoint(-4.0F, 6.8F, -1.5F);
            this.ArmGuardPieceRight4.addBox(0.1F, -1.3F, 0.0F, 1, 2, 3, 0.0F);
            this.setRotateAngle(ArmGuardPieceRight4, 0.0F, -0.0F, 0.17453292519943295F);

            this.ShoulderPadRight1 = new ModelRenderer(this, 236, 53);
            this.ShoulderPadRight1.setRotationPoint(-3.5F, -2.0F, -2.5F);
            this.ShoulderPadRight1.addBox(0.0F, 0.0F, 0.0F, 5, 4, 5, 0.0F);

            this.ShoulderPadRight2 = new ModelRenderer(this, 232, 63);
            this.ShoulderPadRight2.setRotationPoint(-5.0F, -2.5F, -3.0F);
            this.ShoulderPadRight2.addBox(-0.1F, 2.1F, 0.0F, 6, 2, 6, 0.0F);
            this.setRotateAngle(ShoulderPadRight2, 0.0F, -0.0F, -0.3665191429188092F);

            this.ShoulderPadRight3 = new ModelRenderer(this, 215, 53);
            this.ShoulderPadRight3.setRotationPoint(-5.0F, -3.0F, -2.5F);
            this.ShoulderPadRight3.addBox(0.6F, 0.7F, 0.0F, 5, 2, 5, 0.0F);
            this.setRotateAngle(ShoulderPadRight3, 0.0F, -0.0F, -0.20943951023931953F);

            this.ShoulderPadRight4 = new ModelRenderer(this, 219, 61);
            this.ShoulderPadRight4.setRotationPoint(-4.5F, -2.5F, -2.0F);
            this.ShoulderPadRight4.addBox(1.3F, -1.5F, 0.5F, 3, 2, 3, 0.0F);
            this.setRotateAngle(ShoulderPadRight4, 0.0F, -0.0F, -0.12217304763960307F);

            this.bipedRightArm = new ModelRenderer(this, 40, 16);
            this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
            this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);

            this.ArmStrapRightTop = new ModelRenderer(this, 225, 77);
            this.ArmStrapRightTop.setRotationPoint(-0.5F, 3.0F, -2.5F);
            this.ArmStrapRightTop.addBox(0.0F, 0.0F, 0.0F, 2, 1, 5, 0.0F);

            this.ArmStrapRightBottom = new ModelRenderer(this, 225, 77);
            this.ArmStrapRightBottom.setRotationPoint(-0.5F, 7.0F, -2.5F);
            this.ArmStrapRightBottom.addBox(0.0F, 0.0F, 0.0F, 2, 1, 5, 0.0F);
        }

        /* Leggs */
        {
            this.LeggsTop = new ModelRenderer(this, 121, 15);
            this.LeggsTop.setRotationPoint(0.5F, 8.0F, -2.4F);
            this.LeggsTop.addBox(-5.0F, 0.0F, 0.0F, 9, 4, 5, 0.0F);
            // Left
            this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
            this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
            this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);

            this.MainLegPieceLeft = new ModelRenderer(this, 149, 0);
            this.MainLegPieceLeft.setRotationPoint(-2.0F, -1F, -2.5F);
            this.MainLegPieceLeft.addBox(0.0F, 0.0F, 0.0F, 4, 10, 5, 0.0F);

            this.LegPieceLeft1 = new ModelRenderer(this, 138, 0);
            this.LegPieceLeft1.setRotationPoint(1.5F, -1F, -2.0F);
            this.LegPieceLeft1.addBox(0.0F, 0.0F, 0.0F, 1, 10, 4, 0.0F);

            this.LegPieceLeft2 = new ModelRenderer(this, 138, 0);
            this.LegPieceLeft2.setRotationPoint(-2.5F, -1F, -2.0F);
            this.LegPieceLeft2.addBox(0.0F, 0.0F, 0.0F, 1, 10, 4, 0.0F);

            this.MainKneePadLeft = new ModelRenderer(this, 149, 15);
            this.MainKneePadLeft.setRotationPoint(0.0F, 2.5F, -3.0F);
            this.MainKneePadLeft.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
            this.setRotateAngle(MainKneePadLeft, 0.0F, -0.0F, 0.7853981633974483F);

            this.KneePieceLeft1 = new ModelRenderer(this, 156, 15);
            this.KneePieceLeft1.setRotationPoint(-0.5F, 2.0F, -2.7F);
            this.KneePieceLeft1.addBox(0.0F, 0.0F, 0.0F, 1, 4, 1, 0.0F);

            // Right
            this.bipedRightLeg = new ModelRenderer(this, 0, 16);
            this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
            this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);

            this.MainLegPieceRight = new ModelRenderer(this, 149, 0);
            this.MainLegPieceRight.setRotationPoint(-2.0F, -1F, -2.5F);
            this.MainLegPieceRight.addBox(0.0F, 0.0F, 0.0F, 4, 10, 5, 0.0F);

            this.LegPieceRight1 = new ModelRenderer(this, 138, 0);
            this.LegPieceRight1.setRotationPoint(-2.5F, -1F, -2.0F);
            this.LegPieceRight1.addBox(0.0F, 0.0F, 0.0F, 1, 10, 4, 0.0F);

            this.LegPieceRight2 = new ModelRenderer(this, 138, 0);
            this.LegPieceRight2.setRotationPoint(1.5F, -1F, -2.0F);
            this.LegPieceRight2.addBox(0.0F, 0.0F, 0.0F, 1, 10, 4, 0.0F);

            this.MainKneePadRight = new ModelRenderer(this, 149, 15);
            this.MainKneePadRight.setRotationPoint(0.0F, 2.5F, -3.0F);
            this.MainKneePadRight.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);

            this.setRotateAngle(MainKneePadRight, 0.0F, -0.0F, 0.7853981633974483F);
            this.KneePieceRight1 = new ModelRenderer(this, 156, 15);
            this.KneePieceRight1.setRotationPoint(-0.5F, 2.0F, -2.7F);
            this.KneePieceRight1.addBox(0.0F, 0.0F, 0.0F, 1, 4, 1, 0.0F);
        }

        /* Boots */
        {
            // Left
            this.MainBootPieceLeft = new ModelRenderer(this, 119, 0);
            this.MainBootPieceLeft.setRotationPoint(-2.0F, 9.1F, -2.5F);
            this.MainBootPieceLeft.addBox(0.0F, 0.0F, 0.0F, 4, 3, 5, 0.0F);

            this.BootPieceLeft1 = new ModelRenderer(this, 129, 9);
            this.BootPieceLeft1.setRotationPoint(-1.5F, 9.0F, -3.5F);
            this.BootPieceLeft1.addBox(0.0F, 0.0F, 0.0F, 3, 3, 1, 0.0F);

            this.BootPieceLeft2 = new ModelRenderer(this, 122, 9);
            this.BootPieceLeft2.setRotationPoint(-1.0F, 9.0F, -2.5F);
            this.BootPieceLeft2.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
            this.setRotateAngle(BootPieceLeft2, -0.40142572795869574F, -0.0F, 0.0F);

            this.BootPieceLeft3 = new ModelRenderer(this, 138, 0);
            this.BootPieceLeft3.setRotationPoint(1.5F, 9.05F, -2.0F);
            this.BootPieceLeft3.addBox(0.0F, 0.0F, 0.0F, 1, 3, 4, 0.0F);

            this.BootPieceLeft4 = new ModelRenderer(this, 138, 0);
            this.BootPieceLeft4.setRotationPoint(-2.5F, 9.05F, -2.0F);
            this.BootPieceLeft4.addBox(0.0F, 0.0F, 0.0F, 1, 3, 4, 0.0F);

            // Right
            this.MainBootPieceRight = new ModelRenderer(this, 119, 0);
            this.MainBootPieceRight.setRotationPoint(-2.0F, 9.1F, -2.5F);
            this.MainBootPieceRight.addBox(0.0F, 0.0F, 0.0F, 4, 3, 5, 0.0F);

            this.BootPieceRight1 = new ModelRenderer(this, 129, 9);
            this.BootPieceRight1.setRotationPoint(-1.5F, 9.0F, -3.5F);
            this.BootPieceRight1.addBox(0.0F, 0.0F, 0.0F, 3, 3, 1, 0.0F);

            this.BootPieceRight2 = new ModelRenderer(this, 122, 9);
            this.BootPieceRight2.setRotationPoint(-1.0F, 9.0F, -2.5F);
            this.BootPieceRight2.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
            this.setRotateAngle(BootPieceRight2, -0.40142572795869574F, -0.0F, 0.0F);

            this.BootPieceRight3 = new ModelRenderer(this, 138, 0);
            this.BootPieceRight3.setRotationPoint(-2.5F, 9.05F, -2.0F);
            this.BootPieceRight3.addBox(0.0F, 0.0F, 0.0F, 1, 3, 4, 0.0F);

            this.BootPieceRight4 = new ModelRenderer(this, 138, 0);
            this.BootPieceRight4.setRotationPoint(1.5F, 9.05F, -2.0F);
            this.BootPieceRight4.addBox(0.0F, 0.0F, 0.0F, 1, 3, 4, 0.0F);
        }

        this.bipedHead.cubeList.clear();
        this.bipedHeadwear.cubeList.clear();
        if (isHelmet) {
            this.bipedHead.addChild(this.MainHelmPieceLeft1);
            this.bipedHead.addChild(this.HelmPieceFront2);
            this.bipedHead.addChild(this.MainHelmPieceRight2);
            this.bipedHead.addChild(this.MainHelmPieceTop);
            this.bipedHead.addChild(this.MainHelmPieceFrontBottom);
            this.bipedHead.addChild(this.HornPieceRight1);
            this.bipedHead.addChild(this.HornPieceLeft1);

            this.bipedHead.addChild(this.MainHelmPieceLeft2);
            this.bipedHead.addChild(this.HornPieceRight3);
            this.bipedHead.addChild(this.HornPieceLeft3);
            this.bipedHead.addChild(this.HelmPieceBack1);

            this.bipedHead.addChild(this.MainHelmPieceLeft3);
            this.bipedHead.addChild(this.MainHelmPieceRight1);
            this.bipedHead.addChild(this.HornPieceRight2);
            this.bipedHead.addChild(this.MainHelmPieceBack);
            this.bipedHead.addChild(this.MainHelmPieceRight3);
            this.bipedHead.addChild(this.HelmPieceFront1);
            this.bipedHead.addChild(this.MainHelmPieceBottom);
            this.bipedHead.addChild(this.MainHelmPieceFrontTop);
            this.bipedHead.addChild(this.HornPieceLeft2);

            if (isDraconic) {
                this.bipedHead.addChild(this.HornPieceLeft4);
                this.bipedHead.addChild(this.HornPieceRight4);
            } else {
                this.bipedHead.addChild(this.WyHelmPieceTop1);
                this.bipedHead.addChild(this.WyHelmPieceTop2);
                this.bipedHead.addChild(this.WyHelmPieceTop3);
                this.bipedHead.addChild(this.WyHelmPieceTop4);
            }
        }

        this.bipedBody.cubeList.clear();
        this.bipedRightArm.cubeList.clear();
        this.bipedLeftArm.cubeList.clear();
        if (isChestPiece) {
            this.bipedBody.addChild(this.MainChestPieceBottom);
            this.bipedBody.addChild(this.ChestDecorationPiece2);
            this.bipedBody.addChild(this.ChestDecorationPiece4);

            if (isDraconic) {
                this.bipedBody.addChild(this.DrMainChestPieceTop);
                this.bipedBody.addChild(this.DrMainChestPieceMid);
                this.bipedBody.addChild(this.DrChestDecorationPiece1);
                this.bipedBody.addChild(this.DrChestDecorationPiece3);

                this.bipedLeftArm.addChild(this.ArmGuardPieceLeft1);
                this.bipedLeftArm.addChild(this.ArmGuardPieceLeft2);
                this.bipedLeftArm.addChild(this.ArmGuardPieceLeft3);
                this.bipedLeftArm.addChild(this.ArmGuardPieceLeft4);
                this.bipedLeftArm.addChild(this.MainArmGuardLeft);
                this.bipedLeftArm.addChild(this.ArmStrapLeftTop);
                this.bipedLeftArm.addChild(this.ArmStrapLeftBottom);

                this.bipedRightArm.addChild(this.ArmGuardPieceRight1);
                this.bipedRightArm.addChild(this.ArmGuardPieceRight2);
                this.bipedRightArm.addChild(this.ArmGuardPieceRight3);
                this.bipedRightArm.addChild(this.ArmGuardPieceRight4);
                this.bipedRightArm.addChild(this.MainArmGuardRight);
                this.bipedRightArm.addChild(this.ArmStrapRightTop);
                this.bipedRightArm.addChild(this.ArmStrapRightBottom);
            } else {
                this.bipedBody.addChild(this.WyMainChestPieceMid);
                this.bipedBody.addChild(this.WyChestPieceTop2);
                this.bipedBody.addChild(this.WyChestPieceTop1);
                this.bipedBody.addChild(this.WyChestPieceTop3);
                this.bipedBody.addChild(this.WyChestDecorationPiece3);
                this.bipedBody.addChild(this.WyMainChestPieceTop);
            }

            this.bipedLeftArm.addChild(this.ShoulderPadLeft1);
            this.bipedLeftArm.addChild(this.ShoulderPadLeft2);
            this.bipedLeftArm.addChild(this.ShoulderPadLeft3);
            this.bipedLeftArm.addChild(this.ShoulderPadLeft4);

            this.bipedRightArm.addChild(this.ShoulderPadRight1);
            this.bipedRightArm.addChild(this.ShoulderPadRight2);
            this.bipedRightArm.addChild(this.ShoulderPadRight3);
            this.bipedRightArm.addChild(this.ShoulderPadRight4);
        }

        this.bipedLeftLeg.cubeList.clear();
        this.bipedRightLeg.cubeList.clear();
        if (isLeggings) {
            this.bipedBody.addChild(this.LeggsTop);

            this.bipedLeftLeg.addChild(this.LegPieceLeft1);
            this.bipedLeftLeg.addChild(this.MainKneePadLeft);
            this.bipedLeftLeg.addChild(this.MainLegPieceLeft);
            this.bipedLeftLeg.addChild(this.KneePieceLeft1);
            this.bipedLeftLeg.addChild(this.LegPieceLeft2);

            this.bipedRightLeg.addChild(this.LegPieceRight2);
            this.bipedRightLeg.addChild(this.LegPieceRight1);
            this.bipedRightLeg.addChild(this.MainKneePadRight);
            this.bipedRightLeg.addChild(this.KneePieceRight1);
            this.bipedRightLeg.addChild(this.MainLegPieceRight);

            if (isDraconic) {
                this.bipedBody.addChild(this.BeltFront);
                this.bipedBody.addChild(this.BeltBack);
                this.bipedBody.addChild(this.BeltLeft);
                this.bipedBody.addChild(this.BeltRight);
                this.bipedBody.addChild(this.BeltBuckle);
            }
        }

        if (isdBoots) {
            this.bipedLeftLeg.addChild(this.MainBootPieceLeft);
            this.bipedLeftLeg.addChild(this.BootPieceLeft2);
            this.bipedLeftLeg.addChild(this.BootPieceLeft1);
            this.bipedLeftLeg.addChild(this.BootPieceLeft3);
            this.bipedLeftLeg.addChild(this.BootPieceLeft4);

            this.bipedRightLeg.addChild(this.MainBootPieceRight);
            this.bipedRightLeg.addChild(this.BootPieceRight1);
            this.bipedRightLeg.addChild(this.BootPieceRight2);
            this.bipedRightLeg.addChild(this.BootPieceRight3);
            this.bipedRightLeg.addChild(this.BootPieceRight4);
        }
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.bipedRightLeg.render(f5);
        this.bipedBody.render(f5 * 1.05F); // * 1.1F);
        this.bipedLeftArm.render(f5);
        this.bipedRightArm.render(f5);
        this.bipedLeftLeg.render(f5);
        this.bipedHead.render(f5 * 1.05F);
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
