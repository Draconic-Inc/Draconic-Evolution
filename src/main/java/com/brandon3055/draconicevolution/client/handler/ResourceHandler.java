package com.brandon3055.draconicevolution.client.handler;

import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.util.HashMap;
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
		savePath = event.getModConfigurationDirectory().getParentFile().getAbsolutePath() + "/mods/draconicevolution";
	}

	public static File getSaveFolder()
	{
		if (saveFolder == null) { saveFolder = new File(savePath); }
		if (!saveFolder.exists()) saveFolder.mkdir();

		return saveFolder;
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
