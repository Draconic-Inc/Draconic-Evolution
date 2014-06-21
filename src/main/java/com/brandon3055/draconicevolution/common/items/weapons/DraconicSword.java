package draconicevolution.common.items.weapons;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import draconicevolution.DraconicEvolution;
import draconicevolution.common.items.ModItems;
import draconicevolution.common.items.tools.ToolHandler;
import draconicevolution.common.lib.References;
import draconicevolution.common.lib.Strings;

public class DraconicSword extends ItemSword {
	public DraconicSword() {
		super(ModItems.DRACONIUM_T2);
		this.setUnlocalizedName(Strings.draconicSwordName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(1));
		GameRegistry.registerItem(this, Strings.draconicSwordName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(final IIconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "draconic_sword");
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		ToolHandler.AOEAttack(player, entity, stack, 15, 3);
		ToolHandler.demageEntytyBasedOnHealth(entity, player, 0.3F);
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation)
	{
		list.add(EnumChatFormatting.DARK_RED + "Your enemy's strength will be their undoing");
		list.add("");
		list.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + "Further Draconic research has allowed");
		list.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + "you to unlock even better methods");
	}

	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		return EnumRarity.rare;
	}

	public static void registerRecipe()
	{
		CraftingManager.getInstance().addRecipe(new ItemStack(ModItems.draconicSword), "ISI", "DPD", "ITI", 'P', ModItems.wyvernSword, 'D', ModItems.draconicCompound, 'S', ModItems.sunFocus, 'T', ModItems.draconicCore, 'I', ModItems.draconiumIngot);
	}
}
