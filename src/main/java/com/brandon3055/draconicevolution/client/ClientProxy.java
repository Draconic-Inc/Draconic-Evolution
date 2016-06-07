package com.brandon3055.draconicevolution.client;

import codechicken.lib.render.TextureUtils;
import com.brandon3055.draconicevolution.CommonProxy;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.keybinding.KeyBindings;
import com.brandon3055.draconicevolution.client.keybinding.KeyInputHandler;
import com.brandon3055.draconicevolution.client.model.ToolModelLoader;
import com.brandon3055.draconicevolution.lib.DEImageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	private final static boolean debug = DraconicEvolution.debug;
	public static String downloadLocation;
	
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
        OBJLoader.INSTANCE.addDomain(DraconicEvolution.MODID);
//        ModelLoaderRegistry.registerLoader(new CustomModelLoader());
		DraconicEvolution.featureParser.registerRendering();
		DEImageHandler.init(event);

        ToolModelLoader.buildItemMap();

        ToolModelLoader loader = new ToolModelLoader();
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(loader);
        TextureUtils.addIconRegister(loader);

        ToolModelLoader.registerModels();
	}

	@Override
	public void init(FMLInitializationEvent event)
	{


		super.init(event);
		MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
//		if (ConfigHandler.enableVersionChecker) FMLCommonHandler.instance().bus().register(new UpdateChecker());
//		MinecraftForge.EVENT_BUS.register(new HudHandler());
//		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		KeyBindings.init();
		registerRenderIDs();
		registerRendering();
//		ResourceHandler.instance.tick(null);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
//		ResourceHandler.instance.tick(null);
	}

	public void registerRendering()
    {

//		//Item Renderers
//		MinecraftForgeClient.registerItemRenderer(ModItems.wyvernBow, new RenderBow());
//		MinecraftForgeClient.registerItemRenderer(ModItems.draconicBow, new RenderBow());
//		MinecraftForgeClient.registerItemRenderer(ModItems.mobSoul, new RenderMobSoul());
//		MinecraftForgeClient.registerItemRenderer(ModItems.chaosShard, new RenderChaosShard());
//		MinecraftForgeClient.registerItemRenderer(ModItems.reactorStabilizerParts, new RenderStabilizerPart());
//		MinecraftForgeClient.registerItemRenderer(ModItems.chaosFragment, new RenderChaosFragment());
//
//		if (!ConfigHandler.useOldArmorModel)
//		{
//			MinecraftForgeClient.registerItemRenderer(ModItems.wyvernHelm, new RenderArmor(ModItems.wyvernHelm));
//			MinecraftForgeClient.registerItemRenderer(ModItems.wyvernChest, new RenderArmor(ModItems.wyvernChest));
//			MinecraftForgeClient.registerItemRenderer(ModItems.wyvernLeggs, new RenderArmor(ModItems.wyvernLeggs));
//			MinecraftForgeClient.registerItemRenderer(ModItems.wyvernBoots, new RenderArmor(ModItems.wyvernBoots));
//			MinecraftForgeClient.registerItemRenderer(ModItems.draconicHelm, new RenderArmor(ModItems.draconicHelm));
//			MinecraftForgeClient.registerItemRenderer(ModItems.draconicChest, new RenderArmor(ModItems.draconicChest));
//			MinecraftForgeClient.registerItemRenderer(ModItems.draconicLeggs, new RenderArmor(ModItems.draconicLeggs));
//			MinecraftForgeClient.registerItemRenderer(ModItems.draconicBoots, new RenderArmor(ModItems.draconicBoots));
//		}
//
//		if (!ConfigHandler.useOldD2DToolTextures)
//		{
//			MinecraftForgeClient.registerItemRenderer(ModItems.draconicSword, new RenderTool("models/tools/DraconicSword.obj", "textures/models/tools/DraconicSword.png", (IRenderTweak) ModItems.draconicSword));
//			MinecraftForgeClient.registerItemRenderer(ModItems.wyvernPickaxe, new RenderTool("models/tools/Pickaxe.obj", "textures/models/tools/Pickaxe.png", (IRenderTweak) ModItems.wyvernPickaxe));
//			MinecraftForgeClient.registerItemRenderer(ModItems.draconicPickaxe, new RenderTool("models/tools/DraconicPickaxe.obj", "textures/models/tools/DraconicPickaxe.png", (IRenderTweak) ModItems.draconicPickaxe));
//			MinecraftForgeClient.registerItemRenderer(ModItems.draconicAxe, new RenderTool("models/tools/DraconicLumberAxe.obj", "textures/models/tools/DraconicLumberAxe.png", (IRenderTweak) ModItems.draconicAxe));
//			MinecraftForgeClient.registerItemRenderer(ModItems.wyvernShovel, new RenderTool("models/tools/Shovel.obj", "textures/models/tools/Shovel.png", (IRenderTweak) ModItems.wyvernShovel));
//			MinecraftForgeClient.registerItemRenderer(ModItems.draconicShovel, new RenderTool("models/tools/DraconicShovel.obj", "textures/models/tools/DraconicShovel.png", (IRenderTweak) ModItems.draconicShovel));
//			MinecraftForgeClient.registerItemRenderer(ModItems.wyvernSword, new RenderTool("models/tools/Sword.obj", "textures/models/tools/Sword.png", (IRenderTweak) ModItems.wyvernSword));
//			MinecraftForgeClient.registerItemRenderer(ModItems.draconicDestructionStaff, new RenderTool("models/tools/DraconicStaffOfPower.obj", "textures/models/tools/DraconicStaffOfPower.png", (IRenderTweak) ModItems.draconicDestructionStaff));
//			MinecraftForgeClient.registerItemRenderer(ModItems.draconicHoe, new RenderTool("models/tools/DraconicHoe.obj", "textures/models/tools/DraconicHoe.png", (IRenderTweak) ModItems.draconicHoe));
//			MinecraftForgeClient.registerItemRenderer(ModItems.draconicBow, new RenderBowModel(true));
//			MinecraftForgeClient.registerItemRenderer(ModItems.wyvernBow, new RenderBowModel(false));
//		}
//
//		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.draconiumChest), new RenderDraconiumChest());
//		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.particleGenerator), new RenderParticleGen());
//		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.energyInfuser), new RenderEnergyInfuser());
//		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.energyCrystal), new RenderCrystal());
//		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.reactorStabilizer), new RenderReactorStabilizer());
//		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.reactorEnergyInjector), new RenderReactorEnergyInjector());
//		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.reactorCore), new RenderReactorCore());
//		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.chaosCrystal), new RenderChaosShard());
//		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.upgradeModifier), new RenderUpgradeModifier());
//
//		//ISimpleBlockRendering
//		RenderingRegistry.registerBlockHandler(new RenderTeleporterStand());
//		RenderingRegistry.registerBlockHandler(new RenderPortal());
//
//		//TileEntitySpecialRenderers
//		ClientRegistry.bindTileEntitySpecialRenderer(TileParticleGenerator.class, new RenderTileParticleGen());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyInfuser.class, new RenderTileEnergyInfiser());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileCustomSpawner.class, new RenderTileCustomSpawner());
//		//ClientRegistry.bindTileEntitySpecialRenderer(TileTestBlock.class, new RenderTileCrystal());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyStorageCore.class, new RenderTileEnergyStorageCore());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyPylon.class, new RenderTileEnergyPylon());
//		ClientRegistry.bindTileEntitySpecialRenderer(TilePlacedItem.class, new RenderTilePlacedItem());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileDissEnchanter.class, new RenderTileDissEnchanter());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileTeleporterStand.class, new RenderTileTeleporterStand());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileDraconiumChest.class, new RenderTileDraconiumChest());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyRelay.class, new RenderTileCrystal());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyTransceiver.class, new RenderTileCrystal());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileWirelessEnergyTransceiver.class, new RenderTileCrystal());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileReactorCore.class, new RenderTileReactorCore());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileReactorStabilizer.class, new RenderTileReactorStabilizer());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileReactorEnergyInjector.class, new RenderTileReactorEnergyInjector());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileChaosShard.class, new RenderTileChaosShard());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileUpgradeModifier.class, new RenderTileUpgradeModifier());
//
//		//Entitys
//		RenderingRegistry.registerEntityRenderingHandler(EntityCustomDragon.class, new RenderDragon());
//		RenderingRegistry.registerEntityRenderingHandler(EntityChaosGuardian.class, new RenderDragon());
//		RenderingRegistry.registerEntityRenderingHandler(EntityDragonHeart.class, new RenderDragonHeart());
//		RenderingRegistry.registerEntityRenderingHandler(EntityDragonProjectile.class, new RenderDragonProjectile());
//		RenderingRegistry.registerEntityRenderingHandler(EntityChaosCrystal.class, new RenderChaosCrystal());
//		RenderingRegistry.registerEntityRenderingHandler(EntityChaosVortex.class, new RenderEntityChaosVortex());
//		RenderingRegistry.registerEntityRenderingHandler(EntityCustomArrow.class, new RenderEntityCustomArrow());
	}

	public void registerRenderIDs (){
//		References.idTeleporterStand = RenderingRegistry.getNextAvailableRenderId();
//		References.idPortal = RenderingRegistry.getNextAvailableRenderId();
	}

    @Override
    public void registerParticles() {
        DEParticles.registerClient();
    }

    //	@Override
//	public ParticleEnergyBeam energyBeam(World worldObj, double x, double y, double z, double tx, double ty, double tz, int powerFlow, boolean advanced, ParticleEnergyBeam oldBeam, boolean render, int beamType) {
//		if (!worldObj.isRemote) return null;
//		ParticleEnergyBeam beam = oldBeam;
//		boolean inRange = ParticleHandler.isInRange(x, y, z, 50) || ParticleHandler.isInRange(tx, ty, tz, 50);
//
//		if (beam == null || beam.isDead)
//		{
//			if (inRange)
//			{
//				beam = new ParticleEnergyBeam(worldObj, x, y, z, tx, ty, tz, 8, powerFlow, advanced, beamType);
//
//				FMLClientHandler.instance().getClient().effectRenderer.addEffect(beam);
//			}
//		}
//		else if (!inRange)
//		{
//			beam.setDead();
//			return null;
//		}
//		else
//		{
//			beam.update(powerFlow, render);
//		}
//		return beam;
//	}
//
//	@Override
//	public ParticleEnergyField energyField(World worldObj, double x, double y, double z, int type, boolean advanced, ParticleEnergyField oldBeam, boolean render) {
//		if (!worldObj.isRemote) return null;
//		ParticleEnergyField beam = oldBeam;
//		boolean inRange = ParticleHandler.isInRange(x, y, z, 50);
//
//		if (beam == null || beam.isDead)
//		{
//			if (inRange)
//			{
//				beam = new ParticleEnergyField(worldObj, x, y, z, 8, type, advanced);
//
//				FMLClientHandler.instance().getClient().effectRenderer.addEffect(beam);
//			}
//		}
//		else if (!inRange)
//		{
//			beam.setDead();
//			return null;
//		}
//		else
//		{
//			beam.update(render);
//		}
//		return beam;
//	}
//
//	@Override
//	public ParticleReactorBeam reactorBeam(TileEntity tile, ParticleReactorBeam oldBeam, boolean render) {
//		if (!tile.getWorldObj().isRemote || !(tile instanceof IReactorPart)) return null;
//		ParticleReactorBeam beam = oldBeam;
//		boolean inRange = ParticleHandler.isInRange(tile.xCoord, tile.yCoord, tile.zCoord, 50);
//
//		if (beam == null || beam.isDead)
//		{
//			if (inRange)
//			{
//				beam = new ParticleReactorBeam(tile);
//
//				FMLClientHandler.instance().getClient().effectRenderer.addEffect(beam);
//			}
//		}
//		else if (!inRange)
//		{
//			beam.setDead();
//			return null;
//		}
//		else
//		{
//			beam.update(render);
//		}
//		return beam;
//	}


	public boolean isOp(String paramString)
	{
		return Minecraft.getMinecraft().theWorld.getWorldInfo().getGameType().isCreative();
	}

	@Override
	public void spawnParticle(Object particle, int range) {
//		if (particle instanceof EntityFX && ((EntityFX)particle).worldObj.isRemote) ParticleHandler.spawnCustomParticle((EntityFX)particle, range);
	}

	@Override
	public ISound playISound(ISound sound) {
		FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
		return sound;
	}
}
