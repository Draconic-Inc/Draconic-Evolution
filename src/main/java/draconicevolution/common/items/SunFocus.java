package draconicevolution.common.items;

import draconicevolution.common.DraconicEvolution;
import draconicevolution.common.lib.Strings;

public class SunFocus extends TolkienItem {
	public SunFocus() {
		this.setUnlocalizedName(Strings.sunFocusName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		ModItems.register(this);
	}

}
