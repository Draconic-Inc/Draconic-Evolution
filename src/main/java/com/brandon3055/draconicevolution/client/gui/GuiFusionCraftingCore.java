package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.effects.GuiEffectRenderer;
import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.baseelements.GuiButton;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiLabel;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiSlotRender;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiStackIcon;
import com.brandon3055.brandonscore.client.gui.modulargui.guielements.GuiTexture;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.GuiAlign;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.TBasicMachine;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingCore;
import com.brandon3055.draconicevolution.inventory.ContainerFusionCraftingCore;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.Random;

import static com.brandon3055.brandonscore.client.gui.GuiToolkit.LayoutPos.BOTTOM_CENTER;

public class GuiFusionCraftingCore extends ModularGuiContainer<ContainerFusionCraftingCore> {

    private final PlayerEntity player;
    private final TileCraftingCore tile;
    private IFusionRecipe currentRecipe = null;
    //    private IFusionRecipeOld lastRecipe = null;
    private String canCraft = "";
    private Button startCrafting;
    private GuiEffectRenderer guiEffectRenderer = new GuiEffectRenderer();
    private Random rand = new Random();
    private int[] boltStats = {0, 0, 0, 0, 0, 0};
    protected GuiToolkit<GuiFusionCraftingCore> toolkit = new GuiToolkit<>(this, 180, 200);
    private GuiStackIcon stackIcon;

    public GuiFusionCraftingCore(ContainerFusionCraftingCore container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.player = inv.player;
        this.tile = container.tile;
        this.imageWidth = 180;
        this.imageHeight = 200;
    }

    @Override
    public void addElements(GuiElementManager manager) {
        TBasicMachine template = new TBasicMachine(this, tile);

        template.background = GuiTexture.newDynamicTexture(this.xSize(), this.ySize(), () -> BCSprites.getThemed("background_dynamic"));
        template.background.onReload((guiTex) -> guiTex.setPos(this.guiLeft(), this.guiTop()));
        toolkit.loadTemplate(template);

        template.background.addChild(new GuiSlotRender().setRelPos(81, 25));
        template.background.addChild(new GuiSlotRender().setRelPos(81, 69));
        template.background.addChild(stackIcon = new GuiStackIcon(null).setRelPos(81, 47));

        template.background.addChild(new GuiButton("Craft")
                .setPosAndSize(width / 2 - 40, topPos + 93, 80, 14)
                .setVanillaButtonRender(true)
                .setEnabledCallback(() -> currentRecipe != null && !tile.isCrafting())
                .onPressed(() -> tile.sendPacketToServer(output -> {}, 0)));

        template.background.addChild(new GuiLabel()
                .setPosAndSize(width / 2 - 40, topPos + 93, 80, 14)
                .setAlignment(GuiAlign.CENTER)
                .setEnabledCallback(() -> currentRecipe != null && tile.isCrafting())
                .setDisplaySupplier(() -> {
                    int state = 0;//tile.craftingStage.get();
                    String status = state > 1000 ? "Crafting" : "Charging";
                    double d = state > 1000 ? (state - 1000F) / 1000D : state / 1000D;
                    String progress = ((int) (d * 100) + "%");
                    return status + ": " + progress;
                }));

        GuiLabel wip = new GuiLabel("WIP: Fusion crafting will be completely overhauled before final release.")
                .setSize(xSize() + 20, 20)
                .setWrap(true)
                .setTextColour(TextFormatting.RED);
        template.background.addChild(wip);
        toolkit.placeOutside(wip, template.background, BOTTOM_CENTER, 0, 0);
    }

    @Override
    public void tick() {
        super.tick();

        currentRecipe = tile.getLevel().getRecipeManager().getRecipeFor(DraconicAPI.FUSION_RECIPE_TYPE, tile, tile.getLevel()).orElse(null);
        if (currentRecipe == null) {
            stackIcon.setStack(ItemStack.EMPTY);
        } else {
            stackIcon.setStack(currentRecipe.getResultItem());
        }

    }

    //
//    @Override
//    public void initGui() {
//        super.initGui();
//        buttonList.clear();
//        buttonList.add(startCrafting = new StartButton(0, width / 2 - 40, guiTop + 93, 80, 14, I18n.format("gui.de.button.start")));
//        startCrafting.visible = false;
//        initRecipeComponents();
//    }
//
//    public void initRecipeComponents() {
//        manager.removeByGroup("RECIPE_ELEMENTS");
//        if (currentRecipe == null) {
//            return;
//        }
//
//        List ingredients = currentRecipe.getRecipeIngredients();
//        int nColumns = ingredients.size() > 16 ? 4 : 2;             //The number of ingredient columns.
//        LinkedList<MGuiList> iColumns = new LinkedList<>();         //The list of ingredient columns.
//
//        for (int i = 0; i < nColumns; i++) {
//            int x = (nColumns == 2 ? 15 + i * 130 : 6 + ((i % 2) * 19) + ((i / 2) * 129));
//            MGuiList list = new MGuiList(this, guiLeft() + x, guiTop() + 8, 20, 98).setScrollingEnabled(false);
//            list.addChild(new MGuiBorderedRect(this, list.xPos, list.yPos - 1, list.xSize, list.ySize + 2).setBorderColour(0xFFAA00FF).setFillColour(0));
//            list.topPadding = list.bottomPadding = 0;
//            iColumns.add((MGuiList) addElement(list));
//        }
//
//        int i = 0;
//        for (Object ingredient : ingredients) {
//            ItemStack ingredStack = OreDictHelper.resolveObject(ingredient);
//            MGuiList column = iColumns.get(iColumns.size() == 4 ? i % 4 : i % 2);
//            column.addEntry(new MGuiListEntryWrapper(this, new MGuiStackIcon(this, 0, 0, 16, 16, new StackReference(ingredStack)).setDrawHoverHighlight(true)));
//            column.sortEvenSpacing(true);
//            i++;
//        }
//
//        manager.initElements();
//    }
//
//    private MGuiElementBase addElement(MGuiElementBase elementBase) {
//        return manager.add(elementBase.addToGroup("RECIPE_ELEMENTS"));
//    }
//
//    @Override
//    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
//        GuiHelper.drawGuiBaseBackground(this, guiLeft, guiTop, xSize, ySize);
//
//        //region Draw Fancy Background
//
//        //Back
//        GuiHelper.drawColouredRect(guiLeft + 3, guiTop + 3, xSize - 6, 110, 0xFF00FFFF);
//        GuiHelper.drawColouredRect(guiLeft + 4, guiTop + 4, xSize - 8, 108, 0xFF000000);
//
//        if (currentRecipe != null && canCraft != null && canCraft.equals("true")) {
//            //Items
//            GuiHelper.drawColouredRect(guiLeft + (xSize / 2) - 10, guiTop + 24, 20, 64, 0xFF00FFFF);
//            GuiHelper.drawColouredRect(guiLeft + (xSize / 2) - 9, guiTop + 25, 18, 62, 0xFF000000);
//        }
//
//        //endregion
//
//        drawCenteredString(fontRenderer, I18n.format("gui.de.fusionCraftingCore.name"), guiLeft + (xSize / 2), guiTop + 5, InfoHelper.GUI_TITLE);
//        RenderSystem.color(1F, 1F, 1F, 1F);
//
//        ResourceHelperDE.bindTexture(DETextures.GUI_FUSION_CRAFTING);
//        //drawTexturedModalRect(guiLeft + (xSize / 2) - 8, guiTop + 45, 0, 0, 15, 21);
//        GuiHelper.drawPlayerSlots(this, guiLeft + (xSize / 2), guiTop + 115, true);
//        if (currentRecipe == null || canCraft == null || !canCraft.equals("true")) {
//            drawTexturedModalRect(guiLeft + (xSize / 2) - 9, guiTop + 25, 138, 0, 18, 18);
//            if (tile.getStackInSlot(1) != null) {
//                drawTexturedModalRect(guiLeft + (xSize / 2) - 9, guiTop + 69, 138, 0, 18, 18);
//            }
//        }
//
//        if (currentRecipe != null) {
//            GuiHelper.drawStack2D(currentRecipe.getRecipeOutput(tile.getStackInCore(0)), mc, guiLeft + (xSize / 2) - 8, guiTop + 70, 16F);
//
//            //region Draw EnergyFX
//
//            if (tile.isCrafting.get() && tile.craftingStage.get() > 0) {
//
//                RenderSystem.depthMask(false);
//                double charge = tile.craftingStage.get() / 1000D;
//                if (charge > 1) {
//                    charge = 1;
//                }
//
//                int size = (int) ((1D - charge) * 98);
//
//                RenderEnergyBolt.renderBoltBetween(new Vec3D(guiLeft + 16 + boltStats[0], guiTop + 106, 0), new Vec3D(guiLeft + 16 + boltStats[1], guiTop + 8 + size, 0), 1, charge * 10, 10, boltStats[2], true);
//                RenderEnergyBolt.renderBoltBetween(new Vec3D(guiLeft + 16 + boltStats[3], guiTop + 106, 0), new Vec3D(guiLeft + 16 + boltStats[4], guiTop + 8 + size, 0), 1, charge * 10, 10, boltStats[5], true);
//
//                RenderEnergyBolt.renderBoltBetween(new Vec3D(guiLeft + xSize - 34 + boltStats[0], guiTop + 106, 0), new Vec3D(guiLeft + xSize - 34 + boltStats[1], guiTop + 8 + size, 0), 1, charge * 10, 10, boltStats[2], true);
//                RenderEnergyBolt.renderBoltBetween(new Vec3D(guiLeft + xSize - 34 + boltStats[3], guiTop + 106, 0), new Vec3D(guiLeft + xSize - 34 + boltStats[4], guiTop + 8 + size, 0), 1, charge * 10, 10, boltStats[5], true);
//                RenderSystem.depthMask(true);
//            }
//
//            //endregion
//
//            //Draw Progress
//            if (tile.isCrafting.get() && tile.craftingStage.get() >= 0) {
//                int state = tile.craftingStage.get();
//                String status = state > 1000 ? I18n.format("gui.fusionCrafting.crafting.info") : I18n.format("gui.fusionCrafting.charging.info");
//                double d = state > 1000 ? (state - 1000F) / 1000D : state / 1000D;
//                String progress = ((int) (d * 100) + "%");
//                if (state < 1000 && isShiftKeyDown()) {
//                    long totalCharge = 0;
//
//                    for (ICraftingInjector pedestal : tile.getInjectors()) {
//                        if (pedestal.getStackInPedestal().isEmpty()) {
//                            continue;
//                        }
//                        totalCharge += pedestal.getInjectorCharge();
//                    }
//
//                    long averageCharge = totalCharge / currentRecipe.getRecipeIngredients().size();
//                    double percentage = averageCharge / (double) currentRecipe.getIngredientEnergyCost();
//                    progress = (((int) (percentage * 100000D)) / 1000D) + "%";
//                }
//                drawCenteredString(fontRenderer, status + ": " + TextFormatting.GOLD + progress, width / 2, guiTop + 95, state < 1000 ? 0x00FF00 : 0x00FFFF);
//            }
//        }
//        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
//    }
//
//    @Override
//    public void updateScreen() {
//        super.updateScreen();
//        boolean hasNoRecipe = currentRecipe == null;
//        currentRecipe = RecipeManager.FUSION_REGISTRY.findRecipe(tile, player.world, tile.getPos());
//        if (currentRecipe != null) {
//            canCraft = currentRecipe.canCraft(tile, player.world, tile.getPos());
//            if (hasNoRecipe || currentRecipe != lastRecipe) {
//                lastRecipe = currentRecipe;
//                initRecipeComponents();
//            }
//        }
//        else if (!hasNoRecipe) {
//            manager.removeByGroup("RECIPE_ELEMENTS");
//        }
//
//        startCrafting.enabled = startCrafting.visible = currentRecipe != null && canCraft != null && canCraft.equals("true") && !tile.craftingInProgress();
//
//        //region Spawn Particles
//
//        if (currentRecipe != null && canCraft != null && canCraft.equals("true")) {
//            int centerX = guiLeft + xSize / 2;
//            int centerY = guiTop + ySize / 2 - 45;
//
//            for (MGuiElementBase element : manager.getElements()) {
//                if (element instanceof MGuiList) {
//                    for (MGuiElementBase item : ((MGuiList) element).listEntries) {
//                        if (rand.nextInt(10) == 0) {
//                            double xPos = item.xPos + (rand.nextDouble() * 16);
//                            double yPos = item.yPos + (rand.nextDouble() * 16);
//                            double ty = centerY + (-20 + (rand.nextDouble() * 40));
//                            guiEffectRenderer.addEffect(new EnergyEffect(player.world, xPos, yPos, centerX, ty, 0));
//                        }
//                    }
//                }
//            }
//
//            if (tile.craftingStage.get() > 1000) {
//                double xPos = centerX - 8 + (rand.nextDouble() * 16);
//                double yTop = guiTop + 35 - 8 + (rand.nextDouble() * 16);
//                guiEffectRenderer.addEffect(new EnergyEffect(player.world, xPos, yTop, centerX, guiTop + 78, 1));
//            }
//            guiEffectRenderer.updateEffects();
//        }
//        else {
//            guiEffectRenderer.clearEffects();
//        }
//
//        //endregion
//
//        //region Update Bolt Stats
//
//        boltStats[0] = (int) (rand.nextDouble() * 18);
//        boltStats[1] = (int) (rand.nextDouble() * 18);
//        boltStats[2] = rand.nextInt();
//        boltStats[3] = (int) (rand.nextDouble() * 18);
//        boltStats[4] = (int) (rand.nextDouble() * 18);
//        boltStats[5] = rand.nextInt();
//
//        //endregion
//    }
//
//    @Override
//    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
//        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
//
//        if (currentRecipe != null) {
//            if (canCraft != null && !canCraft.equals("true")) {
//                if (canCraft.equals("tierLow")) {
//                    GuiHelper.drawCenteredString(fontRenderer, I18n.format("gui.fusionCrafting.tierLow.info"), (xSize / 2), 95, 0xAA00FF, false);
//                }
//                else if (canCraft.equals("outputObstructed")) {
//                    GuiHelper.drawCenteredString(fontRenderer, I18n.format("gui.fusionCrafting.outputObstructed.info"), (xSize / 2), 95, 0xAA00FF, false);
//                }
//                else {
//                    RenderSystem.translate(0, 0, 600);
//                    GuiHelper.drawColouredRect(5, 88, xSize - 10, 20, 0xFFFF0000);
//                    GuiHelper.drawColouredRect(6, 89, xSize - 12, 18, 0xFF000000);
//                    GuiHelper.drawCenteredSplitString(fontRenderer, I18n.format(canCraft), (xSize / 2), 90, xSize - 10, 0xAA00FF, false);
//                    RenderSystem.translate(0, 0, -600);
//                }
//            }
//        }
//        else if (ModHelper.isJEIInstalled) {
//            GuiHelper.drawBorderedRect(81, 45, 18, 22, 1, 0xFF101010, 0xFF303030);
//            fontRenderer.drawString("R", 87, 52, 0xA0A0A0, false);
//        }
//    }
//
//    @Override
//    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        super.drawScreen(mouseX, mouseY, partialTicks);
//        guiEffectRenderer.renderEffects(partialTicks);
//    }
//
//    @Override
//    protected void actionPerformed(GuiButton button) throws IOException {
//        tile.sendPacketToServer(output -> {
//        }, button.id);
//    }
//
//    public static class EnergyEffect extends GuiEffect {
//
//        private final double xTarget;
//        private final double yTarget;
//        private final int type;
//
//        public EnergyEffect(World world, double xCoord, double yCoord, double xTarget, double yTarget, int type) {
//            super(world, xCoord, yCoord, 0, 0);
//            this.xTarget = xTarget;
//            this.yTarget = yTarget;
//            this.type = type;
//            motionX = motionY = 0;
//            particleMaxAge = 12;
//            particleScale = 1F;
//            particleTextureIndexY = 1;
//
//            if (type == 1) {
//                particleRed = 0;
//                particleGreen = 0.8F;
//                particleBlue = 1F;
//                particleMaxAge = 21;
//            }
//        }
//
//        @Override
//        public boolean isTransparent() {
//            return true;
//        }
//
//        @Override
//        public void onUpdate() {
//            this.prevPosX = this.posX;
//            this.prevPosY = this.posY;
//
//            if (this.particleAge++ >= this.particleMaxAge || Utils.getDistanceAtoB(new Vec3D(posX, posY, 0), new Vec3D(xTarget, yTarget, 0)) < -10) {
//                this.setExpired();
//            }
//
//            if (particleMaxAge - particleAge < 10) {
//                float d = ((particleMaxAge - particleAge) / 10F);
//                particleScale = d * 1F;
//            }
//
//            if (type == 1 && particleMaxAge - particleAge < 2) {
//                particleScale = 3F;
//            }
//
//            particleTextureIndexX = rand.nextInt(5);
//
//            Vec3D dir = Vec3D.getDirectionVec(new Vec3D(posX, posY, 0), new Vec3D(xTarget, yTarget, 0));
//            double speed = type == 0 ? 5D : 3D;
//            motionX = dir.x * speed;
//            motionY = dir.y * speed;
//
//            this.moveEntity(this.motionX, this.motionY);
//        }
//
//        @Override
//        public void renderParticle(float partialTicks) {
//            ResourceHelperDE.bindTexture("textures/particle/particles.png");
//
//            float minU = (float) this.particleTextureIndexX / 8.0F;
//            float maxU = minU + 0.125F;
//            float minV = (float) this.particleTextureIndexY / 8.0F;
//            float maxV = minV + 0.125F;
//            float scale = 8F * this.particleScale;
//
//            Tessellator tessellator = Tessellator.getInstance();
//            BufferBuilder vertexbuffer = tessellator.getBuffer();
//            vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
//
//            float renderX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks);
//            float renderY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks);
//
//            vertexbuffer.pos((double) (renderX - 1 * scale), (double) (renderY - 1 * scale), 0D).tex((double) maxU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).endVertex();
//            vertexbuffer.pos((double) (renderX - 1 * scale), (double) (renderY + 1 * scale), 0D).tex((double) maxU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).endVertex();
//            vertexbuffer.pos((double) (renderX + 1 * scale), (double) (renderY + 1 * scale), 0D).tex((double) minU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).endVertex();
//            vertexbuffer.pos((double) (renderX + 1 * scale), (double) (renderY - 1 * scale), 0D).tex((double) minU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).endVertex();
//
//            tessellator.draw();
//        }
//    }
//
//    public class StartButton extends GuiButton {
//
//        public StartButton(int buttonId, int x, int y, int width, int height, String text) {
//            super(buttonId, x, y, width, height, text);
//
//        }
//
//        @Override
//        public void drawButton(Minecraft mc, int mouseX, int mouseY, float pt) {
//            if (visible) {
//                this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
//
//                int back = 0xFF000000;
//                GuiHelper.drawColouredRect(x + 1, y + 1, width - 2, height - 2, back);
//                //int border = hovered ? 0xFF009999 : 0xFF220033;
//                int border = hovered ? 0xFF009999 : 0xFF8800bb;
//                GuiHelper.drawColouredRect(x, y, width, 1, border);
//                GuiHelper.drawColouredRect(x, y + height - 1, width, 1, border);
//                GuiHelper.drawColouredRect(x, y, 1, height, border);
//                GuiHelper.drawColouredRect(x + width - 1, y, 1, height, border);
//
//                GuiHelper.drawCenteredString(mc.fontRenderer, displayString, x + width / 2, y + (height / 2) - (mc.fontRenderer.FONT_HEIGHT / 2), 0xFFFFFF, false);
//            }
//        }
//
//        public void drawToolTips(Minecraft mc, int mouseX, int mouseY) {
////            if (hovered && stack != null && stack.getItem() instanceof IConfigurableItem) {
////                renderToolTip(stack, mouseX, mouseY);
////            }
//        }
//
//        @Override
//        public void playPressSound(SoundHandler soundHandlerIn) {
//            super.playPressSound(soundHandlerIn);
//        }
//    }
}
