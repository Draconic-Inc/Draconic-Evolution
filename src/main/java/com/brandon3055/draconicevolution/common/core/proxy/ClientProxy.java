package com.brandon3055.draconicevolution.common.core.proxy;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.handler.ToolHudHandler;
import com.brandon3055.draconicevolution.client.keybinding.KeyBindings;
import com.brandon3055.draconicevolution.client.keybinding.KeyInputHandler;
import com.brandon3055.draconicevolution.client.render.block.RenderDraconiumChest;
import com.brandon3055.draconicevolution.client.render.block.RenderEnergyInfuser;
import com.brandon3055.draconicevolution.client.render.block.RenderParticleGen;
import com.brandon3055.draconicevolution.client.render.block.RenderTeleporterStand;
import com.brandon3055.draconicevolution.client.render.entity.RenderDragon;
import com.brandon3055.draconicevolution.client.render.item.RenderBow;
import com.brandon3055.draconicevolution.client.render.item.RenderMobSoul;
import com.brandon3055.draconicevolution.client.render.tile.*;
import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.entity.EntityCustomDragon;
import com.brandon3055.draconicevolution.common.items.ModItems;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.tileentities.*;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyPylon;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyStorageCore;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

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
		FMLCommonHandler.instance().bus().register(new KeyInputHandler());
		FMLCommonHandler.instance().bus().register(new ClientEventHandler());
		MinecraftForge.EVENT_BUS.register(new ToolHudHandler());
		KeyBindings.init();
		registerRenderIDs();
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
		//Item Renderers
		MinecraftForgeClient.registerItemRenderer(ModItems.wyvernBow, new RenderBow());
		MinecraftForgeClient.registerItemRenderer(ModItems.draconicBow, new RenderBow());
		MinecraftForgeClient.registerItemRenderer(ModItems.mobSoul, new RenderMobSoul());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.draconiumChest), new RenderDraconiumChest());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.particleGenerator), new RenderParticleGen());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.energyInfuser), new RenderEnergyInfuser());

		//ISimpleBlockRendering
		RenderingRegistry.registerBlockHandler(new RenderTeleporterStand());

		//TileEntitySpecialRenderers
		ClientRegistry.bindTileEntitySpecialRenderer(TileParticleGenerator.class, new RenderTileParticleGen());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyInfuser.class, new RenderTileEnergyInfiser());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCustomSpawner.class, new RenderTileCustomSpawner());
		ClientRegistry.bindTileEntitySpecialRenderer(TileTestBlock.class, new RenderTileTestBlock());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyStorageCore.class, new RenderTileEnergyStorageCore());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyPylon.class, new RenderTileEnergyPylon());
		ClientRegistry.bindTileEntitySpecialRenderer(TilePlacedItem.class, new RenderTilePlacedItem());
		ClientRegistry.bindTileEntitySpecialRenderer(TileDissEnchanter.class, new RenderTileDissEnchanter());
		ClientRegistry.bindTileEntitySpecialRenderer(TileTeleporterStand.class, new RenderTileTeleporterStand());
		ClientRegistry.bindTileEntitySpecialRenderer(TileDraconiumChest.class, new RenderTileDraconiumChest());

		//Entitys
		RenderingRegistry.registerEntityRenderingHandler(EntityCustomDragon.class, new RenderDragon());
	}

	public void registerRenderIDs (){
		References.idTeleporterStand = RenderingRegistry.getNextAvailableRenderId();
	}

}
