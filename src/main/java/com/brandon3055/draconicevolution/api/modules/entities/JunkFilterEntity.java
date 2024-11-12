package com.brandon3055.draconicevolution.api.modules.entities;

import codechicken.lib.gui.modular.sprite.Material;
import com.brandon3055.brandonscore.client.BCGuiTextures;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.NoData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 21/01/2023
 */
public class JunkFilterEntity extends FilteredModuleEntity<NoData> {

    public JunkFilterEntity(Module<NoData> module) {
        super(module, 9);
        addEnabledProperty("junk_filter_mod", true);
    }

    @Override
    protected List<Slot> layoutSlots(int x, int y, int width, int height) {
        List<Slot> slots = new ArrayList<>();
        double slotXSize = width / 3D;
        double slotYSize = height / 3D;
        for (int i = 0; i < slotsCount; i++) {
            double xPos = x + ((i % 3) * slotXSize);
            //noinspection IntegerDivisionInFloatingPointContext
            double yPos = y + ((i / 3) * slotYSize);
            slots.add(new Slot(i, xPos, yPos, slotXSize));
        }
        return slots;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected Material getSlotOverlay() {
        return BCGuiTextures.get("slots/trash");
    }
}
