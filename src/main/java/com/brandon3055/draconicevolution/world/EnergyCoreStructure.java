package com.brandon3055.draconicevolution.world;

import com.brandon3055.brandonscore.lib.MultiBlockStorage;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.ModelUtils;
import com.brandon3055.brandonscore.utils.MultiBlockHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileInvisECoreBlock;
import com.brandon3055.draconicevolution.client.gui.GuiEnergyCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Created by brandon3055 on 1/4/2016.
 */
public class EnergyCoreStructure extends MultiBlockHelper {
    private final int FLAG_RENDER = 0;
    private final int FLAG_FORME = 1;
    private final int FLAG_REVERT = 2;
    private MultiBlockStorage[] structureTiers = new MultiBlockStorage[8];
    private TileEnergyStorageCore core;
    public static boolean coreForming = false;

    public EnergyCoreStructure initialize(TileEnergyStorageCore core) {
        this.core = core;
        structureTiers[0] = buildTier1();
        structureTiers[1] = buildTier2();
        structureTiers[2] = buildTier3();
        structureTiers[3] = buildTier4();
        structureTiers[4] = buildTier5();
        structureTiers[5] = buildTier6();
        structureTiers[6] = buildTier7();
        structureTiers[7] = buildTierOMG();
        return this;
    }

    public boolean checkTier(int tier) {
        BlockPos offset = getCoreOffset(tier);

        switch (tier) {
            case 1:
                return structureTiers[0].checkStructure(core.getWorld(), core.getPos().add(offset));
            case 2:
                return structureTiers[1].checkStructure(core.getWorld(), core.getPos().add(offset));
            case 3:
                return structureTiers[2].checkStructure(core.getWorld(), core.getPos().add(offset));
            case 4:
                return structureTiers[3].checkStructure(core.getWorld(), core.getPos().add(offset));
            case 5:
                return structureTiers[4].checkStructure(core.getWorld(), core.getPos().add(offset));
            case 6:
                return structureTiers[5].checkStructure(core.getWorld(), core.getPos().add(offset));
            case 7:
                return structureTiers[6].checkStructure(core.getWorld(), core.getPos().add(offset));
            case 8:
                return structureTiers[7].checkStructure(core.getWorld(), core.getPos().add(offset));
        }
        if (tier <= 0) {
            LogHelper.error("[EnergyCoreStructure] Tier value to small. As far as TileEnergyStorageCore is concerned the tiers now start at 1 not 0. This class automatically handles the conversion now");
        }
        else {
            LogHelper.error("[EnergyCoreStructure#checkTeir] What exactly were you expecting after Tier 8? Infinity.MAX_VALUE?");
        }


        return false;
    }

    public void placeTier(int tier) {
        BlockPos offset = getCoreOffset(tier);

        switch (tier) {
            case 1:
                structureTiers[0].placeStructure(core.getWorld(), core.getPos().add(offset));
                return;
            case 2:
                structureTiers[1].placeStructure(core.getWorld(), core.getPos().add(offset));
                return;
            case 3:
                structureTiers[2].placeStructure(core.getWorld(), core.getPos().add(offset));
                return;
            case 4:
                structureTiers[3].placeStructure(core.getWorld(), core.getPos().add(offset));
                return;
            case 5:
                structureTiers[4].placeStructure(core.getWorld(), core.getPos().add(offset));
                return;
            case 6:
                structureTiers[5].placeStructure(core.getWorld(), core.getPos().add(offset));
                return;
            case 7:
                structureTiers[6].placeStructure(core.getWorld(), core.getPos().add(offset));
                return;
            case 8:
                structureTiers[7].placeStructure(core.getWorld(), core.getPos().add(offset));
                return;
        }
        if (tier <= 0) {
            LogHelper.error("[EnergyCoreStructure] Tier value to small. As far as TileEnergyStorageCore is concerned the tiers now start at 1 not 0. This class automatically handles the conversion now");
        }
        else {
            LogHelper.error("[EnergyCoreStructure#placeTier] What exactly were you expecting after Tier 8? Infinity.MAX_VALUE?");
        }
    }

    public void renderTier(int tier) {
        forTier(tier, FLAG_RENDER);
    }

    public void formTier(int tier) {
        coreForming = true;
        forTier(tier, FLAG_FORME);
        coreForming = false;
    }

    public void revertTier(int tier) {
        forTier(tier, FLAG_REVERT);
    }

    private void forTier(int tier, int flag) {
        tier -= 1;
        if (tier < 0) {
            LogHelper.error("[EnergyCoreStructure] Tier value to small. As far as TileEnergyStorageCore is concerned the tiers now start at 1 not 0. This class automatically handles the conversion now");
        }
        else if (tier >= structureTiers.length) {
            LogHelper.error("[EnergyCoreStructure#placeTier] What exactly were you expecting after Tier 8? Infinity.MAX_VALUE?");
        }
        else {
            structureTiers[tier].forEachInStructure(core.getWorld(), core.getPos().add(getCoreOffset(tier + 1)), flag);
        }
    }

    public MultiBlockStorage getStorageForTier(int tier) {
        return structureTiers[tier - 1];
    }

    @Override
    public void forBlock(String name, World world, BlockPos pos, BlockPos startPos, int flag) {
        if (name.isEmpty() || name.equals("draconicevolution:energy_storage_core")) {
            return;
        }

        //region Render Build Guide

        if (flag == FLAG_RENDER) {
            if (world.isRemote) {
                renderBuildGuide(name, world, pos, startPos, flag);
            }
        }

        //endregion

        //region Activate

        else if (flag == FLAG_FORME) {
            world.setBlockState(pos, DEFeatures.invisECoreBlock.getDefaultState());
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileInvisECoreBlock) {
                ((TileInvisECoreBlock) tile).blockName = name;
                ((TileInvisECoreBlock) tile).setController(core);
            }
        }

        //endregion

        //region Deactivate

        else if (flag == FLAG_REVERT) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileInvisECoreBlock) {
                ((TileInvisECoreBlock) tile).revert();
            }
        }

        //endregion
    }

    @SideOnly(Side.CLIENT)
    private void renderBuildGuide(String name, World world, BlockPos pos, BlockPos startPos, int flag) {
        Block block = Block.REGISTRY.getObject(new ResourceLocation(name));

        Vec3D corePos = Vec3D.getCenter(startPos.subtract(getCoreOffset(core.tier.get())));
        double dist = Utils.getDistanceAtoB(corePos, Vec3D.getCenter(pos));
        double pDist = Minecraft.getMinecraft().player.getDistance(corePos.x, corePos.y, corePos.z);

        if (GuiEnergyCore.layer != -1) {
            pDist = GuiEnergyCore.layer + 2;
        }

        IBlockState atPos = world.getBlockState(pos);
        boolean invalid = !world.isAirBlock(pos) && (atPos.getBlock().getRegistryName() == null || !atPos.getBlock().getRegistryName().toString().equals(name));

        if (dist + 2 > pDist && !invalid) {
            return;
        }

        if (name.equals("") || name.equals("air")) {
            return;
        }

        BlockPos translation = new BlockPos(pos.getX() - startPos.getX(), pos.getY() - startPos.getY(), pos.getZ() - startPos.getZ());
        translation = translation.add(getCoreOffset(core.tier.get()));

        int alpha = 0xFF000000;
        if (invalid) {
            alpha = (int) (((Math.sin(ClientEventHandler.elapsedTicks / 20D) + 1D) / 2D) * 255D) << 24;
        }

        IBlockState state = block.getDefaultState();

        GlStateManager.pushMatrix();
        GlStateManager.translate(translation.getX(), translation.getY(), translation.getZ());
        if (invalid) {
            GlStateManager.disableDepth();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
            double s = Math.sin(ClientEventHandler.elapsedTicks / 10D) * 0.1D;
            GlStateManager.scale(0.8 + s, 0.8 + s, 0.8 + s);
            GlStateManager.translate(0.1 - s, 0.1 - s, 0.1 - s);
        }
        else {
            GlStateManager.scale(0.8, 0.8, 0.8);
            GlStateManager.translate(0.1, 0.1, 0.1);
        }

        float brightnessX = OpenGlHelper.lastBrightnessX;
        float brightnessY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 150f, 150f);

        List<BakedQuad> blockQuads = ModelUtils.getModelQuads(state);

        ModelUtils.renderQuadsARGB(blockQuads, (invalid ? 0x00500000 : 0x00404040) | alpha);

        if (invalid) {
            GlStateManager.enableDepth();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        }
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightnessX, brightnessY);
        GlStateManager.popMatrix();
    }

    //region Structure Builders

    private MultiBlockStorage buildTier1() {
        MultiBlockStorage storage = new MultiBlockStorage(1, this);
        String X = "draconicevolution:energy_storage_core";

        storage.addRow(X);

        return storage;
    }

    private MultiBlockStorage buildTier2() {
        MultiBlockStorage storage = new MultiBlockStorage(3, this);
        String e = "";
        String X = "draconicevolution:energy_storage_core";
        String D = "draconicevolution:draconium_block";

        storage.addRow(e, e, e);
        storage.addRow(e, D, e);
        storage.addRow(e, e, e);

        storage.newLayer();
        storage.addRow(e, D, e);
        storage.addRow(D, X, D);
        storage.addRow(e, D, e);

        storage.newLayer();
        storage.addRow(e, e, e);
        storage.addRow(e, D, e);
        storage.addRow(e, e, e);

        return storage;
    }

    private MultiBlockStorage buildTier3() {
        MultiBlockStorage storage = new MultiBlockStorage(3, this);
        String X = "draconicevolution:energy_storage_core";
        String D = "draconicevolution:draconium_block";

        storage.addRow(D, D, D);
        storage.addRow(D, D, D);
        storage.addRow(D, D, D);

        storage.newLayer();
        storage.addRow(D, D, D);
        storage.addRow(D, X, D);
        storage.addRow(D, D, D);

        storage.newLayer();
        storage.addRow(D, D, D);
        storage.addRow(D, D, D);
        storage.addRow(D, D, D);

        return storage;
    }

    private MultiBlockStorage buildTier4() {
        MultiBlockStorage storage = new MultiBlockStorage(5, this);
        String e = "";
        String X = "draconicevolution:energy_storage_core";
        String D = "draconicevolution:draconium_block";
        String R = "minecraft:redstone_block";

        storage.addRow(e, e, e, e, e);
        storage.addRow(e, D, D, D, e);
        storage.addRow(e, D, D, D, e);
        storage.addRow(e, D, D, D, e);
        storage.addRow(e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, D, D, D, e);
        storage.addRow(D, R, R, R, D);
        storage.addRow(D, R, R, R, D);
        storage.addRow(D, R, R, R, D);
        storage.addRow(e, D, D, D, e);

        storage.newLayer();
        storage.addRow(e, D, D, D, e);
        storage.addRow(D, R, R, R, D);
        storage.addRow(D, R, X, R, D);
        storage.addRow(D, R, R, R, D);
        storage.addRow(e, D, D, D, e);

        storage.newLayer();
        storage.addRow(e, D, D, D, e);
        storage.addRow(D, R, R, R, D);
        storage.addRow(D, R, R, R, D);
        storage.addRow(D, R, R, R, D);
        storage.addRow(e, D, D, D, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e);
        storage.addRow(e, D, D, D, e);
        storage.addRow(e, D, D, D, e);
        storage.addRow(e, D, D, D, e);
        storage.addRow(e, e, e, e, e);

        return storage;
    }

    private MultiBlockStorage buildTier5() {
        MultiBlockStorage storage = new MultiBlockStorage(7, this);
        String e = "";
        String X = "draconicevolution:energy_storage_core";
        String D = "draconicevolution:draconium_block";
        String R = "minecraft:redstone_block";

        storage.addRow(e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e);
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e);
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(D, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, D);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(e, e, D, D, D, e, e);

        storage.newLayer();
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(D, R, R, R, R, R, D);
        storage.addRow(D, R, R, X, R, R, D);
        storage.addRow(D, R, R, R, R, R, D);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(e, e, D, D, D, e, e);

        storage.newLayer();
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(D, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, D);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(e, e, D, D, D, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e);
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e);
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e);

        return storage;
    }

    private MultiBlockStorage buildTier6() {
        MultiBlockStorage storage = new MultiBlockStorage(9, this);
        String e = "";
        String X = "draconicevolution:energy_storage_core";
        String D = "draconicevolution:draconium_block";
        String R = "minecraft:redstone_block";

        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, D, D, D, e, e, e);
        storage.addRow(e, e, e, D, D, D, e, e, e);
        storage.addRow(e, e, e, D, D, D, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, D, D, D, D, D, e, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, e, D, D, D, D, D, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, D, D, D, D, D, e, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, e, D, D, D, D, D, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, D, D, D, e, e, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(D, R, R, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, R, R, D);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, e, e, D, D, D, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, D, D, D, e, e, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(D, R, R, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, X, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, R, R, D);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, e, e, D, D, D, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, D, D, D, e, e, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(D, R, R, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, R, R, D);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, e, e, D, D, D, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, D, D, D, D, D, e, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, e, D, D, D, D, D, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, D, D, D, D, D, e, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, e, D, D, D, D, D, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, D, D, D, e, e, e);
        storage.addRow(e, e, e, D, D, D, e, e, e);
        storage.addRow(e, e, e, D, D, D, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);

        return storage;
    }

    private MultiBlockStorage buildTier7() {
        MultiBlockStorage storage = new MultiBlockStorage(11, this);
        String e = "";
        String X = "draconicevolution:energy_storage_core";
        String D = "draconicevolution:draconium_block";
        String R = "minecraft:redstone_block";


        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(D, R, R, R, R, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, R, R, R, R, D);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);

        //Centre
        storage.newLayer();
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(D, R, R, R, R, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, X, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, R, R, R, R, D);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(D, R, R, R, R, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, R, R, R, R, D);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);

        return storage;
    }

    private MultiBlockStorage buildTierOMG() {
        MultiBlockStorage storage = new MultiBlockStorage(13, this);
        String e = "";
        String X = "draconicevolution:energy_storage_core";
        String A = "draconicevolution:draconic_block";
        String D = "draconicevolution:draconium_block";
//        String d = "draconicevolution:draconium_block";

        //region Hard
        if (DEConfig.hardMode) {
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, A, A, A, A, A, A, A, A, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, A, A, A, A, A, A, A, A, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, A, A, A, A, A, A, A, A, e, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, e, A, A, A, A, A, A, A, A, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);

            //Centre
            storage.newLayer();
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, X, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, A, A, A, A, A, A, A, A, e, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, e, A, A, A, A, A, A, A, A, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, A, A, A, A, A, A, A, A, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, A, A, A, A, A, A, A, A, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
        }
        //endregion

        //region Hard
        else {
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);

            //Centre
            storage.newLayer();
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, X, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
        }
        //endregion

        return storage;
    }

    //endregion

    @Override
    public boolean checkBlock(String name, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileInvisECoreBlock && ((TileInvisECoreBlock) tile).blockName.equals(name)) {
            return true;
        }
        else {
            return super.checkBlock(name, world, pos);
        }
    }

    public BlockPos getCoreOffset(int tier) {
        int offset = tier == 1 ? 0 : tier == 2 || tier == 3 ? -1 : -(tier - 2);
        return new BlockPos(offset, offset, offset);
    }

    @Override
    public void setBlock(String name, World world, BlockPos pos) {
        if (!name.equals("draconicevolution:energy_storage_core") && name.length() > 0) {
            super.setBlock(name, world, pos);
        }
    }
}
