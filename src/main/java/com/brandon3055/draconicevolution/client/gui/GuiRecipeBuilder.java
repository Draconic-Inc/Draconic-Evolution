package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.gui.guicomponents.ColourRectButton;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.draconicevolution.integration.ModHelper;
import com.brandon3055.draconicevolution.inventory.ContainerRecipeBuilder;
import com.brandon3055.draconicevolution.lib.RecipeManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.*;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 21/07/2016.
 */
public class GuiRecipeBuilder extends GuiContainer {
    private ContainerRecipeBuilder container;
    private GuiTextField textField;
    private int recipeType = 0;
    private GuiButton genShaped;
    private GuiButton genShapeless;
    private GuiButton genOre;
    private GuiButton genFusion;
    private boolean ore = true;

    public GuiRecipeBuilder(EntityPlayer player) {
        super(new ContainerRecipeBuilder(player));
        this.container = (ContainerRecipeBuilder) this.inventorySlots;
        container.arangeCraftingSlots(0);
        xSize = 200;
        ySize = 230;
    }

    //region Init

    @Override
    public void initGui() {
        super.initGui();
        textField = new GuiTextField(0, fontRendererObj, 1, guiTop + 119, guiLeft + xSize, 12);
        textField.setEnableBackgroundDrawing(false);
        textField.setTextColor(0x00FF00);
        textField.setMaxStringLength(1000);
        buttonList.clear();
        buttonList.add(new ColourRectButton(0, "Crafting", guiLeft + 18, guiTop + 130, 50, 12, 0xFF440000, 0xFF000066, 0xFF006600));
        buttonList.add(new ColourRectButton(1, "Fusion", guiLeft + 70, guiTop + 130, 50, 12, 0xFF440000, 0xFF000066, 0xFF006600));


        buttonList.add(new ColourRectButton(9, "Copy", guiLeft + xSize - 31, guiTop + 130, 29, 12, 0xFF440000, 0xFF000066, 0xFF006600));
        buttonList.add(genShaped = new ColourRectButton(10, "Shaped", guiLeft + 18, guiTop + 100, 42, 12, 0xFF440000, 0xFF000066, 0xFF006600));
        buttonList.add(genShapeless = new ColourRectButton(11, "Shapeless", guiLeft + 61, guiTop + 100, 56, 12, 0xFF440000, 0xFF000066, 0xFF006600));
        buttonList.add(genOre = new ColourRectButton(12, "Ore: true", guiLeft + 118, guiTop + 100, 56, 12, 0xFF440000, 0xFF000066, 0xFF006600));
        buttonList.add(genFusion = new ColourRectButton(13, "Gen Fusion", guiLeft + 18, guiTop + 100, 70, 12, 0xFF440000, 0xFF000066, 0xFF006600));
        buttonList.add(new ColourRectButton(100, "Reload Recipes", guiLeft - 90 , guiTop + ySize - 12, 90, 12, 0xFF440000, 0xFF000066, 0xFF006600));
        genFusion.visible = false;
    }

    //endregion

    //region Rendering

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GuiHelper.drawBorderedRect(guiLeft, guiTop, xSize, ySize, 1, 0xFF000000, 0xFFFF0000);
        GuiHelper.drawBorderedRect(0, guiTop + 117, guiLeft + xSize, 12, 1, 0xFF111111, 0xFFFFFFFF);

        int posX = 20;
        int posY = 145;
        GuiHelper.drawBorderedRect(guiLeft + posX - 2, guiTop + posY - 2, 164, 78, 1, 0xFF000000, 0xFF00FFFF);


        for (int x = 0; x < 9; x++) {
            GuiHelper.drawBorderedRect(guiLeft + posX + 18 * x - 1, guiTop + posY + 57, 18, 18, 1, 0xFFAAAAAA, 0xFF444444);
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                GuiHelper.drawBorderedRect(guiLeft + posX + 18 * x - 1, guiTop + posY + y * 18 - 1, 18, 18, 1, 0xFFAAAAAA, 0xFF444444);
            }
        }


        if (recipeType == 0) {
            posY = 30;
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    GuiHelper.drawBorderedRect(guiLeft + posX + 18 * x - 1, guiTop + posY + y * 18 - 1, 18, 18, 1, 0xFFAAAAAA, 0xFF444444);
                }
            }
            GuiHelper.drawBorderedRect(guiLeft + 70 + 36, guiTop + posY + 18 - 1, 18, 18, 1, 0xFFAAAAAA, 0xFF444444);
        } else {
            GuiHelper.drawBorderedRect(guiLeft + 89 - 18, guiTop + 19, 18, 18, 1, 0xFFAAAAAA, 0xFF444444);
            GuiHelper.drawBorderedRect(guiLeft + 89 + 18, guiTop + 19, 18, 18, 1, 0xFFAAAAAA, 0xFF444444);

            for (int i = 1; i < 10; i++) {
                GuiHelper.drawBorderedRect(guiLeft + 19 + (i - 1) * 18, guiTop + 49, 18, 18, 1, 0xFFAAAAAA, 0xFF444444);
                GuiHelper.drawBorderedRect(guiLeft + 19 + (i - 1) * 18, guiTop + 67, 18, 18, 1, 0xFFAAAAAA, 0xFF444444);
            }
        }

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        textField.drawTextBox();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.666, 0.666, 0.666);
        fontRendererObj.drawSplitString(textField.getText(), 5, 5, (int) ((guiLeft + xSize - 10) * 1.5D), 0xFFFFFF);
        GlStateManager.popMatrix();
    }

    //endregion

    //region UserInput

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id <= 1) {
            container.arangeCraftingSlots(recipeType = button.id);
            if (button.id == 0) {
                genShaped.visible = genShapeless.visible = true;
                genFusion.visible = false;
            } else {
                genShaped.visible = genShapeless.visible = false;
                genFusion.visible = true;
            }
        }
        else if (button.id == 2) {
            container.inventoryCache.clear();
        }
        else if (button == genOre) {
            ore = !ore;
            genOre.displayString = "Ore: " + ore;
        }
        else if (button == genShaped) {
            buildShaped(ore);
        }
        else if (button == genShapeless) {
            buildShapeless(ore);
        }
        else if (button == genFusion) {
            buildFusion(ore);
        }
        else if (button.id == 9) {
            try {
                StringSelection stringselection = new StringSelection(textField.getText());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringselection, (ClipboardOwner) null);
            }
            catch (Exception var2) {
                ;
            }
        }
        else if (button.id == 100) {
            RecipeManager.initialize();
            ModHelper.reloadJEI();
        }

//        Recipes take a back seat...
//        Get placable items up and running so i can display stuff in the booth! (Even if its just a quick fix and not the upgrade i planned)

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        textField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!this.textField.textboxKeyTyped(typedChar, keyCode)) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    //endregion

    //region Build

    private void buildShaped(boolean ore) {
        ItemStack output = container.inventoryCache.getStackInSlot(0);

        if (output == null) {
            textField.setText("No Output Detected");
            return;
        }

        String recipeString = "";

        if (output.stackSize > 1 || output.getItemDamage() > 0) {
            if (output.getItemDamage() > 0) {
                recipeString += String.format("new ItemStack(%s, %s, %s)", findItemString(output, false), output.stackSize, output.getItemDamage());
            } else {
                recipeString += String.format("new ItemStack(%s, %s)", findItemString(output, false), output.stackSize);
            }
        } else {
            recipeString += findItemString(output, false);
        }

        Map<String, String> charMap = new LinkedHashMap<String, String>();
        Map<String, ItemStack> stackMap = new HashMap<String, ItemStack>();
        char lastChar = 'A';

        recipeString += ", \"";
        for (int i = 1; i < 10; i++) {
            ItemStack stack = container.inventoryCache.getStackInSlot(i);

            if (stack == null) {
                recipeString += " ";
            } else {
                if (!charMap.containsKey(stack.toString())) {
                    charMap.put(stack.toString(), String.valueOf(lastChar));
                    stackMap.put(stack.toString(), stack);
                    lastChar++;
                }
                recipeString += charMap.get(stack.toString());
            }

            if (i % 3 == 0) {
                recipeString += i == 9 ? "\", " : "\", \"";
            }
        }

        for (String stackString : charMap.keySet()) {
            String charString = charMap.get(stackString);
            ItemStack stack = stackMap.get(stackString);

            recipeString += String.format("'%s', ", charString);

            if (stack.getItemDamage() > 0) {
                recipeString += String.format("new ItemStack(%s, 1, %s), ", findItemString(stack, false), stack.getItemDamage());
            } else {
                recipeString += findItemString(stack, ore) + ", ";
            }
        }

        recipeString = recipeString.substring(0, recipeString.lastIndexOf(","));

        textField.setText(recipeString);
    }

    private void buildShapeless(boolean ore) {
        ItemStack output = container.inventoryCache.getStackInSlot(0);

        if (output == null) {
            textField.setText("No Output Detected");
            return;
        }

        String recipeString = "";

        if (output.stackSize > 1 || output.getItemDamage() > 0) {
            if (output.getItemDamage() > 0) {
                recipeString += String.format("new ItemStack(%s, %s, %s)", findItemString(output, false), output.stackSize, output.getItemDamage());
            } else {
                recipeString += String.format("new ItemStack(%s, %s)", findItemString(output, false), output.stackSize);
            }
        } else {
            recipeString += findItemString(output, false);
        }

        recipeString += ", ";

        for (int i = 1; i < 10; i++) {
            ItemStack stack = container.inventoryCache.getStackInSlot(i);

            if (stack == null) {
                continue;
            }

            if (stack.getItemDamage() > 0) {
                recipeString += String.format("new ItemStack(%s, 1, %s), ", findItemString(stack, false), stack.getItemDamage());
            } else {
                recipeString += findItemString(stack, ore) + ", ";
            }
        }

        recipeString = recipeString.substring(0, recipeString.lastIndexOf(","));
        textField.setText(recipeString);
    }

    private void buildFusion(boolean ore) {
        ItemStack input = container.inventoryCache.getStackInSlot(0);
        ItemStack output = container.inventoryCache.getStackInSlot(1);

        if (output == null || input == null) {
            textField.setText("No Output and or Input Detected");
            return;
        }

        String recipeString = "";

        if (output.stackSize > 1 || output.getItemDamage() > 0) {
            if (output.getItemDamage() > 0) {
                recipeString += String.format("new ItemStack(%s, %s, %s)", findItemString(output, false), output.stackSize, output.getItemDamage());
            } else {
                recipeString += String.format("new ItemStack(%s, %s)", findItemString(output, false), output.stackSize);
            }
        }
        else {
            recipeString += String.format("new ItemStack(%s)", findItemString(output, false));
        }

        recipeString += ", ";
        if (input.stackSize > 1 || input.getItemDamage() > 0) {
            if (input.getItemDamage() > 0) {
                recipeString += String.format("new ItemStack(%s, %s, %s)", findItemString(input, false), input.stackSize, input.getItemDamage());
            } else {
                recipeString += String.format("new ItemStack(%s, %s)", findItemString(input, false), input.stackSize);
            }
        }
        else {
            recipeString += String.format("new ItemStack(%s)", findItemString(input, false));
        }

        recipeString += ", [RF Cost], [Tier], ";

        for (int i = 2; i < container.inventoryCache.getSizeInventory(); i++) {
            ItemStack stack = container.inventoryCache.getStackInSlot(i);

            if (stack == null) {
                continue;
            }

            if (stack.getItemDamage() > 0) {
                recipeString += String.format("new ItemStack(%s, 1, %s), ", findItemString(stack, false), stack.getItemDamage());
            } else {
                recipeString += findItemString(stack, ore) + ", ";
            }
        }

        recipeString = recipeString.substring(0, recipeString.lastIndexOf(","));
        textField.setText(recipeString);
    }

    //FusionRecipeRegistry.addRecipe(new SimpleFusionRecipe(new ItemStack(Items.STONE_AXE), new ItemStack(Items.WOODEN_AXE), 1000, 0, new ItemStack(DEFeatures.draconicCore)));

    private static String findItemString(ItemStack stack, boolean findOre) {
        if (findOre && OreDictionary.getOreIDs(stack).length > 0) {
            return "\"" + OreDictionary.getOreName(OreDictionary.getOreIDs(stack)[0]) + "\"";
        }

        Item item = stack.getItem();

        if (item.getRegistryName().getResourceDomain().equals("minecraft")) {
            //return (item instanceof ItemBlock ? "Blocks." : "Items.") + item.getRegistryName().getResourcePath().toUpperCase();
            return item.getRegistryName().getResourcePath().toUpperCase();
        }
        if (item.getRegistryName().getResourceDomain().equals("draconicevolution")) {
            //return "DEFeatures." + item.getRegistryName().getResourcePath();
            return item.getRegistryName().getResourcePath();
        }

        return "Unknown";
    }

    //endregion
}
