package com.brandon3055.draconicevolution.client;

import codechicken.lib.packet.PacketCustom;
import codechicken.lib.reflect.ObfMapping;
import codechicken.lib.render.CCRenderEventHandler;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.ResourceUtils;
import com.brandon3055.draconicevolution.CommonProxy;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.IENetEffectTile;
import com.brandon3055.draconicevolution.blocks.energynet.rendering.ENetFXHandler;
import com.brandon3055.draconicevolution.blocks.reactor.ReactorEffectHandler;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.WikiDocManager;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.keybinding.KeyBindings;
import com.brandon3055.draconicevolution.client.keybinding.KeyInputHandler;
import com.brandon3055.draconicevolution.client.model.ArmorModelHelper;
import com.brandon3055.draconicevolution.client.render.entity.*;
import com.brandon3055.draconicevolution.entity.*;
import com.brandon3055.draconicevolution.lib.DEImageHandler;
import com.brandon3055.draconicevolution.network.ccnetwork.ClientPacketHandler;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderTippedArrow;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

    public static LayerContributorPerkRenderer layerWings;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        OBJLoader.INSTANCE.addDomain(DraconicEvolution.MODID);
        TextureUtils.addIconRegister(new DETextures());
        ResourceUtils.registerReloadListener(new DETextures());

        DEImageHandler.init(event);

        TextureUtils.addIconRegister(new ArmorModelHelper());
        TextureUtils.addIconRegister(new DETextureCache());

        registerRendering();
        if (ObfMapping.obfuscated) {
            WikiDocManager.initialize();
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {

        super.init(event);
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        KeyBindings.init();
        CCRenderEventHandler.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        for (RenderPlayer renderPlayer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
            renderPlayer.addLayer(layerWings = new LayerContributorPerkRenderer(renderPlayer));
        }
    }

    @Override
    public void initializeNetwork() {
        super.initializeNetwork();
        PacketCustom.assignHandler("DEPCChannel", new ClientPacketHandler());
    }

    public void registerRendering() {

        //Entities
        RenderingRegistry.registerEntityRenderingHandler(EntityChaosGuardian.class, RenderChaosGuardian::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityDragonHeart.class, RenderDragonHeart::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityGuardianProjectile.class, RenderGuardianProjectile::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityGuardianCrystal.class, RenderGuardianCrystal::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityChaosImplosion.class, RenderEntityChaosVortex::new);

        if (DEConfig.disableCustomArrowModel) {
            RenderingRegistry.registerEntityRenderingHandler(EntityCustomArrow.class, manager -> new RenderArrow<EntityCustomArrow>(manager) {
                @Override
                protected ResourceLocation getEntityTexture(EntityCustomArrow entity) {
                    return RenderTippedArrow.RES_ARROW;
                }
            });
        }
        else {
            RenderingRegistry.registerEntityRenderingHandler(EntityCustomArrow.class, RenderCustomArrow::new);
        }

        RenderingRegistry.registerEntityRenderingHandler(EntityLootCore.class, RenderLootCore::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityEnderEnergyManipulator.class, RenderEntityEnderEnergyManipulator::new);
    }

    @Override
    public void registerParticles() {
        DEParticles.registerClient();
    }

    public boolean isOp(String paramString) {
        return Minecraft.getMinecraft().world.getWorldInfo().getGameType().isCreative();
    }

    @Override
    public ENetFXHandler createENetFXHandler(IENetEffectTile tile) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            return super.createENetFXHandler(tile);
        }
        return tile.createClientFXHandler();
    }

    @Override
    public ReactorEffectHandler createReactorFXHandler(TileReactorCore tile) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            return super.createReactorFXHandler(tile);
        }
        return new ReactorEffectHandler(tile);
    }

    @Override
    public ISound playISound(ISound sound) {
        FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
        return sound;
    }
}
