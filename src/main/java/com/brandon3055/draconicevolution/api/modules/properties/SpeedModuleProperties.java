package com.brandon3055.draconicevolution.api.modules.properties;

import com.brandon3055.draconicevolution.api.TechLevel;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 4/16/20.
 */
public class SpeedModuleProperties extends ModuleProperties<SpeedModuleProperties> {
    private final double playerSpeedMultiplier;
    private final double machineSpeedMultiplier;

    public SpeedModuleProperties(TechLevel techLevel, double playerSpeedMultiplier, double machineSpeedMultiplier, int width, int height) {
        super(techLevel, width, height);
        this.playerSpeedMultiplier = playerSpeedMultiplier;
        this.machineSpeedMultiplier = machineSpeedMultiplier;
    }

    public SpeedModuleProperties(TechLevel techLevel, double playerSpeedMultiplier, double machineSpeedMultiplier) {
        this(techLevel, playerSpeedMultiplier, machineSpeedMultiplier, 2, 2);
    }

    public double getMachineSpeedMultiplier() {
        return machineSpeedMultiplier;
    }

    public double getPlayerSpeedMultiplier() {
        return playerSpeedMultiplier;
    }

    @Override
    public void addCombinedStats(List<SpeedModuleProperties> propertiesList, Map<ITextComponent, ITextComponent> map) {
        map.put(new StringTextComponent("SpeedModule"), new StringTextComponent("TODO"));
    }
}
