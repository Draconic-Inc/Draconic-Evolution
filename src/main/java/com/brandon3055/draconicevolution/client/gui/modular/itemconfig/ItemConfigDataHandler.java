package com.brandon3055.draconicevolution.client.gui.modular.itemconfig;

import codechicken.lib.util.SneakyUtils;
import com.brandon3055.draconicevolution.DEConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by brandon3055 on 17/5/20.
 */
public class ItemConfigDataHandler {

    public static CompoundNBT retrieveData() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hasSingleplayerServer()) {
            ServerWorld world = mc.getSingleplayerServer().getLevel(World.OVERWORLD);
            SinglePlayerWorldData data = world.getDataStorage().computeIfAbsent(SinglePlayerWorldData::new, "draconic_item_config");
            return data.data;
        } else {
            Path file = Paths.get("./config/brandon3055/servers/" + DEConfig.serverID + ".dat");
            if (Files.exists(file)) {
                try (InputStream is = Files.newInputStream(file)) {
                    return CompressedStreamTools.readCompressed(is);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new CompoundNBT();
    }

    public static void saveData(CompoundNBT nbt) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hasSingleplayerServer()) {
            ServerWorld world = mc.getSingleplayerServer().getLevel(World.OVERWORLD);
            SinglePlayerWorldData data = world.getDataStorage().computeIfAbsent(SinglePlayerWorldData::new, "draconic_item_config");
            data.data = nbt;
            data.setDirty();
        } else {
            Path file = Paths.get("./config/brandon3055/servers/" + DEConfig.serverID + ".dat");
            if (Files.notExists(file)) {
                SneakyUtils.sneaky(() -> Files.createDirectories(file.getParent()));
            } else {
                SneakyUtils.sneaky(() -> Files.delete(file));
            }

            try (OutputStream os = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
                CompressedStreamTools.writeCompressed(nbt, os);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static class SinglePlayerWorldData extends WorldSavedData {
        private CompoundNBT data = new CompoundNBT();

        public SinglePlayerWorldData() {
            super("draconic_item_config");
        }

        @Override
        public void load(CompoundNBT nbt) {
            data = nbt;
        }

        @Override
        public CompoundNBT save(CompoundNBT compound) {
            return data;
        }
    }

}
