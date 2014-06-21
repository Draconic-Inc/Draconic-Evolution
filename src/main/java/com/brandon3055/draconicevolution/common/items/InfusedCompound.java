package draconicevolution.common.items;

import draconicevolution.DraconicEvolution;
import draconicevolution.common.lib.Strings;

public class InfusedCompound extends TolkienItem {
	public InfusedCompound() {
		this.setUnlocalizedName(Strings.infusedCompoundName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		ModItems.register(this);
	}

}