package com.brandon3055.draconicevolution.api.modules.data;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class ShieldData implements ModuleData<ShieldData> {
    private final int shieldCapacity;
    private final int shieldRecharge;

    public ShieldData(int shieldCapacity, int shieldRecharge) {
        this.shieldCapacity = shieldCapacity;
        this.shieldRecharge = shieldRecharge;
    }

    public int getShieldCapacity() {
        return shieldCapacity;
    }

    public int getShieldRecharge() {
        return shieldRecharge;
    }

    @Override
    public ShieldData combine(ShieldData other) {
        return new ShieldData(shieldCapacity + other.shieldCapacity, shieldRecharge + other.shieldRecharge);
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map, ModuleContext context) {
        map.put(new StringTextComponent("ShieldModule"), new StringTextComponent("TODO"));
    }
}
