package com.brandon3055.draconicevolution.client.gui.componentguis;

import com.brandon3055.draconicevolution.client.gui.guicomponents.*;
import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.container.DummyContainer;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.brandonscore.common.utills.InfoHelper;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brandon on 6/03/2015.
 */
public class GUIManual extends GUIScrollingBase {

	private static List<ManualPage> pageList = new ArrayList<ManualPage>();
	public static List<String> imageURLs = new ArrayList<String>();
	private static ManualPage currentPage = null;

	public GUIManual() {
		super(new DummyContainer(), 255, 325);

		if (currentPage != null)
		{
			collection.addComponent(new ComponentManualPage(0, 0, this, currentPage)).setGroup(GR_PAGE);
			collection.addComponent(new ComponentButton(102, 314, 50, 12, 1, this, StatCollector.translateToLocal("button.de.back.txt"))).setGroup(GR_PAGE);
			collection.setOnlyGroupEnabled(GR_BACKGROUND);
			collection.setGroupEnabled(GR_PAGE, true);
		}
	}

	@Override
	public void initGui() {
		super.initGui();

	}

	public static final String GR_BACKGROUND = "BACKGROUND";
	public static final String GR_INTRO = "INTRO";
	public static final String GR_INDEX = "INDEX";
	public static final String GR_PAGE = "PAGE";

	@Override
	protected ComponentCollection assembleComponents() {
		collection = new ComponentCollection(0, 0, xSize, ySize, this);

		collection.addComponent(new ComponentTexturedRect(0, 0, 255, 255, ResourceHandler.getResource("textures/gui/manualTop.png"))).setGroup(GR_BACKGROUND);
		collection.addComponent(new ComponentTexturedRect(0, 255, 255, 69, ResourceHandler.getResource("textures/gui/manualBottom.png"))).setGroup(GR_BACKGROUND);

		collection.addComponent(new ComponentTexturedRect(7, 100, 0, 0, 255, 255, ResourceHandler.getResource("textures/gui/images/debanner.png"), true)).setGroup(GR_INTRO).setName("BANNER");

		collection.addComponent(new ComponentButton(75, 260, 100, 20, 0, this, StatCollector.translateToLocal("info.de.manual.indexButton.txt"), StatCollector.translateToLocal("info.de.manual.indexButtonTip.txt"))).setGroup(GR_INTRO);

		for (int i = 0; i < pageList.size(); i++)
		{
			collection.addComponent(new ComponentIndexButton(20, 20 + i * 20, this, pageList.get(i))).setGroup(GR_INDEX);
			pageLength += 20;
		}

		collection.setOnlyGroupEnabled(GR_BACKGROUND);
		collection.setGroupEnabled(GR_INTRO, true);
		return collection;
	}

	@Override
	public void handleScrollInput(int direction) {
		if (currentPage != null) return;
		scrollOffset += direction * (InfoHelper.isShiftKeyDown() ? 30 : 10);
		if (scrollOffset < 0) scrollOffset = 0;
		if (scrollOffset > pageLength - ySize + 40) scrollOffset = pageLength - ySize + 40;
		if (pageLength + 40 <= ySize) scrollOffset = 0;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		super.drawScreen(mouseX, mouseY, par3);
		int posX = (this.width - xSize) / 2;
		int posY = (this.height - ySize) / 2;
		if (collection.getComponent("BANNER") != null && collection.getComponent("BANNER").isEnabled()) fontRendererObj.drawSplitString(StatCollector.translateToLocal("info.de.manual.introTxt.txt"), posX + 20, posY + 190, 150, 0x000000);
		if (collection.getComponent("BANNER") != null && collection.getComponent("BANNER").isEnabled()) {
			GL11.glPushMatrix();
			GL11.glScalef(0.8F, 0.8F, 0.8F);
			fontRendererObj.drawSplitString("I am still in the process of adding documentation to this manual. There is also some fine tuning that i still need to do.", posX + 110, posY + 380, 280, 0xFF0000);
			GL11.glPopMatrix();
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {


		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);

		ResourceHandler.bindResource("textures/gui/manualBottom.png");
		GL11.glColor4f(1f, 1f, 1f, 1f);
		drawTexturedModalRect(0, ySize - 34, 0, 69, 256, 34);
		drawTexturedModalRect(0, 0, 0, 103, 256, 34);

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	public void buttonClicked(int id) {
		super.buttonClicked(id);
		if (id == 0)
		{
			collection.setOnlyGroupEnabled(GR_BACKGROUND);
			collection.setGroupEnabled(GR_INDEX, true);
		}
		else if (id == 1)
		{
			currentPage = null;
			collection.setOnlyGroupEnabled(GR_BACKGROUND);
			collection.setGroupEnabled(GR_INDEX, true);
			collection.schedulRemoval(GR_PAGE);
		}
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		super.keyTyped(par1, par2);
		if (Keyboard.KEY_BACK == par2)
		{
			if (currentPage != null)
			{
				currentPage = null;
				collection.setOnlyGroupEnabled(GR_BACKGROUND);
				collection.setGroupEnabled(GR_INDEX, true);
				collection.schedulRemoval(GR_PAGE);
			}
			else
			{
				collection.setOnlyGroupEnabled(GR_BACKGROUND);
				collection.setGroupEnabled(GR_INTRO, true);
			}
		}
	}

	public static void loadPages()
	{
		try
		{
			File manualJSON = new File(ResourceHandler.getConfigFolder(), "manual.json");

			InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(References.RESOURCESPREFIX + "manual.json")).getInputStream();
			OutputStream os = new FileOutputStream(manualJSON);
			IOUtils.copy(is, os);
			is.close();
			os.close();



			JsonReader reader = new JsonReader(new FileReader(manualJSON));
			List<String> images;
			List<String> content;

			reader.beginArray();

			//Read next page
			while (reader.hasNext())
			{
				String name;
				String nameL = null;
				int meta = 0;
				images = new ArrayList<String>();
				content = new ArrayList<String>();

				reader.beginObject();

				//Read page name
				String s = reader.nextName();
				if (s.equals("name"))
				{
					name = reader.nextString();
				}
				else throw new IOException("Error reading manual.json (invalid name in place of \"name\" [Found:\"" + s + "\"])");

				//Read page images
				s = reader.nextName();
				if (s.equals("nameL"))
				{
					nameL = reader.nextString();
					s = reader.nextName();
				}
				if (s.equals("meta"))
				{
					meta = reader.nextInt();
					s = reader.nextName();
				}

				if (s.equals("images"))
				{
					reader.beginArray();
					while (reader.hasNext())
					{
						String url = reader.nextString();
						images.add(url);
						if (!imageURLs.contains(url)) imageURLs.add(url);
					}
					reader.endArray();
				}
				else throw new IOException("Error reading manual.json (invalid name in place of \"images\" [Found:\"" + s + "\"])");

				//Read page content
				s = reader.nextName();
				if (s.equals("content"))
				{
					reader.beginArray();
					while (reader.hasNext())
					{
						content.add(reader.nextString());
					}
					reader.endArray();
				}
				else throw new IOException("Error reading manual.json (invalid name in place of \"content\" [Found:\"" + s + "\"])");

				reader.endObject();

				if (isValidPage(name))pageList.add(new ManualPage(name, images.toArray(new String[images.size()]), content.toArray(new String[content.size()]), nameL, meta));
			}

			reader.endArray();

		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		if (buttonPressed) return;

		for (ComponentBase c : collection.getComponents())
		{
			if (c instanceof ComponentIndexButton && c.isEnabled() && ((ComponentIndexButton) c).isOnScreen() && c.isMouseOver(x - guiLeft, y - guiTop))
			{
				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
				currentPage = ((ComponentIndexButton) c).getPage();
				collection.addComponent(new ComponentManualPage(0, 0, this, currentPage)).setGroup(GR_PAGE);
				collection.addComponent(new ComponentButton(102, 314, 50, 12, 1, this, StatCollector.translateToLocal("button.de.back.txt"))).setGroup(GR_PAGE);
				collection.setOnlyGroupEnabled(GR_BACKGROUND);
				collection.setGroupEnabled(GR_PAGE, true);
				break;
			}
		}
	}

	private static boolean isValidPage(String name)
	{
		if (name.contains("info.")) return true;
		else if (!ConfigHandler.disabledNamesList.contains(name)) return true;
		return false;
	}
}
