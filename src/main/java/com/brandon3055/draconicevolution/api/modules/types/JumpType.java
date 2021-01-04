package com.brandon3055.draconicevolution.api.modules.types;

import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.DecimalProperty;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.data.JumpData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.brandon3055.draconicevolution.api.config.ConfigProperty.DecimalFormatter.PLUS_PERCENT_0;

/**
 * Created by brandon3055 on 2/8/20
 */
public class JumpType extends ModuleTypeImpl<JumpData> {

    public JumpType(String name, int defaultWidth, int defaultHeight, ModuleCategory... categories) {
        super(name, defaultWidth, defaultHeight, categories);
    }

    @Override
    public void getTypeProperties(@Nullable JumpData jumpData, Map<ConfigProperty, Consumer<JumpData>> propertyMap) {
//        if (jumpData != null) {
//            double jumpVal = jumpData.getMultiplier();
//            DecimalProperty jump = new DecimalProperty("de.module.jump_boost_run.prop", jumpVal).setFormatter(PLUS_PERCENT_0).range(0, jumpVal);
//            propertyMap.put(jump, e-> jump.range(0, jumpVal));
//            DecimalProperty runJump = new DecimalProperty("de.module.jump_boost.prop", 0).setFormatter(PLUS_PERCENT_0).range(0, jumpVal);
//            propertyMap.put(runJump, e-> runJump.range(0, jumpVal));
//        }
    }
}
