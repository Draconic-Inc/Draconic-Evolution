package com.brandon3055.draconicevolution.lib;

import com.brandon3055.brandonscore.utills.LogHelper;
import com.google.common.io.ByteStreams;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by Brandon on 8/02/2015.
 * This class handles images for the information tablet
 */
public class DEImageHandler {
	public static DEImageHandler instance = new DEImageHandler();
	public static Map<String, ManualImageLocation> downloadedImages = new HashMap<String, ManualImageLocation>();


	private static String savePath;
	private static File saveFolder;
	private static File imagesFolder;
	private static DownloadThread downloadThread;
	public static int downloadStatus = 0;


	//-------------------- File Handling -----------------------//

	@SubscribeEvent
	public void tick(TickEvent.ClientTickEvent event)
	{
		if (downloadThread != null && downloadThread.isFinished)
		{
			if (downloadThread.isReloadRequired()) LogHelper.info("Image Download Finished");
			downloadStatus = downloadThread.wasSuccessful ? 1 : 2;
			MinecraftForge.EVENT_BUS.unregister(this);
			addRSPack(event != null);
			downloadThread = null;
		}
	}

	public static void init(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(instance);

		if (event != null) savePath = event.getModConfigurationDirectory().getParentFile().getAbsolutePath() + "/config/draconicevolution";
//		GUIManual.loadPages();

//		downloadThread = new DownloadThread(GUIManual.imageURLs);
//		downloadThread.start();
	}


	public static class DownloadThread extends Thread
	{
		private List<String> imageURLs;
		private boolean isFinished = false;
		private boolean wasSuccessful = true;
		private boolean reloadRequired = false;

		public DownloadThread(List<String> imageURLs)
		{
			this.imageURLs = new ArrayList<String>(imageURLs);
		}

		@Override
		public void run() {

			for (String s : imageURLs)
			{
				if (!checkExistence(s)) if (downloadImage(s)) reloadRequired = true;
				if (checkExistence(s))
				{
					try
					{
						URL url = new URL(s);
						String fileName = url.getFile();

						BufferedImage bi = ImageIO.read(new File(getImagesFolder(), FilenameUtils.getName(fileName)));
						downloadedImages.put(FilenameUtils.getName(fileName), new ManualImageLocation(FilenameUtils.getName(fileName), bi.getWidth(), bi.getHeight()));
					}
					catch (MalformedURLException e)
					{
						LogHelper.error("Image Read Failed");
						e.printStackTrace();
					}
					catch (IOException e)
					{
						LogHelper.error("Image Read Failed");
						e.printStackTrace();
					}
				}
			}

			isFinished = true;
		}

		private static boolean downloadImage(String urlString)
		{
			try {
				URL url = new URL(urlString);
				String fileName = url.getFile();

				LogHelper.info("Downloading Image " + FilenameUtils.getName(fileName));

				File dll = new File(getImagesFolder(), FilenameUtils.getName(fileName));

				InputStream is = url.openStream();
				OutputStream os = new FileOutputStream(dll);

				ByteStreams.copy(is, os);

				is.close();
				os.close();

			}catch (IOException e){
				LogHelper.error("Download Failed");
				e.printStackTrace();
				return false;
			}
			return true;
		}

		private static boolean checkExistence(String urlS)
		{
			try
			{
				URL url = new URL(urlS);
				String fileName = url.getFile();
				return Arrays.asList(getImagesFolder().list()).contains(FilenameUtils.getName(fileName));
			}
			catch (MalformedURLException e)
			{
				LogHelper.error("Unable to check files existence. Invalid URL: " + urlS);
				e.printStackTrace();
				return false;
			}
		}

		public boolean isFinished() {
			return isFinished;
		}

		public boolean wasSuccessful() {
			return wasSuccessful;
		}

		public boolean isReloadRequired() {
			return reloadRequired;
		}
	}


	private static void addRSPack(boolean refreash)
	{
		File rspack = new File(getConfigFolder(), "/resources");
		if (!rspack.exists()) return;

		if (!Arrays.asList(rspack.list()).contains("pack.mcmeta"))
		{
			try
			{
				JsonWriter writer = new JsonWriter(new FileWriter(new File(rspack, "pack.mcmeta")));
				writer.beginObject();
				writer.name("pack");
				writer.beginObject();
				writer.name("pack_format").value(1);
				writer.name("description").value("Draconic Evolution GUI Images");
				writer.endObject();
				writer.endObject();
				writer.close();
			}
			catch (IOException e)
			{
				LogHelper.error("Error creating pack.mcmeta");
				e.printStackTrace();
			}
		}

		Field f = ReflectionHelper.findField(Minecraft.class, "defaultResourcePacks", "field_110449_ao");
		f.setAccessible(true);
		try {
			List defaultResourcePacks = (List)f.get(Minecraft.getMinecraft());
			defaultResourcePacks.add(new FolderResourcePack(rspack));

			f.set(Minecraft.getMinecraft(), defaultResourcePacks);
			LogHelper.info("RS Added");
			if (refreash) Minecraft.getMinecraft().refreshResources();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static File getConfigFolder()
	{
		if (saveFolder == null) { saveFolder = new File(savePath); }
		if (!saveFolder.exists()) saveFolder.mkdir();

		return saveFolder;
	}

	public static File getImagesFolder()
	{
		if (imagesFolder == null) { imagesFolder = new File(getConfigFolder(), "/resources/assets/draconicevolution/textures/gui/manualimages"); }
		if (!imagesFolder.exists()) imagesFolder.mkdirs();

		return imagesFolder;
	}

	//----------------------------------------------------------//
}
