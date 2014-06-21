package draconicevolution.common.items;

import draconicevolution.DraconicEvolution;
import draconicevolution.common.lib.Strings;

public class DraconiumBlend extends TolkienItem {
	public DraconiumBlend() {
		this.setUnlocalizedName(Strings.draconiumBlendName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		ModItems.register(this);
	}

}