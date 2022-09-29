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

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

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

    public static CCShaderInstance chaosBlockShader;
    public static CCUniform chaosBlockTime;
    public static CCUniform chaosBlockYaw;
    public static CCUniform chaosBlockPitch;
    public static CCUniform chaosBlockAlpha;

    public static CCShaderInstance chaosEntityShader;
    public static CCUniform chaosEntityModelMat;
    public static CCUniform chaosEntityTime;
    public static CCUniform chaosEntityYaw;
    public static CCUniform chaosEntityPitch;
    public static CCUniform chaosEntityAlpha;
    public static CCUniform chaosEntitySimpleLight;

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
        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "reactor"), DefaultVertexFormat.POSITION_TEX), e -> {
            reactorShader = (CCShaderInstance) e;
            reactorTime = reactorShader.getUniform("time");
            reactorIntensity = reactorShader.getUniform("intensity");
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "reactor_shield"), DefaultVertexFormat.POSITION_TEX), e -> {
            reactorShieldShader = (CCShaderInstance) e;
            reactorShieldTime = reactorShieldShader.getUniform("time");
            reactorShieldIntensity = reactorShieldShader.getUniform("intensity");
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "chaos_block"), DefaultVertexFormat.BLOCK), e -> {
            chaosBlockShader = (CCShaderInstance) e;
            chaosBlockTime = chaosBlockShader.getUniform("Time");
            chaosBlockYaw = chaosBlockShader.getUniform("Yaw");
            chaosBlockPitch = chaosBlockShader.getUniform("Pitch");
            chaosBlockAlpha = chaosBlockShader.getUniform("Alpha");
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "chaos_entity"), DefaultVertexFormat.NEW_ENTITY), e -> {
            chaosEntityShader = (CCShaderInstance) e;
            chaosEntityModelMat = chaosEntityShader.getUniform("ModelMat");
            chaosEntityTime = chaosEntityShader.getUniform("Time");
            chaosEntityYaw = chaosEntityShader.getUniform("Yaw");
            chaosEntityPitch = chaosEntityShader.getUniform("Pitch");
            chaosEntityAlpha = chaosEntityShader.getUniform("Alpha");
            chaosEntitySimpleLight = chaosEntityShader.getUniform("SimpleLight");
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "armor_shield"), DefaultVertexFormat.POSITION_TEX), e -> {
            armorShieldShader = (CCShaderInstance) e;
            armorShieldTime = armorShieldShader.getUniform("Time");
            armorShieldActivation = armorShieldShader.getUniform("Activation");
            armorShieldColour = armorShieldShader.getUniform("BaseColour");
            armorShieldShader.onApply(() -> armorShieldTime.glUniform1f((TimeKeeper.getClientTick() + Minecraft.getInstance().getFrameTime()) / 20F));
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "energy_crystal"), DefaultVertexFormat.POSITION_TEX), e -> {
            energyCrystalShader = (CCShaderInstance) e;
            energyCrystalTime = energyCrystalShader.getUniform("Time");
            energyCrystalColour = energyCrystalShader.getUniform("Colour");
            energyCrystalMipmap = energyCrystalShader.getUniform("Mipmap");
            energyCrystalAngle = energyCrystalShader.getUniform("Angle");
            energyCrystalShader.onApply(() -> energyCrystalTime.glUniform1f((TimeKeeper.getClientTick() + Minecraft.getInstance().getFrameTime()) / 50F));
        });

        try {
            event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "test"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), e -> {
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

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "block"), DefaultVertexFormat.BLOCK), e -> {
            customBlockShader = (CCShaderInstance) e;
            modelMat = customBlockShader.getUniform("ModelMat");
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "energy_core"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), e -> {
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
