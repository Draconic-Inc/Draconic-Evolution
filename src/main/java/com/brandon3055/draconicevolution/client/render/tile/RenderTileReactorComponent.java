package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorInjector;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorStabilizer;
import com.brandon3055.draconicevolution.client.model.ModelReactorEnergyInjector;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerCore;
import com.brandon3055.draconicevolution.client.model.ModelReactorStabilizerRing;
import com.brandon3055.draconicevolution.init.DEContent;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Quaternion;
import net.covers1624.quack.collection.StreamableIterable;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Map;

import static codechicken.lib.math.MathHelper.torad;
import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 20/01/2017.
 */
public class RenderTileReactorComponent implements BlockEntityRenderer<TileReactorComponent> {

    private static final RenderType STAB_FRAME_TYPE = RenderType.entitySolid(new ResourceLocation(MODID, "textures/block/reactor/reactor_stabilizer.png"));
    private static final RenderType INJECTOR_FRAME_TYPE = RenderType.entitySolid(new ResourceLocation(MODID, "textures/block/reactor/reactor_injector.png"));

    private static final RenderType STAB_GLOW_TYPE = RenderType.create(MODID + ":stab_glow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/block/reactor/reactor_stabilizer.png"), false, false))
            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getNewEntityShader))
            .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
            .createCompositeState(false)
    );

    private static final RenderType INJECTOR_GLOW_TYPE = RenderType.create(MODID + ":injector_glow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(MODID, "textures/block/reactor/reactor_injector.png"), false, false))
            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getNewEntityShader))
            .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
            .createCompositeState(false)
    );

    private static CCModel modelInjectorBase;
    private static CCModel modelInjectorEmitters;

    private static CCModel modelStabFrame;
    private static CCModel modelStabInnerRotor;
    private static CCModel modelStabInnerRotorArm;
    private static CCModel modelStabOuterRotor;
    private static CCModel modelStabOuterRotorArm;
    private static CCModel modelStabRing;
    private static CCModel modelStabRingEmitter;

    private static CCModel modelInnerRotorPart;
    private static CCModel modelOuterRotorPart;

    static {
        Map<String, CCModel> map = new OBJParser(new ResourceLocation(MODID, "models/block/reactor/reactor_injector.obj")).quads().ignoreMtl().parse();
        modelInjectorBase = CCModel.combine(StreamableIterable.of(map.entrySet())
                .filter(e -> !e.getKey().startsWith("emitter"))
                .map(Map.Entry::getValue)
                .toLinkedList()).backfacedCopy();
        modelInjectorEmitters = CCModel.combine(StreamableIterable.of(map.entrySet())
                .filter(e -> e.getKey().startsWith("emitter"))
                .map(Map.Entry::getValue)
                .toLinkedList()).backfacedCopy();

        Map<String, CCModel> stabMap = new OBJParser(new ResourceLocation(MODID, "models/block/reactor/reactor_stabilizer.obj")).quads().ignoreMtl().parse();
        modelStabFrame = CCModel.combine(StreamableIterable.of(stabMap.entrySet())
                .filter(e -> e.getKey().startsWith("frame"))
                .map(Map.Entry::getValue)
                .toLinkedList()).backfacedCopy();

        modelStabInnerRotor = CCModel.combine(StreamableIterable.of(stabMap.entrySet())
                .filter(e -> e.getKey().startsWith("inner_rotor_blade"))
                .map(Map.Entry::getValue)
                .toLinkedList()).backfacedCopy();

        modelStabInnerRotorArm = CCModel.combine(StreamableIterable.of(stabMap.entrySet())
                .filter(e -> e.getKey().startsWith("inner_rotor_arm"))
                .map(Map.Entry::getValue)
                .toLinkedList()).backfacedCopy();

        modelStabOuterRotor = CCModel.combine(StreamableIterable.of(stabMap.entrySet())
                .filter(e -> e.getKey().startsWith("outer_rotor_blade"))
                .map(Map.Entry::getValue)
                .toLinkedList()).backfacedCopy();

        modelStabOuterRotorArm = CCModel.combine(StreamableIterable.of(stabMap.entrySet())
                .filter(e -> e.getKey().startsWith("outer_rotor_arm"))
                .map(Map.Entry::getValue)
                .toLinkedList()).backfacedCopy();

        modelStabRing = CCModel.combine(StreamableIterable.of(stabMap.entrySet())
                .filter(e -> e.getKey().startsWith("ring"))
                .map(Map.Entry::getValue)
                .toLinkedList()).backfacedCopy();

        modelStabRingEmitter = CCModel.combine(StreamableIterable.of(stabMap.entrySet())
                .filter(e -> e.getKey().startsWith("focus_panel"))
                .map(Map.Entry::getValue)
                .toLinkedList()).backfacedCopy();

        modelInnerRotorPart = CCModel.combine(StreamableIterable.of(stabMap.entrySet())
                .filter(e -> e.getKey().startsWith("inner_rotor_blade_a"))
                .map(Map.Entry::getValue)
                .toLinkedList()).backfacedCopy();

        modelOuterRotorPart = CCModel.combine(StreamableIterable.of(stabMap.entrySet())
                .filter(e -> e.getKey().startsWith("outer_rotor_blade_a"))
                .map(Map.Entry::getValue)
                .toLinkedList()).backfacedCopy();
    }

    public RenderTileReactorComponent(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileReactorComponent te, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        Matrix4 mat = new Matrix4(mStack);
        mat.translate(0.5, 0, 0.5);

        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;

        switch (te.facing.get()) {
            case SOUTH -> mat.rotate(180 * torad, Vector3.Y_POS);
            case EAST -> mat.rotate(-90 * torad, Vector3.Y_POS);
            case WEST -> mat.rotate(90 * torad, Vector3.Y_POS);
            case UP -> mat.apply(new Rotation(90 * torad, Vector3.X_POS).at(new Vector3(0, 0.5, 0)));
            case DOWN -> mat.apply(new Rotation(-90 * torad, Vector3.X_POS).at(new Vector3(0, 0.5, 0)));
        }

        if (te instanceof TileReactorStabilizer) {
            float coreRotation = te.animRotation + (partialTicks * te.animRotationSpeed);//Remember Partial Ticks here
            renderStabilizer(ccrs, mat, getter, coreRotation, te.animRotationSpeed / 15F, packedLight, packedOverlay);
        } else if (te instanceof TileReactorInjector) {
            renderInjector(ccrs, mat, getter, te.animRotationSpeed / 15F, packedLight, packedOverlay);
        }

        ccrs.reset();
    }

    public static void renderStabilizer(CCRenderState ccrs, Matrix4 mat, MultiBufferSource getter, float coreRotation, float brightness, int packedLight, int packedOverlay) {
        float ringRotation = coreRotation * -0.5F;//Remember Partial Ticks here

        Matrix4 innerRotorMat = mat.copy();
        innerRotorMat.apply(new Rotation(coreRotation * torad, Vector3.Z_POS).at(new Vector3(0, 0.5, 0)));
        Matrix4 outerRotorMat = mat.copy();
        outerRotorMat.apply(new Rotation(coreRotation * torad * -2, Vector3.Z_POS).at(new Vector3(0, 0.5, 0)));

        ccrs.bind(STAB_FRAME_TYPE, getter);
        modelStabFrame.render(ccrs, mat);
        modelStabInnerRotorArm.render(ccrs, innerRotorMat);
        modelStabOuterRotorArm.render(ccrs, outerRotorMat);

        for (int i = 0; i < 4; i++) {
            ccrs.brightness = packedLight;
            Matrix4 ringMat = mat.copy();
            ringMat.apply(new Rotation(((90 * i) + ringRotation) * torad, Vector3.Z_POS).at(new Vector3(0, 0.5, 0)));
            modelStabRing.render(ccrs, ringMat);

            ccrs.brightness = (int) (brightness * 240);
            Matrix4 emitterMat = ringMat.copy();
            emitterMat.apply(new Rotation(45 * torad, Vector3.X_NEG).at(new Vector3(0, 15 / 16F, -((8 + 1.5) / 16F))));
            modelStabRingEmitter.render(ccrs, emitterMat);
        }

        ccrs.brightness = (int) (brightness * 240);
        modelStabInnerRotor.render(ccrs, innerRotorMat);
        modelStabOuterRotor.render(ccrs, outerRotorMat);

        if (brightness >= 1) {
            ccrs.bind(STAB_GLOW_TYPE, getter);
            modelStabInnerRotor.render(ccrs, innerRotorMat);
            modelStabOuterRotor.render(ccrs, outerRotorMat);

            for (int i = 0; i < 4; i++) {
                Matrix4 emitterMat = mat.copy();
                emitterMat.apply(new Rotation(((90 * i) + ringRotation) * torad, Vector3.Z_POS).at(new Vector3(0, 0.5, 0)));
                emitterMat.apply(new Rotation(45 * torad, Vector3.X_NEG).at(new Vector3(0, 15 / 16F, -((8 + 1.5) / 16F))));
                modelStabRingEmitter.render(ccrs, emitterMat);
            }
        }
    }

    public static void renderInjector(CCRenderState ccrs, Matrix4 mat, MultiBufferSource getter, float brightness, int packedLight, int packedOverlay) {
        ccrs.bind(INJECTOR_FRAME_TYPE, getter);
        modelInjectorBase.render(ccrs, mat);

        ccrs.brightness = (int) (brightness * 240);
        modelInjectorEmitters.render(ccrs, mat);

        if (brightness >= 1) {
            ccrs.bind(INJECTOR_GLOW_TYPE, getter);
            modelInjectorEmitters.render(ccrs, mat);
        }
    }

    public static void renderComponent(Item item, CCRenderState ccrs, Matrix4 mat, MultiBufferSource getter, int packedLight, int packedOverlay) {
        if (item == DEContent.reactor_prt_stab_frame) {
            ccrs.bind(STAB_FRAME_TYPE, getter);
            mat.translate(0.5, 0, 0.5);
            modelStabFrame.render(ccrs, mat);

        } else if (item == DEContent.reactor_prt_in_rotor) {
            ccrs.bind(STAB_FRAME_TYPE, getter);
            mat.translate(0.3, 0, 0.5);
            mat.scale(1.5F, 1.5F, 1.5F);
            modelInnerRotorPart.render(ccrs, mat);

        } else if (item == DEContent.reactor_prt_out_rotor) {
            ccrs.bind(STAB_FRAME_TYPE, getter);
            mat.translate(0.3, 0, 0.5);
            mat.scale(1.5F, 1.5F, 1.5F);
            modelOuterRotorPart.render(ccrs, mat);

        } else if (item == DEContent.reactor_prt_rotor_full) {
            ccrs.bind(STAB_FRAME_TYPE, getter);
            mat.translate(0.5, -0.2, 0.5);
            mat.scale(1.5F, 1.5F, 1.5F);

            modelStabInnerRotor.render(ccrs, mat);
            modelStabInnerRotorArm.render(ccrs, mat);
            mat.apply(new Rotation(60 * torad, Vector3.Z_NEG).at(new Vector3(0, 0.5, 0)));
            modelStabOuterRotor.render(ccrs, mat);
            modelStabOuterRotorArm.render(ccrs, mat);

        } else if (item == DEContent.reactor_prt_focus_ring) {
            ccrs.bind(STAB_FRAME_TYPE, getter);
            mat.translate(0.5, -0.2, 1.25);
            mat.scale(1.5F, 1.5F, 1.5F);

            for (int i = 0; i < 4; i++) {
                Matrix4 ringMat = mat.copy();
                ringMat.apply(new Rotation((90 * i) * torad, Vector3.Z_POS).at(new Vector3(0, 0.5, 0)));
                modelStabRing.render(ccrs, ringMat);

                Matrix4 emitterMat = ringMat.copy();
                emitterMat.apply(new Rotation(45 * torad, Vector3.X_NEG).at(new Vector3(0, 15 / 16F, -((8 + 1.5) / 16F))));
                modelStabRingEmitter.render(ccrs, emitterMat);
            }
        }
    }
}
