package com.brandon3055.draconicevolution.client.gui.guicomponents;

import com.brandon3055.brandonscore.client.gui.guicomponents.ComponentScrollingBase;
import com.brandon3055.brandonscore.client.gui.guicomponents.GUIScrollingBase;
import com.brandon3055.brandonscore.client.utills.GuiHelper;
import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.brandon3055.brandonscore.common.utills.Utills;
import com.brandon3055.draconicevolution.client.gui.componentguis.ManualPage;
import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.client.utill.CustomResourceLocation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.commons.io.FilenameUtils;
import org.lwjgl.opengl.GL11;

/**
 * Created by Brandon on 19/03/2015.
 */
public class ComponentManualPage extends ComponentScrollingBase {
    public ManualPage page;
    public List<ContentComponent> contentList = new ArrayList<ContentComponent>();
    private int pageLength = 0;
    ItemStack stack = null;
    private int slideTick = 0;

    public int scrollOffset = 0;
    public int scrollLimit = 0;

    public ComponentManualPage(int x, int y, GUIScrollingBase gui, ManualPage page) {
        super(x, y, gui);
        this.page = page;
        pageLength = 25;
        stack = Utills.getStackFromName(page.name, page.meta);
        ContentComponent title = new ContentComponent("title." + page.getLocalizedName(), pageLength, this);
        pageLength += title.getHeight();
        contentList.add(title);
        for (String s : page.content) {
            ContentComponent c = new ContentComponent(s, pageLength, this);
            pageLength += c.getHeight();
            contentList.add(c);
        }
        pageLength += 25;

        scrollLimit = pageLength - 325;
    }

    @Override
    public void handleScrollInput(int direction) {
        //	scrollOffset += direction * 10;
        scrollOffset += direction * (InfoHelper.isShiftKeyDown() ? 30 : 10);
        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > pageLength - getHeight()) scrollOffset = pageLength - getHeight();
        if (pageLength <= getHeight()) scrollOffset = 0;
    }

    @Override
    public int getWidth() {
        return gui.getXSize();
    }

    @Override
    public int getHeight() {
        return gui.getYSize();
    }

    @Override
    public void renderBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {

        for (ContentComponent c : contentList) {
            c.render(mouseX, mouseY);
        }
    }

    @Override
    public void renderFinal(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {}

    @Override
    public void renderForground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {}

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (!InfoHelper.isShiftKeyDown()) slideTick++;
    }

    private static class ContentComponent {

        private String content;
        /**
         * 0 = text, 1 = image, 2 = crafting
         */
        private int type;

        public int yPos;
        private ComponentManualPage page;
        private String[] textLines;
        private List<IRecipe> recipes = new ArrayList<IRecipe>();
        private List<ItemStack> smeltingRecipes = new ArrayList<ItemStack>();
        private ItemStack result;
        private boolean isTitle = false;
        private boolean slide = false;
        private List<String> slides = new ArrayList<String>();

        public ContentComponent(String content, int y, ComponentManualPage page) {
            this.content = content;
            this.yPos = y;
            this.page = page;
            init();
        }

        public void init() {
            isTitle = content != null && content.contains("title.");

            type = content.contains(".png") ? 1 : content.contains("[c]") ? 2 : 0;

            slide = (type == 1) && content.contains("[>]");
            if (slide) {
                String s = content;
                while (s.contains("[>]")) {
                    slides.add(s.substring(0, s.indexOf("[>]")));
                    s = s.substring(s.indexOf("[>]") + 3);
                }
                slides.add(s);
            }

            if (type == 0) {
                List<String> l = page.fontRendererObj.listFormattedStringToWidth(content, 220);
                List<String> sl = new ArrayList<String>();
                for (String st : l) {
                    String s = st;
                    while (s.contains("\\n")) {
                        String s2 = s.substring(0, s.indexOf("\\n"));
                        s = s.substring(s.indexOf("\\n") + 2);
                        sl.add(s2);
                    }
                    sl.add(s);
                }
                textLines = sl.toArray(new String[sl.size()]);
            } else if (type == 2) {
                String s = content.substring(content.indexOf("[c]") + 3);
                String name = s.substring(0, s.lastIndexOf(":"));
                ItemStack stack = Utills.getStackFromName(name, Integer.parseInt(s.substring(s.lastIndexOf(":") + 1)));
                result = stack;

                if (stack != null) {

                    for (IRecipe recipe :
                            (List<IRecipe>) CraftingManager.getInstance().getRecipeList()) {
                        if (recipe == null) continue;

                        ItemStack result = recipe.getRecipeOutput();
                        if (result == null || !result.isItemEqual(stack)) continue;

                        Object[] input = getRecipeInput(recipe);
                        if (input == null) continue;

                        recipes.add(recipe);
                    }

                    Iterator iterator = FurnaceRecipes.smelting()
                            .getSmeltingList()
                            .entrySet()
                            .iterator();
                    Map.Entry entry;

                    while (iterator.hasNext()) {
                        entry = (Map.Entry) iterator.next();
                        if (entry.getKey() instanceof ItemStack && ((ItemStack) entry.getValue()).isItemEqual(stack)) {
                            smeltingRecipes.add((ItemStack) entry.getKey());
                        }
                    }
                }
            } else textLines = null;
        }

        public int getHeight() {
            if (type == 1
                    && (ResourceHandler.downloadedImages.containsKey(FilenameUtils.getName(content))
                            || (slide
                                    && ResourceHandler.downloadedImages.containsKey(FilenameUtils.getName(content))))) {
                CustomResourceLocation texture = slide
                        ? ResourceHandler.downloadedImages.get(FilenameUtils.getName(content))
                        : ResourceHandler.downloadedImages.get(FilenameUtils.getName(content));
                return (int) ((double) texture.getHeight() / (double) texture.getWidth() * 220D) + 5;
            } else if (type == 2) {
                return (recipes.size() + smeltingRecipes.size()) * 57;
            }

            return ((textLines != null ? textLines.length * 10 : 10) + 5)
                    + ((isTitle && (page.page.name.contains("tile.") || page.page.name.contains("item."))) ? 18 : 0);
        }

        public void render(int mouseX, int mouseY) {
            if (type == 1) {
                renderImage();
                return;
            } else if (type == 2) {
                renderCrafting(mouseX, mouseY);
                return;
            } else if (isTitle) {
                int y = yPos - page.scrollOffset;
                if (y > 310 || y < 5) return;
                page.drawCenteredString(
                        page.fontRendererObj, content.substring(content.indexOf("title.") + 6), 128, y, 0x00ffff);

                if ((page.page.name.contains("tile.") || page.page.name.contains("item.")) && page.stack != null) {
                    ResourceHandler.bindResource("textures/gui/Widgets.png");
                    GL11.glColor4f(0.5F, 0.5F, 0.5F, 1F);
                    page.drawTexturedModalRect(119, y + 11, 138, 0, 18, 18);
                    page.drawItemStack(page.stack, 120, 12 + y, "");
                }
            } else if (textLines != null) {
                for (int i = 0; i < textLines.length; i++) {
                    int y = (yPos + i * 10) - page.scrollOffset;
                    if (y > 310 || y < 5) continue;
                    page.fontRendererObj.drawString(textLines[i], 20, y, 0x000000);
                }
            }
        }

        private void renderImage() {
            CustomResourceLocation image =
                    null; // slide ? ResourceHandler.downloadedImages.get(slides.get(page.slideTick % slides.size() -
            // 1)) : ResourceHandler.downloadedImages.get(content);
            if (slide) {
                int index = (page.slideTick / 20) % (slides.size());
                image = (slides.size() > index)
                        ? ResourceHandler.downloadedImages.get(FilenameUtils.getName(slides.get(index)))
                        : null;
            } else if (ResourceHandler.downloadedImages.containsKey(FilenameUtils.getName(content))) {
                image = ResourceHandler.downloadedImages.get(FilenameUtils.getName(content));
            }

            if (image != null) {
                // CustomResourceLocation image = ResourceHandler.downloadedImages.get(content);
                image.bind();
                Tessellator tess = Tessellator.instance;

                double ySize = (double) image.getHeight() / (double) image.getWidth() * 220D;

                double topS = 0;
                if ((yPos - page.scrollOffset) < 0) {
                    topS = (yPos - 5 - page.scrollOffset);
                }
                double btmS = 0;
                if ((yPos - page.scrollOffset) + ySize > 310) {
                    btmS = (yPos - 5 - page.scrollOffset) - 310 + ySize;
                }

                double xmin = 17.5D;
                double xmax = 17.5D + 220D;
                double ymin = yPos - page.scrollOffset - topS;
                double ymax = yPos + ySize - page.scrollOffset - btmS;

                double vmin = Math.max(0D, topS / -ySize);
                double vmax = Math.min(1D, 1D - btmS / ySize);

                tess.startDrawingQuads();
                tess.setColorRGBA_F(1f, 1f, 1f, 1f);

                tess.addVertexWithUV(xmin, ymin, 0, 0, vmin);
                tess.addVertexWithUV(xmin, ymax, 0, 0, vmax);
                tess.addVertexWithUV(xmax, ymax, 0, 1, vmax);
                tess.addVertexWithUV(xmax, ymin, 0, 1, vmin);

                tess.draw();

                if (slide && (int) ymax - 10 < 305 && (int) ymax - 10 > 0 && !InfoHelper.isShiftKeyDown())
                    page.fontRendererObj.drawString(page.ttl("info.de.shiftToPause.txt"), 20, (int) ymax - 8, 0xff0000);
            }
        }

        private void renderCrafting(int mouseX, int mouseY) {
            if (result == null) return;
            ResourceHandler.bindResource("textures/gui/Widgets.png");
            GL11.glColor4f(1f, 1f, 1f, 1f);
            int posX = 70;
            int posY = yPos - page.scrollOffset;

            for (IRecipe recipe : recipes) {
                int index = recipes.indexOf(recipe);

                int yDown = index * 57;
                for (int i = 0; i < 9; i++) {
                    int x = i % 3 * 18;
                    int y = i / 3 * 18;

                    if (y + yDown + posY < 0 || y + yDown + posY > 305) continue;
                    page.drawTexturedModalRect(x + posX, y + yDown + posY, 138, 0, 18, 18);
                    ItemStack stack = (ItemStack)
                            (getRecipeInput(recipe).length > i && getRecipeInput(recipe)[i] instanceof ItemStack
                                    ? getRecipeInput(recipe)[i]
                                    : getRecipeInput(recipe).length > i
                                                    && getRecipeInput(recipe)[i] instanceof ArrayList
                                            ? ((ArrayList) getRecipeInput(recipe)[i]).get(0)
                                            : null);

                    if (stack != null) {
                        if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) stack.setItemDamage(0);
                        page.drawItemStack(stack, 1 + x + posX, 1 + y + yDown + posY, "");
                    }
                }

                if (14 + yDown + posY > 0 && 14 + yDown + posY < 300)
                    page.drawTexturedModalRect(90 + posX, 14 + yDown + posY, 156, 0, 26, 26);
                if (19 + yDown + posY > 0 && 19 + yDown + posY < 305) {
                    page.drawTexturedModalRect(61 + posX, 19 + yDown + posY, 204, 0, 22, 15);
                    if (recipe.getRecipeOutput() != null)
                        page.drawItemStack(recipe.getRecipeOutput(), 5 + 90 + posX, 5 + 14 + yDown + posY, "");
                }
            }

            for (IRecipe recipe : recipes) {
                int index = recipes.indexOf(recipe);

                int yDown = index * 57;

                if (19 + yDown + posY > 0 && 19 + yDown + posY < 305) {
                    if (GuiHelper.isInRect(5 + 90 + posX, 5 + 14 + yDown + posY, 18, 18, mouseX, mouseY)) {
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                        if (recipe.getRecipeOutput() != null)
                            page.renderToolTip(recipe.getRecipeOutput(), mouseX, mouseY);
                        ResourceHandler.bindResource("textures/gui/Widgets.png");
                        GL11.glColor4f(1f, 1f, 1f, 1f);
                        GL11.glPopAttrib();
                    }
                }

                for (int i = 0; i < 9; i++) {
                    int x = i % 3 * 18;
                    int y = i / 3 * 18;

                    if (y + yDown + posY < 0 || y + yDown + posY > 305) continue;
                    ItemStack stack = (ItemStack)
                            (getRecipeInput(recipe).length > i && getRecipeInput(recipe)[i] instanceof ItemStack
                                    ? getRecipeInput(recipe)[i]
                                    : getRecipeInput(recipe).length > i
                                                    && getRecipeInput(recipe)[i] instanceof ArrayList
                                            ? ((ArrayList) getRecipeInput(recipe)[i]).get(0)
                                            : null);

                    if (stack != null) {
                        if (GuiHelper.isInRect(1 + x + posX, 1 + y + yDown + posY, 18, 18, mouseX, mouseY)) {
                            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                            page.renderToolTip(stack, mouseX, mouseY);
                            ResourceHandler.bindResource("textures/gui/Widgets.png");
                            GL11.glColor4f(1f, 1f, 1f, 1f);
                            GL11.glPopAttrib();
                        }
                    }
                }
            }

            for (ItemStack recipe : smeltingRecipes) {
                int index = smeltingRecipes.indexOf(recipe);
                int yDown = (recipes.size() + index) * 57;

                if (yDown + posY > 0 && yDown + posY < 305) {
                    page.drawTexturedModalRect(posX + 18, yDown + posY, 138, 0, 18, 18);
                    page.drawItemStack(recipe, posX + 19, yDown + posY + 1, "");
                }
                if (21 + yDown + posY > 0 && 21 + yDown + posY < 305)
                    page.drawTexturedModalRect(posX + 18, yDown + posY + 21, 238, 0, 18, 15);
                if (36 + yDown + posY > 0 && 36 + yDown + posY < 305)
                    page.drawTexturedModalRect(posX + 18, yDown + posY + 36, 238, 15, 18, 33);
                if (14 + yDown + posY > 0 && 14 + yDown + posY < 300) {
                    page.drawTexturedModalRect(90 + posX, 14 + yDown + posY, 156, 0, 26, 26);
                    page.drawItemStack(result, 95 + posX, 19 + yDown + posY, "");
                }
                if (19 + yDown + posY > 0 && 19 + yDown + posY < 305)
                    page.drawTexturedModalRect(50 + posX, 19 + yDown + posY, 204, 0, 22, 15);

                if (yDown + posY > 0 && yDown + posY < 305) {
                    if (GuiHelper.isInRect(posX + 19, yDown + posY + 1, 18, 18, mouseX, mouseY)) {
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                        page.renderToolTip(recipe, mouseX, mouseY);
                        ResourceHandler.bindResource("textures/gui/Widgets.png");
                        GL11.glColor4f(1f, 1f, 1f, 1f);
                        GL11.glPopAttrib();
                    }
                }

                if (14 + yDown + posY > 0 && 14 + yDown + posY < 305) {
                    if (GuiHelper.isInRect(95 + posX, 19 + yDown + posY, 18, 18, mouseX, mouseY)) {
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                        page.renderToolTip(result, mouseX, mouseY);
                        ResourceHandler.bindResource("textures/gui/Widgets.png");
                        GL11.glColor4f(1f, 1f, 1f, 1f);
                        GL11.glPopAttrib();
                    }
                }
            }
        }

        private Object[] getRecipeInput(IRecipe recipe) {
            if (recipe instanceof ShapelessOreRecipe)
                return ((ShapelessOreRecipe) recipe).getInput().toArray();
            else if (recipe instanceof ShapedOreRecipe) return getShapedOreRecipe((ShapedOreRecipe) recipe);
            else if (recipe instanceof ShapedRecipes) return ((ShapedRecipes) recipe).recipeItems;
            else if (recipe instanceof ShapelessRecipes)
                return ((ShapelessRecipes) recipe).recipeItems.toArray(new ItemStack[0]);
            return null;
        }

        private Object[] getShapedOreRecipe(ShapedOreRecipe recipe) {
            try {
                Field field = ShapedOreRecipe.class.getDeclaredField("width");
                if (field != null) {
                    field.setAccessible(true);
                    int width = field.getInt(recipe);
                    Object[] input = recipe.getInput();
                    Object[] grid = new Object[9];
                    for (int i = 0, offset = 0, y = 0; y < 3; y++) {
                        for (int x = 0; x < 3; x++, i++) {
                            if (x < width && offset < input.length) {
                                grid[i] = input[offset];
                                offset++;
                            } else {
                                grid[i] = null;
                            }
                        }
                    }
                    return grid;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
