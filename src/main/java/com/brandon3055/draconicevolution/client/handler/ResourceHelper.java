package com.brandon3055.draconicevolution.client.handler;

import com.brandon3055.draconicevolution.common.lib.References;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Brandon on 8/02/2015.
 */
public class ResourceHelper {
	private static ResourceLocation defaultParticles;
	private static ResourceLocation particles = new ResourceLocation(References.RESOURCESPREFIX + "textures/particle/particles.png");

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
}
