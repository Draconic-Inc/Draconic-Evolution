package com.brandon3055.draconicevolution.datagen;

import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.concurrent.CompletableFuture;

/**
 * Created by brandon3055 on 31/10/2024
 */
public class CuriosProvider extends CuriosDataProvider {

    public CuriosProvider(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> registries) {
        super(DraconicEvolution.MODID, output, fileHelper, registries);
    }

    @Override
    public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
        createSlot("curio").size(2);
        createSlot("belt");
        createSlot("charm");
        createSlot("body");
        createSlot("back");
        createEntities("curio")
                .addPlayer()
                .addSlots("curio", "belt", "charm", "body", "back");
    }
}
