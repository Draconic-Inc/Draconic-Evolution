package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.gui.effects.GuiEffect;
import com.brandon3055.brandonscore.client.gui.effects.GuiEffectRenderer;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.network.PacketTileMessage;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.OreDictHelper;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.client.render.effect.RenderEnergyBolt;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.integration.ModHelper;
import com.brandon3055.draconicevolution.inventory.ContainerFusionCraftingCore;
import com.brandon3055.draconicevolution.lib.RecipeManager;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class GuiFusionCraftingCore extends GuiContainer {

    private final EntityPlayer player;
    private final TileFusionCraftingCore tile;
    private IFusionRecipe currentRecipe = null;
    private String canCraft = "";
    private GuiButton startCrafting;
    private GuiEffectRenderer guiEffectRenderer = new GuiEffectRenderer();
    private Random rand = new Random();
    private int[] boltStats = {0, 0, 0, 0, 0, 0};

    public GuiFusionCraftingCore(EntityPlayer player, TileFusionCraftingCore tile) {
        super(new ContainerFusionCraftingCore(player, tile));
        this.player = player;
        this.tile = tile;

        this.xSize = 180;
        this.ySize = 200;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonList.add(startCrafting = new StartButton(0, width / 2 - 40, guiTop + 93, 80, 14, I18n.format("gui.de.button.start")));
        startCrafting.visible = false;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GuiHelper.drawGuiBaseBackground(this, guiLeft, guiTop, xSize, ySize);

        //region Draw Fancy Background

        //Back
        GuiHelper.drawColouredRect(guiLeft + 3, guiTop + 3, xSize - 6, 110, 0xFF00FFFF);
        GuiHelper.drawColouredRect(guiLeft + 4, guiTop + 4, xSize - 8, 108, 0xFF000000);

        if (currentRecipe != null && canCraft != null && canCraft.equals("true")) {

            //Ingredients
            GuiHelper.drawColouredRect(guiLeft + 15, guiTop + 7, 20, 100, 0xFFAA00FF);
            GuiHelper.drawColouredRect(guiLeft + 16, guiTop + 8, 18, 98, 0xFF000000);
            GuiHelper.drawColouredRect(guiLeft + xSize - 35, guiTop + 7, 20, 100, 0xFFAA00FF);
            GuiHelper.drawColouredRect(guiLeft + xSize - 34, guiTop + 8, 18, 98, 0xFF000000);

            //Items
            GuiHelper.drawColouredRect(guiLeft + (xSize / 2) - 10, guiTop + 24, 20, 64, 0xFF00FFFF);
            GuiHelper.drawColouredRect(guiLeft + (xSize / 2) - 9, guiTop + 25, 18, 62, 0xFF000000);
        }

        //endregion

        drawCenteredString(fontRendererObj, I18n.format("gui.de.fusionCraftingCore.name"), guiLeft + (xSize / 2), guiTop + 5, InfoHelper.GUI_TITLE);
        GlStateManager.color(1F, 1F, 1F, 1F);

        ResourceHelperDE.bindTexture(DETextures.GUI_FUSION_CRAFTING);
        //drawTexturedModalRect(guiLeft + (xSize / 2) - 8, guiTop + 45, 0, 0, 15, 21);
        GuiHelper.drawPlayerSlots(this, guiLeft + (xSize / 2), guiTop + 115, true);
        if (currentRecipe == null || canCraft == null || !canCraft.equals("true")){
            drawTexturedModalRect(guiLeft + (xSize / 2) - 9, guiTop + 25, 138, 0, 18, 18);
            if (tile.getStackInSlot(1) != null){
                drawTexturedModalRect(guiLeft + (xSize / 2) - 9, guiTop + 69, 138, 0, 18, 18);
            }
        }
        //drawTexturedModalRect(guiLeft + (xSize / 2) - 9, guiTop + 69, 138, 0, 18, 18);

        if (currentRecipe != null) {
            GuiHelper.drawStack2D(currentRecipe.getRecipeOutput(tile.getStackInCore(0)), mc, guiLeft + (xSize / 2) - 8, guiTop + 70, 16F);
            List ingredients = currentRecipe.getRecipeIngredients();

            int centerX = guiLeft + xSize / 2;
            int centerY = guiTop + ySize / 2 - 42;

            //region Draw EnergyFX

            if (tile.isCrafting.value && tile.craftingStage.value > 0) {

                GlStateManager.depthMask(false);
                double charge = tile.craftingStage.value / 1000D;
                if (charge > 1) {
                    charge = 1;
                }

                int size = (int) ((1D - charge) * 98);

                RenderEnergyBolt.renderBoltBetween(new Vec3D(guiLeft + 16 + boltStats[0], guiTop + 106, 0), new Vec3D(guiLeft + 16 + boltStats[1], guiTop + 8 + size, 0), 1, charge * 10, 10, boltStats[2], true);
                RenderEnergyBolt.renderBoltBetween(new Vec3D(guiLeft + 16 + boltStats[3], guiTop + 106, 0), new Vec3D(guiLeft + 16 + boltStats[4], guiTop + 8 + size, 0), 1, charge * 10, 10, boltStats[5], true);

                RenderEnergyBolt.renderBoltBetween(new Vec3D(guiLeft + xSize - 34 + boltStats[0], guiTop + 106, 0), new Vec3D(guiLeft + xSize - 34 + boltStats[1], guiTop + 8 + size, 0), 1, charge * 10, 10, boltStats[2], true);
                RenderEnergyBolt.renderBoltBetween(new Vec3D(guiLeft + xSize - 34 + boltStats[3], guiTop + 106, 0), new Vec3D(guiLeft + xSize - 34 + boltStats[4], guiTop + 8 + size, 0), 1, charge * 10, 10, boltStats[5], true);
            }

            //endregion

            //region Draw Ingredients

            for (int i = 0; i < ingredients.size(); i++) {
                boolean isLeft = i % 2 == 0;
                boolean isOdd = ingredients.size() % 2 == 1;
                int sideCount = ingredients.size() / 2;

                if (isOdd && !isLeft) {
                    sideCount--;
                }

                int xPos;
                int yPos;

                if (isLeft) {
                    xPos = centerX - 65;
                    int ySize = 80 / Math.max(sideCount - (isOdd ? 0 : 1), 1);
                    int sideIndex = i / 2;

                    if (sideCount <= 1 && (!isOdd || ingredients.size() == 1)) {
                        sideIndex = 1;
                        ySize = 40;
                    }

                    yPos = centerY - 40 + (sideIndex * ySize);
                } else {
                    xPos = centerX + 65;
                    int ySize = 80 / Math.max(sideCount - (isOdd ? 0 : 1), 1);
                    int sideIndex = i / 2;

                    if (isOdd) {
                        sideCount++;
                    }

                    if (sideCount <= 1) {
                        sideIndex = 1;
                        ySize = 40;
                    }

                    yPos = centerY - 40 + (sideIndex * ySize);
                }

                GuiHelper.drawStack2D(OreDictHelper.resolveObject(ingredients.get(i)), mc, xPos - 8, yPos - 8, 16F);
            }

            //endregion

            //Draw Progress
            if (tile.isCrafting.value && tile.craftingStage.value >= 0) {
                int state = tile.craftingStage.value;
                String status = state > 1000 ? I18n.format("gui.fusionCrafting.crafting.info") : I18n.format("gui.fusionCrafting.charging.info");
                double d = state > 1000 ? (state - 1000F) / 1000D : state / 1000D;
                drawCenteredString(fontRendererObj, status + ": " + TextFormatting.GOLD + ((int) (d * 100) + "%"), width / 2, guiTop + 95, state < 1000 ? 0x00FF00 : 0x00FFFF);
            }

//            if (currentRecipe != null) {
//                if (canCraft != null && !canCraft.equals("true")) {
//                    if (canCraft.equals("tierLow")) {
//                        GuiHelper.drawCenteredString(fontRendererObj, I18n.format("gui.fusionCrafting.tierLow.info"), guiLeft + (xSize / 2), guiTop + 95, 0xFF0000, false);
//                    } else if (canCraft.equals("outputObstructed")) {
//                        GuiHelper.drawCenteredString(fontRendererObj, I18n.format("gui.fusionCrafting.outputObstructed.info"), guiLeft + (xSize / 2), guiTop + 95, 0xAA00FF, false);
//                    } else {
//                        GuiHelper.drawCenteredString(fontRendererObj, I18n.format(canCraft), guiLeft + (xSize / 2), guiTop + 95, 0xFF0000, false);
//                    }
//                }
//            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        currentRecipe = RecipeManager.FUSION_REGISTRY.findRecipe(tile, player.worldObj, tile.getPos());
        if (currentRecipe != null) {
            canCraft = currentRecipe.canCraft(tile, player.worldObj, tile.getPos());
        }

        startCrafting.enabled = startCrafting.visible = currentRecipe != null && canCraft != null && canCraft.equals("true") && !tile.craftingInProgress();

        //region Spawn Particles

        if (currentRecipe != null && canCraft != null && canCraft.equals("true")) {
            List<Object> ingredients = currentRecipe.getRecipeIngredients();

            int centerX = guiLeft + xSize / 2;
            int centerY = guiTop + ySize / 2 - 45;

            for (int i = 0; i < ingredients.size(); i++) {
                boolean isLeft = i % 2 == 0;
                boolean isOdd = ingredients.size() % 2 == 1;
                int sideCount = ingredients.size() / 2;

                if (isOdd && !isLeft) {
                    sideCount--;
                }

                int xPos;
                int yPos;


                if (isLeft) {
                    xPos = centerX - 65;
                    int ySize = 80 / Math.max(sideCount - (isOdd ? 0 : 1), 1);

                    int sideIndex = i / 2;

                    if (sideCount <= 1 && (!isOdd || ingredients.size() == 1)) {
                        sideIndex = 1;
                        ySize = 40;
                    }

                    yPos = centerY - 40 + (sideIndex * ySize);
                } else {
                    xPos = centerX + 65;

                    int ySize = 80 / Math.max(sideCount - (isOdd ? 0 : 1), 1);

                    int sideIndex = i / 2;

                    if (isOdd) {
                        sideCount++;
                    }

                    if (sideCount <= 1) {
                        sideIndex = 1;
                        ySize = 40;
                    }

                    yPos = centerY - 40 + (sideIndex * ySize);
                }

                if (rand.nextInt(10) == 0) {
                    xPos += -8 + (rand.nextDouble() * 16);
                    yPos += -8 + (rand.nextDouble() * 16);
                    double ty = centerY + (-20 + (rand.nextDouble() * 40));
                    guiEffectRenderer.addEffect(new EnergyEffect(player.worldObj, xPos, yPos, centerX, ty, 0));
                }
            }

            if (tile.craftingStage.value > 1000) {
                double xPos = centerX - 8 + (rand.nextDouble() * 16);
                double yTop = guiTop + 35 - 8 + (rand.nextDouble() * 16);
                guiEffectRenderer.addEffect(new EnergyEffect(player.worldObj, xPos, yTop, centerX, guiTop + 78, 1));
            }
            guiEffectRenderer.updateEffects();
        }
        else {
            guiEffectRenderer.clearEffects();
        }

        //endregion

        //region Update Bolt Stats

        boltStats[0] = (int)(rand.nextDouble() * 18);
        boltStats[1] = (int)(rand.nextDouble() * 18);
        boltStats[2] = rand.nextInt();
        boltStats[3] = (int)(rand.nextDouble() * 18);
        boltStats[4] = (int)(rand.nextDouble() * 18);
        boltStats[5] = rand.nextInt();

        //endregion
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if (currentRecipe != null) {
            if (canCraft != null && !canCraft.equals("true")) {
                if (canCraft.equals("tierLow")) {
                    GuiHelper.drawCenteredString(fontRendererObj, I18n.format("gui.fusionCrafting.tierLow.info"), (xSize / 2), 95, 0xAA00FF, false);
                } else if (canCraft.equals("outputObstructed")) {
                    GuiHelper.drawCenteredString(fontRendererObj, I18n.format("gui.fusionCrafting.outputObstructed.info"), (xSize / 2), 95, 0xAA00FF, false);
                } else {

                    GlStateManager.translate(0, 0, 600);
                    GuiHelper.drawColouredRect(5, 88, xSize - 10, 20, 0xFFFF0000);
                    GuiHelper.drawColouredRect(6, 89, xSize - 12, 18, 0xFF000000);
                    GuiHelper.drawCenteredSplitString(fontRendererObj, I18n.format(canCraft), (xSize / 2), 90, xSize - 10, 0xAA00FF, false);
                    GlStateManager.translate(0, 0, -600);
                }
            }
        }
        else if (ModHelper.isJEIInstalled){
            GuiHelper.drawBorderedRect(81, 45, 18, 22, 1, 0xFF101010, 0xFF303030);
            fontRendererObj.drawString("R", 87, 52, 0xA0A0A0, false);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        guiEffectRenderer.renderEffects(partialTicks);


        if (currentRecipe != null && canCraft != null) {
            List<Object> ingredients = currentRecipe.getRecipeIngredients();

            int centerX = guiLeft + xSize / 2;
            int centerY = guiTop + ySize / 2 - 45;

            for (int i = 0; i < ingredients.size(); i++) {
                boolean isLeft = i % 2 == 0;
                boolean isOdd = ingredients.size() % 2 == 1;
                int sideCount = ingredients.size() / 2;

                if (isOdd && !isLeft) {
                    sideCount--;
                }

                int xPos;
                int yPos;


                if (isLeft) {
                    xPos = centerX - 65;
                    int ySize = 80 / Math.max(sideCount - (isOdd ? 0 : 1), 1);

                    int sideIndex = i / 2;

                    if (sideCount <= 1 && (!isOdd || ingredients.size() == 1)) {
                        sideIndex = 1;
                        ySize = 40;
                    }

                    yPos = centerY - 40 + (sideIndex * ySize);
                } else {
                    xPos = centerX + 65;

                    int ySize = 80 / Math.max(sideCount - (isOdd ? 0 : 1), 1);

                    int sideIndex = i / 2;

                    if (isOdd) {
                        sideCount++;
                    }

                    if (sideCount <= 1) {
                        sideIndex = 1;
                        ySize = 40;
                    }

                    yPos = centerY - 40 + (sideIndex * ySize);
                }

                if (GuiHelper.isInRect(xPos - 9, yPos - 9, 18, 18, mouseX, mouseY)){
                    ItemStack stack = OreDictHelper.resolveObject(ingredients.get(i));
                    if (stack != null) {
                        renderToolTip(stack, mouseX, mouseY);
                    }
                }
            }

            if (GuiHelper.isInRect(centerX - 8, guiTop + 70, 18, 18, mouseX, mouseY)){
                ItemStack stack = currentRecipe.getRecipeOutput(tile.getStackInCore(0));
                if (stack != null) {
                    renderToolTip(stack, mouseX, mouseY);
                }
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        tile.sendPacketToServer(new PacketTileMessage(tile, (byte) button.id, true, false));
    }

    private void drawItemStack(ItemStack stack, int x, int y, String altText)
    {
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = null;
        if (stack != null) font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = fontRendererObj;
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, altText);
        this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;
    }

    public static class EnergyEffect extends GuiEffect {

        private final double xTarget;
        private final double yTarget;
        private final int type;

        public EnergyEffect(World world, double xCoord, double yCoord, double xTarget, double yTarget, int type) {
            super(world, xCoord, yCoord, 0, 0);
            this.xTarget = xTarget;
            this.yTarget = yTarget;
            this.type = type;
            motionX = motionY = 0;
            particleMaxAge = 12;
            particleScale = 1F;
            particleTextureIndexY = 1;

            if (type == 1){
                particleRed = 0;
                particleGreen = 0.8F;
                particleBlue = 1F;
                particleMaxAge = 21;
            }
        }

        @Override
        public boolean isTransparent() {
            return true;
        }

        @Override
        public void onUpdate() {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;

            if (this.particleAge++ >= this.particleMaxAge || Utils.getDistanceAtoB(new Vec3D(posX, posY, 0), new Vec3D(xTarget, yTarget, 0)) < -10) {
                this.setExpired();
            }

            if (particleMaxAge - particleAge < 10){
                float d = ((particleMaxAge - particleAge) / 10F);
                particleScale = d * 1F;
            }

            if (type == 1 && particleMaxAge - particleAge < 2){
                particleScale = 3F;
            }

            particleTextureIndexX = rand.nextInt(5);

            Vec3D dir = Vec3D.getDirectionVec(new Vec3D(posX, posY, 0), new Vec3D(xTarget, yTarget, 0));
            double speed = type == 0 ? 5D : 3D;
            motionX = dir.x * speed;
            motionY = dir.y * speed;

            this.moveEntity(this.motionX, this.motionY);
        }

        @Override
        public void renderParticle(float partialTicks) {
            ResourceHelperDE.bindTexture("textures/particle/particles.png");

            float minU = (float) this.particleTextureIndexX / 8.0F;
            float maxU = minU + 0.125F;
            float minV = (float) this.particleTextureIndexY / 8.0F;
            float maxV = minV + 0.125F;
            float scale = 8F * this.particleScale;

            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer vertexbuffer = tessellator.getBuffer();
            vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

            float renderX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks);
            float renderY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks);

            vertexbuffer.pos((double) (renderX - 1 * scale), (double) (renderY - 1 * scale), 0D).tex((double) maxU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).endVertex();
            vertexbuffer.pos((double) (renderX - 1 * scale), (double) (renderY + 1 * scale), 0D).tex((double) maxU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).endVertex();
            vertexbuffer.pos((double) (renderX + 1 * scale), (double) (renderY + 1 * scale), 0D).tex((double) minU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).endVertex();
            vertexbuffer.pos((double) (renderX + 1 * scale), (double) (renderY - 1 * scale), 0D).tex((double) minU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).endVertex();

            tessellator.draw();
        }
    }

    public class StartButton extends GuiButton {

        public StartButton(int buttonId, int x, int y, int width, int height, String text) {
            super(buttonId, x, y, width, height, text);

        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (visible) {
                this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

                int back = 0xFF000000;
                GuiHelper.drawColouredRect(xPosition + 1, yPosition + 1, width - 2, height - 2, back);
                //int border = hovered ? 0xFF009999 : 0xFF220033;
                int border = hovered ? 0xFF009999 : 0xFF8800bb;
                GuiHelper.drawColouredRect(xPosition, yPosition, width, 1, border);
                GuiHelper.drawColouredRect(xPosition, yPosition + height - 1, width, 1, border);
                GuiHelper.drawColouredRect(xPosition, yPosition, 1, height, border);
                GuiHelper.drawColouredRect(xPosition + width - 1, yPosition, 1, height, border);

                GuiHelper.drawCenteredString(mc.fontRendererObj, displayString, xPosition + width / 2, yPosition + (height / 2) - (mc.fontRendererObj.FONT_HEIGHT / 2), 0xFFFFFF, false);
            }
        }

        public void drawToolTips(Minecraft mc, int mouseX, int mouseY) {
//            if (hovered && stack != null && stack.getItem() instanceof IConfigurableItem) {
//                renderToolTip(stack, mouseX, mouseY);
//            }
        }

        @Override
        public void playPressSound(SoundHandler soundHandlerIn) {
            super.playPressSound(soundHandlerIn);
        }
    }
}
