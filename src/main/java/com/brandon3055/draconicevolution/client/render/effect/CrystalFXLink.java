package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalWirelessIO;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public class   CrystalFXLink extends CrystalFXBase<TileCrystalBase> {

    private final Vec3D linkTarget;
    private final boolean terminateSource;
    private final boolean terminateTarget;
    public int timeout = 0;

    public CrystalFXLink(ClientLevel worldIn, TileCrystalBase tile, Vec3D linkTarget) {
        super(worldIn, tile);
        this.age = worldIn.random.nextInt(1024);
        this.setPosition(tile.getBeamLinkPos(linkTarget.getPos()));
        this.terminateSource = true;
        this.linkTarget = linkTarget;
        Direction face = tile instanceof TileCrystalWirelessIO ? ((TileCrystalWirelessIO) tile).getReceiversFaces().get(linkTarget.getPos()) : null;
        if (face != null) {
            linkTarget.add(face.getStepX() * 0.6, face.getStepY() * 0.6, face.getStepZ() * 0.6);
        }
        this.terminateTarget = true;
        setBoundingBox(new AABB(x, y, z, this.linkTarget.x, this.linkTarget.y, this.linkTarget.z));
    }

    @Override
    public void tick() {
        super.tick();
        if (!ClientEventHandler.playerHoldingWrench && timeout <= 0) {
            remove();
        } else if (timeout > 0) {
            timeout--;
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        float scale = 0.1F + (timeout * 0.005F);
        Vec3 viewVec = renderInfo.getPosition();
        Vector3 source = new Vector3(x - viewVec.x, y - viewVec.y, z - viewVec.z);
        Vector3 target = linkTarget.toVector3().subtract(viewVec.x, viewVec.y, viewVec.z);
        Vector3 dirVec = source.copy().subtract(target).normalize();
        Vector3 planeA = dirVec.copy().perpendicular().normalize();
        Vector3 planeB = dirVec.copy().crossProduct(planeA);
        Vector3 planeC = planeB.copy().rotate(45 * MathHelper.torad, dirVec).normalize();
        Vector3 planeD = planeB.copy().rotate(-45 * MathHelper.torad, dirVec).normalize();
        planeA.multiply(scale);
        planeB.multiply(scale);
        planeC.multiply(scale);
        planeD.multiply(scale);
        float dist = 0.2F * (float) Utils.getDistance(new Vec3D(source), new Vec3D(target));
        float anim = (ClientEventHandler.elapsedTicks + partialTicks) / -15F;

        Vector3 p1 = source.copy().add(planeA);
        Vector3 p2 = target.copy().add(planeA);
        Vector3 p3 = source.copy().subtract(planeA);
        Vector3 p4 = target.copy().subtract(planeA);
        bufferQuad(buffer, p1, p2, p3, p4, anim, dist);

        p1 = source.copy().add(planeB);
        p2 = target.copy().add(planeB);
        p3 = source.copy().subtract(planeB);
        p4 = target.copy().subtract(planeB);
        bufferQuad(buffer, p1, p2, p3, p4, anim, dist);

        p1 = source.copy().add(planeC);
        p2 = target.copy().add(planeC);
        p3 = source.copy().subtract(planeC);
        p4 = target.copy().subtract(planeC);
        bufferQuad(buffer, p1, p2, p3, p4, anim, dist);

        p1 = source.copy().add(planeD);
        p2 = target.copy().add(planeD);
        p3 = source.copy().subtract(planeD);
        p4 = target.copy().subtract(planeD);
        bufferQuad(buffer, p1, p2, p3, p4, anim, dist);

        scale *= 2;
        float minU = 0.0F;
        float maxU = 0.53F;
        float minV = 0.0F;
        float maxV = 0.53F;


        if (terminateSource) {
            float viewX = (float) (this.x - viewVec.x());
            float viewY = (float) (this.y - viewVec.y());
            float viewZ = (float) (this.z - viewVec.z());
            Vector3f[] renderVector = getRenderVectors(renderInfo, viewX, viewY, viewZ, scale);
            buffer.addVertex(renderVector[0].x(), renderVector[0].y(), renderVector[0].z()).setColor(1F, 0F, 0F, 1F).setUv(maxU, maxV).setUv2(240, 240);
            buffer.addVertex(renderVector[1].x(), renderVector[1].y(), renderVector[1].z()).setColor(1F, 0F, 0F, 1F).setUv(maxU, minV).setUv2(240, 240);
            buffer.addVertex(renderVector[2].x(), renderVector[2].y(), renderVector[2].z()).setColor(1F, 0F, 0F, 1F).setUv(minU, minV).setUv2(240, 240);
            buffer.addVertex(renderVector[3].x(), renderVector[3].y(), renderVector[3].z()).setColor(1F, 0F, 0F, 1F).setUv(minU, maxV).setUv2(240, 240);
        }

        if (terminateTarget) {
            float viewX = (float) (this.linkTarget.x - viewVec.x());
            float viewY = (float) (this.linkTarget.y - viewVec.y());
            float viewZ = (float) (this.linkTarget.z - viewVec.z());
            Vector3f[] renderVector = getRenderVectors(renderInfo, viewX, viewY, viewZ, scale);
            buffer.addVertex(renderVector[0].x(), renderVector[0].y(), renderVector[0].z()).setColor(1F, 0F, 0F, 1F).setUv(maxU, maxV).setUv2(240, 240);
            buffer.addVertex(renderVector[1].x(), renderVector[1].y(), renderVector[1].z()).setColor(1F, 0F, 0F, 1F).setUv(maxU, minV).setUv2(240, 240);
            buffer.addVertex(renderVector[2].x(), renderVector[2].y(), renderVector[2].z()).setColor(1F, 0F, 0F, 1F).setUv(minU, minV).setUv2(240, 240);
            buffer.addVertex(renderVector[3].x(), renderVector[3].y(), renderVector[3].z()).setColor(1F, 0F, 0F, 1F).setUv(minU, maxV).setUv2(240, 240);
        }

    }

    private void bufferQuad(VertexConsumer buffer, Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, float anim, float dist) {
        buffer.addVertex((float) p1.x, (float) p1.y, (float) p1.z).setColor(1F, 0F, 0F, 1F).setUv(0.5F, anim).setUv2(240, 240);
        buffer.addVertex((float) p2.x, (float) p2.y, (float) p2.z).setColor(1F, 0F, 0F, 1F).setUv(0.5F, dist + anim).setUv2(240, 240);
        buffer.addVertex((float) p4.x, (float) p4.y, (float) p4.z).setColor(1F, 0F, 0F, 1F).setUv(1.0F, dist + anim).setUv2(240, 240);
        buffer.addVertex((float) p3.x, (float) p3.y, (float) p3.z).setColor(1F, 0F, 0F, 1F).setUv(1.0F, anim).setUv2(240, 240);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return HANDLER;
    }

    private static final ParticleRenderType HANDLER = new FXHandler();

    public static class FXHandler implements ParticleRenderType {
        private static final ResourceLocation highlightTexture = ResourceLocation.fromNamespaceAndPath(DraconicEvolution.MODID, "textures/particle/energy_beam_highlight.png");

        public FXHandler() {}

        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            //TODO, Is this blend func going to cause issues?
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.setShaderTexture(0, highlightTexture);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        }
    }
}