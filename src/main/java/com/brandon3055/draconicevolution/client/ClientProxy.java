package com.brandon3055.draconicevolution.client;

import com.brandon3055.draconicevolution.CommonProxy;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

public class ClientProxy extends CommonProxy {

//    public static LayerContributorPerkRenderer layerWings;

    @Override
    public void commonSetup(FMLCommonSetupEvent event) {
        super.commonSetup(event);
    }

    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        super.clientSetup(event);
    }

    @Override
    public void serverSetup(FMLDedicatedServerSetupEvent event) {
        super.serverSetup(event);
    }


//    @Override
//    public void preInit(FMLPreInitializationEvent event) {
//        super.preInit(event);
//
//        OBJLoader.INSTANCE.addDomain(DraconicEvolution.MODID);
//        TextureUtils.addIconRegister(new DETextures());
//        ResourceUtils.registerReloadListener(new DETextures());
//
//        DEImageHandler.init(event);
//
//        TextureUtils.addIconRegister(new ArmorModelHelper());
//        TextureUtils.addIconRegister(new DETextureCache());
//
//        registerRendering();
//    }
//
//    @Override
//    public void init(FMLInitializationEvent event) {
//
//        super.init(event);
//        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
//        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
//        KeyBindings.init();
//        CCRenderEventHandler.init();
//    }
//
//    @Override
//    public void postInit(FMLPostInitializationEvent event) {
//        super.postInit(event);
//
//        for (RenderPlayer renderPlayer : Minecraft.getInstance().getRenderManager().getSkinMap().values()) {
//            renderPlayer.addLayer(layerWings = new LayerContributorPerkRenderer(renderPlayer));
//        }
//    }
//
//    @Override
//    public void initializeNetwork() {
//        super.initializeNetwork();
//        PacketCustom.assignHandler("DEPCChannel", new ClientPacketHandler());
//    }
//
//    public void registerRendering() {
//
//        //Entities
//        RenderingRegistry.registerEntityRenderingHandler(EntityChaosGuardian.class, RenderChaosGuardian::new);
//        RenderingRegistry.registerEntityRenderingHandler(EntityDragonHeart.class, RenderDragonHeart::new);
//        RenderingRegistry.registerEntityRenderingHandler(EntityGuardianProjectile.class, RenderGuardianProjectile::new);
//        RenderingRegistry.registerEntityRenderingHandler(EntityGuardianCrystal.class, RenderGuardianCrystal::new);
//        RenderingRegistry.registerEntityRenderingHandler(EntityChaosImplosion.class, RenderEntityChaosVortex::new);
//
//        if (DEConfig.disableCustomArrowModel) {
//            RenderingRegistry.registerEntityRenderingHandler(EntityCustomArrow.class, manager -> new RenderArrow<EntityCustomArrow>(manager) {
//                @Override
//                protected ResourceLocation getEntityTexture(EntityCustomArrow entity) {
//                    return RenderTippedArrow.RES_ARROW;
//                }
//            });
//        }
//        else {
//            RenderingRegistry.registerEntityRenderingHandler(EntityCustomArrow.class, RenderCustomArrow::new);
//        }
//
//        RenderingRegistry.registerEntityRenderingHandler(EntityLootCore.class, RenderLootCore::new);
//        RenderingRegistry.registerEntityRenderingHandler(EntityEnderEnergyManipulator.class, RenderEntityEnderEnergyManipulator::new);
//    }
//
//    @Override
//    public void registerParticles() {
//        DEParticles.registerClient();
//    }
//
//    public boolean isOp(String paramString) {
//        return Minecraft.getInstance().world.getWorldInfo().getGameType().isCreative();
//    }
//
//    @Override
//    public ENetFXHandler createENetFXHandler(IENetEffectTile tile) {
//        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
//            return super.createENetFXHandler(tile);
//        }
//        return tile.createClientFXHandler();
//    }
//
//    @Override
//    public ReactorEffectHandler createReactorFXHandler(TileReactorCore tile) {
//        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
//            return super.createReactorFXHandler(tile);
//        }
//        return new ReactorEffectHandler(tile);
//    }
//
//    @Override
//    public ISound playISound(ISound sound) {
//        FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
//        return sound;
//    }
}
