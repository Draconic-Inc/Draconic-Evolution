package draconicevolution.common.items;

import draconicevolution.common.DraconicEvolution;
import draconicevolution.common.lib.Strings;

public class DraconiumIngot extends TolkienItem {
	public DraconiumIngot() {
		this.setUnlocalizedName(Strings.draconiumIngotName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		ModItems.register(this);
	}

}
