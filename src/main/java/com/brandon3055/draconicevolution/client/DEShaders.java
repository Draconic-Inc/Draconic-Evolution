package com.brandon3055.draconicevolution.client;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.shader.CCShaderInstance;
import codechicken.lib.render.shader.CCUniform;
import codechicken.lib.util.ClientUtils;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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
    public static CCUniform chaosEntityDisableLight;
    public static CCUniform chaosEntityDisableOverlay;

    public static CCShaderInstance toolBaseShader;
    public static CCUniform toolBaseModelMat;
    public static CCUniform toolBaseUV1Override;
    public static CCUniform toolBaseUV2Override;

    public static CCShaderInstance toolGemShader;
    public static CCUniform toolGemModelMat;
    public static CCUniform toolGemTime;
    public static CCUniform toolGemBaseColor;

    public static CCShaderInstance toolTraceShader;
    public static CCUniform toolTraceModelMat;
    public static CCUniform toolTraceTime;
    public static CCUniform toolTraceBaseColor;

    public static CCShaderInstance toolBladeShader;
    public static CCUniform toolBladeModelMat;
    public static CCUniform toolBladeTime;
    public static CCUniform toolBladeBaseColor;

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
            chaosEntityDisableLight = chaosEntityShader.getUniform("SimpleLight");
            chaosEntityDisableOverlay = chaosEntityShader.getUniform("DisableOverlay");
            chaosEntityShader.onApply(() -> {
                Player player = Minecraft.getInstance().player;
                chaosEntityTime.glUniform1f((float) ClientUtils.getRenderTime());
                chaosEntityYaw.glUniform1f((float) (player.getYRot() * MathHelper.torad));
                chaosEntityPitch.glUniform1f((float) -(player.getXRot() * MathHelper.torad));
            });
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "tools/tool_base"), DefaultVertexFormat.NEW_ENTITY), e -> {
            toolBaseShader = (CCShaderInstance) e;
            toolBaseModelMat = toolBaseShader.getUniform("ModelMat");
            toolBaseUV1Override = toolBaseShader.getUniform("UV1Override");
            toolBaseUV2Override = toolBaseShader.getUniform("UV2Override");
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "tools/tool_gem"), DefaultVertexFormat.NEW_ENTITY), e -> {
            toolGemShader = (CCShaderInstance) e;
            toolGemModelMat = toolGemShader.getUniform("ModelMat");
            toolGemTime = toolGemShader.getUniform("Time");
            toolGemBaseColor = toolGemShader.getUniform("BaseColor");
            toolGemShader.onApply(() -> {
                toolGemTime.glUniform1f((float) (ClientUtils.getRenderTime() / 20));
            });
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "tools/tool_trace"), DefaultVertexFormat.NEW_ENTITY), e -> {
            toolTraceShader = (CCShaderInstance) e;
            toolTraceModelMat = toolTraceShader.getUniform("ModelMat");
            toolTraceTime = toolTraceShader.getUniform("Time");
            toolTraceBaseColor = toolTraceShader.getUniform("BaseColor");
            toolTraceShader.onApply(() -> {
                toolTraceTime.glUniform1f((float) (ClientUtils.getRenderTime() / 20));
            });
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "tools/tool_blade"), DefaultVertexFormat.NEW_ENTITY), e -> {
            toolBladeShader = (CCShaderInstance) e;
            toolBladeModelMat = toolBladeShader.getUniform("ModelMat");
            toolBladeTime = toolBladeShader.getUniform("Time");
            toolBladeBaseColor = toolBladeShader.getUniform("BaseColor");
            toolBladeShader.onApply(() -> {
                toolBladeTime.glUniform1f((float) (ClientUtils.getRenderTime() / 20));
            });
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
