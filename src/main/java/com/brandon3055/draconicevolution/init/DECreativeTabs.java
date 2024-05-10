package com.brandon3055.draconicevolution.init;

import com.brandon3055.brandonscore.lib.CustomTabHandling;
import com.brandon3055.draconicevolution.api.modules.items.ModuleItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 05/02/2024
 */
public class DECreativeTabs {

    public static void init() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(DECreativeTabs::registerTabs);
    }

    private static void registerTabs(RegisterEvent event) {
        event.register(Registries.CREATIVE_MODE_TAB, helper -> {
            List<ItemStack> blocksIcons = new ArrayList<>();
            List<ItemStack> itemsIcons = new ArrayList<>();
            List<ItemStack> modulesIcons = new ArrayList<>();
            helper.register(new ResourceLocation(MODID, "blocks"), CreativeModeTab.builder().title(Component.translatable("itemGroup.draconicevolution.blocks"))
                            .displayItems((params, output) -> {
                                for (ResourceLocation key : ForgeRegistries.BLOCKS.getKeys()) {
                                    if (key.getNamespace().equals(MODID)) {
                                        Block block = ForgeRegistries.BLOCKS.getValue(key);
                                        if (block instanceof CustomTabHandling || block == null) continue;
                                        output.accept(block.asItem());
                                        blocksIcons.add(new ItemStack(block));
                                    }
                                }
                            })
                            .withTabFactory(builder -> new CyclingTab(builder, blocksIcons))
                            .build()
            );

            helper.register(new ResourceLocation(MODID, "items"), CreativeModeTab.builder().title(Component.translatable("itemGroup.draconicevolution.items"))
                            .displayItems((params, output) -> {
                                for (ResourceLocation key : ForgeRegistries.ITEMS.getKeys()) {
                                    if (key.getNamespace().equals(MODID)) {
                                        Item item = ForgeRegistries.ITEMS.getValue(key);
                                        if (item instanceof CustomTabHandling || item == null || item instanceof BlockItem || item instanceof ModuleItem) continue;
                                        output.accept(item);
                                        itemsIcons.add(new ItemStack(item));
                                    }
                                }
                            })
                            .withTabFactory(builder -> new CyclingTab(builder, itemsIcons))
                            .build()
            );

            helper.register(new ResourceLocation(MODID, "modules"), CreativeModeTab.builder().title(Component.translatable("itemGroup.draconicevolution.modules"))
                    .displayItems((params, output) -> {
                        for (ResourceLocation key : ForgeRegistries.ITEMS.getKeys()) {
                            if (key.getNamespace().equals(MODID)) {
                                Item item = ForgeRegistries.ITEMS.getValue(key);
                                if (!(item instanceof ModuleItem)) continue;
                                output.accept(item);
                                modulesIcons.add(new ItemStack(item));
                            }
                        }
                    })
                    .withTabFactory(builder -> new CyclingTab(builder, modulesIcons))
                    .build()
            );
        });
    }

    private static class CyclingTab extends CreativeModeTab {
        private final List<ItemStack> stacks;

        public CyclingTab(CreativeModeTab.Builder builder, List<ItemStack> stacks) {
            super(builder);
            this.stacks = stacks;
        }

        @Override
        public ItemStack getIconItem() {
            int idx = (int) (System.currentTimeMillis() / 1200) % stacks.size();
            return stacks.get(idx);
        }
    }
}
