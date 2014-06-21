package draconicevolution.common.items;

import java.util.List;

import draconicevolution.DraconicEvolution;
import draconicevolution.common.lib.Strings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class DraconicCompound extends TolkienItem {
	public DraconicCompound() {
		this.setUnlocalizedName(Strings.draconicCompoundName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		ModItems.register(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean extraInformation)
	{
		list.add("Imbued with the power of the dragon heart");
	}

}