package draconicevolution.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import draconicevolution.common.items.tools.DraconicAxe;
import draconicevolution.common.items.tools.DraconicDistructionStaff;
import draconicevolution.common.items.tools.DraconicHoe;
import draconicevolution.common.items.tools.DraconicPickaxe;
import draconicevolution.common.items.tools.DraconicShovel;
import draconicevolution.common.items.tools.TeleporterMKI;
import draconicevolution.common.items.tools.TeleporterMKII;
import draconicevolution.common.items.tools.WyvernPickaxe;
import draconicevolution.common.items.tools.WyvernShovel;
import draconicevolution.common.items.weapons.DraconicBow;
import draconicevolution.common.items.weapons.DraconicSword;
import draconicevolution.common.items.weapons.WyvernBow;
import draconicevolution.common.items.weapons.WyvernSword;

public class ModItems {
	public static ArmorMaterial DRACONIUMARMOR_T1 = EnumHelper.addArmorMaterial("DRACONIUMARMOR_T1", -1, new int[] { 3, 8, 6, 3 }, 30);
	public static ToolMaterial DRACONIUM_T1 = EnumHelper.addToolMaterial("DRACONIUM_T1", 4, -1, 12.0F, 11.0F, 35);
	public static ToolMaterial DRACONIUM_T2 = EnumHelper.addToolMaterial("DRACONIUM_T2", 4, -1, 16.0F, 15.0F, 40);
	public static ToolMaterial DRACONIUM_T3 = EnumHelper.addToolMaterial("DRACONIUM_T3", 4, -1, 400.0F, 20.0F, 45);

	public static Item draconicPickaxe;
	public static Item draconicShovel;
	public static Item draconicHoe;
	public static Item draconicAxe;
	public static Item draconicSword;
	public static Item draconicBow;

	public static Item wyvernPickaxe;
	public static Item wyvernShovel;
	public static Item wyvernSword;
	public static Item wyvernBow;
	public static Item wyvernChestPlate;

	public static Item infusedCompound;
	public static Item rawDraconicPickHead;
	public static Item rawDraconicSwordBlade;
	public static Item draconicPickHead;
	public static Item draconicSwordBlade;
	public static Item draconiumDust;
	public static Item draconiumIngot;
	public static Item draconiumBlend;
	public static Item dragonHeart;
	public static Item draconicDistructionStaff;
	public static Item draconicCompound;
	public static Item tclogo;
	public static Item draconicCore;
	public static Item sunFocus;
	
	public static Item teleporterMKI;
	public static Item teleporterMKII;
	
	public static Item reader;

	public static Item testPick;

	public static void init()
	{	
		draconicDistructionStaff = new DraconicDistructionStaff();
		draconicPickaxe = new DraconicPickaxe();
		draconicAxe = new DraconicAxe();
		draconicShovel = new DraconicShovel();
		draconicHoe = new DraconicHoe();
		draconicSword = new DraconicSword();
		draconicBow = new DraconicBow();
		
		wyvernPickaxe = new WyvernPickaxe();
		wyvernShovel = new WyvernShovel();
		wyvernSword = new WyvernSword();
		wyvernBow = new WyvernBow();
		//wyvernChestPlate = new WyvernChestPlate();
		
		infusedCompound = new InfusedCompound();
		draconiumDust = new DraconiumDust();
		draconiumIngot = new DraconiumIngot();
		draconiumBlend = new DraconiumBlend();
		dragonHeart = new DragonHeart();
		draconicCompound = new DraconicCompound();
		teleporterMKI = new TeleporterMKI();
		teleporterMKII = new TeleporterMKII();
		tclogo = new Tclogo();
		draconicCore = new DraconicCore();
		sunFocus = new SunFocus();
		//reader = new Reader();

		//testPick = new TestPick();
	}

	public static void register(final TolkienItem item)
	{
		GameRegistry.registerItem(item, item.getUnwrappedUnlocalizedName(item.getUnlocalizedName()));
	}
}
