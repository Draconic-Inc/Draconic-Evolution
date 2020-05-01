package com.brandon3055.draconicevolution.api.modules.properties;

import com.brandon3055.draconicevolution.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.capability.IModuleHost;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 4/16/20.
 */
public class ShieldModuleProperties extends ModuleProperties<ShieldModuleProperties> {
    public static final SubProperty<ShieldModuleProperties> SHIELD_CAPACITY = new SubProperty<>("shield_capacity");
    public static final SubProperty<ShieldModuleProperties> SHIELD_RECHARGE = new SubProperty<>("shield_recharge");

    private final int shieldCapacity;
    private final int shieldRecharge;


    public ShieldModuleProperties(TechLevel techLevel, int shieldCapacity, int shieldRecharge, int width, int height) {
        super(techLevel, width, height);
        this.shieldCapacity = shieldCapacity;
        this.shieldRecharge = shieldRecharge;
    }

    public ShieldModuleProperties(TechLevel techLevel, int shieldCapacity, int shieldRecharge) {
        this(techLevel, shieldCapacity, shieldRecharge, 2, 2);
    }

    @Override
    public void addCombinedStats(List<ShieldModuleProperties> propertiesList, Map<ITextComponent, ITextComponent> map, IModuleHost moduleHost) {
        if (moduleHost.isSubPropertySupported(this, SHIELD_CAPACITY)) {
            //
        }
        map.put(new StringTextComponent("ShieldModule"), new StringTextComponent("TODO"));
    }

    public int getShieldCapacity() {
        return shieldCapacity;
    }

    public int getShieldRecharge() {
        return shieldRecharge;
    }

    //    ShieldModuleProperties setShieldPoints(int points);

//    class Impl implements ShieldModuleProperties {
//
//        public int width = 2;
//        public int height = 2;
//        public int shieldPoints;
//
//        @Override
//        public ShieldModuleProperties setShieldPoints(int points) {
//            this.shieldPoints = points;
//            return this;
//        }
//
//        @Override
//        public ShieldModuleProperties setDimensions(int width, int height) {
//            this.width = width;
//            this.height = height;
//            return this;
//        }
//
//        public void merge(Impl other) {
//            shieldPoints += other.shieldPoints;
//        }
//
//    }
}
