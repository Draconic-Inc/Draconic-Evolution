package com.brandon3055.draconicevolution.common;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.creativetab.DETab;
import com.brandon3055.draconicevolution.client.gui.GuiHandler;
import com.brandon3055.draconicevolution.client.render.particle.ParticleEnergyBeam;
import com.brandon3055.draconicevolution.client.render.particle.ParticleEnergyField;
import com.brandon3055.draconicevolution.client.render.particle.ParticleReactorBeam;
import com.brandon3055.draconicevolution.common.achievements.Achievements;
import com.brandon3055.draconicevolution.common.entity.*;
import com.brandon3055.draconicevolution.common.handler.*;
import com.brandon3055.draconicevolution.common.lib.OreDoublingRegistry;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.magic.EnchantmentReaper;
import com.brandon3055.draconicevolution.common.magic.PotionHandler;
import com.brandon3055.draconicevolution.common.network.*;
import com.brandon3055.draconicevolution.common.tileentities.*;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileEnergyRelay;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileEnergyTransceiver;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileWirelessEnergyTransceiver;
import com.brandon3055.draconicevolution.common.tileentities.gates.TileFluidGate;
import com.brandon3055.draconicevolution.common.tileentities.gates.TileFluxGate;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.*;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorCore;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorEnergyInjector;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorStabilizer;
import com.brandon3055.draconicevolution.common.utills.DragonChunkLoader;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import com.brandon3055.draconicevolution.common.world.DraconicWorldGenerator;
import com.brandon3055.draconicevolution.integration.computers.CCOCIntegration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.audio.ISound;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        BalanceConfigHandler.init(event.getModConfigurationDirectory());
        registerEventListeners(event.getSide());
        ModBlocks.init();
        ModItems.init();
        ContributorHandler.init();
        registerTileEntities();
        initializeNetwork();
        registerOres();

        DraconicEvolution.reaperEnchant = new EnchantmentReaper(ConfigHandler.reaperEnchantID);
        //
        //		Potion[] potionTypes = null;
        //		LogHelper.info("Expanding Potion array size to 256");
        //
        //		for (Field f : Potion.class.getDeclaredFields()) {
        //			f.setAccessible(true);
        //
        //			try {
        //				if (f.getName().equals("potionTypes") || f.getName().equals("field_76425_a")) {
        //					Field modfield = Field.class.getDeclaredField("modifiers");
        //					modfield.setAccessible(true);
        //					modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);
        //					potionTypes = (Potion[]) f.get(null);
        //					final Potion[] newPotionTypes = new Potion[256];
        //					System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
        //					f.set(null, newPotionTypes);
        //				}
        //			}
        //			catch (Exception e) {
        //				LogHelper.error("Severe error, please report this to the mod author:");
        //				e.printStackTrace();
        //			}
        //		}

        Achievements.addModAchievements();
        LogHelper.info("Finished PreInitialization");
    }

    public void init(FMLInitializationEvent event) {
        CraftingHandler.init();
        registerGuiHandeler();
        registerWorldGen();
        registerEntities();
        DETab.initialize();
        PotionHandler.init();
        CCOCIntegration.init();
        DragonChunkLoader.init();

        LogHelper.info("Finished Initialization");
    }

    public void postInit(FMLPostInitializationEvent event) {
        BalanceConfigHandler.finishLoading();
        OreDoublingRegistry.init();
        Achievements.registerAchievementPane();
        LogHelper.info("Finished PostInitialization");
    }

    public void initializeNetwork() {
        DraconicEvolution.network = NetworkRegistry.INSTANCE.newSimpleChannel(DraconicEvolution.networkChannelName);
        DraconicEvolution.network.registerMessage(ButtonPacket.Handler.class, ButtonPacket.class, 0, Side.SERVER);
        DraconicEvolution.network.registerMessage(
                ParticleGenPacket.Handler.class, ParticleGenPacket.class, 1, Side.SERVER);
        DraconicEvolution.network.registerMessage(
                PlacedItemPacket.Handler.class, PlacedItemPacket.class, 2, Side.SERVER);
        DraconicEvolution.network.registerMessage(
                PlayerDetectorButtonPacket.Handler.class, PlayerDetectorButtonPacket.class, 3, Side.SERVER);
        DraconicEvolution.network.registerMessage(
                PlayerDetectorStringPacket.Handler.class, PlayerDetectorStringPacket.class, 4, Side.SERVER);
        DraconicEvolution.network.registerMessage(
                TeleporterPacket.Handler.class, TeleporterPacket.class, 5, Side.SERVER);
        DraconicEvolution.network.registerMessage(
                TileObjectPacket.Handler.class, TileObjectPacket.class, 6, Side.CLIENT);
        DraconicEvolution.network.registerMessage(
                MountUpdatePacket.Handler.class, MountUpdatePacket.class, 7, Side.CLIENT);
        DraconicEvolution.network.registerMessage(
                MountUpdatePacket.Handler.class, MountUpdatePacket.class, 8, Side.SERVER);
        DraconicEvolution.network.registerMessage(
                ItemConfigPacket.Handler.class, ItemConfigPacket.class, 9, Side.SERVER);
        DraconicEvolution.network.registerMessage(
                TileObjectPacket.Handler.class, TileObjectPacket.class, 10, Side.SERVER);
        DraconicEvolution.network.registerMessage(
                BlockUpdatePacket.Handler.class, BlockUpdatePacket.class, 11, Side.SERVER);
        DraconicEvolution.network.registerMessage(
                SpeedRequestPacket.Handler.class, SpeedRequestPacket.class, 12, Side.SERVER);
        DraconicEvolution.network.registerMessage(
                SpeedRequestPacket.Handler.class, SpeedRequestPacket.class, 13, Side.CLIENT);
        DraconicEvolution.network.registerMessage(ToolModePacket.Handler.class, ToolModePacket.class, 14, Side.SERVER);
        DraconicEvolution.network.registerMessage(
                GenericParticlePacket.Handler.class, GenericParticlePacket.class, 15, Side.CLIENT);
        DraconicEvolution.network.registerMessage(
                ShieldHitPacket.Handler.class, ShieldHitPacket.class, 16, Side.CLIENT);
        DraconicEvolution.network.registerMessage(
                ContributorPacket.Handler.class, ContributorPacket.class, 17, Side.CLIENT);
        DraconicEvolution.network.registerMessage(
                ContributorPacket.Handler.class, ContributorPacket.class, 18, Side.SERVER);
        DraconicEvolution.network.registerMessage(
                MagnetTogglePacket.Handler.class, MagnetTogglePacket.class, 19, Side.SERVER);
        DraconicEvolution.network.registerMessage(
                MagnetToggleAckPacket.Handler.class, MagnetToggleAckPacket.class, 20, Side.CLIENT);
    }

    public void registerTileEntities() {
        GameRegistry.registerTileEntity(
                TileWeatherController.class, References.RESOURCESPREFIX + "TileWeatherController");
        GameRegistry.registerTileEntity(TileSunDial.class, References.RESOURCESPREFIX + "TileSunDial");
        GameRegistry.registerTileEntity(TileGrinder.class, References.RESOURCESPREFIX + "TileGrinder");
        GameRegistry.registerTileEntity(TilePotentiometer.class, References.RESOURCESPREFIX + "TilePotentiometer");
        GameRegistry.registerTileEntity(
                TileParticleGenerator.class, References.RESOURCESPREFIX + "TileParticleGenerator");
        GameRegistry.registerTileEntity(TilePlayerDetector.class, References.RESOURCESPREFIX + "TilePlayerDetector");
        GameRegistry.registerTileEntity(
                TilePlayerDetectorAdvanced.class, References.RESOURCESPREFIX + "TilePlayerDetectorAdvanced");
        GameRegistry.registerTileEntity(TileEnergyInfuser.class, References.RESOURCESPREFIX + "TileEnergyInfuser");
        GameRegistry.registerTileEntity(TileCustomSpawner.class, References.RESOURCESPREFIX + "TileCustomSpawner");
        GameRegistry.registerTileEntity(TileGenerator.class, References.RESOURCESPREFIX + "TileGenerator");
        GameRegistry.registerTileEntity(
                TileEnergyStorageCore.class, References.RESOURCESPREFIX + "TileEnergyStorageCore");
        GameRegistry.registerTileEntity(TileEarth.class, References.RESOURCESPREFIX + "TileEarth");
        GameRegistry.registerTileEntity(
                TileInvisibleMultiblock.class, References.RESOURCESPREFIX + "TileInvisibleMultiblock");
        GameRegistry.registerTileEntity(TileEnergyPylon.class, References.RESOURCESPREFIX + "TileEnergyPylon");
        GameRegistry.registerTileEntity(
                TileEnderResurrection.class, References.RESOURCESPREFIX + "TileEnderResurrection");
        GameRegistry.registerTileEntity(TilePlacedItem.class, References.RESOURCESPREFIX + "TilePlacedItem");
        GameRegistry.registerTileEntity(TileCKeyStone.class, References.RESOURCESPREFIX + "TileCKeyStone");
        GameRegistry.registerTileEntity(TileDissEnchanter.class, References.RESOURCESPREFIX + "TileDissEnchanter");
        GameRegistry.registerTileEntity(TileTeleporterStand.class, References.RESOURCESPREFIX + "TileTeleporterStand");
        GameRegistry.registerTileEntity(TileDraconiumChest.class, References.RESOURCESPREFIX + "TileDraconiumChest");
        GameRegistry.registerTileEntity(TileEnergyRelay.class, References.RESOURCESPREFIX + "TileEnergyRelay");
        GameRegistry.registerTileEntity(
                TileEnergyTransceiver.class, References.RESOURCESPREFIX + "TileEnergyTransceiver");
        GameRegistry.registerTileEntity(
                TileWirelessEnergyTransceiver.class, References.RESOURCESPREFIX + "TileWirelessEnergyTransceiver");
        GameRegistry.registerTileEntity(
                TileDislocatorReceptacle.class, References.RESOURCESPREFIX + "TileDislocatorReceptacle");
        GameRegistry.registerTileEntity(TilePortalBlock.class, References.RESOURCESPREFIX + "TilePortalBlock");
        GameRegistry.registerTileEntity(TileReactorCore.class, References.RESOURCESPREFIX + "TileReactorCore");
        GameRegistry.registerTileEntity(TileFluxGate.class, References.RESOURCESPREFIX + "TileFluxGate");
        GameRegistry.registerTileEntity(TileFluidGate.class, References.RESOURCESPREFIX + "TileFluidGate");
        GameRegistry.registerTileEntity(
                TileReactorStabilizer.class, References.RESOURCESPREFIX + "TileReactorStabilizer");
        GameRegistry.registerTileEntity(
                TileReactorEnergyInjector.class, References.RESOURCESPREFIX + "TileReactorEnergyInjector");
        GameRegistry.registerTileEntity(TileChaosShard.class, References.RESOURCESPREFIX + "TileChaosShard");
        GameRegistry.registerTileEntity(
                TileUpgradeModifier.class, References.RESOURCESPREFIX + "TileEnhancementModifier");
        if (DraconicEvolution.debug) {
            GameRegistry.registerTileEntity(TileTestBlock.class, References.RESOURCESPREFIX + "TileTestBlock");
            GameRegistry.registerTileEntity(
                    TileContainerTemplate.class, References.RESOURCESPREFIX + "TileContainerTemplate");
        }
    }

    public void registerEventListeners(Side s) {
        MinecraftForge.EVENT_BUS.register(new MinecraftForgeEventHandler());
        MinecraftForge.EVENT_BUS.register(new Achievements());
        FMLCommonHandler.instance().bus().register(new Achievements());
        FMLCommonHandler.instance().bus().register(new FMLEventHandler());
    }

    public void registerGuiHandeler() {
        new GuiHandler();
    }

    public void registerWorldGen() {
        GameRegistry.registerWorldGenerator(new DraconicWorldGenerator(), 0);
    }

    public void registerOres() {
        if (ModBlocks.isEnabled(ModBlocks.draconiumOre))
            OreDictionary.registerOre("oreDraconium", ModBlocks.draconiumOre);
        if (ModBlocks.isEnabled(ModBlocks.draconiumBlock))
            OreDictionary.registerOre("blockDraconium", new ItemStack(ModBlocks.draconiumBlock));
        if (ModBlocks.isEnabled(ModBlocks.draconicBlock))
            OreDictionary.registerOre("blockDraconiumAwakened", new ItemStack(ModBlocks.draconicBlock));

        if (ModItems.isEnabled(ModItems.draconiumIngot))
            OreDictionary.registerOre("ingotDraconium", ModItems.draconiumIngot);
        if (ModItems.isEnabled(ModItems.draconiumDust))
            OreDictionary.registerOre("dustDraconium", ModItems.draconiumDust);
        if (ModItems.isEnabled(ModItems.draconicIngot))
            OreDictionary.registerOre("ingotDraconiumAwakened", ModItems.draconicIngot);
        if (ModItems.isEnabled(ModItems.nugget)) {
            OreDictionary.registerOre("nuggetDraconium", ModItems.nuggetDraconium.copy());
            OreDictionary.registerOre("nuggetDraconiumAwakened", ModItems.nuggetAwakened.copy());
        }
    }

    // @Callback
    public void registerEntities() {
        EntityRegistry.registerModEntity(
                EntityCustomDragon.class, "EnderDragon", 0, DraconicEvolution.instance, 256, 3, true);
        EntityRegistry.registerModEntity(
                EntityPersistentItem.class, "Persistent Item", 1, DraconicEvolution.instance, 32, 5, true);
        EntityRegistry.registerModEntity(
                EntityDraconicArrow.class, "Arrow", 2, DraconicEvolution.instance, 32, 5, true);
        EntityRegistry.registerModEntity(
                EntityEnderArrow.class, "Ender Arrow", 3, DraconicEvolution.instance, 32, 1, true);
        // EntityRegistry.registerModEntity(EntityChaosDrill.class, "Chaos Drill", 4, DraconicEvolution.instance, 10, 5,
        // false);
        EntityRegistry.registerModEntity(
                EntityDragonHeart.class, "Dragon Heart Item", 5, DraconicEvolution.instance, 32, 5, true);
        EntityRegistry.registerModEntity(
                EntityChaosGuardian.class, "ChaosGuardian", 6, DraconicEvolution.instance, 256, 1, true);
        EntityRegistry.registerModEntity(
                EntityDragonProjectile.class, "DragonProjectile", 7, DraconicEvolution.instance, 256, 1, true);
        EntityRegistry.registerModEntity(
                EntityChaosCrystal.class, "ChaosCrystal", 8, DraconicEvolution.instance, 256, 5, false);
        EntityRegistry.registerModEntity(
                EntityChaosBolt.class, "ChaosBolt", 9, DraconicEvolution.instance, 32, 5, true);
        EntityRegistry.registerModEntity(
                EntityChaosVortex.class, "EntityChaosEnergyVortex", 10, DraconicEvolution.instance, 512, 5, true);
        EntityRegistry.registerModEntity(
                EntityCustomArrow.class, "CustomArrow", 11, DraconicEvolution.instance, 128, 1, true);
    }

    public ParticleEnergyBeam energyBeam(
            World worldObj,
            double x,
            double y,
            double z,
            double tx,
            double ty,
            double tz,
            int powerFlow,
            boolean advanced,
            ParticleEnergyBeam oldBeam,
            boolean render,
            int beamType) {
        return null;
    }

    public ParticleEnergyField energyField(
            World worldObj,
            double x,
            double y,
            double z,
            int type,
            boolean advanced,
            ParticleEnergyField oldBeam,
            boolean render) {
        return null;
    }

    public ParticleReactorBeam reactorBeam(TileEntity tile, ParticleReactorBeam oldBeam, boolean render) {
        return null;
    }

    public void spawnParticle(Object particle, int range) {}

    public ISound playISound(ISound sound) {
        return null;
    }
}
