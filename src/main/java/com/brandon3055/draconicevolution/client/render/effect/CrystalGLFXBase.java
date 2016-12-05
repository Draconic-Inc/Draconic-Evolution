package com.brandon3055.draconicevolution.client.render.effect;

import codechicken.lib.render.state.GlStateManagerHelper;
import com.brandon3055.brandonscore.client.particle.BCParticle;
import com.brandon3055.brandonscore.client.particle.IGLFXHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 29/11/2016.
 */
public abstract class CrystalGLFXBase<T extends TileCrystalBase> extends BCParticle {

    protected final T tile;
    protected int ticksTillDeath = 0;
    protected float fxState;

    public CrystalGLFXBase(World worldIn, T tile) {
        super(worldIn, Vec3D.getCenter(tile.getPos()));
        this.tile = tile;
        this.ticksTillDeath = 4;
        this.texturesPerRow = 8;
        this.particleScale = 1.5F;
    }

    @Override
    public boolean isRawGLParticle() {
        return true;
    }

    public void updateFX(float fxState) {
        this.fxState = fxState;
        ticksTillDeath = 4;
    }

    @Override
    public abstract void onUpdate();

    @Override
    public abstract void renderParticle(VertexBuffer vertexbuffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ);

    public static final IGLFXHandler CRYSTAL_FX_HANDLER = new IGLFXHandler() {
        @Override
        public void preDraw(int layer, VertexBuffer vertexbuffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManagerHelper.pushState();
            GlStateManager.depthMask(false);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
            vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        }

        @Override
        public void postDraw(int layer, VertexBuffer vertexbuffer, Tessellator tessellator) {
            tessellator.getBuffer().sortVertexData(0, 0, 0);
            tessellator.draw();
            GlStateManagerHelper.popState();
        }
    };
}
