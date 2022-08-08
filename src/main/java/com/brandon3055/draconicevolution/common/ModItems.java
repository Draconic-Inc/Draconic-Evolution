package com.brandon3055.draconicevolution.common;

import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.items.*;
import com.brandon3055.draconicevolution.common.items.armor.DraconicArmor;
import com.brandon3055.draconicevolution.common.items.armor.WyvernArmor;
import com.brandon3055.draconicevolution.common.items.tools.*;
import com.brandon3055.draconicevolution.common.items.weapons.DraconicBow;
import com.brandon3055.draconicevolution.common.items.weapons.DraconicSword;
import com.brandon3055.draconicevolution.common.items.weapons.WyvernBow;
import com.brandon3055.draconicevolution.common.items.weapons.WyvernSword;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

@GameRegistry.ObjectHolder(References.MODID)
public class ModItems {
    public static ArmorMaterial WYVERN_ARMOR =
            EnumHelper.addArmorMaterial("WYVERN_ARMOR", -1, new int[] {3, 8, 6, 3}, 30);
    public static ArmorMaterial DRACONIC_ARMOR =
            EnumHelper.addArmorMaterial("DRACONIC_ARMOR", -1, new int[] {3, 8, 6, 3}, 30);
    public static ToolMaterial WYVERN = EnumHelper.addToolMaterial("WYVERN", 10, -1, 12.0F, 20.0F, 35);
    public static ToolMaterial AWAKENED = EnumHelper.addToolMaterial("AWAKENED", 10, -1, 16.0F, 40.0F, 40);
    public static ToolMaterial CHAOTIC = EnumHelper.addToolMaterial("CHAOTIC", 10, -1, 400.0F, 60.0F, 45);

    public static Item draconicPickaxe;
    public static Item draconicShovel;
    public static Item draconicHoe;
    public static Item draconicAxe;
    public static Item draconicSword;
    public static Item draconicBow;
    public static ItemArmor draconicHelm;
    public static ItemArmor draconicChest;
    public static ItemArmor draconicLeggs;
    public static ItemArmor draconicBoots;
    public static Item draconicDestructionStaff;

    public static Item wyvernPickaxe;
    public static Item wyvernShovel;
    public static Item wyvernSword;
    public static Item wyvernBow;
    public static ItemArmor wyvernHelm;
    public static ItemArmor wyvernChest;
    public static ItemArmor wyvernLeggs;
    public static ItemArmor wyvernBoots;

    public static Item dezilsMarshmallow;

    public static ItemDE wyvernCore;
    public static ItemDE draconiumDust;
    public static ItemDE draconiumIngot;
    public static ItemDE draconiumBlend;
    public static ItemDE dragonHeart;
    public static ItemDE awakenedCore;
    public static ItemDE tclogo;
    public static ItemDE draconicCore;
    public static ItemDE sunFocus;
    public static ItemDE mobSoul;
    public static ItemDE enderArrow;
    public static ItemDE safetyMatch;
    public static ItemDE key;
    public static ItemDE creativeStructureSpawner;
    public static ItemDE adminSpawnEgg;
    public static ItemDE infoTablet;
    public static ItemDE draconicIngot;
    public static ItemDE draconiumEnergyCore;
    public static ItemDE draconiumFluxCapacitor;
    public static ItemDE wrench;
    public static ItemDE chaosShard;
    public static ItemDE reactorStabilizerParts;
    public static ItemDE chaoticCore;
    public static ItemDE nugget;
    public static ItemDE chaosFragment;
    public static ItemDE magnet;

    public static ItemDE teleporterMKI;
    public static ItemDE teleporterMKII;

    public static ItemStack wyvernFluxCapacitor;
    public static ItemStack draconicFluxCapacitor;
    public static ItemStack wyvernEnergyCore;
    public static ItemStack draconicEnergyCore;

    public static ItemStack partStabFrame;
    public static ItemStack partStabRotorInner;
    public static ItemStack partStabRotorOuter;
    public static ItemStack partStabRotorAssembly;
    public static ItemStack partStabRing;

    public static ItemStack nuggetDraconium;
    public static ItemStack nuggetAwakened;

    public static void init() {
        draconicDestructionStaff = new DraconicDistructionStaff();
        draconicPickaxe = new DraconicPickaxe();
        draconicAxe = new DraconicAxe();
        draconicShovel = new DraconicShovel();
        draconicHoe = new DraconicHoe();
        draconicSword = new DraconicSword();
        draconicBow = new DraconicBow();
        draconicHelm = new DraconicArmor(DRACONIC_ARMOR, 0, Strings.draconicHelmName);
        draconicChest = new DraconicArmor(DRACONIC_ARMOR, 1, Strings.draconicChestName);
        draconicLeggs = new DraconicArmor(DRACONIC_ARMOR, 2, Strings.draconicLeggsName);
        draconicBoots = new DraconicArmor(DRACONIC_ARMOR, 3, Strings.draconicBootsName);

        wyvernPickaxe = new WyvernPickaxe();
        wyvernShovel = new WyvernShovel();
        wyvernSword = new WyvernSword();
        wyvernBow = new WyvernBow();
        wyvernHelm = new WyvernArmor(WYVERN_ARMOR, 0, Strings.wyvernHelmName);
        wyvernChest = new WyvernArmor(WYVERN_ARMOR, 1, Strings.wyvernChestName);
        wyvernLeggs = new WyvernArmor(WYVERN_ARMOR, 2, Strings.wyvernLeggsName);
        wyvernBoots = new WyvernArmor(WYVERN_ARMOR, 3, Strings.wyvernBootsName);

        draconicCore = new DraconicCore();
        wyvernCore = new WyvernCore();
        awakenedCore = new AwakenedCore();
        chaoticCore = new ChaoticCore();
        draconiumDust = new DraconiumDust();
        draconiumIngot = new DraconiumIngot();
        draconiumBlend = new DraconiumBlend();
        dragonHeart = new DragonHeart();
        teleporterMKI = new TeleporterMKI();
        teleporterMKII = new TeleporterMKII();
        tclogo = new Tclogo();
        sunFocus = new SunFocus();
        mobSoul = new MobSoul();
        enderArrow = new EnderArrow();
        safetyMatch = new SafetyMatch();
        key = new Key();
        creativeStructureSpawner = new CreativeStructureSpawner();
        adminSpawnEgg = new AdminSpawnEgg();
        infoTablet = new InfoTablet();
        draconicIngot = new DraconicIngot();
        draconiumEnergyCore = new DraconiumEnergyCore();
        draconiumFluxCapacitor = new DraconiumFluxCapacitor();
        wrench = new Wrench();
        chaosShard = new ChaosShard();
        reactorStabilizerParts = new ReactorStabiliserPart();
        nugget = new Nugget();
        chaosFragment = new ChaosFragment();
        magnet = new Magnet();

        dezilsMarshmallow = new DezilsMarshmallow();

        // Custom ItemStacks
        wyvernEnergyCore = new ItemStack(ModItems.draconiumEnergyCore, 1, 0);
        draconicEnergyCore = new ItemStack(ModItems.draconiumEnergyCore, 1, 1);
        wyvernFluxCapacitor = new ItemStack(ModItems.draconiumFluxCapacitor, 1, 0);
        draconicFluxCapacitor = new ItemStack(ModItems.draconiumFluxCapacitor, 1, 1);

        partStabFrame = new ItemStack(reactorStabilizerParts, 1, 0);
        partStabRotorInner = new ItemStack(reactorStabilizerParts, 1, 1);
        partStabRotorOuter = new ItemStack(reactorStabilizerParts, 1, 2);
        partStabRotorAssembly = new ItemStack(reactorStabilizerParts, 1, 3);
        partStabRing = new ItemStack(reactorStabilizerParts, 1, 4);

        nuggetDraconium = new ItemStack(nugget, 1, 0);
        nuggetAwakened = new ItemStack(nugget, 1, 1);
    }

    public static void register(final ItemDE item) {
        String name = item.getUnwrappedUnlocalizedName(item.getUnlocalizedName());
        if (isEnabled(item)) GameRegistry.registerItem(item, name.substring(name.indexOf(":") + 1));
    }

    public static boolean isEnabled(Item item) {
        return !ConfigHandler.disabledNamesList.contains(item.getUnlocalizedName());
    }
}
