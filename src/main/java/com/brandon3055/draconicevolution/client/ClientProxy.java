package com.brandon3055.draconicevolution.client;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.handler.HudHandler;
import com.brandon3055.draconicevolution.client.keybinding.KeyBindings;
import com.brandon3055.draconicevolution.client.keybinding.KeyInputHandler;
import com.brandon3055.draconicevolution.client.render.block.RenderDraconiumChest;
import com.brandon3055.draconicevolution.client.render.block.RenderEnergyInfuser;
import com.brandon3055.draconicevolution.client.render.block.RenderParticleGen;
import com.brandon3055.draconicevolution.client.render.block.RenderTeleporterStand;
import com.brandon3055.draconicevolution.client.render.entity.RenderDragon;
import com.brandon3055.draconicevolution.client.render.entity.RenderDragonHeart;
import com.brandon3055.draconicevolution.client.render.item.RenderBow;
import com.brandon3055.draconicevolution.client.render.item.RenderMobSoul;
import com.brandon3055.draconicevolution.client.render.tile.*;
import com.brandon3055.draconicevolution.common.CommonProxy;
import com.brandon3055.draconicevolution.common.ModBlocks;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityCustomDragon;
import com.brandon3055.draconicevolution.common.entity.EntityDragonHeart;
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
	public static String downloadLocation;
	
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{if(debug)
		System.out.println("on Client side");
		super.preInit(event);



//		downloadLocation = event.getModConfigurationDirectory().getParentFile().getAbsolutePath() + "/mods/derspack";
//		downloadLocation = downloadLocation.replaceAll("\\\\", "/");

//		File rescourcePack = new File(event.getModConfigurationDirectory().getParentFile(), "/mods/derspack");
//
//
//		//File file = new File(downloadLocation);
//		if (!rescourcePack.exists()) rescourcePack.mkdir();
//
//		LogHelper.info("Downloading Images");
//
//		try {
//			URL url = new URL("http://i.imgur.com/oHRx1yQ.jpg");
//			String fileName = url.getFile();
//			//String destName = downloadLocation + fileName.substring(fileName.lastIndexOf("/"));
//			File dll = new File(rescourcePack, fileName.substring(fileName.lastIndexOf("/")));
//
//			InputStream is = url.openStream();
//			OutputStream os = new FileOutputStream(dll);
//
//			ByteStreams.copy(is, os);
//
//			is.close();
//			os.close();
//		}catch (IOException e){
//			LogHelper.info(e);
//		}
//
//
//		List defaultResourcePacks = Lists.newArrayList();
//		Field f = ReflectionHelper.findField(Minecraft.class, "defaultResourcePacks", "field_110449_ao");
//		f.setAccessible(true);
//		try {
//			defaultResourcePacks = (List)f.get(Minecraft.getMinecraft());
//			defaultResourcePacks.add(new FolderResourcePack(rescourcePack));
//			for (Object o : defaultResourcePacks){
//				if (o instanceof FolderResourcePack) LogHelper.info(((FolderResourcePack) o).getPackName());
//				if (o instanceof FileResourcePack) LogHelper.info(((FileResourcePack)o).getPackName());
//			}
//
//			f.set(Minecraft.getMinecraft(), defaultResourcePacks);
//		}
//		catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}


	}

	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		FMLCommonHandler.instance().bus().register(new KeyInputHandler());
		FMLCommonHandler.instance().bus().register(new ClientEventHandler());
		MinecraftForge.EVENT_BUS.register(new HudHandler());
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		KeyBindings.init();
		registerRenderIDs();
		registerRendering();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);

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
		//ClientRegistry.bindTileEntitySpecialRenderer(TileTestBlock.class, new RenderTileTestBlock());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyStorageCore.class, new RenderTileEnergyStorageCore());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyPylon.class, new RenderTileEnergyPylon());
		ClientRegistry.bindTileEntitySpecialRenderer(TilePlacedItem.class, new RenderTilePlacedItem());
		ClientRegistry.bindTileEntitySpecialRenderer(TileDissEnchanter.class, new RenderTileDissEnchanter());
		ClientRegistry.bindTileEntitySpecialRenderer(TileTeleporterStand.class, new RenderTileTeleporterStand());
		ClientRegistry.bindTileEntitySpecialRenderer(TileDraconiumChest.class, new RenderTileDraconiumChest());

		//Entitys
		RenderingRegistry.registerEntityRenderingHandler(EntityCustomDragon.class, new RenderDragon());
		RenderingRegistry.registerEntityRenderingHandler(EntityDragonHeart.class, new RenderDragonHeart());
	}

	public void registerRenderIDs (){
		References.idTeleporterStand = RenderingRegistry.getNextAvailableRenderId();
	}

	@Override
	public boolean isDedicatedServer() {
		return false;
	}
}
