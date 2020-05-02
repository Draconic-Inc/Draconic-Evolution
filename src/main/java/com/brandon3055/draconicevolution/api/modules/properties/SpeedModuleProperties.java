package com.brandon3055.draconicevolution.api.modules.properties;

import com.brandon3055.draconicevolution.api.TechLevel;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 4/16/20.
 */
public class SpeedModuleProperties extends ModuleProperties<SpeedModuleProperties> {
    public static final SubProperty<ShieldModuleProperties> PLAYER_SPEED = new SubProperty<>("player_speed");
    public static final SubProperty<ShieldModuleProperties> MACHINE_SPEED = new SubProperty<>("machine_speed");

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
    public void addCombinedStats(List<SpeedModuleProperties> propertiesList, Map<ITextComponent, ITextComponent> map, ModuleHost moduleHost) {
//        PLAYER_SPEED / MACHINE_SPEED
        map.put(new StringTextComponent("SpeedModule"), new StringTextComponent("TODO"));
    }
}
