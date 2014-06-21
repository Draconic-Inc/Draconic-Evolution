package com.brandon3055.draconicevolution.common.core.proxy;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import com.brandon3055.draconicevolution.client.render.BowRenderer;
import com.brandon3055.draconicevolution.client.render.ItemParticleGenRenderer;
import com.brandon3055.draconicevolution.client.render.ParticleGenRenderer;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.tileentities.TileParticleGenerator;

public class ClientProxy extends CommonProxy {
	private final static boolean debug = DraconicEvolution.debug;
	
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{if(debug)
		System.out.println("on Client side");
		super.preInit(event);
		
		//Client Only
		registerRendering();
	}

	@Override
	public void init(FMLInitializationEvent event)
	{if(debug)
		System.out.println("on Client side");
		super.init(event);

		//Client Only
	}

	@Override
	public void postInit(FMLPostInitializationEvent event)
	{if(debug)
		System.out.println("on Client side");
		super.postInit(event);
		
		//Client Only
	}

	public void registerRendering()
	{
		MinecraftForgeClient.registerItemRenderer(ModItems.wyvernBow, new BowRenderer());
		MinecraftForgeClient.registerItemRenderer(ModItems.draconicBow, new BowRenderer());
		
		TileEntitySpecialRenderer render = new ParticleGenRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileParticleGenerator.class, render);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.particleGenerator), new ItemParticleGenRenderer(render, new TileParticleGenerator()));
        
	}
}
