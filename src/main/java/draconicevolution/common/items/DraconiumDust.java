package draconicevolution.common.items;

import draconicevolution.common.DraconicEvolution;
import draconicevolution.common.lib.Strings;

public class DraconiumDust extends TolkienItem {
	public DraconiumDust() {
		this.setUnlocalizedName(Strings.draconiumDustName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		ModItems.register(this);
	}

}
