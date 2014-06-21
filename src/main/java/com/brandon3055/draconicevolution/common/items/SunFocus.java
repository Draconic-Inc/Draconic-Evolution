package draconicevolution.common.items;

import draconicevolution.DraconicEvolution;
import draconicevolution.common.lib.Strings;

public class SunFocus extends TolkienItem {
	public SunFocus() {
		this.setUnlocalizedName(Strings.sunFocusName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		ModItems.register(this);
	}

}
