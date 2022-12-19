package com.brandon3055.draconicevolution.client;

import codechicken.lib.render.shader.CCShaderInstance;
import codechicken.lib.render.shader.CCUniform;
import codechicken.lib.util.ClientUtils;
import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.draconicevolution.client.shader.ShieldShader;
import com.brandon3055.draconicevolution.client.shader.ToolShader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
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

    public static final ToolShader TOOL_BASE_SHADER = new ToolShader("tools/tool_base", DefaultVertexFormat.NEW_ENTITY);
    public static final ToolShader TOOL_GEM_SHADER = new ToolShader("tools/tool_gem", DefaultVertexFormat.NEW_ENTITY)
            .onShaderApplied(e -> e.getTimeUniform().glUniform1f((float) (ClientUtils.getRenderTime() / 20)));
    public static final ToolShader TOOL_TRACE_SHADER = new ToolShader("tools/tool_trace", DefaultVertexFormat.NEW_ENTITY)
            .onShaderApplied(e -> e.getTimeUniform().glUniform1f((float) (ClientUtils.getRenderTime() / 20)));
    public static final ToolShader TOOL_BLADE_SHADER = new ToolShader("tools/tool_blade", DefaultVertexFormat.NEW_ENTITY)
            .onShaderApplied(e -> e.getTimeUniform().glUniform1f((float) (ClientUtils.getRenderTime() / 20)));
    public static final ToolShader BOW_STRING_SHADER = new ToolShader("tools/bow_string", DefaultVertexFormat.NEW_ENTITY)
            .onShaderApplied(e -> e.getTimeUniform().glUniform1f((float) (ClientUtils.getRenderTime() / 20)));
    public static final ToolShader CHESTPIECE_GEM_SHADER = new ToolShader("tools/chestpiece_gem", DefaultVertexFormat.NEW_ENTITY)
            .onShaderApplied(e -> e.getTimeUniform().glUniform1f((float) (ClientUtils.getRenderTime() / 20)));
    public static final ShieldShader CHESTPIECE_SHIELD_SHADER = new ShieldShader("tools/chestpiece_shield", DefaultVertexFormat.NEW_ENTITY)
            .onShaderApplied(e -> e.getTimeUniform().glUniform1f((float) (ClientUtils.getRenderTime() / 20)));

    public static CCShaderInstance shieldShader;
    public static CCUniform shieldTime;
    public static CCUniform shieldActivation;
    public static CCUniform shieldColour;
    public static CCUniform shieldBarMode;

    public static CCShaderInstance energyCrystalShader;
    public static CCUniform energyCrystalTime;
    public static CCUniform energyCrystalColour;
    public static CCUniform energyCrystalMipmap;
    public static CCUniform energyCrystalAngle;

//    public static CCShaderInstance testShader;
//    public static CCUniform testTime;
//    public static CCUniform testColour;
//    public static CCUniform testInB;
//    public static CCUniform testInC;
//    public static CCUniform testInD;

    public static CCShaderInstance energyCoreShader;
    public static CCUniform energyCoreTime;
    public static CCUniform energyCoreActivation;
    public static CCUniform energyCoreEffectColour;
    public static CCUniform energyCoreFrameColour;
    public static CCUniform energyCoreRotTriColour;

    public static CCShaderInstance reactorBeamShader;
    public static CCUniform reactorBeamTime;
    public static CCUniform reactorBeamFade;
    public static CCUniform reactorBeamPower;
    public static CCUniform reactorBeamStartup;
    public static CCUniform reactorBeamType;

    public static CCShaderInstance explosionFlashShader;
    public static CCUniform explosionFlashScreenPos;
    public static CCUniform explosionFlashScreenSize;
    public static CCUniform explosionFlashIntensity;

    public static CCShaderInstance explosionShader;
    public static CCUniform explosionTime;
    public static CCUniform explosionScale;
    public static CCUniform explosionAlpha;
    public static CCUniform explosionType;


    public static void init() {
        LOCK.lock();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        TOOL_BASE_SHADER.register(bus);
        TOOL_GEM_SHADER.register(bus);
        TOOL_TRACE_SHADER.register(bus);
        TOOL_BLADE_SHADER.register(bus);
        BOW_STRING_SHADER.register(bus);
        CHESTPIECE_GEM_SHADER.register(bus);
        CHESTPIECE_SHIELD_SHADER.register(bus);
        bus.addListener(DEShaders::onRegisterShaders);
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

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "shield"), DefaultVertexFormat.POSITION_TEX), e -> {
            shieldShader = (CCShaderInstance) e;
            shieldTime = shieldShader.getUniform("Time");
            shieldActivation = shieldShader.getUniform("Activation");
            shieldColour = shieldShader.getUniform("BaseColour");
            shieldBarMode = shieldShader.getUniform("BarMode");
            shieldShader.onApply(() -> shieldTime.glUniform1f((TimeKeeper.getClientTick() + Minecraft.getInstance().getFrameTime()) / 20F));
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "energy_crystal"), DefaultVertexFormat.POSITION_TEX), e -> {
            energyCrystalShader = (CCShaderInstance) e;
            energyCrystalTime = energyCrystalShader.getUniform("Time");
            energyCrystalColour = energyCrystalShader.getUniform("Colour");
            energyCrystalMipmap = energyCrystalShader.getUniform("Mipmap");
            energyCrystalAngle = energyCrystalShader.getUniform("Angle");
            energyCrystalShader.onApply(() -> energyCrystalTime.glUniform1f((TimeKeeper.getClientTick() + Minecraft.getInstance().getFrameTime()) / 50F));
        });

//        try {
//            event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "test"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), e -> {
//                testShader = (CCShaderInstance) e;
//                testTime = testShader.getUniform("Time");
//                testColour = testShader.getUniform("TestColour");
//                testInB = testShader.getUniform("TestInB");
//                testInC = testShader.getUniform("TestInC");
//                testInD = testShader.getUniform("TestInD");
//                testShader.onApply(() -> {
//                    try {
//                        testTime.glUniform1f((TimeKeeper.getClientTick() + Minecraft.getInstance().getFrameTime()) / 20F);
//                    } catch (Throwable e2) {
//                        e2.printStackTrace();
//                    }
//                });
//            });
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }

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

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "reactor_beam"), DefaultVertexFormat.POSITION_COLOR_TEX), e -> {
            reactorBeamShader = (CCShaderInstance) e;
            reactorBeamTime = reactorBeamShader.getUniform("Time");
            reactorBeamFade = reactorBeamShader.getUniform("Fade");
            reactorBeamPower = reactorBeamShader.getUniform("Power");
            reactorBeamStartup = reactorBeamShader.getUniform("Startup");
            reactorBeamType = reactorBeamShader.getUniform("Type");
            reactorBeamShader.onApply(() -> reactorBeamTime.glUniform1f((TimeKeeper.getClientTick() + Minecraft.getInstance().getFrameTime()) * 0.02F));
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "explosion_flash"), DefaultVertexFormat.POSITION_COLOR), e -> {
            explosionFlashShader = (CCShaderInstance) e;
            explosionFlashScreenPos = explosionFlashShader.getUniform("ScreenPos");
            explosionFlashScreenSize = explosionFlashShader.getUniform("ScreenSize");
            explosionFlashIntensity = explosionFlashShader.getUniform("Intensity");
        });

        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation(MODID, "explosion"), DefaultVertexFormat.POSITION_TEX), e -> {
            explosionShader = (CCShaderInstance) e;
            explosionTime = explosionShader.getUniform("Time");
            explosionScale = explosionShader.getUniform("Scale");
            explosionAlpha = explosionShader.getUniform("Alpha");
            explosionType = explosionShader.getUniform("Type");
        });
    }

}
