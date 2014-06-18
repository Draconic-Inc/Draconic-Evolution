package draconicevolution.common.items.armor;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import draconicevolution.common.DraconicEvolution;
import draconicevolution.common.items.ModItems;
import draconicevolution.common.lib.References;
import draconicevolution.common.lib.Strings;

public class WyvernChestPlate extends ItemArmor {
	public IIcon itemIcon1;

	public WyvernChestPlate() {
		super(ModItems.DRACONIUMARMOR_T1, 1, 1);
		this.setUnlocalizedName(Strings.wyvernChestPlateName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(1));
		GameRegistry.registerItem(this, Strings.wyvernChestPlateName);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
	{
		return "draconicevolution:textures/diamond_layer_1.png";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(final IIconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "wyvernPickaxe0");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1)
	{
		return itemIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation)
	{
		if ((!Keyboard.isKeyDown(42)) && (!Keyboard.isKeyDown(54)))
			list.add(EnumChatFormatting.DARK_GREEN + "Hold shift for information");
		else {
			list.add(EnumChatFormatting.GREEN + "Shift Right-click to change mode");
			list.add("");
			list.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + "Weary of plain tools you begin to understand");
			list.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + "ways to use Draconic energy to upgrade");
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		return EnumRarity.uncommon;
	}

	public static void registerRecipe()
	{
		//CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.wyvernPickaxe), " C ", "CPC", " C ", 'C', ModItems.infusedCompound, 'P', Items.diamond_pickaxe);
	}
}
