package draconicevolution.common.items;

import draconicevolution.common.DraconicEvolution;
import draconicevolution.common.lib.Strings;

public class DraconicCore extends TolkienItem {
	public DraconicCore() {
		this.setUnlocalizedName(Strings.draconicCoreName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		ModItems.register(this);
	}

}
