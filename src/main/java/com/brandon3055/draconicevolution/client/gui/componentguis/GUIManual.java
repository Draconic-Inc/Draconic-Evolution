package com.brandon3055.draconicevolution.client.gui.componentguis;

import com.brandon3055.draconicevolution.client.gui.guicomponents.ComponentCollection;
import com.brandon3055.draconicevolution.client.gui.guicomponents.ComponentIndexButton;
import com.brandon3055.draconicevolution.client.gui.guicomponents.ComponentTexturedRect;
import com.brandon3055.draconicevolution.client.gui.guicomponents.GUIScrollingBase;
import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.container.DummyContainer;
import com.google.gson.stream.JsonReader;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brandon on 6/03/2015.
 */
public class GUIManual extends GUIScrollingBase {

	private static List<ManualPage> pageList = new ArrayList<ManualPage>();
	public static List<String> imageURLs = new ArrayList<String>();

	public GUIManual() {
		super(new DummyContainer(), 0, 0);
		this.xSize = 255;
		this.ySize = 325;
	}

	@Override
	public void initGui() {
		super.initGui();

	}

	public static final String GR_BACKGROUND = "BACKGROUND";
	public static final String GR_INTRO = "INTRO";
	public static final String GR_INDEX = "INDEX";
	public static final String GR_4 = "BACKGROUND";

	@Override
	protected ComponentCollection assembleComponents() {
		loadPages();
		collection = new ComponentCollection(0, 0, xSize, ySize, this);

		collection.addComponent(new ComponentTexturedRect(0, 0, 255, 255, ResourceHandler.getResource("textures/gui/manualTop.png"))).setGroup(GR_BACKGROUND);
		collection.addComponent(new ComponentTexturedRect(0, 255, 255, 69, ResourceHandler.getResource("textures/gui/manualBottom.png"))).setGroup(GR_BACKGROUND);

		collection.addComponent(new ComponentTexturedRect(0, 100, 0, 0, 255, 255, ResourceHandler.getResource("textures/gui/manualimages/debanner.png"), true)).setGroup(GR_INTRO);

//		ITextureObject iTextureObject = mc.getTextureManager().getTexture(ResourceHandler.getResource("textures/gui/manualBottom.png"));
//		mc.getResourceManager().getResource(ResourceHandler.getResource("textures/gui/manualBottom.png")).
//		LogHelper.info(iTextureObject);
//		if (iTextureObject instanceof TextureMap)
//		{
//			LogHelper.info(((TextureMap) iTextureObject).getTextureExtry("textures/gui/manualBottom.png"));
//		}

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
		scrollOffset += direction * 10;
		if (scrollOffset < 0) scrollOffset = 0;
		if (scrollOffset > pageLength - ySize + 40) scrollOffset = pageLength - ySize + 40;
		if (pageLength + 40 <= ySize) scrollOffset = 0;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		super.drawScreen(mouseX, mouseY, par3);

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);

		ResourceHandler.bindResource("textures/gui/manualBottom.png");
		GL11.glColor4f(1f, 1f, 1f, 1f);
		drawTexturedModalRect(0, ySize - 34, 0, 69, 256, 34);
		drawTexturedModalRect(0, 0, 0, 103, 256, 34);

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();

	}


	public static void loadPages()
	{
		//LogHelper.info("loading pages");

		try
		{
			pageList.clear();
			File manualJSON = new File(ResourceHandler.getConfigFolder(), "manual.json");
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

				pageList.add(new ManualPage(name, images.toArray(new String[images.size()]), content.toArray(new String[content.size()]), nameL, meta));
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

		//for (ManualPage p : pageList) LogHelper.info(p.getLocalizedName());
	}

}
