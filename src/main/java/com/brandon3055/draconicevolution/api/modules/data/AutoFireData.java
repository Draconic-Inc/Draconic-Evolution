package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Map;

public class AutoFireData implements ModuleData<AutoFireData> {

    private boolean isEnabled;

    public AutoFireData(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public AutoFireData combine(AutoFireData other) {
        return new AutoFireData(this.isEnabled() || other.isEnabled());
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map, @Nullable ModuleContext context, boolean stack) {

    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
