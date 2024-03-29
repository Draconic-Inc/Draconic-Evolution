package com.brandon3055.draconicevolution.client.render.modelfx;

import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.api.TechLevel;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.Random;

/**
 * Created by brandon3055 on 28/2/21
 */
public abstract class ModelEffect {
    public Vector3 pos = new Vector3();
    public double scale = 1;
    public float red = 1F;
    public float green = 1F;
    public float blue = 1F;
    public float alpha = 1F;

    protected static Random rand = new Random();
    private static float[] r = new float[256];
    private static float[] randSet = new float[4096];
    private static int randPos = 0;

    static {
        rand.setSeed(123); //Just to ensure rendering consistency
        for (int i = 0; i < r.length; i++) {
            r[i] = rand.nextFloat();
        }
        for (int i = 0; i < randSet.length; i++) {
            randSet[i] = rand.nextFloat();
        }
    }

    public ModelEffect() {
    }

    public abstract RenderType getRenderType();

    protected abstract void doRender(VertexConsumer builder, float partialTicks, TechLevel techLevel);

    public void renderEffect(Matrix4 mat, MultiBufferSource getter, float partialTicks, TechLevel techLevel) {
        VertexConsumer builder = new TransformingVertexConsumer(getter.getBuffer(getRenderType()), mat);
        doRender(builder, partialTicks, techLevel);
    }

    protected void drawParticle(VertexConsumer builder, double x, double y, double z, double scale, float red, float green, float blue, float alpha) {
        double min = (1 - scale) * 0.5;
        double max = 0.5 + (scale * 0.5);
        builder.vertex(x + min, y + 0.5, z + min).color(red, green, blue, alpha).uv(0, 0)/*.lightmap(240)*/.endVertex();
        builder.vertex(x + min, y + 0.5, z + max).color(red, green, blue, alpha).uv(0, 1)/*.lightmap(240)*/.endVertex();
        builder.vertex(x + max, y + 0.5, z + max).color(red, green, blue, alpha).uv(1, 1)/*.lightmap(240)*/.endVertex();
        builder.vertex(x + max, y + 0.5, z + min).color(red, green, blue, alpha).uv(1, 0)/*.lightmap(240)*/.endVertex();

        builder.vertex(x + min, y + min, z + 0.5).color(red, green, blue, alpha).uv(0, 0)/*.lightmap(240)*/.endVertex();
        builder.vertex(x + min, y + max, z + 0.5).color(red, green, blue, alpha).uv(0, 1)/*.lightmap(240)*/.endVertex();
        builder.vertex(x + max, y + max, z + 0.5).color(red, green, blue, alpha).uv(1, 1)/*.lightmap(240)*/.endVertex();
        builder.vertex(x + max, y + min, z + 0.5).color(red, green, blue, alpha).uv(1, 0)/*.lightmap(240)*/.endVertex();

        builder.vertex(x + 0.5, y + min, z + min).color(red, green, blue, alpha).uv(0, 0)/*.lightmap(240)*/.endVertex();
        builder.vertex(x + 0.5, y + min, z + max).color(red, green, blue, alpha).uv(0, 1)/*.lightmap(240)*/.endVertex();
        builder.vertex(x + 0.5, y + max, z + max).color(red, green, blue, alpha).uv(1, 1)/*.lightmap(240)*/.endVertex();
        builder.vertex(x + 0.5, y + max, z + min).color(red, green, blue, alpha).uv(1, 0)/*.lightmap(240)*/.endVertex();
    }

    protected static float noise(float input) {
        input = input % (r.length - 1F);
        int xMin = (int) input;
        float t = input - xMin;
        return codechicken.lib.math.MathHelper.interpolate(r[xMin], r[xMin + 1], t);
    }

    protected static float flicker(float input) {
        input = input % (r.length - 1F);
        int xMin = (int) input;
        float t = input - xMin;
        return codechicken.lib.math.MathHelper.interpolate(r[xMin], 0, t);
    }

    protected static float flickerFlair(float input) {
        input = input % (r.length - 1F);
        int xMin = (int) input;
        float t = input - xMin;
        if (t < 0.1) {
            return codechicken.lib.math.MathHelper.interpolate(0F, r[xMin], t / 0.1F);
        } else {
            return codechicken.lib.math.MathHelper.interpolate(r[xMin], 0, (t - 0.1F) / 0.9F);
        }
    }

    /**
     * This is a 'random' float generator.
     * - It actually juts loops through a set of randSet.length different random floats
     * Much faster than java random and more than sufficient for certain rendering tasks.
     */
    protected static float nextFloat() {
        return randSet[randPos++ % randSet.length];
    }

    /**
     * Sets the current position for 'random' float generator.
     * Effectively the same function as {@link Random#setSeed(long)} because the floats
     * will always be supplied in the same order.
     */
    protected static void setRandSeed(int i) {
        randPos = i % randSet.length;
    }
}
