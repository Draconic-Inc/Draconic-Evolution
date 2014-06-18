package draconicevolution.common.items;

import draconicevolution.common.DraconicEvolution;
import draconicevolution.common.lib.Strings;

public class DragonHeart extends TolkienItem {
	public DragonHeart() {
		this.setUnlocalizedName(Strings.dragonHeartName);
		this.setCreativeTab(DraconicEvolution.getCreativeTab(2));
		ModItems.register(this);
	}

}