package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.network.PacketTileMessage;
import com.brandon3055.brandonscore.network.wrappers.SyncableBool;
import com.brandon3055.brandonscore.network.wrappers.SyncableShort;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.fusioncrafting.FusionRecipeRegistry;
import com.brandon3055.draconicevolution.api.fusioncrafting.ICraftingPedestal;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionCraftingInventory;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.render.effect.EffectTrackerFusionCrafting;
import com.brandon3055.draconicevolution.client.sound.FusionRotationSound;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.lib.DESoundHandler;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 11/06/2016.
 */
public class TileFusionCraftingCore extends TileInventoryBase implements IFusionCraftingInventory, ITickable, ISidedInventory {

    public List<ICraftingPedestal> pedestals = new ArrayList<ICraftingPedestal>();
    public final SyncableBool isCrafting = new SyncableBool(false, true, false, true);
    /**
     * 0 = Not crafting<br>
     * 1 -> 1000 = Charge percentage<br>
     * 1000 -> 2000 = Crafting progress
     */
    public final SyncableShort craftingStage = new SyncableShort((short) 0, true, false, true);
    public IFusionRecipe activeRecipe = null;

    @SideOnly(Side.CLIENT)
    public LinkedList<EffectTrackerFusionCrafting> effects;

    public TileFusionCraftingCore() {
        setInventorySize(2);
        registerSyncableObject(isCrafting, false);
        registerSyncableObject(craftingStage, false);
        setShouldRefreshOnBlockChange();
    }

    //region Logic

    @Override
    public void update() {
        //LogHelper.info("- " + isCrafting);
        if (worldObj.isRemote) {
            if (isCrafting.value && !isCrafting.lastTickValue) {
                initializeEffects();
            }
            isCrafting.lastTickValue = isCrafting.value;
            updateEffects();
        }

        detectAndSendChanges();

        //Update Crafting
        if (isCrafting.value && !worldObj.isRemote) {

            for (ICraftingPedestal pedestal : pedestals) {
                if (((TileEntity) pedestal).isInvalid()) {
                    invalidateCrafting();
                    return;
                }
            }

            if (activeRecipe == null || !activeRecipe.matches(this, worldObj, pos) || activeRecipe.canCraft(this, worldObj, pos) == null || !activeRecipe.canCraft(this, worldObj, pos).equals("true")) {
                invalidateCrafting();
                return;
            }

            long totalCharge = 0;

            for (ICraftingPedestal pedestal : pedestals) {
                if (pedestal.getStackInPedestal() == null) {
                    continue;
                }
                totalCharge += pedestal.getCharge();
            }

            int averageCharge = (int) (totalCharge / activeRecipe.getRecipeIngredients().size());
            double percentage = averageCharge / (double) activeRecipe.getEnergyCost();

            if (percentage <= 1D && craftingStage.value < 1000) {
                craftingStage.value = (short) (percentage * 1000D);
            } else if (craftingStage.value < 2000) {
                craftingStage.value += 2;
            } else if (craftingStage.value >= 2000) {
                activeRecipe.craft(this, worldObj, pos);
                for (ICraftingPedestal pedestal : pedestals) {
                    pedestal.onCraft();
                }
            }
        }
    }

    public void attemptStartCrafting() {
        updatePedestals();
        activeRecipe = FusionRecipeRegistry.findRecipe(this, worldObj, pos);

        if (activeRecipe != null && activeRecipe.canCraft(this, worldObj, pos) != null && activeRecipe.canCraft(this, worldObj, pos).equals("true")) {
            isCrafting.value = true;
        } else {
            activeRecipe = null;
        }
    }

    private void invalidateCrafting() {
        isCrafting.value = false;
        activeRecipe = null;
        craftingStage.value = 0;
        pedestals.clear();
    }

    /**
     * Clears the pedestal list and then re acquires all valid pedestals.
     */
    public void updatePedestals() {
        if (isCrafting.value) {
            return;
        }

        pedestals.clear();
        int range = 16;

        List<BlockPos> positions = new ArrayList<BlockPos>();
        //X
        positions.addAll(Lists.newArrayList(BlockPos.getAllInBox(pos.add(-range, -1, -1), pos.add(range, 1, 1))));
        //Y
        positions.addAll(Lists.newArrayList(BlockPos.getAllInBox(pos.add(-1, -range, -1), pos.add(1, range, 1))));
        //Z
        positions.addAll(Lists.newArrayList(BlockPos.getAllInBox(pos.add(-1, -1, -range), pos.add(1, 1, range))));

        for (BlockPos checkPos : positions) {
            TileEntity tile = worldObj.getTileEntity(checkPos);

            if (tile instanceof ICraftingPedestal) {
                ICraftingPedestal pedestal = (ICraftingPedestal) tile;
                Vec3D dirVec = new Vec3D(tile.getPos()).subtract(pos);
                double dist = Utils.getDistanceAtoB(new Vec3D(tile.getPos()), new Vec3D(pos));

                if (dist >= 2 && EnumFacing.getFacingFromVector((int) dirVec.x, (int) dirVec.y, (int) dirVec.z) == pedestal.getDirection().getOpposite() && pedestal.setCraftingInventory(this)) {
                    pedestals.add(pedestal);
                }
            }
        }
    }

    @Override
    public boolean craftingInProgress() {
        return isCrafting.value;
    }

    @Override
    public void receivePacketFromClient(PacketTileMessage packet, EntityPlayerMP client) {
        attemptStartCrafting();
    }

    @Override
    public int getRequiredCharge() {
        if (activeRecipe == null) {
            return 0;
        } else {
            return activeRecipe.getEnergyCost();
        }
    }

    @Override
    public int getCraftingStage() {
        return craftingStage.value;
    }

    //endregion

    //region Inventory


    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);
        updateBlock();
    }

    @Override
    public ItemStack getStackInCore(int slot) {
        return getStackInSlot(slot);
    }

    @Override
    public void setStackInCore(int slot, ItemStack stack) {
        setInventorySlotContents(slot, stack);
    }

    @Override
    public List<ICraftingPedestal> getPedestals() {
        return pedestals;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{0, 1};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return index == 0;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == 1;
    }

    //endregion

    //region Effects

    @SideOnly(Side.CLIENT)
    public void initializeEffects() {
        activeRecipe = FusionRecipeRegistry.findRecipe(this, worldObj, pos);

        if (activeRecipe == null) {
            effects = null;
            return;
        }

        effects = new LinkedList<EffectTrackerFusionCrafting>();

        for (ICraftingPedestal pedestal : pedestals) {
            if (pedestal.getStackInPedestal() == null) {
                continue;
            }

            pedestal.setCraftingInventory(this);
            Vec3D spawn = new Vec3D(((TileEntity) pedestal).getPos());
            spawn.add(0.5 + pedestal.getDirection().getFrontOffsetX() * 0.45, 0.5 + pedestal.getDirection().getFrontOffsetY() * 0.45, 0.5 + pedestal.getDirection().getFrontOffsetZ() * 0.45);
            effects.add(new EffectTrackerFusionCrafting(worldObj, spawn, new Vec3D(pos), this));
//            BCEffectHandler.effectRenderer.addEffect(ResourceHelperDE.getResource("textures/blocks/fusion_crafting/fusionParticle.png"), new ParticleFusionCrafting(worldObj, spawn, new Vec3D(pos), this));
        }
    }

    private double effectRotation = 0;
    private boolean allLocked = false;
    private boolean halfCycle = false;

    @SideOnly(Side.CLIENT)
    public void updateEffects() {
        if (effects == null) {
            effectRotation = 0;
            allLocked = false;
            return;
        }

//        craftingStage.value = 1500;


        //region Calculate Distance
        double distFromCore = 1.2;
        if (getCraftingStage() > 1600){
            distFromCore *= 1.0D - (getCraftingStage() - 1600) / 400D;
        }

        if (allLocked){
            effectRotation -= Math.min(((getCraftingStage() - 1100D) / 900D) * 0.8D, 0.5D);
            if (effectRotation > 0){
                effectRotation = 0;
            }

        }

        int index = 0;
        int count = effects.size();
        boolean flag = true;
        boolean isMoving = getCraftingStage() > 1000;
        for (EffectTrackerFusionCrafting effect : effects) {
            effect.onUpdate(isMoving);
            if (!effect.positionLocked){
                flag = false;
            }

            if (isMoving) {
                effect.scale = 0.7F + ((float) (distFromCore / 1.2D) * 0.3F);
                effect.green = effect.blue = (float) (distFromCore - 0.2);
                effect.red = 1F - (float) (distFromCore - 0.2);
            }

            double indexPos = (double)index / (double)count;
            double offset = indexPos * (Math.PI * 2);
            double offsetX = Math.sin(effectRotation + offset) * distFromCore;
            double offsetZ = Math.cos(effectRotation + offset) * distFromCore;

            double mix = effectRotation / 5F;
            double xAdditive = offsetX * Math.sin(-mix);
            double zAdditive = offsetZ * Math.cos(-mix);

            double offsetY = (xAdditive + zAdditive) * 0.2 * (distFromCore / 1.2);

            effect.circlePosition.set(pos.getX() + 0.5 + offsetX, pos.getY() + 0.5 + offsetY, pos.getZ() + 0.5 + offsetZ);
            index++;
        }

//        //LogHelper.info(Math.sin(effectRotation));
//        double rotationPos = Math.sin(effectRotation * 2);
//        float pitch = 0.1F + (((getCraftingStage() - 1000) / 1000F) * 1.9F);
//
//        if (rotationPos > 0 && !halfCycle){
//            halfCycle = true;
//            worldObj.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, DESoundHandler.fusionRotation, SoundCategory.BLOCKS, 1F, pitch, false);
//        }
//        else if (rotationPos < 0 && halfCycle) {
//            halfCycle = false;
//            worldObj.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, DESoundHandler.fusionRotation, SoundCategory.BLOCKS, 1F, pitch, false);
//        }

        if (!allLocked && flag){
            FMLClientHandler.instance().getClient().getSoundHandler().playSound(new FusionRotationSound(this));
        }

        allLocked = flag;

        if (!isCrafting.value){
            for (int i = 0; i < 100 ; i++) {
                BCEffectHandler.effectRenderer.addEffect(DEParticles.DE_SHEET, new EffectTrackerFusionCrafting.SubParticle(worldObj, new Vec3D(pos).add(0.5, 0.5, 0.5)));
            }
            worldObj.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, DESoundHandler.fusionComplete, SoundCategory.BLOCKS, 2F, 1F, false);
            effects = null;
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderEffects(float partialTicks) {
  //     craftingStage.value = 1500;
        if (effects != null) {
            ResourceHelperDE.bindTexture("textures/blocks/fusion_crafting/fusionParticle.png");
            Tessellator tessellator = Tessellator.getInstance();

            //Pre-Render
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);

            for (EffectTrackerFusionCrafting effect : effects) {
                effect.renderEffect(tessellator, partialTicks);
            }

            //Post-Render
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.add(-16, -16, -16), pos.add(17, 17, 17));
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    //endregion
}
