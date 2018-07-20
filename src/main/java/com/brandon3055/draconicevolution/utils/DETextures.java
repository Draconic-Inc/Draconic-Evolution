package com.brandon3055.draconicevolution.utils;

import codechicken.lib.texture.TextureUtils.IIconRegister;
import com.brandon3055.draconicevolution.client.model.GlassParticleDummyModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

/**
 * Created by brandon3055 on 31/08/2016.
 */
public class DETextures implements IIconRegister, IResourceManagerReloadListener {

    private static TextureMap map;

    @Override
    public void registerIcons(TextureMap textureMap) {
        map = textureMap;

        //@formatter:off
        WYVERN_AXE =                register(TOOLS_ + "wyvern_axe");
        WYVERN_BOW00 =              register(TOOLS_ + "wyvern_bow00");
        WYVERN_BOW01 =              register(TOOLS_ + "wyvern_bow01");
        WYVERN_BOW02 =              register(TOOLS_ + "wyvern_bow02");
        WYVERN_BOW03 =              register(TOOLS_ + "wyvern_bow03");
        WYVERN_PICKAXE =            register(TOOLS_ + "wyvern_pickaxe");
        WYVERN_SHOVEL =             register(TOOLS_ + "wyvern_shovel");
        WYVERN_SWORD =              register(TOOLS_ + "wyvern_sword");
        DRACONIC_AXE =              register(TOOLS_ + "draconic_axe");
        DRACONIC_BOW00 =            register(TOOLS_ + "draconic_bow00");
        DRACONIC_BOW01 =            register(TOOLS_ + "draconic_bow01");
        DRACONIC_BOW02 =            register(TOOLS_ + "draconic_bow02");
        DRACONIC_BOW03 =            register(TOOLS_ + "draconic_bow03");
        DRACONIC_HOE =              register(TOOLS_ + "draconic_hoe");
        DRACONIC_PICKAXE =          register(TOOLS_ + "draconic_pickaxe");
        DRACONIC_SHOVEL =           register(TOOLS_ + "draconic_shovel");
        DRACONIC_STAFF_OF_POWER =   register(TOOLS_ + "draconic_staff_of_power");
        DRACONIC_SWORD =            register(TOOLS_ + "draconic_sword");

        //OBJ textures. Just need to be registered, never accessed by us through here.
        register(TOOLS_OBJ_ + "wyvern_axe");
        register(TOOLS_OBJ_ + "wyvern_bow00");
        register(TOOLS_OBJ_ + "wyvern_bow01");
        register(TOOLS_OBJ_ + "wyvern_bow02");
        register(TOOLS_OBJ_ + "wyvern_bow03");
        register(TOOLS_OBJ_ + "wyvern_pickaxe");
        register(TOOLS_OBJ_ + "wyvern_shovel");
        register(TOOLS_OBJ_ + "wyvern_sword");
        register(TOOLS_OBJ_ + "draconic_axe");
        register(TOOLS_OBJ_ + "draconic_bow00");
        register(TOOLS_OBJ_ + "draconic_bow01");
        register(TOOLS_OBJ_ + "draconic_bow02");
        register(TOOLS_OBJ_ + "draconic_bow03");
        register(TOOLS_OBJ_ + "draconic_hoe");
        register(TOOLS_OBJ_ + "draconic_pickaxe");
        register(TOOLS_OBJ_ + "draconic_shovel");
        register(TOOLS_OBJ_ + "draconic_staff_of_power");
        register(TOOLS_OBJ_ + "draconic_sword");

        WYVERN_BOW = new TextureAtlasSprite[] {
                WYVERN_BOW00,
                WYVERN_BOW01,
                WYVERN_BOW02,
                WYVERN_BOW03
        };
        DRACONIC_BOW = new TextureAtlasSprite[] {
                DRACONIC_BOW00,
                DRACONIC_BOW01,
                DRACONIC_BOW02,
                DRACONIC_BOW03
        };
        //@formatter:on
    }

    // Bouncer to make the class readable.
    private static TextureAtlasSprite register(String sprite) {

        return map.registerSprite(new ResourceLocation(sprite));
    }

    //These are BELLOW register icons because readability.
    private static final String ITEMS_ = "draconicevolution:items/";
    private static final String TOOLS_ = ITEMS_ + "tools/";
    private static final String TOOLS_OBJ_ = TOOLS_ + "obj/";

    public static TextureAtlasSprite WYVERN_AXE;
    public static TextureAtlasSprite WYVERN_BOW00;
    public static TextureAtlasSprite WYVERN_BOW01;
    public static TextureAtlasSprite WYVERN_BOW02;
    public static TextureAtlasSprite WYVERN_BOW03;
    public static TextureAtlasSprite WYVERN_PICKAXE;
    public static TextureAtlasSprite WYVERN_SHOVEL;
    public static TextureAtlasSprite WYVERN_SWORD;
    public static TextureAtlasSprite DRACONIC_AXE;
    public static TextureAtlasSprite DRACONIC_BOW00;
    public static TextureAtlasSprite DRACONIC_BOW01;
    public static TextureAtlasSprite DRACONIC_BOW02;
    public static TextureAtlasSprite DRACONIC_BOW03;
    public static TextureAtlasSprite DRACONIC_HOE;
    public static TextureAtlasSprite DRACONIC_PICKAXE;
    public static TextureAtlasSprite DRACONIC_SHOVEL;
    public static TextureAtlasSprite DRACONIC_STAFF_OF_POWER;
    public static TextureAtlasSprite DRACONIC_SWORD;

    public static TextureAtlasSprite[] WYVERN_BOW;
    public static TextureAtlasSprite[] DRACONIC_BOW;

    //TODO in 1.11 or 1.12 make there full names with mod prefix and ether remove texture cache or have it auto detect when the modid is already present.
    public static final String ENERGY_INFUSER_DECORATION = "textures/blocks/energy_infuser/energy_infuser_decoration.png";
    public static final String FUSION_PARTICLE = "textures/blocks/fusion_crafting/fusion_particle.png";
    public static final String STABILIZER_LARGE = "textures/blocks/particle_gen/stabilizer_large.png";
    public static final String CHAOS_GUARDIAN = "textures/entity/chaos_guardian.png";
    public static final String CHAOS_GUARDIAN_CRYSTAL = "textures/entity/guardian_crystal.png";
    public static final String PROJECTILE_CHAOS = "textures/entity/projectile_chaos.png";
    public static final String PROJECTILE_ENERGY = "textures/entity/projectile_energy.png";
    public static final String PROJECTILE_FIRE = "textures/entity/projectile_fire.png";
    public static final String PROJECTILE_IGNITION = "textures/entity/projectile_ignition.png";
    public static final String GUI_DISLOCATOR_ADVANCED = "textures/gui/dislocator_advanced.png";
    public static final String GUI_ENERGY_INFUSER = "textures/gui/energy_infuser.png";
    public static final String GUI_FUSION_CRAFTING = "textures/gui/fusion_crafting.png";
    public static final String GUI_GENERATOR = "textures/gui/generator.png";
    public static final String GUI_GRINDER = "textures/gui/grinder.png";
    public static final String GUI_HUD = "textures/gui/hud.png";
    public static final String GUI_JEI_FUSION = "textures/gui/jei_fusion_background.png";
    public static final String GUI_WIDGETS = "textures/gui/widgets.png";
    public static final String GUI_REACTOR = "textures/gui/reactor.png";
    public static final String GUI_DRACONIUM_CHEST = "textures/gui/draconium_chest.png";
    public static final String DRAGON_HEART = "textures/items/components/dragon_heart.png";
    public static final String CHAOS_CRYSTAL = "textures/models/chaos_crystal.png";
    public static final String REACTOR_CORE = "textures/models/reactor_core.png";
    public static final String REACTOR_SHIELD = "textures/models/reactor_shield.png";
    public static final String STABILIZER_BEAM = "textures/models/stabilizer_beam.png";
    public static final String CELESTIAL_PARTICLE = "textures/particle/celestial_manipulator.png";
    public static final String ENERGY_CRYSTAL_BASE = "textures/models/crystal_purple_transparent.png";
    public static final String ENERGY_CRYSTAL_NO_SHADER = "textures/models/crystal_no_shader.png";
    public static final String ENERGY_BEAM_BASIC = "textures/particle/energy_beam_basic.png";
    public static final String ENERGY_BEAM_WYVERN = "textures/particle/energy_beam_wyvern.png";
    public static final String ENERGY_BEAM_DRACONIC = "textures/particle/energy_beam_draconic.png";

    public static final String REACTOR_STABILIZER = "textures/models/reactor_stabilizer_core.png";
    public static final String REACTOR_STABILIZER_RING = "textures/models/reactor_stabilizer_ring.png";
    public static final String REACTOR_INJECTOR = "textures/models/model_reactor_power_injector.png";

    public static final String DRACONIUM_CHEST = "textures/models/draconium_chest.png";

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        GlassParticleDummyModel.INSTANCE.sprite = null;
    }
}
