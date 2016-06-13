package com.brandon3055.draconicevolution;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.brandonscore.blocks.BlockMobSafe;
import com.brandon3055.brandonscore.blocks.ItemBlockBCore;
import com.brandon3055.brandonscore.blocks.ItemBlockBasic;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.brandonscore.items.ItemSimpleSubs;
import com.brandon3055.draconicevolution.blocks.*;
import com.brandon3055.draconicevolution.blocks.itemblock.ItemDraconiumBlock;
import com.brandon3055.draconicevolution.blocks.tileentity.*;
import com.brandon3055.draconicevolution.items.Debugger;
import com.brandon3055.draconicevolution.items.armor.DraconicArmor;
import com.brandon3055.draconicevolution.items.armor.WyvernArmor;
import com.brandon3055.draconicevolution.items.tools.*;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 18/3/2016.
 * This class contains a reference to all blocks and items in Draconic Evolution
 */
public class DEFeatures {

	/* ------------------ Blocks ------------------ */

    //region Simple Blocks
	@Feature(name = "draconiumOre", variantMap = {"0:type=normal", "1:type=nether", "2:type=end"}, itemBlock = ItemBlockBasic.class)
	public static DraconiumOre draconiumOre = (DraconiumOre) new DraconiumOre().setHardness(10f).setResistance(20.0f);

	@Feature(name = "draconiumBlock", variantMap = {"0:charged=false", "1:charged=true"}, itemBlock = ItemDraconiumBlock.class)
	public static DraconiumBlock draconiumBlock = (DraconiumBlock) new DraconiumBlock().setHardness(10f).setResistance(20.0f);

	@Feature(name = "draconicBlock")
	public static BlockMobSafe draconicBlock = (BlockMobSafe) ((BlockBCore) new BlockMobSafe(Material.IRON).setHardness(20F).setResistance(1000F)).setHarvestTool("pickaxe", 4);

	@Feature(name = "infusedObsidian")
	public static BlockMobSafe infusedObsidian = (BlockMobSafe) ((BlockBCore) new BlockMobSafe(Material.IRON).setHardness(100F).setResistance(4000F)).setHarvestTool("pickaxe", 4);
    //endregion

    //region Machines

    @Feature(name = "generator", tileEntity = TileGenerator.class, itemBlock = ItemBlockBCore.class, cTab = 1)
	public static Generator generator = new Generator();

	@Feature(name = "grinder", tileEntity = TileGrinder.class, itemBlock = ItemBlockBCore.class, cTab = 1)
	public static Grinder grinder = new Grinder();

	@Feature(name = "particleGenerator", variantMap = {"0:type=normal", "1:type=inverted", "2:type=stabilizer"}, cTab = 1, itemBlock = ItemBlockBasic.class)
	public static ParticleGenerator particleGenerator = new ParticleGenerator();

    @Feature(name = "energyInfuser", tileEntity = TileEnergyInfuser.class, itemBlock = ItemBlockBCore.class, cTab = 1)
    public static EnergyInfuser energyInfuser = new EnergyInfuser();

    @Feature(name = "upgradeModifier", tileEntity = TileUpgradeModifier.class, itemBlock = ItemBlockBCore.class, cTab = 1)
    public static UpgradeModifier upgradeModifier = new UpgradeModifier();

    @Feature(name = "craftingPedestal", variantMap = {"0:facing=up,tier=basic", "1:facing=up,tier=wyvern", "2:facing=up,tier=draconic", "3:facing=up,tier=chaotic"}, tileEntity = TileCraftingPedestal.class, itemBlock = ItemBlockBasic.class, cTab = 1)
    public static CraftingPedestal craftingPedestal = new CraftingPedestal();

    //endregion

    //region Advanced Machines
	@Feature(name = "energyStorageCore", tileEntity = TileEnergyStorageCore.class, cTab = 1)
	public static EnergyStorageCore energyStorageCore = new EnergyStorageCore();

    @Feature(name = "energyPylon", tileEntity = TileEnergyPylon.class, cTab = 1)
    public static EnergyPylon energyPylon = new EnergyPylon();

    @Feature(name = "invisECoreBlock", tileEntity = TileInvisECoreBlock.class, cTab = -1)
    public static InvisECoreBlock invisECoreBlock = new InvisECoreBlock();

    @Feature(name = "fusionCraftingCore", tileEntity = TileFusionCraftingCore.class, itemBlock = ItemBlockBCore.class, cTab = 1)
    public static FusionCraftingCore fusionCraftingCore = new FusionCraftingCore();
    //endregion

	/* ------------------ Items ------------------ */

    //region Crafting Components / Base items
	@Feature(name = "draconiumDust", stateOverride = "simpleComponents#type=draconiumDust")
	public static Item draconiumDust = new Item();

	@Feature(name = "draconiumIngot", stateOverride = "simpleComponents#type=draconiumIngot")
	public static Item draconiumIngot = new Item();

	@Feature(name = "draconicIngot", stateOverride = "simpleComponents#type=draconicIngot")
	public static Item draconicIngot = new Item();

	@Feature(name = "draconicCore", stateOverride = "simpleComponents#type=draconicCore")
	public static Item draconicCore = new Item();

	@Feature(name = "wyvernCore", stateOverride = "simpleComponents#type=wyvernCore")
	public static Item wyvernCore = new Item();

	@Feature(name = "awakenedCore", stateOverride = "simpleComponents#type=awakenedCore")
	public static Item awakenedCore = new Item();

	@Feature(name = "chaoticCore", stateOverride = "simpleComponents#type=chaoticCore")
	public static Item chaoticCore = new Item();

    @Feature(name = "wyvernEnergyCore", stateOverride = "simpleComponents#type=wyvernECore")
    public static Item wyvernEnergyCore = new Item();

    @Feature(name = "draconicEnergyCore", stateOverride = "simpleComponents#type=draconicECore")
    public static Item draconicEnergyCore = new Item();

    @Feature(name = "debugger", stateOverride = "simpleComponents#type=draconicIngot")
    public static Item debugger = new Debugger();

	@Feature(name = "nugget", variantMap = {"0:type=draconium", "1:type=awakened"})
	public static ItemSimpleSubs nugget = new ItemSimpleSubs(new String[]{"0:draconium", "1:awakened"});
    //endregion

    //region Tools

    @Feature(name = "draconiumCapacitor", variantMap = {"0:type=wyvern", "1:type=draconic", "2:type=creative"}, cTab = 1)
    public static DraconiumCapacitor draconiumCapacitor = new DraconiumCapacitor();
    public static ItemStack wyvernCapacitor = new ItemStack(draconiumCapacitor, 1, 0);
    public static ItemStack draconicCapacitor = new ItemStack(draconiumCapacitor, 1, 1);
    public static ItemStack creativeCapacitor = new ItemStack(draconiumCapacitor, 1, 2);

    @Feature(name = "wyvernAxe", cTab = 1)
    public static WyvernAxe wyvernAxe = new WyvernAxe();

    @Feature(name = "wyvernBow", cTab = 1)
    public static WyvernBow wyvernBow = new WyvernBow();

    @Feature(name = "wyvernPick", cTab = 1)
    public static WyvernPick wyvernPick = new WyvernPick();

    @Feature(name = "wyvernShovel", cTab = 1)
    public static WyvernShovel wyvernShovel = new WyvernShovel();

    @Feature(name = "wyvernSword", cTab = 1)
    public static WyvernSword wyvernSword = new WyvernSword();


    @Feature(name = "draconicAxe", cTab = 1)
    public static DraconicAxe draconicAxe = new DraconicAxe();

    @Feature(name = "draconicBow", cTab = 1)
    public static DraconicBow draconicBow = new DraconicBow();

    @Feature(name = "draconicHoe", cTab = 1)
    public static DraconicHoe draconicHoe = new DraconicHoe();

    @Feature(name = "draconicPick", cTab = 1)
    public static DraconicPick draconicPick = new DraconicPick();

    @Feature(name = "draconicShovel", cTab = 1)
    public static DraconicShovel draconicShovel = new DraconicShovel();

    @Feature(name = "draconicStaffOfPower", cTab = 1)
    public static DraconicStaffOfPower draconicStaffOfPower = new DraconicStaffOfPower();

    @Feature(name = "draconicSword", cTab = 1)
    public static DraconicSword draconicSword = new DraconicSword();

    //endregion

    //region Armor
    @Feature(name = "wyvernHelm", cTab = 1)
    public static WyvernArmor wyvernHelm = new WyvernArmor(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.HEAD);

    @Feature(name = "wyvernChest", cTab = 1)
    public static WyvernArmor wyvernChest = new WyvernArmor(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.CHEST);

    @Feature(name = "wyvernLegs", cTab = 1)
    public static WyvernArmor wyvernLegs = new WyvernArmor(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.LEGS);

    @Feature(name = "wyvernBoots", cTab = 1)
    public static WyvernArmor wyvernBoots = new WyvernArmor(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.FEET);

    @Feature(name = "draconicHelm", cTab = 1)
    public static DraconicArmor draconicHelm = new DraconicArmor(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.HEAD);

    @Feature(name = "draconicChest", cTab = 1)
    public static DraconicArmor draconicChest = new DraconicArmor(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.CHEST);

    @Feature(name = "draconicLegs", cTab = 1)
    public static DraconicArmor draconicLegs = new DraconicArmor(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.LEGS);

    @Feature(name = "draconicBoots", cTab = 1)
    public static DraconicArmor draconicBoots = new DraconicArmor(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.FEET);

    //endregion
}

//@Feature(name = "wyvernAxe", stateOverride = "tools#type=wyvernAxe")
//public static WyvernAxe wyvernAxe = new WyvernAxe();
//
//@Feature(name = "wyvernBow", stateOverride = "tools#type=wyvernBow")
//public static WyvernBow wyvernBow = new WyvernBow();
//
//@Feature(name = "wyvernPick", stateOverride = "tools#type=wyvernPick")
//public static WyvernPick wyvernPick = new WyvernPick();
//
//@Feature(name = "wyvernShovel", stateOverride = "tools#type=wyvernShovel")
//public static WyvernShovel wyvernShovel = new WyvernShovel();
//
//@Feature(name = "wyvernSword", stateOverride = "tools#type=wyvernSword")
//public static WyvernSword wyvernSword = new WyvernSword();
//
//
//@Feature(name = "draconicAxe", stateOverride = "tools#type=draconicAxe")
//public static DraconicAxe draconicAxe = new DraconicAxe();
//
//@Feature(name = "draconicBow", stateOverride = "tools#type=draconicBow")
//public static DraconicBow draconicBow = new DraconicBow();
//
//@Feature(name = "draconicHoe", stateOverride = "tools#type=draconicHoe")
//public static DraconicHoe draconicHoe = new DraconicHoe();
//
//@Feature(name = "draconicPick", stateOverride = "tools#type=draconicPick")
//public static DraconicPick draconicPick = new DraconicPick();
//
//@Feature(name = "draconicShovel", stateOverride = "tools#type=draconicShovel")
//public static DraconicShovel draconicShovel = new DraconicShovel();
//
//@Feature(name = "draconicStaffOfPower", stateOverride = "tools#type=draconicStaffOfPower")
//public static DraconicStaffOfPower draconicStaffOfPower = new DraconicStaffOfPower();
//
//@Feature(name = "draconicSword", stateOverride = "tools#type=draconicSword")
//public static DraconicSword draconicSword = new DraconicSword();
