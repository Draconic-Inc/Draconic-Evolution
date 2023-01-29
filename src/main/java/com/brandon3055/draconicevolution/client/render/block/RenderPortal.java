package com.brandon3055.draconicevolution.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

/**
 * Created by Brandon on 25/5/2015.
 */
public class RenderPortal implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
            RenderBlocks renderer) {
        Tessellator tess = Tessellator.instance;

        int meta = world.getBlockMetadata(x, y, z);

        IIcon c = Blocks.obsidian.getIcon(0, 0);
        float u = c.getMinU();
        float v = c.getMinV();
        float U = c.getMaxU();
        float V = c.getMaxV();

        tess.setColorRGBA(0, 0, 0, 256);
        switch (meta) {
            case 1:
                tess.addVertexWithUV(x - 0.01, y - 0.01, z + 0.75, u, v);
                tess.addVertexWithUV(x - 0.01, y + 1.01, z + 0.75, u, V);
                tess.addVertexWithUV(x + 1.01, y + 1.01, z + 0.75, U, V);
                tess.addVertexWithUV(x + 1.01, y - 0.01, z + 0.75, U, v);

                tess.addVertexWithUV(x + 1.01, y - 0.01, z + 0.25, U, v);
                tess.addVertexWithUV(x + 1.01, y + 1.01, z + 0.25, U, V);
                tess.addVertexWithUV(x - 0.01, y + 1.01, z + 0.25, u, V);
                tess.addVertexWithUV(x - 0.01, y - 0.01, z + 0.25, u, v);

                if (isFrame(world, x + 1, y, z)) {
                    tess.addVertexWithUV(x + 0.99, y - 0.01, z + 0.25, u, v);
                    tess.addVertexWithUV(x + 0.99, y - 0.01, z + 0.75, u, V);
                    tess.addVertexWithUV(x + 0.99, y + 1.01, z + 0.75, U, V);
                    tess.addVertexWithUV(x + 0.99, y + 1.01, z + 0.25, U, v);
                }
                if (isFrame(world, x - 1, y, z)) {
                    tess.addVertexWithUV(x + 0.01, y + 1.01, z + 0.25, U, v);
                    tess.addVertexWithUV(x + 0.01, y + 1.01, z + 0.75, U, V);
                    tess.addVertexWithUV(x + 0.01, y - 0.01, z + 0.75, u, V);
                    tess.addVertexWithUV(x + 0.01, y - 0.01, z + 0.25, u, v);
                }
                if (isFrame(world, x, y - 1, z)) {
                    tess.addVertexWithUV(x - 0.01, y + 0.01, z + 0.25, u, v);
                    tess.addVertexWithUV(x - 0.01, y + 0.01, z + 0.75, u, V);
                    tess.addVertexWithUV(x + 1.01, y + 0.01, z + 0.75, U, V);
                    tess.addVertexWithUV(x + 1.01, y + 0.01, z + 0.25, U, v);
                }
                if (isFrame(world, x, y + 1, z)) {
                    tess.addVertexWithUV(x + 1.01, y + 0.99, z + 0.25, U, v);
                    tess.addVertexWithUV(x + 1.01, y + 0.99, z + 0.75, U, V);
                    tess.addVertexWithUV(x - 0.01, y + 0.99, z + 0.75, u, V);
                    tess.addVertexWithUV(x - 0.01, y + 0.99, z + 0.25, u, v);
                }

                break;
            case 2:
                tess.addVertexWithUV(x + 0.75, y - 0.01, z - 0.01, u, v);
                tess.addVertexWithUV(x + 0.75, y - 0.01, z + 1.01, u, V);
                tess.addVertexWithUV(x + 0.75, y + 1.01, z + 1.01, U, V);
                tess.addVertexWithUV(x + 0.75, y + 1.01, z - 0.01, U, v);

                tess.addVertexWithUV(x + 0.25, y + 1.01, z - 0.01, U, v);
                tess.addVertexWithUV(x + 0.25, y + 1.01, z + 1.01, U, V);
                tess.addVertexWithUV(x + 0.25, y - 0.01, z + 1.01, u, V);
                tess.addVertexWithUV(x + 0.25, y - 0.01, z - 0.01, u, v);

                if (isFrame(world, x, y, z + 1)) {
                    tess.addVertexWithUV(x + 0.25, y - 0.01, z + 0.99, u, v);
                    tess.addVertexWithUV(x + 0.25, y + 1.01, z + 0.99, U, V);
                    tess.addVertexWithUV(x + 0.75, y + 1.01, z + 0.99, U, V);
                    tess.addVertexWithUV(x + 0.75, y - 0.01, z + 0.99, u, v);
                }
                if (isFrame(world, x, y, z - 1)) {
                    tess.addVertexWithUV(x + 0.75, y - 0.01, z + 0.01, u, v);
                    tess.addVertexWithUV(x + 0.75, y + 1.01, z + 0.01, U, V);
                    tess.addVertexWithUV(x + 0.25, y + 1.01, z + 0.01, U, V);
                    tess.addVertexWithUV(x + 0.25, y - 0.01, z + 0.01, u, v);
                }
                if (isFrame(world, x, y - 1, z)) {
                    tess.addVertexWithUV(x + 0.25, y + 0.01, z - 0.01, u, v);
                    tess.addVertexWithUV(x + 0.25, y + 0.01, z + 1.01, u, V);
                    tess.addVertexWithUV(x + 0.75, y + 0.01, z + 1.01, U, V);
                    tess.addVertexWithUV(x + 0.75, y + 0.01, z - 0.01, U, v);
                }
                if (isFrame(world, x, y + 1, z)) {
                    tess.addVertexWithUV(x + 0.75, y + 0.99, z - 0.01, U, v);
                    tess.addVertexWithUV(x + 0.75, y + 0.99, z + 1.01, U, V);
                    tess.addVertexWithUV(x + 0.25, y + 0.99, z + 1.01, u, V);
                    tess.addVertexWithUV(x + 0.25, y + 0.99, z - 0.01, u, v);
                }

                break;
            case 3:
                tess.addVertexWithUV(x - 0.01, y + 0.25, z - 0.01, u, v);
                tess.addVertexWithUV(x - 0.01, y + 0.25, z + 1.01, u, V);
                tess.addVertexWithUV(x + 1.01, y + 0.25, z + 1.01, U, V);
                tess.addVertexWithUV(x + 1.01, y + 0.25, z - 0.01, U, v);

                tess.addVertexWithUV(x + 1.01, y + 0.75, z - 0.01, U, v);
                tess.addVertexWithUV(x + 1.01, y + 0.75, z + 1.01, U, V);
                tess.addVertexWithUV(x - 0.01, y + 0.75, z + 1.01, u, V);
                tess.addVertexWithUV(x - 0.01, y + 0.75, z - 0.01, u, v);

                if (isFrame(world, x, y, z + 1)) {
                    tess.addVertexWithUV(x - 0.01, y + 0.25, z + 0.99, u, v);
                    tess.addVertexWithUV(x - 0.01, y + 0.75, z + 0.99, u, V);
                    tess.addVertexWithUV(x + 1.01, y + 0.75, z + 0.99, U, V);
                    tess.addVertexWithUV(x + 1.01, y + 0.25, z + 0.99, U, v);
                }
                if (isFrame(world, x, y, z - 1)) {
                    tess.addVertexWithUV(x + 1.01, y + 0.25, z + 0.01, U, v);
                    tess.addVertexWithUV(x + 1.01, y + 0.75, z + 0.01, U, V);
                    tess.addVertexWithUV(x - 0.01, y + 0.75, z + 0.01, u, V);
                    tess.addVertexWithUV(x - 0.01, y + 0.25, z + 0.01, u, v);
                }
                if (isFrame(world, x + 1, y, z)) {
                    tess.addVertexWithUV(x + 0.99, y + 0.25, z - 0.01, u, v);
                    tess.addVertexWithUV(x + 0.99, y + 0.25, z + 1.01, u, V);
                    tess.addVertexWithUV(x + 0.99, y + 0.75, z + 1.01, U, V);
                    tess.addVertexWithUV(x + 0.99, y + 0.75, z - 0.01, U, v);
                }
                if (isFrame(world, x - 1, y, z)) {
                    tess.addVertexWithUV(x + 0.01, y + 0.75, z - 0.01, U, v);
                    tess.addVertexWithUV(x + 0.01, y + 0.75, z + 1.01, U, V);
                    tess.addVertexWithUV(x + 0.01, y + 0.25, z + 1.01, u, V);
                    tess.addVertexWithUV(x + 0.01, y + 0.25, z - 0.01, u, v);
                }
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return References.idPortal;
    }

    private boolean isFrame(IBlockAccess access, int x, int y, int z) {
        Block block = access.getBlock(x, y, z);
        return block == ModBlocks.infusedObsidian || block == ModBlocks.dislocatorReceptacle;
    }
}
