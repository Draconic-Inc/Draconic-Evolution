package com.brandon3055.draconicevolution.api.modules.properties;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map;

/**
 * Created by brandon3055 on 3/5/20.
 */
public class SpeedData implements ModuleData<SpeedData> {
    private final double playerSpeedMultiplier;
    private final double machineSpeedMultiplier;

    public SpeedData(double playerSpeedMultiplier, double machineSpeedMultiplier) {
        this.playerSpeedMultiplier = playerSpeedMultiplier;
        this.machineSpeedMultiplier = machineSpeedMultiplier;
    }

    public double getMachineSpeedMultiplier() {
        return machineSpeedMultiplier;
    }

    public double getPlayerSpeedMultiplier() {
        return playerSpeedMultiplier;
    }

    @Override
    public SpeedData combine(SpeedData other) {
        return new SpeedData(playerSpeedMultiplier + other.playerSpeedMultiplier, machineSpeedMultiplier + other.machineSpeedMultiplier);
    }

    @Override
    public void addInformation(Map<ITextComponent, ITextComponent> map) {
        map.put(new StringTextComponent("SpeedModule"), new StringTextComponent("TODO"));
    }
}
