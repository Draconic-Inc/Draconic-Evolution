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
import com.brandon3055.draconicevolution.items.tools.DraconiumCapacitor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
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
	public static BlockMobSafe draconicBlock = (BlockMobSafe) ((BlockBCore) new BlockMobSafe(Material.iron).setHardness(20F).setResistance(1000F)).setHarvestTool("pickaxe", 4);

	@Feature(name = "infusedObsidian")
	public static BlockMobSafe infusedObsidian = (BlockMobSafe) ((BlockBCore) new BlockMobSafe(Material.rock).setHardness(100F).setResistance(4000F)).setHarvestTool("pickaxe", 4);
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
    //endregion

    //region Advanced Machines
	@Feature(name = "energyStorageCore", tileEntity = TileEnergyStorageCore.class, cTab = 1)
	public static EnergyStorageCore energyStorageCore = new EnergyStorageCore();

    @Feature(name = "energyPylon", tileEntity = TileEnergyPylon.class, cTab = 1)
    public static EnergyPylon energyPylon = new EnergyPylon();

    @Feature(name = "invisECoreBlock", tileEntity = TileInvisECoreBlock.class, cTab = -1)
    public static InvisECoreBlock invisECoreBlock = new InvisECoreBlock();
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

    @Feature(name = "wyvernEnergyCore", stateOverride = "simpleComponents#type=wyvernecore")
    public static Item wyvernEnergyCore = new Item();

    @Feature(name = "draconicEnergyCore", stateOverride = "simpleComponents#type=draconicecore")
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

    //endregion
}
