package com.brandon3055.draconicevolution.client.handler;

import com.brandon3055.draconicevolution.client.gui.componentguis.GUIManual;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Brandon on 8/02/2015.
 */
public class ResourceHandler {
	private static ResourceLocation defaultParticles;
	private static ResourceLocation particles = new ResourceLocation(References.RESOURCESPREFIX + "textures/particle/particles.png");
	private static Map<String, ResourceLocation> cachedResources = new HashMap<String, ResourceLocation>();

	private static String savePath;
	private static File saveFolder;


	//-------------------- File Handling -----------------------//
	public static void init(FMLPreInitializationEvent event)
	{
		savePath = event.getModConfigurationDirectory().getParentFile().getAbsolutePath() + "/config/draconicevolution";
		GUIManual.loadPages();

		addRSPack();
	}

	public static File getConfigFolder()
	{
		if (saveFolder == null) { saveFolder = new File(savePath); }
		if (!saveFolder.exists()) saveFolder.mkdir();

		return saveFolder;
	}


	private static void downloadImages()
	{

	}

	private static boolean checkExistence(String url)
	{

		return true;
	}

	private static void addRSPack()
	{
		File rspack = new File(getConfigFolder(), "/resources");
		if (!rspack.exists()) return;
		List defaultResourcePacks = Lists.newArrayList();
		Field f = ReflectionHelper.findField(Minecraft.class, "defaultResourcePacks", "field_110449_ao");
		f.setAccessible(true);
		try {
			defaultResourcePacks = (List)f.get(Minecraft.getMinecraft());
			defaultResourcePacks.add(new FolderResourcePack(rspack));
			for (Object o : defaultResourcePacks){
				if (o instanceof FolderResourcePack) LogHelper.info(((FolderResourcePack) o).getPackName());
				if (o instanceof FileResourcePack) LogHelper.info(((FileResourcePack)o).getPackName());
			}

			f.set(Minecraft.getMinecraft(), defaultResourcePacks);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}





	//----------------------------------------------------------//









	public static void bindTexture(ResourceLocation texture)
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
	}

	/**Binds the vanilla particle sheet*/
	public static void bindDefaultParticles()
	{
		if (defaultParticles == null)
		{
			try
			{
				defaultParticles = (ResourceLocation) ReflectionHelper.getPrivateValue(EffectRenderer.class, null, "particleTextures", "field_110737_b");
			}
			catch (Exception e) {}
		}
		if (defaultParticles != null) bindTexture(defaultParticles);
	}

	public static void bindParticles()
	{
		bindTexture(particles);
	}

	public static ResourceLocation getResource(String rs)
	{
		if (!cachedResources.containsKey(rs)) cachedResources.put(rs, new ResourceLocation(References.RESOURCESPREFIX + rs));
		return cachedResources.get(rs);
	}

	public static void bindResource(String rs)
	{
		bindTexture(ResourceHandler.getResource(rs));
	}


}
