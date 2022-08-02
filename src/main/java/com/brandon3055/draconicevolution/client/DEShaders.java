package com.brandon3055.draconicevolution.client;

import codechicken.lib.render.shader.CCShaderInstance;
import codechicken.lib.render.shader.CCUniform;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Created by brandon3055 on 15/05/2022
 */
public class DEShaders {
    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    public static CCShaderInstance reactorShader;
    public static CCUniform reactorTime;
    public static CCUniform reactorIntensity;

    public static CCShaderInstance reactorShieldShader;
    public static CCUniform reactorShieldTime;
    public static CCUniform reactorShieldIntensity;

    public static CCShaderInstance chaosShader;
    public static CCUniform chaosTime;
    public static CCUniform chaosYaw;
    public static CCUniform chaosPitch;
    public static CCUniform chaosAlpha;

    public static CCShaderInstance armorShieldShader;
    public static CCUniform armorShieldTime;
    public static CCUniform armorShieldActivation;
    public static CCUniform armorShieldColour;

    public static CCShaderInstance energyCrystalShader;
    public static CCUniform energyCrystalTime;
    public static CCUniform energyCrystalColour;
    public static CCUniform energyCrystalMipmap;
    public static CCUniform energyCrystalAngle;

    public static CCShaderInstance testShader;
    public static CCUniform testTime;
    public static CCUniform testColour;
    public static CCUniform testInB;
    public static CCUniform testInC;
    public static CCUniform testInD;

    public static CCShaderInstance customBlockShader;
    public static CCUniform modelMat;

    public static CCShaderInstance energyCoreShader;
    public static CCUniform energyCoreTime;
    public static CCUniform energyCoreActivation;
    public static CCUniform energyCoreEffectColour;
    public static CCUniform energyCoreFrameColour;
    public static CCUniform energyCoreRotTriColour;


    public static void init() {
        LOCK.lock();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DEShaders::onRegisterShaders);
    }

    private static void onRegisterShaders(RegisterShadersEvent event) {
        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(DraconicEvolution.MODID, "reactor"), DefaultVertexFormat.POSITION_TEX), e -> {
            reactorShader = (CCShaderInstance) e;
            reactorTime = reactorShader.getUniform("time");
            reactorIntensity = reactorShader.getUniform("intensity");
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(DraconicEvolution.MODID, "reactor_shield"), DefaultVertexFormat.POSITION_TEX), e -> {
            reactorShieldShader = (CCShaderInstance) e;
            reactorShieldTime = reactorShieldShader.getUniform("time");
            reactorShieldIntensity = reactorShieldShader.getUniform("intensity");
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(DraconicEvolution.MODID, "chaos"), DefaultVertexFormat.POSITION_COLOR_LIGHTMAP), e -> {
            chaosShader = (CCShaderInstance) e;
            chaosTime = chaosShader.getUniform("Time");
            chaosYaw = chaosShader.getUniform("Yaw");
            chaosPitch = chaosShader.getUniform("Pitch");
            chaosAlpha = chaosShader.getUniform("Alpha");
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(DraconicEvolution.MODID, "armor_shield"), DefaultVertexFormat.POSITION_TEX), e -> {
            armorShieldShader = (CCShaderInstance) e;
            armorShieldTime = armorShieldShader.getUniform("Time");
            armorShieldActivation = armorShieldShader.getUniform("Activation");
            armorShieldColour = armorShieldShader.getUniform("BaseColour");
            armorShieldShader.onApply(() -> armorShieldTime.glUniform1f((TimeKeeper.getClientTick() + Minecraft.getInstance().getFrameTime()) / 20F));
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(DraconicEvolution.MODID, "energy_crystal"), DefaultVertexFormat.POSITION_TEX), e -> {
            energyCrystalShader = (CCShaderInstance) e;
            energyCrystalTime = energyCrystalShader.getUniform("Time");
            energyCrystalColour = energyCrystalShader.getUniform("Colour");
            energyCrystalMipmap = energyCrystalShader.getUniform("Mipmap");
            energyCrystalAngle = energyCrystalShader.getUniform("Angle");
            energyCrystalShader.onApply(() -> energyCrystalTime.glUniform1f((TimeKeeper.getClientTick() + Minecraft.getInstance().getFrameTime()) / 50F));
        });

        try {
            event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(DraconicEvolution.MODID, "test"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), e -> {
                testShader = (CCShaderInstance) e;
                testTime = testShader.getUniform("Time");
                testColour = testShader.getUniform("TestColour");
                testInB = testShader.getUniform("TestInB");
                testInC = testShader.getUniform("TestInC");
                testInD = testShader.getUniform("TestInD");
                testShader.onApply(() -> {
                    try {
                        testTime.glUniform1f((TimeKeeper.getClientTick() + Minecraft.getInstance().getFrameTime()) / 20F);
                    } catch (Throwable e2) {
                        e2.printStackTrace();
                    }
                });
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(DraconicEvolution.MODID, "block"), DefaultVertexFormat.BLOCK), e -> {
            customBlockShader = (CCShaderInstance) e;
            modelMat = customBlockShader.getUniform("ModelMat");
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(DraconicEvolution.MODID, "energy_core"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), e -> {
            energyCoreShader = (CCShaderInstance) e;
            energyCoreTime = energyCoreShader.getUniform("Time");
            energyCoreActivation = energyCoreShader.getUniform("Activation");
            energyCoreEffectColour = energyCoreShader.getUniform("EffectColour");
            energyCoreFrameColour = energyCoreShader.getUniform("FrameColour");
            energyCoreRotTriColour = energyCoreShader.getUniform("InnerTriColour");
            energyCoreShader.onApply(() -> {
                energyCoreTime.glUniform1f((TimeKeeper.getClientTick() + Minecraft.getInstance().getFrameTime()) / 20F);
            });
        });
    }


}
