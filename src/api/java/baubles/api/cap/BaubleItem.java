package baubles.api.cap;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.item.ItemStack;

public class BaubleItem implements IBauble
{
	private BaubleType baubleType;

	public BaubleItem(BaubleType type) {
		baubleType = type;
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return baubleType;
	}
}
