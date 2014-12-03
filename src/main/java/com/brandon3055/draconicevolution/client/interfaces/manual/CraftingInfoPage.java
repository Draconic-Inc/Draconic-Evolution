package com.brandon3055.draconicevolution.client.interfaces.manual;

import com.brandon3055.draconicevolution.common.utills.LogHelper;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Brandon on 18/09/2014.
 */
public class CraftingInfoPage extends TitledPage {
	protected static RenderItem itemRenderer = new RenderItem();
	public ItemStack result;
	private boolean isSmelting = false;
	private ItemStack[] recipe = new ItemStack[9];
	private boolean hasRecipe = false;
	private String rawDescription;
	private List<String> formattedDescription;
	private float descriptionScale = 0.66f;

	public CraftingInfoPage(String name, PageCollection collection, ItemStack itemStack, String unlocalizedDescription) {
		super(name, true, collection, itemStack.getUnlocalizedName()+".name", 0x00ff00);
		this.result = itemStack;
		this.recipe = getFirstRecipeForItem(itemStack);
		for (ItemStack stack : recipe) if (stack != null) hasRecipe = true;
		if (unlocalizedDescription == "") rawDescription = ttl(itemStack.getUnlocalizedName()+".description");
		else rawDescription = ttl(unlocalizedDescription);
	}

	@Override
	public void renderBackgroundLayer(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.renderBackgroundLayer(minecraft, offsetX, offsetY, mouseX, mouseY);
		GL11.glPushMatrix();
		if (isSmelting){
			drawTexturedModalRect(offsetX + 87, offsetY + 15, 116, 202, 82, 54);
		}else {
			if (hasRecipe) drawTexturedModalRect(offsetX + 70, offsetY + 15, 0, 202, 116, 54);
			else {
				drawTexturedModalRect(offsetX + 119, offsetY + 17, 0, 202, 18, 18);
				drawString(fontRendererObj, "No Crafting Recipe", offsetX + 145, offsetY + 17, 0xff0000);
			}
		}
		GL11.glPopMatrix();

	}

	public void drawScreen(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.drawScreen(minecraft, offsetX, offsetY, mouseX, mouseY);
		int relativeMouseX = mouseX + offsetX;
		int relativeMouseY = mouseY + offsetY;
		int gridOffsetX = isSmelting ? 88 : 71;
		int gridOffsetY = 16;
		int itemBoxSize = 18;
		addDescription(minecraft, offsetX, offsetY);

		ItemStack tooltip = null;
		int i = 0;
		for (ItemStack input : recipe) {
			if (input != null) {
				int row = (i % 3);
				int column = i / 3;
				int itemX = offsetX + gridOffsetX + (row * itemBoxSize);
				int itemY = offsetY + gridOffsetY + (column * itemBoxSize);
				drawItemStack(input, itemX, itemY, "");
				if (relativeMouseX > itemX - 2 && relativeMouseX < itemX - 2 + itemBoxSize &&
						relativeMouseY > itemY - 2 && relativeMouseY < itemY - 2 + itemBoxSize) {
					tooltip = input;
				}
			}
			i++;
		}
		int itemX = offsetX + (isSmelting ? 148 : 165);
		int itemY = offsetY + 34;
		if (!hasRecipe){
			itemX = offsetX+120;
			itemY = offsetY+18;
		}

		drawItemStack(result, itemX, itemY, "");
		if (relativeMouseX > itemX - 2 && relativeMouseX < itemX - 2 + itemBoxSize &&
				relativeMouseY > itemY - 2 && relativeMouseY < itemY - 2 + itemBoxSize) {
			tooltip = result;
		}
		if (tooltip != null) {
			drawItemStackTooltip(tooltip, relativeMouseX, relativeMouseY);
		}
	}

	public void addDescription(Minecraft minecraft, int offsetX, int offsetY){
		GL11.glPushMatrix();
		if (hasRecipe) GL11.glTranslated(offsetX+5, offsetY+75, 1);
		else GL11.glTranslated(offsetX+5, offsetY+40, 1);
		GL11.glScalef(descriptionScale, descriptionScale, descriptionScale);
		int offset = 0;
		for (String s : getFormattedText(fontRendererObj)) {
			if (s == null) break;
			if (s.substring(0,2).equals("\\%")){
				s = s.substring(2);
				offset += fontRendererObj.FONT_HEIGHT/2;
			}
			fontRendererObj.drawString(s, 0, offset, 0x000000);
			offset += fontRendererObj.FONT_HEIGHT;
		}
		GL11.glPopMatrix();
	}


	@SuppressWarnings("unchecked")
	public List<String> getFormattedText(FontRenderer fr) {
	if (formattedDescription == null) {
		formattedDescription = new ArrayList<String>();

		if (Strings.isNullOrEmpty(rawDescription)) {
			formattedDescription = ImmutableList.of();
			return formattedDescription;
		}
		if (!rawDescription.contains("\\n")) {
			formattedDescription = ImmutableList.copyOf(fr.listFormattedStringToWidth(rawDescription, 370));
			return formattedDescription;
		}

		List<String> segments = new ArrayList(); //Each separate string that is separated by a \n
		String raw = rawDescription;


		int escape = 0;
		while (raw.contains("\\n")) {
			segments.add(raw.substring(0, raw.indexOf("\\n")));
			raw = raw.substring(raw.indexOf("\\n") + 2);
			if (!raw.contains("\\n")) segments.add(raw);

			escape++;
			if (escape > 100) {
				LogHelper.error("Bailing Out!");
				break;
			}
		}

		for (String s : segments)
			formattedDescription.addAll(ImmutableList.copyOf(fr.listFormattedStringToWidth(s, 370)));
	}
	return formattedDescription;
}

	protected void drawItemStackTooltip(ItemStack stack, int x, int y) {
		final Minecraft mc = Minecraft.getMinecraft();
		FontRenderer font = Objects.firstNonNull(stack.getItem().getFontRenderer(stack), mc.fontRenderer);

		@SuppressWarnings("unchecked")
		List<String> list = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);

		List<String> colored = Lists.newArrayListWithCapacity(list.size());
		colored.add(stack.getRarity().rarityColor + list.get(0));
		for (String line : list)
			colored.add(EnumChatFormatting.GRAY + line);

		if (colored.size() >= 2) colored.remove(1);
		drawHoveringText(colored, x, y, font);
	}

	private void drawItemStack(ItemStack par1ItemStack, int par2, int par3, String par4Str) {
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		this.zLevel = 200.0F;
		itemRenderer.zLevel = 200.0F;
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glColor3f(1f, 1f, 1f);
		GL11.glEnable(GL11.GL_NORMALIZE);
		FontRenderer font = null;
		if (par1ItemStack != null) font = par1ItemStack.getItem().getFontRenderer(par1ItemStack);
		if (font == null) font = Minecraft.getMinecraft().fontRenderer;
		itemRenderer.renderItemAndEffectIntoGUI(font, Minecraft.getMinecraft().getTextureManager(), par1ItemStack, par2, par3);
		itemRenderer.renderItemOverlayIntoGUI(font, Minecraft.getMinecraft().getTextureManager(), par1ItemStack, par2, par3, par4Str);
		this.zLevel = 0.0F;
		itemRenderer.zLevel = 0.0F;
	}

	@SuppressWarnings("unchecked")
	private ItemStack[] getFirstRecipeForItem(ItemStack resultingItem) {
		ItemStack[] recipeItems = new ItemStack[9];
		for (IRecipe recipe : (List<IRecipe>) CraftingManager.getInstance().getRecipeList()) {
			if (recipe == null) continue;

			ItemStack result = recipe.getRecipeOutput();
			if (result == null || !result.isItemEqual(resultingItem)) continue;

			Object[] input = getRecipeInput(recipe);
			if (input == null) continue;

			for (int i = 0; i < input.length; i++)
				recipeItems[i] = convertToStack(input[i]);
			break;

		}

		Iterator iterator = FurnaceRecipes.smelting().getSmeltingList().entrySet().iterator();
		Map.Entry entry;

		while (iterator.hasNext()){
			entry = (Map.Entry)iterator.next();
			if (entry.getKey() instanceof ItemStack && ((ItemStack)entry.getValue()).isItemEqual(result)){
				isSmelting = true;
				recipeItems[0] = (ItemStack)entry.getKey();
			}
		}

		return recipeItems;
	}

	protected ItemStack convertToStack(Object obj) {
		ItemStack entry = null;
		if (obj instanceof ItemStack) {
			entry = (ItemStack)obj;
		} else if (obj instanceof List) {
			@SuppressWarnings("unchecked")
			List<ItemStack> list = (List<ItemStack>)obj;
			if (list.size() > 0) entry = list.get(0);
		}

		if (entry == null) return null;
		entry = entry.copy();
		if (entry.getItemDamage() == OreDictionary.WILDCARD_VALUE) entry.setItemDamage(0);
		return entry;
	}

	@SuppressWarnings("unchecked")
	private Object[] getRecipeInput(IRecipe recipe) {
		if (recipe instanceof ShapelessOreRecipe) return ((ShapelessOreRecipe)recipe).getInput().toArray();
		else if (recipe instanceof ShapedOreRecipe) return getShapedOreRecipe((ShapedOreRecipe)recipe);
		else if (recipe instanceof ShapedRecipes) return ((ShapedRecipes)recipe).recipeItems;
		else if (recipe instanceof ShapelessRecipes) return ((ShapelessRecipes)recipe).recipeItems.toArray(new ItemStack[0]);
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
